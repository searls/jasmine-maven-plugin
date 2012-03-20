package com.github.searls.jasmine;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;

import com.gargoylesoftware.htmlunit.DownloadedContent;
import com.gargoylesoftware.htmlunit.WebConnection;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebResponseData;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

/**
 * A WebConnection for HTMLUnit that will load resources from the CLASSPATH.
 * It looks for the marker "{@value #PROTOCOL}:" and the host {@value #FAKE_HOST}.
 * 
 * If it does not recognise the request it can delegate to a wrapped WebConnection.
 * 
 * @author Richard Corfield &lt;m0rjc@m0rjc.me.uk&gt;
 */
public class FakeHttpWebConnection implements WebConnection {

	public static final String PROTOCOL="http";
	public static final String FAKE_HOST = "maven.test.dependencies";
	
	/** Level for diagnostics tracing. */
	private static final Level TRACE_LEVEL = Level.INFO;
	private static final Level ERROR_LEVEL = Level.WARNING;
	
	/** Relevant scopes to search in. */
	private static final Set<String> RELEVANT_SCOPES;
	static{
		Set<String> scopes = new HashSet<String>();
		scopes.add(Artifact.SCOPE_COMPILE);
		scopes.add(Artifact.SCOPE_RUNTIME);
		scopes.add(Artifact.SCOPE_TEST);
		RELEVANT_SCOPES = scopes;
	}
	
	/** If I cannot resolve the URL then delegate to the next in the chain. */
	private final WebConnection m_next;
	
	private final ClassLoader m_classLoader;
	
	/**
	 * @param connectionToWrap WebConnection to use if this connection does not recognise the URL.
	 * 	Can be null.
	 */
	public FakeHttpWebConnection(WebConnection connectionToWrap, MavenProject project) {
		m_next = connectionToWrap;
		m_classLoader = new URLClassLoader(getUrls(project));
	}

//	@Override
	public WebResponse getResponse(WebRequest request) throws IOException {
		long startTime = System.currentTimeMillis();
		if(isMine(request)){
			URL url = request.getUrl();
			log(TRACE_LEVEL, "Handling resource: {0}", url);
			final InputStream resource = getResource(url);
			if(resource != null){
				try {
					// Need to copy to allow rewind for the content type sniffer.
					final byte[] buffered = IOUtils.toByteArray(resource);
					return makeResponse(request, buffered, startTime);
				} finally {
					IOUtils.closeQuietly(resource);
				}

			} else {
				log(ERROR_LEVEL, "Resource not found: {0}", url);
			}
		} else if(m_next != null){
			return m_next.getResponse(request);
		}
		
		return makeNotFoundResponse(request, startTime);
	}

	/**
	 * @param request
	 * @param startTime
	 * @return
	 * @throws IOException
	 */
	private WebResponse makeNotFoundResponse(WebRequest request, long startTime)
			throws IOException {
		DownloadedContent responseBody = new DownloadedContent.InMemory("Not Found".getBytes());
		WebResponseData responseData = new WebResponseData(responseBody, 404, "Not found", (List<NameValuePair>) Collections.EMPTY_LIST);
		return new WebResponse(responseData, request, System.currentTimeMillis() - startTime);
	}

	/**
	 * @param request
	 * @param resource
	 * @param startTime
	 * @return
	 * @throws IOException
	 */
	private WebResponse makeResponse(WebRequest request,
			final byte[] data, long startTime) throws IOException {
		
		DownloadedContent responseBody = new DownloadedContent.InMemory(data); 
		String contentType = guessContentType(request.getUrl().getFile(), data);
		List<NameValuePair> headers = new ArrayList<NameValuePair>();
		
		headers.add(new NameValuePair("Content-Type", contentType));
		WebResponseData responseData = new WebResponseData(responseBody, 200, "OK", headers);
		long loadTime = System.currentTimeMillis() - startTime;
		return new WebResponse(responseData, request, loadTime);
	}

