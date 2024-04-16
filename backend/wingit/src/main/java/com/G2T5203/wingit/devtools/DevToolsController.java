package com.G2T5203.wingit.devtools;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.*;

@Controller
public class DevToolsController {
    public DevToolsController(Environment environment) {
        this.environment = environment;
    }

    private Environment environment;

    @Value("${application.name}")
    private String applicationName;

    @Value("${build.version}")
    private String buildVersion;

    @Value("${build.timestamp}")
    private String buildTimestamp;

    @Value("${spring.profiles.active:}")
    private String activeProfile;




    @RequestMapping("/")
    @ResponseBody
    public String test() {
        return "HELLO WORLD!";
    }

    @RequestMapping(value="dev-tools/overview", method=RequestMethod.GET)
    @ResponseBody
    public HashMap<String, String> requestOverview() {
        HashMap<String, String> overview = new LinkedHashMap<String, String>();

        overview.put("DEV-TOOLS overview", applicationName);
        overview.put("WingIt Version", buildVersion);
        overview.put("Build Timestamp", buildTimestamp);
        overview.put("Java version", System.getProperty("java.version"));
        overview.put("Profile", activeProfile);

        return overview;
    }
}
