package com.github.searls.jasmine.driver;

import static org.junit.Assert.*;

import java.util.List;

import org.codehaus.plexus.util.ReflectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;


@RunWith(MockitoJUnitRunner.class)
public class ArgumentsPhantomJsDriverTest {

	private static final String ARRAY_CAP_KEY = "phantomjs.cli.args";
	private DesiredCapabilities capabilities;
	private ArgumentsPhantomJsDriver driver;
	
	@Before
	public void setupCapabilities() {
		capabilities = DesiredCapabilities.phantomjs();
	}

	private void createDriver() {
		driver = new ArgumentsPhantomJsDriver(capabilities);
	}

	@Test
	public void capabilityUsingArrayArgumentsPrefixConventionIsConvertedToStringArrayForOneValue() throws IllegalAccessException {
		capabilities.setCapability(ARRAY_CAP_KEY, "--webdriver-loglevel=DEBUG");
		createDriver();
		List<String> argumentsForPhantomJs = getArgumentsUsedForPhantomJs();
		assertExpectedArgument(argumentsForPhantomJs, "--webdriver-loglevel=DEBUG");
	}

	private List<String> getArgumentsUsedForPhantomJs() throws IllegalAccessException {
		PhantomJSDriverService driverService = (PhantomJSDriverService) ReflectionUtils.getValueIncludingSuperclasses("service", driver.getCommandExecutor());
		List<String> argumentsForPhantomJs = (List<String>) ReflectionUtils.getValueIncludingSuperclasses("args", driverService);
		return argumentsForPhantomJs;
	}

	private void assertExpectedArgument(List<String> argumentsForPhantomJs, String expectedArgument) {
		boolean found = false;
		for (String argument : argumentsForPhantomJs) {
			if (argument.equals(expectedArgument)) found = true;
		}
		assertTrue("Expected " + expectedArgument + " as argument", found);
	}
	@Test
	public void capabilityUsingArrayArgumentsPrefixConventionIsConvertedToStringArrayForMultipleValues() throws IllegalAccessException {
		capabilities.setCapability(ARRAY_CAP_KEY, "--webdriver-loglevel=DEBUG --webdriver-logfile=file.log");
		createDriver();
		List<String> argumentsForPhantomJs = getArgumentsUsedForPhantomJs();
		assertExpectedArgument(argumentsForPhantomJs, "--webdriver-loglevel=DEBUG");
		assertExpectedArgument(argumentsForPhantomJs, "--webdriver-logfile=file.log");
	}

}
