package com.carroll.monitor.analyzer.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 告警发送记录
 * @author: carroll
 * @date 2019/10/16 
 */
@Getter
@Setter
public class NotifyRecord extends BaseModel {
    //发送时间
    private Date sendTime;
    //告警数据ID
    private WarningData warnData;
    //接收人
    private List<Operator> receivers;
    //消息类型
    private MonitorItem.MessageType messageType;
    //通知类型（恢复/告警）
    private NotifyType notifyType;
    //是否发送成功
    private boolean success;
    //发送内容
    private String content;
    //模板编号
    private String templateNo;

    public enum NotifyType{
        WARN("WARN","告警"),
        RECOVERY("RECOVERY","恢复");
        @Getter
        private String code;
        @Getter
        private String desc;
        NotifyType(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }
}
