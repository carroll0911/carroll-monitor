package com.carroll.monitor.analyzer.model;

import lombok.Getter;
import lombok.Setter;

/**O
 * 产品项目信息
 * @author: carroll
 * @date 2019/10/16
 */
@Getter
@Setter
public class Project extends BaseModel {

    /**
     * 名称
     */
    private String name;
    /**
     * tag
     */
    private String tag;
    /**
     * 密码
     */
    private String password;

    /**
     * 状态
     */
    private Status status= Status.ENABLED;

    /**
     * 描述
     */
    private String desc;

    public enum Status {
        ENABLED,
        DISABLED
    }

}
