package com.ndm.core.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class CommonUtil {

    public String issueMemberToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
