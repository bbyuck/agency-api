package com.ndm.core.common.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BeanUtil {
    public static Object getBean(String beanName) {
        return ApplicationContextProvider.getApplicationContext().getBean(beanName);
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {
        return ApplicationContextProvider.getApplicationContext().getBean(beanName, clazz);
    }
}
