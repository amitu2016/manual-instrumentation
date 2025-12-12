package com.demo.instrumentation.logs;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.MDC;
import org.slf4j.Marker;

public class TurboDebugFilter extends TurboFilter {

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String s, Object[] objects, Throwable throwable) {
        var userId = MDC.get("userId");
        if(userId != null && userId.equals("5")){
            return FilterReply.ACCEPT;
        }
        return FilterReply.NEUTRAL;
    }
}
