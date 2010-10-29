package searls.jasmine.io;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

public class FileUtilsWrapper {

	public String readFileToString(File file) throws IOException {
		return FileUtils.readFileToString(file);
	}

	public void forceMkdir(File file) throws IOException {
		FileUtils.forceMkdir(file);
	}

	@SuppressWarnings("unchecked")
	public Collection<File> listFiles(File file, String[] extensions, boolean recursive) {
		return FileUtils.listFiles(file, extensions, recursive);
	}

}
