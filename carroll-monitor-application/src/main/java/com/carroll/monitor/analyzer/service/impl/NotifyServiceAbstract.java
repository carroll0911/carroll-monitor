package com.carroll.monitor.analyzer.service.impl;

import com.carroll.monitor.analyzer.dto.NotifyDataDto;
import com.carroll.monitor.analyzer.model.MonitorItem;
import com.carroll.monitor.analyzer.model.NotifyRecord;
import com.carroll.monitor.analyzer.repository.NotifyRecordRepository;
import com.carroll.monitor.analyzer.service.INotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * @author: carroll
 * @date 2019/11/8
 *
 */
@Slf4j
public abstract class NotifyServiceAbstract implements INotifyService {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private NotifyRecordRepository notifyRecordRepository;

    @PostConstruct
    public void regist() {
        NotifyServiceRegister.regist(context.getBean(this.getClass()));
    }

    protected void saveRecord(NotifyDataDto notifyDataDto, boolean success, NotifyRecord record, MonitorItem.MessageType msgType) {
        record.setMessageType(msgType);
        record.setSendTime(new Date());
        record.setReceivers(notifyDataDto.getMonitorItem().getReceivers());
        record.setSuccess(success);
        record.setWarnData(notifyDataDto.getWarningData());
        try {
            notifyRecordRepository.save(record);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }
}
