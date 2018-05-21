package com.imdyc.sw.serverwindows.bean;

import java.io.Serializable;

/**
 * Created by 邓远超 on 2018/5/20.
 * 存放服务器基本信息,非图表显示数据
 * 实现序列化接口，保证每一个对象都是序列化的，Intent才能够传输
 */
public class SysPoint implements Serializable {
    private String n_cpus;

    private String n_users;

    private String uptime_format;

    private String host;

    public String getN_cpus() {
        return n_cpus;
    }

    public void setN_cpus(String n_cpus) {
        this.n_cpus = n_cpus;
    }

    public String getN_users() {
        return n_users;
    }

    public void setN_users(String n_users) {
        this.n_users = n_users;
    }

    public String getUptime_format() {
        return uptime_format;
    }

    public void setUptime_format(String uptime_format) {
        this.uptime_format = uptime_format;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String toString() {
        return "SysPoint{" +
                "n_cpus='" + n_cpus + '\'' +
                ", n_users='" + n_users + '\'' +
                ", uptime_format='" + uptime_format + '\'' +
                ", host='" + host + '\'' +
                '}';
    }
}
