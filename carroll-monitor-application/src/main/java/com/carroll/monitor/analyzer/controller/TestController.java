package com.carroll.monitor.analyzer.controller;

import com.carroll.monitor.analyzer.model.MonitorItem;
import com.carroll.monitor.analyzer.service.IMonitorItemService;
import com.carroll.monitor.analyzer.service.ITestService;
import com.carroll.monitor.analyzer.service.IWarningDataService;
import com.carroll.monitor.analyzer.service.impl.SmsWsClient;
import com.carroll.spring.rest.starter.BaseController;
import com.carroll.spring.rest.starter.BaseResponse;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @author: carroll
 * @date 2019/9/9
 **/
@Slf4j
@Api(value = "TestController", description = "Test - API")
@RestController
@RequestMapping("/test")
public class TestController extends BaseController {

    @Autowired
    private ITestService testService;
    @Autowired
    private IMonitorItemService monitorItemService;
    @Autowired
    private SmsWsClient smsWsClient;
    @Autowired
    private IWarningDataService warningDataService;
    @Autowired
    private SimpMessagingTemplate template;

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public String test(@RequestParam("n") int n) {
        int ret = testService.testMonitor2(n);
        System.out.println(ret);
        smsWsClient.sendMsg("13982174712//2017-09-09 12:00:00//MySQL访问失败//紧急", SmsWsClient.TEMPLATE_CODE_WARN);
        return "SUCCESS";

    }

    @RequestMapping(value = "saveMonitor", method = RequestMethod.POST)
    public String saveMonitor(@RequestBody MonitorItem item) {
        monitorItemService.save(item);
        return "SUCCESS";
    }

    @MessageMapping(value = "test2")
//    @SendTo("/topic/test")
    public BaseResponse test2(@RequestParam("n") String n) {
        BaseResponse response = new BaseResponse();
        response.setReturnErrMsg(n);
        return response;
    }

    @RequestMapping(value = "sendToC3", method = RequestMethod.POST)
    public String sendToC3(@RequestBody MonitorItem item) {
        template.convertAndSend("/topic/c3",new HashMap(){{put("data","test");}});
        return "SUCCESS";
    }

    @RequestMapping(value = "resendJob", method = RequestMethod.POST)
    public String resendJob() {
        warningDataService.resendData();
        return "SUCCESS";
    }


}
