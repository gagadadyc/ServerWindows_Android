package com.imdyc.sw.serverwindows.utility;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Created by 邓远超 on 2018/5/19.
 * 由于Serializable不支持LinkedHashMap，所以使用实现LinkedHashMap接口的自定义类
 */
public class MapIntent  implements Serializable {
    private LinkedHashMap mMap;

    public void setMap(LinkedHashMap  map){
        mMap=map;
    }

    public LinkedHashMap getMap() {
        return mMap;
    }
}
