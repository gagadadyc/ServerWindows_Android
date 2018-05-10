package com.imdyc.sw.serverwindows;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.imdyc.sw.serverwindows.chartsetting.MyXAxisValueFormatter;
import com.imdyc.sw.serverwindows.chartsetting.MyYAxisValueFormatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 邓远超 on 2018/5/8.
 * 服务器信息图表
 */
public class ChartActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.console_server_chart);


        /**
         *内存/CPU图表
         */
        LineChart chart = (LineChart) findViewById(R.id.chart_memory);


        LinkedHashMap<Long, Float>testMap = new LinkedHashMap<>();

        //自定义轴显示格式
        YAxis left = chart.getAxisLeft();  //显示于左边Y轴
        left.setValueFormatter(new MyYAxisValueFormatter());
        YAxis right = chart.getAxisRight();   //显示于右边的Y轴
        right.setValueFormatter(new MyYAxisValueFormatter());

        String[] values = new String[]{"00:00","00:20","00:40","01:00","01:20"};//定义x轴标签，此处为测试数据
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f); // 间隔限制为一个单位，禁止复制重复轴标签
        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));


        LinkedHashMap<Float, Float>  memoryMap = new LinkedHashMap();//内存数据
        LinkedHashMap<Float, Float>  cpuMap = new LinkedHashMap<>();//cpu数据
        /**
         * 测试数据-内存
         * 80f等价于（float）80
         */
        memoryMap.put(0f,80f);
        memoryMap.put(1f,70f);
        memoryMap.put(2f,65f);
        memoryMap.put(3f,60f);
        memoryMap.put(4f,68f);
        /**
         * cpu
         */
        cpuMap.put(0f,30f);
        cpuMap.put(1f,20f);
        cpuMap.put(2f,15f);
        cpuMap.put(3f,35f);
        cpuMap.put(4f,25f);
        /**
         * 测试数据装填完毕
         */
        List<Entry> entriesM = new ArrayList<>();
        List<Entry> entriesC = new ArrayList<>();

        Iterator<LinkedHashMap.Entry<Float,Float>> iteratorM = memoryMap.entrySet().iterator();
        Iterator<LinkedHashMap.Entry<Float,Float>> iteratorC = cpuMap.entrySet().iterator();
        //迭代内存数据集，并装填内存数据
        while (iteratorM.hasNext()){
            LinkedHashMap.Entry<Float,Float> entry = iteratorM.next();

            float f1 = entry.getKey();
            float f2 = entry.getValue();

            entriesM.add(new Entry(f1,f2));  //此处为图表框架com.github.mikephil.charting.data包中的Entry
        }

        //迭代CPU数据集，并装填CPU数据
        while (iteratorC.hasNext()){
            LinkedHashMap.Entry<Float,Float> entry = iteratorC.next();

            float f1 = entry.getKey();
            float f2 = entry.getValue();

            entriesC.add(new Entry(f1,f2));
        }

        LineDataSet dataSetM = new LineDataSet(entriesM,"内存占用百分比");
        LineDataSet dataSetC = new LineDataSet(entriesC,"CPU占用百分比");
        dataSetM.setColor(Color.RED);
        dataSetM.setValueTextColor(Color.rgb(0,40,80));
        dataSetC.setColor(Color.BLUE);
        dataSetC.setValueTextColor(Color.rgb(240,20,20));

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSetM);
        dataSets.add(dataSetC);

        LineData lineData = new LineData(dataSets);

        chart.setData(lineData);
        chart.invalidate();  //refresh
    }
//    //由于Entry构造函数参数类型Entry(float,float)
//    //此函数负责将时间戳改成相对时间戳，对照点为2018/1/1 00：00：00 ，即1514736000
//    //修改成相对时间戳，缩短长度以便使用float表示
//    private LinkedHashMap<Float, Float>  SetMemoryData(LinkedHashMap<Long,Float> map){
//
//        //使用迭代器取出Set中的key
//        Iterator<Map.Entry<Long,Float>> iterator = map.entrySet().iterator();
//
//        LinkedHashMap<Float, Float>  outMap = new LinkedHashMap<Float, Float>();
//        //迭代转换key
//        while (iterator.hasNext()){
//            LinkedHashMap.Entry<Long,Float> entry = iterator.next();
//            long l = entry.getKey();  //key
//            float i = entry.getValue(); //Value
//            float f = (float)(l-1514736000);
//            outMap.put(f,i);
//        }
//
//        return outMap;
//    }

}
