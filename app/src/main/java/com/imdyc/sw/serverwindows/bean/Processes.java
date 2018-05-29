package com.imdyc.sw.serverwindows.bean;

import java.util.Date;

/**
 * Created by 邓远超 on 2018/5/25.
 */
public class Processes {
    private Date time;
    private float total; //总线程数量
    private float sleeping; //休眠中进程数量
    private float zombies; //僵尸线程数量

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getSleeping() {
        return sleeping;
    }

    public void setSleeping(float sleeping) {
        this.sleeping = sleeping;
    }

    public float getZombies() {
        return zombies;
    }

    public void setZombies(float zombies) {
        this.zombies = zombies;
    }

    @Override
    public String toString() {
        return "    Processes{" +
                "time=" + time +
                ", total=" + total +
                ", sleeping=" + sleeping +
                ", zombies=" + zombies +
                '}';
    }
}
