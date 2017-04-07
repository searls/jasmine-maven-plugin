package com.github.searls.jasmine.driver;

import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.service.DriverService;

public class ArgumentsPhantomJsDriver extends PhantomJSDriver {
	
	private static final String ARRAY_SPLIT_CHARACTER = " ";
	private static final String ARGUMENTS_PREFIX = "--";

	public ArgumentsPhantomJsDriver(Capabilities desiredCapabilities) {
		super(convertArgumentStringsToArryays(desiredCapabilities));
	}

	private static Capabilities convertArgumentStringsToArryays(
			Capabilities desiredCapabilities) {
		DesiredCapabilities result = new DesiredCapabilities();
		Map<String, ?> asMap = desiredCapabilities.asMap();
		for (String capKey : asMap.keySet()) {
			Object value = asMap.get(capKey);
			if (value instanceof String) {
				String valueAsString = (String)value;
				if (valueAsString.contains(ARGUMENTS_PREFIX)) {
					String[] valueAsArray = valueAsString.split(ARRAY_SPLIT_CHARACTER);
					value = valueAsArray;
				}
			}
			result.setCapability(capKey, value);
		}
		return result;
	}

	public ArgumentsPhantomJsDriver(DriverService service,
			Capabilities desiredCapabilities) {
		super(service, convertArgumentStringsToArryays(desiredCapabilities));
	}

}
