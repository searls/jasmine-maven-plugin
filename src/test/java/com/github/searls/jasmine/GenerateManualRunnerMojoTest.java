package com.github.searls.jasmine;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GenerateManualRunnerMojo.class)
public class GenerateManualRunnerMojoTest {

	@InjectMocks private GenerateManualRunnerMojo subject = new GenerateManualRunnerMojo();
	
	@Mock private CreatesManualRunner createsManualRunner;
	
	@Before
	public void stubNew() throws Exception {
		whenNew(CreatesManualRunner.class).withArguments(subject).thenReturn(createsManualRunner);
	}
	
	@Test
	public void createsAManualRunner() throws IOException {
		subject.run();
		
		verify(createsManualRunner).create();
	}
	
}
