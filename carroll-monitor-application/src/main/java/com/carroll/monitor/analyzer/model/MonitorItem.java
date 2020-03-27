package com.carroll.monitor.analyzer.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 监控项信息
 * @author: carroll
 * @date 2019/10/16
 */
@Getter
@Setter
public class MonitorItem extends BaseModel {

    /**
     * 名称
     */
    private String name;
    /**
     * tag
     */
    private String tag;
    /**
     * 类别
     */
    private Category category;

    /**
     * 级别
     */
    private Level level;
    /**
     * 所属项目ID
     */
    private String projectId;
    /**
     * 描述
     */
    private String description;
    /**
     * 告警阈值
     */
    private Long times;
    /**
     * 重复告警周期(分)
     */
    private Long cycle;
    /**
     * 重复告警触发次数
     */
    private Long cycleTimes;
    /**
     * 告警接收人
     */
    private List<Operator> receivers;
    /**
     * 是否发送告警
     */
    private Boolean sendFlag;
    /**
     * 告警信息类型
     */
    private List<MessageType> msgTypes;
    /**
     * 告警恢复阈值
     */
    private Long recoveryTimes;

    /**
     * 处理意见
     */
    private String suggest;

    /**
     * 忽略 host
     */
    private Boolean ignoreHost;
    /**
     * 忽略 告警源
     */
    private Boolean ignoreApp;

    /**
     * 超时时间（毫秒）
     */
    private Long timeoutMs;

    /**
     * 状态
     */
    private Status status = Status.ENABLED;

    /**
     * 判断执行结果的脚本
     */
    private String resultScript;

    public static boolean isNull(MonitorItem item) {
        return item == null || StringUtils.isEmpty(item.getTag());
    }


    public enum Category {
        BUSINESS("BUSINESS", "业务"),
        SYSTEM("SYSTEM", "系统");
        @Getter
        private String code;
        @Getter
        private String desc;

        Category(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    public enum Level {
        MAJOR("严重"),
        CRITICAL("紧急"),
        EVENT("事件"),
        WARNING("警告");
        @Getter
        private String desc;

        Level(String desc) {
            this.desc = desc;
        }


    }

    public enum MessageType {
        SMS("短信"),
        EMAIL("邮件"),
        API_PUSH("接口推送");
        @Getter
        private String desc;

        MessageType(String desc) {
            this.desc = desc;
        }
    }

    public enum Status {
        ENABLED,
        DISABLED;
    }
}
