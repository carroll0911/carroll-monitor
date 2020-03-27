package com.carroll.monitor.analyzer.service;

import com.carroll.monitor.data.collector.annotation.Monitor;
import com.carroll.monitor.data.collector.component.DataSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Component
@Slf4j
public class TestService implements ITestService {
    @Autowired
    private DataSender dataSender;

    private static final String TEST = "aaaaa";

    @Monitor(tag = "test", field = "f")
    public Map testMonitor1(int n) {
        log.info("test");
        Map<String, Object> result;
        if (n % 3 == 0) {
            result = new HashMap<>();
            result.put("f1", TEST);
            result.put("f", TEST);
            return result;
        }
        if (n % 3 == 1) {
            result = new HashMap<>();
            result.put("f1", TEST);
            result.put("f", true);
            return result;
        }
        if (n % 3 == 2) {
            result = new HashMap<>();
            result.put("f1", TEST);
            result.put("f", "0");
            return result;
        }
        return null;
    }

    @Override
    public int testMonitor2(int n) {
        return n % 2;
    }

    @Monitor(tag = "test")
    public void testMonitor3(int n) {
        dataSender.send("test", false);
    }
}