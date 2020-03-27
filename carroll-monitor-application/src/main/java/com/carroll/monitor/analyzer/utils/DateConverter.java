package com.carroll.monitor.analyzer.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DateConverter
 *
 * @author: carroll
 * @date 2019/9/9
 */
@Slf4j
public class DateConverter implements Converter<String, Date> {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String SHORT_DATE_FORMAT = "yyyy-MM-dd";

    @Override
    public Date convert(String source) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }
        source = source.trim();
        try {
            if (source.contains("-")) {
                SimpleDateFormat formatter;
                if (source.contains(":")) {
                    formatter = new SimpleDateFormat(DATE_FORMAT);
                } else {
                    formatter = new SimpleDateFormat(SHORT_DATE_FORMAT);
                }
                Date dtDate = formatter.parse(source);
                return dtDate;
            } else if (source.matches("^\\d+$")) {
                Long lDate = Long.parseLong(source);
                return new Date(lDate);
            }
        } catch (Exception e) {
            log.debug("parser {} to Date fail", source);
        }
        log.debug("parser {} to Date fail", source);
        return null;
    }
}
