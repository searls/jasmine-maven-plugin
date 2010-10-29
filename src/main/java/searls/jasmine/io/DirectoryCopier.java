package searls.jasmine.io;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;

public class DirectoryCopier {

	public void copyDirectory(File srcDir, File destDir, String suffixFilter) throws IOException {
		IOFileFilter filter = FileFilterUtils.suffixFileFilter(suffixFilter);
		filter = FileFilterUtils.andFileFilter(FileFileFilter.FILE, filter);
		filter = FileFilterUtils.orFileFilter(DirectoryFileFilter.DIRECTORY, filter);
		filter = FileFilterUtils.andFileFilter(HiddenFileFilter.VISIBLE, filter);
		FileUtils.copyDirectory(srcDir, destDir, filter);
	}

}
