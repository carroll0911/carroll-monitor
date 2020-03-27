package com.carroll.monitor.analyzer.ws;

import javax.xml.bind.annotation.*;

/**
 * Created on 202017/12/7 16:40 By hehongbo
 * @date 2019/9/9
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskFeedback", propOrder = {
        "_interfaceStatus",
        "_interfaceFailureCause",
        "_taskCode"
})
@XmlRootElement(name = "TaskFeedback",namespace="")
public class TaskFeedback {

    @XmlElement(name = "interfaceStatus")
    private String _interfaceStatus;
    @XmlElement(name = "interfaceFailureCause")
    private String _interfaceFailureCause;
    @XmlElement(name = "taskCode")
    private String _taskCode;

    public String getInterfaceStatus() {
        return _interfaceStatus;
    }

    public void setInterfaceStatus(String interfaceStatus) {
        this._interfaceStatus = interfaceStatus;
    }

    public String getInterfaceFailureCause() {
        return _interfaceFailureCause;
    }

    public void setInterfaceFailureCause(String interfaceFailureCause) {
        this._interfaceFailureCause = interfaceFailureCause;
    }

    public String getTaskCode() {
        return _taskCode;
    }

    public void setTaskCode(String taskCode) {
        this._taskCode = taskCode;
    }
}