	/**
	 * @param url
	 * @param resource
	 * @return
	 * @throws IOException 
	 */
	private String guessContentType(String filename, byte[] data) throws IOException {
		
		String result = URLConnection.guessContentTypeFromName(filename);
		if(result == null)
		{
			result = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(data));
		}
		if(filename.endsWith(".js"))
		{
			return "application/javascript";
		}
		
		if(filename.endsWith(".html"))
		{
			return "text/html";
		}
		
		return "application/octet-stream";
	}

	/**
	 * @param request
	 * @return
	 */
	private boolean isMine(WebRequest request) {
		return PROTOCOL.equals(request.getUrl().getProtocol()) && FAKE_HOST.equalsIgnoreCase(request.getUrl().getHost());
	}

	/**
	 * @param url
	 * @return
	 */
	private InputStream getResource(URL url) {
		String part = url.getFile();
		
		// Class loader can get upset if it sees a leading /
		int startIndex = 0;
		if(part.startsWith("/"))
		{
			startIndex = 1;
		}
		
		// Remove the ?_dc that Ext-JS loves to apply.
		int queryIndex = part.indexOf('?');
		if(queryIndex < 0) queryIndex = part.length();
		
		part = part.substring(startIndex, queryIndex);
		
		InputStream stream = m_classLoader.getResourceAsStream(part);
		return stream;
	}

	/**
	 * Dependency URLs from the project.
	 * @param project
	 * @return
	 * @throws DependencyResolutionRequiredException from Maven
	 */
	private static URL[] getUrls(MavenProject project) {
		
		List<URL> list = new ArrayList<URL>();
		
		mergeTestClasspathElements(list, project);
		mergeDependencyClasspathElements(list, project);
		
		logUrls(list);
		return list.toArray(new URL[list.size()]);
	}

	/**
	 * Trace the output URLs.
	 * @param list to trace.
	 */
	private static void logUrls(List<URL> list) {
		if(isTracingEnabled()){
			StringBuilder logMessage = new StringBuilder("Test classpath entry:\n");
			for(URL url : list){
				logMessage.append("  ");
				logMessage.append(url);
				logMessage.append('\n');
			}
			log(TRACE_LEVEL, logMessage.toString());
		}
	}

	/**
	 * @param result
	 * @param project
	 */
	private static void mergeDependencyClasspathElements(List<URL> result,
			MavenProject project) {
		@SuppressWarnings("unchecked")
		Set<Artifact> artifacts = project.getArtifacts();
		for(Artifact artifact : artifacts)
		{	
			if(RELEVANT_SCOPES.contains(artifact.getScope())) {
				File file = artifact.getFile();
				try {
					URL url = file.toURI().toURL();
					result.add(url);
				} catch (MalformedURLException e) {
					log(ERROR_LEVEL, "Failed to parse test classpath entry for: {0}/{1}", 
							artifact.getGroupId(), artifact.getId());
				} 
			}
		}
	}

	/**
	 * @param result
	 * @param project
	 */
	private static void mergeTestClasspathElements(List<URL> result,
			MavenProject project) {
		try {
			List<String> testClasspathElements = project.getTestClasspathElements();
			for (String path : testClasspathElements) {
				try {
					URL url = new File(path).toURI().toURL();
					result.add(url);
				} catch (MalformedURLException e) {
					log(ERROR_LEVEL, "Failed to parse test classpath element: {0}", path);
				}
			}
		} catch (DependencyResolutionRequiredException e) {
			log(ERROR_LEVEL, "Failed to find project test classpath elements: {0}", e);
		}
	}

	/**
	 * Log something
	 * @param level level to log at
	 * @param message message with {0} style markup
	 * @param params parameters for the message.
	 */
	private static void log(Level level, String message, Object... params){
		Logger log = Logger.getLogger(FakeHttpWebConnection.class.getName());
		log.log(level, message, params);
	}
	
	/** Is tracing enabled? */
	private static boolean isTracingEnabled() {
		Logger log = Logger.getLogger(FakeHttpWebConnection.class.getName());
		return log.isLoggable(TRACE_LEVEL);
	}
}
