package com.imdyc.sw.serverwindows.bean;

import java.util.Date;

/**
 * Created by 邓远超 on 2018/5/22.
 */
public class CPU {
    private String usage_idle; //cpu空闲百分比
    private Date time;

    public String getUsage_idle() {
        return usage_idle;
    }

    public void setUsage_idle(String usage_idle) {
        this.usage_idle = usage_idle;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "CPU{" +
                "usage_idle='" + usage_idle + '\'' +
                ", time=" + time +
                '}';
    }
}
