package com.github.searls.jasmine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility for working out what type of file something is.
 * @author Richard Corfield &lt;m0rjc@m0rjc.me.uk&gt;
 */
final class MimeMagic {
	
	/** Content type keyed on file suffix with the dot. */
	private static final Map<String, String> KNOWN_SUFFIXES;
	static{
		Map<String, String> knownSuffixes = new HashMap<String, String>();
		knownSuffixes.put(".js", "application/javascript");
		knownSuffixes.put(".html", "text/html");
		knownSuffixes.put(".xhtml", "text/html");
		KNOWN_SUFFIXES = Collections.unmodifiableMap(knownSuffixes);
	}
	
	/**
	 * Guess the content type using information available.
	 * @param filename name of the file
	 * @param data cached data.
	 * @return the best guess at the content type.
	 * @throws IOException from the Java libraries.
	 */
	public static String guessContentType(URL url, byte[] data) throws IOException {
		String filename = url.getFile();
		int queryIndex = filename.indexOf('?');
		if(queryIndex > 0){
			filename = filename.substring(0, queryIndex);
		}
		
		String result = URLConnection.guessContentTypeFromName(filename);
		if(result == null)
		{
			result = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(data));
		}
		
		if(result == null)
		{
			result = checkKnownSuffix(filename);
		}
		
		if(result == null)
		{
			result = "application/octet-stream"; 
		}
		
		return result;
	}

	/**
	 * @param filename filename to examine
	 * @return a value from a known suffix if any.
	 */
	private static String checkKnownSuffix(String filename) {
		int lastDot = filename.lastIndexOf('.');
		if(lastDot >= 0){
			String suffix = filename.substring(lastDot);
			return KNOWN_SUFFIXES.get(suffix);
		}
		return null;
	}
}
