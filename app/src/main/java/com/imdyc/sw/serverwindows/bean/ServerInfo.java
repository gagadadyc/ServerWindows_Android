package com.imdyc.sw.serverwindows.bean;

import java.util.List;

/**
 * Created by 邓远超 on 2018/5/3.
 * 存放服务器时序信息,图表显示数据
 */
public class ServerInfo {

    private int id;
    private String Name;
    private String ip;
    private List<MemoryInfo> memory;

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

    public List<MemoryInfo> getMemory() {
        return memory;
    }

    public void setMemory(List<MemoryInfo> memory) {
        this.memory = memory;
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
