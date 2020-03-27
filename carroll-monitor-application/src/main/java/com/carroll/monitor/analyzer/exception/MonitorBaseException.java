package com.carroll.monitor.analyzer.exception;


import com.carroll.monitor.analyzer.enums.ErrEnum;
import com.carroll.spring.rest.starter.BaseException;

/**
 * @author: carroll
 * @date 2019/9/9
 */
public class MonitorBaseException extends BaseException {
    public MonitorBaseException(String code, String msg) {
        super(code, msg);
    }

    public MonitorBaseException(ErrEnum errEnum) {
        super(errEnum.getCode(), errEnum.getMsg());
    }
}
