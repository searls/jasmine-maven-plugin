package com.github.searls.jasmine.io;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;

public class DirectoryCopier {

	private FileUtilsWrapper fileUtilsWrapper = new FileUtilsWrapper();
	private FileFilterUtilsWrapper fileFilterUtilsWrapper = new FileFilterUtilsWrapper();
	
	public void copyDirectory(File srcDir, File destDir, String suffixFilter) throws IOException {
		IOFileFilter filter = fileFilterUtilsWrapper.suffixFileFilter(suffixFilter);
		filter = fileFilterUtilsWrapper.and(FileFileFilter.FILE,filter);
		filter = fileFilterUtilsWrapper.or(DirectoryFileFilter.DIRECTORY,filter);
		filter = fileFilterUtilsWrapper.and(HiddenFileFilter.VISIBLE, filter);
		fileUtilsWrapper.copyDirectory(srcDir, destDir, filter);
	}

}
