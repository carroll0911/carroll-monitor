package com.carroll.monitor.analyzer.service.impl;

import com.carroll.monitor.analyzer.dto.NotifyDataDto;
import com.carroll.monitor.analyzer.model.MonitorItem;
import com.carroll.monitor.analyzer.model.NotifyRecord;
import com.carroll.monitor.analyzer.model.Operator;
import com.carroll.monitor.analyzer.model.WarningData;
import com.carroll.monitor.analyzer.service.INotifyService;
import com.carroll.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 邮件通知实现
 *
 * @author: carroll
 * @date 2019/11/8
 *
 */
@Service
@Slf4j
public class NotifyService4EmailImpl extends NotifyServiceAbstract implements INotifyService {
    /**
     * 系统有新的告警信息，请及时处理。\n【告警内容】 MySQL访问失败\n【告警级别】紧急\n【产生时间】2017-09-09 12:00:00\n【告警次数】1次\n【告警源】XXXX【告警对象】XXXX
     */
    private static final String WARN_EMAIL_FORMAT = "系统有新的告警信息，请及时处理。%n【告警内容】 %s%n【告警级别】%s%n【产生时间】%s%n【告警次数】%s次%n【告警源】%s(%s)%n【告警对象】%s";
    /**
     * 系统恢复告警信息。\n【告警内容】 MySQL访问失败\n【告警级别】紧急\n【产生时间】2017-09-09 12:00:00\n【恢复时间】2017-09-09 12:00:00
     */
    private static final String RECOVERY_EMAIL_FORMAT = "系统恢复告警信息。%n【告警内容】 %s%n【告警级别】%s%n【产生时间】%s%n【恢复时间】%s%n【告警源】%s(%s)%n【告警对象】%s";
    /**
     * 重复告警信息 系统有新的告警信息，请及时处理。\n【告警内容】MySQL访问失败\n【告警级别】紧急\n【产生时间】2017-09-09 12:00:00\【最近告警时间】2017-09-09 12:00:00\n【告警次数】N次\n【告警源】XXXX【告警对象】XXXX
     */
    private static final String WARN_RE_EMAIL_FORMAT = "系统有新的告警信息，请及时处理。%n【告警内容】%s%n【告警级别】%s%n【产生时间】%s%n【最近告警时间】%s%n【告警次数】%s次%n【告警源】%s(%s)%n【告警对象】%s";
    /**
     * 告警清除信息 系统自动清除逾期未处理告警。%n【告警内容】 MySQL访问失败。%n【产生时间】2017-09-09 12:00:00。
     */
    private static final String WARN_CLEAN_EMAIL_FORMAT = "系统自动清除逾期未处理告警。%n【告警内容】 %s。%n【产生时间】%s。";

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from:monitor}")
    private String fromEmail;

    @Value("${host}")
    private String hostSuffix;

    @Override
    public MonitorItem.MessageType getMessageType() {
        return MonitorItem.MessageType.EMAIL;
    }

    @Override
    public void notify(NotifyDataDto data) {
        List<String> toList = new ArrayList<>();
        for (Operator operator : data.getMonitorItem().getReceivers()) {
            if (!StringUtils.isEmpty(operator.getEmail())) {
                toList.add(operator.getEmail());
            }
        }
        if (toList.isEmpty()) {
            log.warn("监控项：{}接收人EMAIL地址为空");
            return;
        }

        WarningData warningData = data.getWarningData();
        MonitorItem monitorItem = data.getMonitorItem();
        String content = null;
        String subject = null;
        NotifyRecord.NotifyType notifyType = NotifyRecord.NotifyType.WARN;
        if (WarningData.Status.NORMAL.equals(data.getWarningData().getStatus())) {
            String host = null != warningData.getHost() ? warningData.getHost() + hostSuffix : hostSuffix.substring(1, hostSuffix.length());
            if (data.getWarningData().getLastSendTime() == null) {
                content = String.format(WARN_EMAIL_FORMAT,
                        monitorItem.getName(),
                        monitorItem.getLevel().getDesc(),
                        DateUtils.getStrTimeFormat(warningData.getFirstTime()),
                        warningData.getTimes(),
                        null != warningData.getApplicationName() ? warningData.getApplicationName() : "",
                        host,
                        null != warningData.getTarget() ? warningData.getTarget() : "");
            } else {
                content = String.format(WARN_RE_EMAIL_FORMAT,
                        monitorItem.getName(),
                        monitorItem.getLevel().getDesc(),
                        DateUtils.getStrTimeFormat(warningData.getFirstTime()),
                        DateUtils.getStrTimeFormat(warningData.getLastSendTime()),
                        warningData.getTimes(),
                        null != warningData.getApplicationName() ? warningData.getApplicationName() : "",
                        host,
                        null != warningData.getTarget() ? warningData.getTarget() : "");
            }

            subject = "【C3数据服务平台】监控系统告警通知";
        } else {
            if (warningData.getRecoveryTime() != null) {
                String host = null != warningData.getHost() ? warningData.getHost() + hostSuffix : hostSuffix.substring(1, hostSuffix.length());
                content = String.format(RECOVERY_EMAIL_FORMAT,
                        monitorItem.getName(),
                        monitorItem.getLevel().getDesc(),
                        DateUtils.getStrTimeFormat(warningData.getFirstTime()),
                        DateUtils.getStrTimeFormat(warningData.getRecoveryTime()),
                        null != warningData.getApplicationName() ? warningData.getApplicationName() : "",
                        host,
                        null != warningData.getTarget() ? warningData.getTarget() : "");
                subject = "【C3数据服务平台】监控系统告警恢复通知";
                notifyType = NotifyRecord.NotifyType.RECOVERY;
            } else {
                content = String.format(WARN_CLEAN_EMAIL_FORMAT, monitorItem.getName(), DateUtils.getStrTimeFormat(warningData.getFirstTime()));
                subject = "【C3数据服务平台】监控系统告警过期告警清除通知";
            }

        }
        boolean success = sendEmail(fromEmail, toList.toArray(new String[]{}), subject, content);
        NotifyRecord record = new NotifyRecord();
        record.setNotifyType(notifyType);
        record.setContent(content);
        saveRecord(data, success, record, MonitorItem.MessageType.EMAIL);
    }

    private boolean sendEmail(String from, String[] to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        try {
            mailSender.send(message);
        } catch (Exception e) {
            log.warn("send notify email failed:", e);
            return false;
        }
        return true;
    }
}
