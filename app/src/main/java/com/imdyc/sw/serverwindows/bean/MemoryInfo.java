package com.imdyc.sw.serverwindows.bean;

import java.util.Date;

/**
 * Created by 邓远超 on 2018/5/12.
 */
public class MemoryInfo {
    private Date time;
    private String used_percent;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getUsed_Percent() {
        return used_percent;
    }

    public void setUsed_Percent(String used_Percent) {
        this.used_percent = used_Percent;
    }

    @Override
    public String toString() {
        return "MemoryInfo{" +
                "time=" + time +
                ", used_Percent='" + used_percent + '\'' +
                '}';
    }
}
