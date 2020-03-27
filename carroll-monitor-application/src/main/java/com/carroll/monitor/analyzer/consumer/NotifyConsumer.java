package com.carroll.monitor.analyzer.consumer;

import com.alibaba.fastjson.JSONObject;
import com.carroll.monitor.analyzer.dto.NotifyDataDto;
import com.carroll.monitor.analyzer.service.INotifyService;
import com.carroll.monitor.analyzer.service.impl.NotifyServiceRegister;
import com.carroll.monitor.common.dto.KafkaTopic;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 告警通知 消费者
 * @author: carroll
 * @date 2019/9/9
 */
@Component
@Slf4j
public class NotifyConsumer {

//    private ExecutorService fixedThreadPool = ;

    @KafkaListener(topics = {KafkaTopic.NOTIFY_DATA})
    @SuppressWarnings("unused")
    public void notify(ConsumerRecord<?, ?> cr) {
        Optional<?> messages = Optional.ofNullable(cr.value());
        messages.ifPresent(m -> {
            String content = m.toString();
            log.debug("NotifyConsumer message:" + content);
            NotifyDataDto dataDto = JSONObject.parseObject(content, NotifyDataDto.class);
            if (!needNotify(dataDto) || !dataDto.getMonitorItem().getSendFlag()) {
                return;
            }
            dataDto.getMonitorItem().getMsgTypes().forEach(msgType -> {
                INotifyService notifyService = NotifyServiceRegister.listener(msgType);
                if(notifyService!=null){
                    notifyService.notify(dataDto);
                }
            });

        });

    }

    /* 判断是否需要发送告警通知  */
    private boolean needNotify(NotifyDataDto data) {
        if (data.getMonitorItem() == null) {
            return false;
        }
        if (data.getMonitorItem().getReceivers() == null || data.getMonitorItem().getReceivers().isEmpty()) {
            log.warn("监控项：{}未配置告警接收人");
            return false;
        }
        if (data.getWarningData() == null) {
            log.warn("告警数据有误");
            return false;
        }
        return true;
    }

}
