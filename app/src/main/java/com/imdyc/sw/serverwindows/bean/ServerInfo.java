package com.imdyc.sw.serverwindows.bean;

/**
 * Created by 邓远超 on 2018/5/3.
 */
public class ServerInfo {

    private int id;
    private String Name;
    private String ip;

    public ServerInfo() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "id=" + id +
                ", Name='" + Name + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
