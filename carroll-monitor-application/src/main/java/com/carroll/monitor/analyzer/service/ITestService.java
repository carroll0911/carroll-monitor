package com.carroll.monitor.analyzer.service;


import com.carroll.monitor.data.collector.annotation.Monitor;

/**
 * @author: carroll
 * @date 2019/9/9
 */
public interface ITestService {
    @Monitor(tag = "test")
    int testMonitor2(int n);
}
