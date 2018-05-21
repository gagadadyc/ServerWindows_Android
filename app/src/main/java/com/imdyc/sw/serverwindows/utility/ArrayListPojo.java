package com.imdyc.sw.serverwindows.utility;

import com.imdyc.sw.serverwindows.bean.SysPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 邓远超 on 2018/5/21.
 * list进行序列化操作时Gson会将其解析成JSONArray格式
 * 此类用于对list进行包装，使Gson解析成JSONObject格式
 */
public class ArrayListPojo {
    private ArrayList<SysPoint> list;

    public ArrayList<SysPoint> getList() {
        return list;
    }

    public void setList(ArrayList<SysPoint> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "ArrayListPojo{" +
                "list=" + list +
                '}';
    }
}
