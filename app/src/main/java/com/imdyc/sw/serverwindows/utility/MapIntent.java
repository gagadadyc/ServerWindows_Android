package com.imdyc.sw.serverwindows.utility;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 邓远超 on 2018/5/19.
 * 由于Serializable不支持LinkedHashMap，所以使用实现LinkedHashMap接口的自定义类
 * 以供页面跳转时intent.putExtra()传参
 */
public class MapIntent  implements Serializable {
    private LinkedHashMap lMap;
    private Map Map;

    public void setLinkedHashMap(LinkedHashMap  map){
        lMap=map;
    }

    public LinkedHashMap getLinkedHashMap() {
        return lMap;
    }

    public void setMap(Map  map){
        Map=map;
    }

    public Map getMap() {
        return Map;
    }




}
