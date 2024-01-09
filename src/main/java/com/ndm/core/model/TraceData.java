package com.ndm.core.model;

import com.ndm.core.common.util.RequestUtil;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TraceData {

    @Builder.Default
    private String traceId = RequestUtil.getTraceId();
}
