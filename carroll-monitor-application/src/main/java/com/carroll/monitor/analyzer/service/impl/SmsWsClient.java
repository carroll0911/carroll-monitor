package com.carroll.monitor.analyzer.service.impl;

import com.carroll.monitor.analyzer.config.SmsServiceConfig;
import com.carroll.monitor.analyzer.ws.SendRealtimeMessageInterface;
import com.carroll.monitor.analyzer.ws.SendRealtimeMessageInterfaceResponse;
import com.carroll.monitor.analyzer.ws.TaskFeedback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Component
@Slf4j
public class SmsWsClient extends WebServiceGatewaySupport {

    public SmsWsClient(Jaxb2Marshaller marshaller) {
        setMarshaller(marshaller);
        setUnmarshaller(marshaller);
    }

    @Autowired
    private SmsServiceConfig config;

    public static final String TEMPLATE_CODE_WARN = "C3XM01";
    public static final String TEMPLATE_CODE_RE_WARN = "C3XM02";
    public static final String TEMPLATE_CODE_RECOVERY = "C3XM03";
    public static final String TEMPLATE_CODE_CLEAN = "C3XM04";
    private static final String SUCCESS_CODE = "200";

    private static final String FAIL_MSG = "发送短信失败";

    public boolean sendMsg(String phonesAndParams, String templateCode) {
        log.debug("begin send sms:{},{}",phonesAndParams,templateCode);
        SendRealtimeMessageInterface request = new SendRealtimeMessageInterface();
        request.setCode(config.getCode());
        request.setPassword(config.getPassword());
        request.setSyscode(config.getSyscode());
        request.setSMSTemplate(templateCode);
        request.setPhonesAndParams(phonesAndParams);
        SendRealtimeMessageInterfaceResponse response = null;
        try{
            response = (SendRealtimeMessageInterfaceResponse) ((JAXBElement) getWebServiceTemplate()
                    .marshalSendAndReceive(config.getUri(), request)).getValue();
        } catch (Exception e){
            log.error(FAIL_MSG, e);
            return false;
        }
        if (response == null || StringUtils.isEmpty(response.getReturn())) {
            log.error(FAIL_MSG);
            return false;
        }
        TaskFeedback feedback = null;
        try {
            feedback = parseFeedBack(response.getReturn());
            if (feedback == null) {
                log.error(FAIL_MSG);
                return false;
            }
            if (!SUCCESS_CODE.equals(feedback.getInterfaceStatus())) {
                log.error("{}[{}-{}]", FAIL_MSG, feedback.getInterfaceStatus(), feedback.getInterfaceFailureCause());
                return false;
            } else {
                return true;
            }
        } catch (JAXBException e) {
            log.error(FAIL_MSG, e);
            return false;
        }
    }

    private TaskFeedback parseFeedBack(String returnStr) throws JAXBException {
        log.debug("send SMS result:{}",returnStr);
        JAXBContext jc = JAXBContext.newInstance(TaskFeedback.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        StringReader reader = new StringReader(returnStr);
        return (TaskFeedback) unmarshaller.unmarshal(reader);
    }
}
