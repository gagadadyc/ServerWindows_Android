package com.imdyc.sw.serverwindows.bean;

import java.util.List;

/**
 * Created by 邓远超 on 2018/5/3.
 * 存放服务器时序信息,图表显示数据
 */
public class ServerInfo {

    private String serverName;
    private List<CPU> CPU;
    private List<Memory> memory;
    private List<Disk> disks;
    private List<Processes> processes;

    public List<Disk> getDisks() {
        return disks;
    }

    public void setDisks(List<Disk> disks) {
        this.disks = disks;
    }

    public List<Memory> getMemory() {
        return memory;
    }

    public void setMemory(List<Memory> memory) {
        this.memory = memory;
    }

    public List<com.imdyc.sw.serverwindows.bean.CPU> getCPU() {
        return CPU;
    }

    public void setCPU(List<com.imdyc.sw.serverwindows.bean.CPU> CPU) {
        this.CPU = CPU;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public List<Processes> getProcesses() {
        return processes;
    }

    public void setProcesses(List<Processes> processes) {
        this.processes = processes;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "serverName='" + serverName + '\'' +
                ", CPU=" + CPU +
                ", memory=" + memory +
                ", disks=" + disks +
                ", processes=" + processes +
                '}';
    }
}
