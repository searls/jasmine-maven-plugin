package com.github.searls.jasmine.mojo;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class CapabilityTest {

    @Test
    public void setMapTest() {
        Map<String, Object> testMap = new HashMap<String, Object>();
        testMap.put("detach", "true");
        testMap.put("args", "[crash-test,no-sandbox]");

        Capability capability = new Capability();
        capability.setMap(testMap);
        Map<String, Object> result = capability.getMap();
        assertTrue((Boolean) result.get("detach"));

        List<String> argsList = Arrays.asList(new String[]{"crash-test", "no-sandbox"});
        assertEquals(argsList, result.get("args"));
    }

}
