package com.github.searls.jasmine;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.maven.project.MavenProject;

import com.gargoylesoftware.htmlunit.DownloadedContent;
import com.gargoylesoftware.htmlunit.WebConnection;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebResponseData;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.github.searls.jasmine.maven.MavenResourceProvider;

/**
 * A WebConnection for HTMLUnit that will load resources from the Maven Test Dependencies Classpath.
 * 
 * It looks for the protocol "{@value #PROTOCOL}:" and the host defined in
 * {@link #setFakeHost(String)}. This defaults to "maven.test.dependencies"
 * 
 * <p>If the requested URL matches then the file part will be extracted and loaded using a
 * URLClassLoader. The Class Loader is initialised with the test classes output directory for
 * the current project, and the locations of dependencies of type "Compile", "Runtime" and "Test".
 * 
 * <p>If it does not recognise the request it can delegate to a wrapped WebConnection.
 * 
 * @author Richard Corfield &lt;m0rjc@m0rjc.me.uk&gt;
 */
public class FakeHttpWebConnection implements WebConnection {
	public static final String PROTOCOL="http";
	
	private static final int HTTP_STATUS_NOT_FOUND = 404;
	private static final int HTTP_STATUS_OK = 200;
	
	/** Level for diagnostics tracing. */
	private static final Level TRACE_LEVEL = Level.FINE;
	private static final Level ERROR_LEVEL = Level.WARNING;
	
	/** If I cannot resolve the URL then delegate to the next in the chain. */
	private final WebConnection m_next;
	
	private String fakeServerHostName = "localhost";

	private MavenResourceProvider mavenResourceProvider;
	
	/**
	 * @param connectionToWrap WebConnection to use if this connection does not recognise the URL.
	 * 	Can be null.
	 */
	public FakeHttpWebConnection(WebConnection connectionToWrap, MavenProject project) {
		m_next = connectionToWrap;
		mavenResourceProvider = new MavenResourceProvider(project);
	}
	
	/**
	 * @return the hostname to use as the fake host.
	 */
	public String getFakeHost() {
		return fakeServerHostName;
	}

	/**
	 * @param fakeHost requests to this fake host will be intercepted.
	 */
	public void setFakeHost(String fakeHost) {
		fakeServerHostName = fakeHost;
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
	 * Make a WebResponse for Not Found
	 * @param request request to include
	 * @param startTime start time used to calculate processing time
	 * @return the Not Found response.
	 * @throws IOException from WebResponseData.
	 */
	private WebResponse makeNotFoundResponse(WebRequest request, long startTime)
			throws IOException {
		DownloadedContent responseBody = new DownloadedContent.InMemory("Not Found".getBytes());
		@SuppressWarnings("unchecked")
		WebResponseData responseData = new WebResponseData(responseBody, HTTP_STATUS_NOT_FOUND, "Not found", (List<NameValuePair>) Collections.EMPTY_LIST);
		return new WebResponse(responseData, request, System.currentTimeMillis() - startTime);
	}

	/**
	 * Make a response for found data.
	 * @param request request to include.
	 * @param data data to wrap
	 * @param startTime start time used to calculate response time.
	 * @return the response containing the data
	 * @throws IOException from WebResponseData.
	 */
	private WebResponse makeResponse(WebRequest request,
			final byte[] data, long startTime) throws IOException {
		
		DownloadedContent responseBody = new DownloadedContent.InMemory(data); 
		String contentType = MimeMagic.guessContentType(request.getUrl(), data);
		List<NameValuePair> headers = new ArrayList<NameValuePair>();
		
		headers.add(new NameValuePair("Content-Type", contentType));
		WebResponseData responseData = new WebResponseData(responseBody, HTTP_STATUS_OK, "OK", headers);
		long loadTime = System.currentTimeMillis() - startTime;
		return new WebResponse(responseData, request, loadTime);
	}

	/**
	 * @param request incoming request
	 * @return true if the request should be intercepted by this class.
	 */
	private boolean isMine(WebRequest request) {
		return PROTOCOL.equals(request.getUrl().getProtocol()) && fakeServerHostName.equalsIgnoreCase(request.getUrl().getHost());
	}

	/**
	 * @param url the requested URL
	 * @return the data as an inputstream, or null if not found.
	 */
	private InputStream getResource(URL url) {
		String part = url.getFile();
		return mavenResourceProvider.getInputStream(part);
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
}
