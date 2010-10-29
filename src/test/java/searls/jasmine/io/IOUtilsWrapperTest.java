package searls.jasmine.io;

import static org.powermock.api.mockito.PowerMockito.*;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(IOUtils.class)
public class IOUtilsWrapperTest {
	private IOUtilsWrapper sut = new IOUtilsWrapper();
	private InputStream inputStream = mock(InputStream.class);

	@Before
	public void powerfullyMockStaticClasses() {
		mockStatic(IOUtils.class);
	}
	
	@Test
	public void shouldDelegateToString() throws IOException {
		String expected = "pants";
		when(IOUtils.toString(inputStream)).thenReturn(expected );
		
		String result = sut.toString(inputStream);
		
		assertThat(result,is(expected));
	}
}
