package com.github.searls.jasmine.io.scripts;

import com.github.searls.jasmine.AbstractJasmineMojo;
import com.github.searls.jasmine.io.ScansDirectory;
import com.github.searls.jasmine.model.ScriptSearch;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

//├── src
//│   ├── main
//│   │   └── javascript
//│   │       ├── a.js
//│   │       └── z.js
//│   └── test
//│       └── javascript
//│           └── AisNotZSpec.js
//└── target
//    └── jasmine
//        ├── spec
//        │   └── AisNotZSpec.js
//        └── src
//            ├── a.js
//            └── z.js
public class TargetDirScriptResolverTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	private File root;
	private File sourceFolder;
	private File testFolder;
	private AbstractJasmineMojo config;
	private File jasmine;

	@Before
	public void initFolders() throws IOException {
		root = tempFolder.newFolder("root");
		sourceFolder = createFile(root, "src/main/javascript", "a.js").getParentFile();
		createFile(root, "src/main/javascript", "z.js");
		testFolder = createFile(root, "src/test/javascript", "AisNotZSpec.js").getParentFile();

		jasmine = new File(new File(root, "target"), "jasmine");
		jasmine.mkdirs();

		createFile(jasmine, "src", "z.js");
		createFile(jasmine, "src", "a.js");
		createFile(jasmine, "spec", "AisNotZSpec.js");

		initMock();
	}

	private void initMock() {
		String[] preloads = {"z.js"};
		config = mock(AbstractJasmineMojo.class);
		when(config.getJasmineTargetDir()).thenReturn(jasmine);
		when(config.getPreloadSources()).thenReturn(Arrays.asList(preloads));
		when(config.getSrcDirectoryName()).thenReturn("src");
		when(config.getSpecDirectoryName()).thenReturn("spec");
		when(config.getSources()).thenReturn(new ScriptSearch(sourceFolder, ScansDirectory.DEFAULT_INCLUDES, Collections.<String>emptyList()));
		when(config.getSpecs()).thenReturn(new ScriptSearch(testFolder, ScansDirectory.DEFAULT_INCLUDES, Collections.<String>emptyList()));
	}

	@Test
	public void shouldResolveScriptsOrderMatters() throws Exception {
		TargetDirScriptResolver targetDirScriptResolver = new TargetDirScriptResolver(config);
		LinkedHashSet<String> allScripts = (LinkedHashSet<String>) targetDirScriptResolver.getAllScripts();
		Iterator<String> iterator = allScripts.iterator();
		String first = iterator.next();
		String second = iterator.next();
		String third = iterator.next();
		assertEquals(3, allScripts.size());
		assertTrue(first.endsWith("/root/target/jasmine/src/z.js"));
		assertTrue(second.endsWith("/root/target/jasmine/src/a.js"));
		assertTrue(third.endsWith("/root/target/jasmine/spec/AisNotZSpec.js"));
	}

	private File createFile(File root, String dir, String filename) throws IOException {
		File directory = new File(root, dir);
		directory.mkdirs();
		File newFile = new File(directory, filename);
		FileUtils.touch(newFile);
		return newFile;
	}

}
