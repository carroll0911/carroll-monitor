package com.carroll.monitor.analyzer.response;

import com.carroll.monitor.analyzer.dto.WarningDataCallBackDto;
import com.carroll.spring.rest.starter.BaseResponse;
import lombok.Data;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Data
public class WarnDataCallBackResponse extends BaseResponse {
    private WarningDataCallBackDto data;
}
