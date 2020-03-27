package com.carroll.monitor.analyzer.utils;

import lombok.extern.slf4j.Slf4j;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * 脚本执行工具类
 *
 * @author: carroll
 * @date 2019/11/14
 *
 */
@Slf4j
public class ScriptUtil {
    private static ScriptEngineManager manager = new ScriptEngineManager();
    private static ScriptEngine engine = manager.getEngineByName("javascript");

    public static Object execute(String script, String... data) {
        try {
            engine.eval(String.format("function f(data){%s}",script));
            if (engine instanceof Invocable) {
                return ((Invocable) engine).invokeFunction("f", data);
            }
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
        }
        return null;
    }
}
