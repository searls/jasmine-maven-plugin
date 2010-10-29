package searls.jasmine.runner;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import searls.jasmine.io.IOUtilsWrapper;
import searls.jasmine.model.JasmineResult;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

@RunWith(MockitoJUnitRunner.class)
public class SpecRunnerExecutorTest {

	private static final String BUILD_CONCLUSION_JS_CONTENTS = "'kaka';";
	private static final String BUILD_REPORT_JS_CONTENTS = "'pants';";
	
	@InjectMocks private SpecRunnerExecutor sut = new SpecRunnerExecutor();
	@Mock private IOUtilsWrapper ioUtilsWrapper;
	
	@Test
	public void shouldFindSpecsInResults() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		when(ioUtilsWrapper.toString(isA(InputStream.class))).thenReturn(BUILD_CONCLUSION_JS_CONTENTS,BUILD_REPORT_JS_CONTENTS);
		URL resource = getClass().getResource("/example_nested_specrunner.html");
		JasmineResult result = sut.execute(resource);
		
		assertThat(result,is(not(nullValue())));
		assertThat(result.getDescription(),containsString("kaka"));
		assertThat(result.getDetails(),containsString("pants"));
		assertThat(result.didPass(),is(false));
	}

	
}
