package com.github.searls.jasmine.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.maven.project.MavenProject;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import com.github.searls.jasmine.maven.MavenResourceProvider;

/**
 * Jetty resource provider that tries to find the resource on the class path.
 * 
 * @author Stephen Fry
 */
public class MavenDependencyResourceHandler extends ResourceHandler {

	private MavenResourceProvider mavenResourceProvider;
	
	public MavenDependencyResourceHandler(MavenProject project) {
		mavenResourceProvider = new MavenResourceProvider(project);
	}
	
	@Override
	public Resource getResource(String path) throws MalformedURLException {
		
		if ("/".equals(path)) {
			return null;
		}
		
		InputStream is = mavenResourceProvider.getInputStream(path);
		
		if (is == null) {
			return null;
		}
		
		return new MavenClasspathResource(is);
	}
	
	private class MavenClasspathResource extends Resource
	{
		private InputStream input;
		
		MavenClasspathResource(InputStream input) {
			this.input = input;
		}
		
		@Override
		public boolean isContainedIn(Resource r) throws MalformedURLException {
			return false;
		}

		@Override
		public void release() {
		}

		@Override
		public boolean exists() {
			return true;
		}

		@Override
		public boolean isDirectory() {
			return false;
		}

		@Override
		public long lastModified() {
			return 0;
		}

		@Override
		public long length() {
			return 0;
		}

		@Override
		public URL getURL() {
			return null;
		}

		@Override
		public File getFile() throws IOException {
			return null;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return input;
		}

		@Override
		public OutputStream getOutputStream() throws IOException,
				SecurityException {
			return null;
		}

		@Override
		public boolean delete() throws SecurityException {
			return false;
		}

		@Override
		public boolean renameTo(Resource dest) throws SecurityException {
			return false;
		}

		@Override
		public String[] list() {
			return null;
		}

		@Override
		public Resource addPath(String path) throws IOException,
				MalformedURLException {
			return null;
		}
		
	}
}
