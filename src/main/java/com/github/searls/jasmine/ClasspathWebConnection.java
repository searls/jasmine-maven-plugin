package com.github.searls.jasmine;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import com.gargoylesoftware.htmlunit.DownloadedContent;
import com.gargoylesoftware.htmlunit.WebConnection;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebResponseData;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

/**
 * A WebConnection for HTMLUnit that will load resources from the CLASSPATH.
 * It looks for the marker "{@value #PROTOCOL}:".
 * 
 * If it does not recognise the request it can delegate to a wrapped WebConnection.
 * 
 * @author Richard Corfield &lt;m0rjc@m0rjc.me.uk&gt;
 */
public class ClasspathWebConnection implements WebConnection {

	public static final String PROTOCOL="http";
	public static final String FAKE_HOST = "fakejava";
	
	/** If I cannot resolve the URL then delegate to the next in the chain. */
	private WebConnection m_next;
	
	/**
	 * @param connectionToWrap WebConnection to use if this connection does not recognise the URL.
	 * 	Can be null.
	 */
	public ClasspathWebConnection(WebConnection connectionToWrap) {
		m_next = connectionToWrap;
	}

//	@Override
	public WebResponse getResponse(WebRequest request) throws IOException {
		long startTime = System.currentTimeMillis();
		if(isMine(request)){
			final InputStream resource = getResource(request.getUrl());
			if(resource != null){
				return makeResponse(request, resource, startTime);
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
			final InputStream resource, long startTime) throws IOException {
		DownloadedContent responseBody = new DownloadedContent() {
			private static final long serialVersionUID = 1L;
			public InputStream getInputStream() throws IOException {
				return resource;
			}
		};
		WebResponseData responseData = new WebResponseData(responseBody, 200, "OK", (List<NameValuePair>) Collections.EMPTY_LIST);
		long loadTime = System.currentTimeMillis() - startTime;
		return new WebResponse(responseData, request, loadTime);
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
		InputStream stream = getClass().getClassLoader().getResourceAsStream("META-INF/resources" + part);
		return stream;
	}

}
