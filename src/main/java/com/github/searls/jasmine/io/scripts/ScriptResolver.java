package com.github.searls.jasmine.io.scripts;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Set;

public interface ScriptResolver {

	void resolveScripts() throws IOException;

	String getSourceDirectory() throws IOException;

	String getSpecDirectory() throws MalformedURLException;

	Set<String> getSources() throws IOException;

	Set<String> getSpecs() throws IOException;

	Set<String> getPreloads() throws IOException;

	Set<String> getAllScripts() throws IOException;
}
