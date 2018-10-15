package com.example.coolapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class CoolAppController {
    private final Logger log = LoggerFactory.getLogger(CoolAppController.class);

    @Value("${isDelayEnabled:false}")
    private boolean isDelayEnabled;

    //@formatter:off
    private static String[] expressions = { "Cool as a cucumber",
                                            "Cool as a breeze on a hot summer day",
                                            "Cool like a fool in a swimming pool",
                                            "Cool beans!"};
    //@formatter:on

    private static int nextIndex = -1;

    @RequestMapping("/")
    public String index() {
        if (isDelayEnabled) {
            return delay();
        }
        return getNextExpression();
    }

    @RequestMapping("/delay")
    public String delay() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (java.lang.InterruptedException e) {
            log.warn("Delay interrupted: {}", e.getMessage());
        }
        return getNextExpression();
    }

    private String getNextExpression() {
        nextIndex = (nextIndex+1)%expressions.length;
        log.info("Getting expression {}: {}", nextIndex, expressions[nextIndex]);
        return expressions[nextIndex];
    }

}

