package com.ndm.core.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

@Slf4j
public class PropertyUtil {
    public static String getProperty(String key) {
        return getProperty(key, null);
    }

    public static String getProperty(String key, String defaultValue) {
        String value = defaultValue;

        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        if (applicationContext.getEnvironment().getProperty(key) == null) {
            log.warn("{} property was not loaded", key);
        }
        else {
            value = applicationContext.getEnvironment().getProperty(key);
        }

        return value;
    }
}
