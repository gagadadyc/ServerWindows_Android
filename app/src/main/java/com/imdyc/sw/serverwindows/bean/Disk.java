package com.imdyc.sw.serverwindows.bean;

import java.util.Date;

/**
 * Created by 邓远超 on 2018/5/24.
 */
public class Disk {

    private Date time;
    private float read_speed; //磁盘每分钟读取字节数
    private float write_speed; //磁盘每分钟写字节数

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public float getWrite_speed() {
        return write_speed;
    }

    public void setWrite_speed(float write_speed) {
        this.write_speed = write_speed;
    }

    public float getRead_speed() {
        return read_speed;
    }

    public void setRead_speed(float read_speed) {
        this.read_speed = read_speed;
    }

    @Override
    public String toString() {
        return "Disk{" +
                "time=" + time +
                ", read_speed='" + read_speed + '\'' +
                ", write_speed='" + write_speed + '\'' +
                '}';
    }
}
