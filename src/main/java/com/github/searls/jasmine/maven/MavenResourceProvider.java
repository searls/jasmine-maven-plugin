package com.github.searls.jasmine.maven;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;

import com.github.searls.jasmine.FakeHttpWebConnection;

/**
 * Provide Resources from the Maven classpath.
 * 
 * @author Stephen Fry
 */
public class MavenResourceProvider {
	
	ClassLoader classLoader;
	
	/** Level for diagnostics tracing. */
	private static final Level TRACE_LEVEL = Level.FINE;
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
	
	public MavenResourceProvider(MavenProject project) {
		classLoader = new URLClassLoader(getClasspathUrlsForProject(project));
	}

	/**
	 * @param path the requested path.
	 * @return the data as an InputStream, or null if not found.
	 */
	public InputStream getInputStream(String path) {
		
		// Class loader can get upset if it sees a leading /
		int startIndex = 0;
		if(path.startsWith("/"))
		{
			startIndex = 1;
		}
		
		// Remove the ?_dc that Ext-JS loves to apply.
		int queryIndex = path.indexOf('?');
		if(queryIndex < 0) queryIndex = path.length();
		
		path = path.substring(startIndex, queryIndex);
		
		InputStream stream = classLoader.getResourceAsStream(path);
		return stream;
	}

	/**
	 * Add test classpath elements, the output diectories, from the project to the result list.
	 * @param result list to append to
	 * @param project project to query.
	 */
	private static void mergeTestClasspathElements(List<URL> result,
			MavenProject project) {
		try {
			@SuppressWarnings("unchecked")
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
	 * Add dependency classpath elements from the project to the result list.
	 * @param result list to append to.
	 * @param project project to query.
	 */
	private static void mergeDependencyClasspathElements(List<URL> result, MavenProject project) {
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
	 * Dependency URLs from the project.
	 * @param project Maven project
	 * @return array of URLs in which to search for resources
	 * @throws DependencyResolutionRequiredException from Maven
	 */
	private static URL[] getClasspathUrlsForProject(MavenProject project) {
		
		List<URL> list = new ArrayList<URL>();
		
		mergeTestClasspathElements(list, project);
		mergeDependencyClasspathElements(list, project);
		
		logUrls(list);
		return list.toArray(new URL[list.size()]);
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
	

	/**
	 * Trace the output URLs to the Logger.
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

	/** Is tracing enabled? */
	private static boolean isTracingEnabled() {
		Logger log = Logger.getLogger(FakeHttpWebConnection.class.getName());
		return log.isLoggable(TRACE_LEVEL);
	}
	
}
