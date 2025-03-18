package com.mladenov.projectmanagement.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupLogger {
    private final Logger logger = LoggerFactory.getLogger(StartupLogger.class);

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        logger.info("Swagger documentation: {}", event.getApplicationContext().getEnvironment().getProperty("swagger.url"));
    }
}
