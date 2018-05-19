package com.imdyc.sw.serverwindows;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.imdyc.sw.serverwindows.application.ResInfoApplication;
import com.imdyc.sw.serverwindows.bean.MemoryInfo;
import com.imdyc.sw.serverwindows.bean.ServerInfo;
import com.imdyc.sw.serverwindows.chartsetting.MyXAxisValueFormatter;
import com.imdyc.sw.serverwindows.chartsetting.MyYAxisValueFormatter;
import com.imdyc.sw.serverwindows.utility.MapIntent;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 邓远超 on 2018/5/8.
 * 服务器信息图表
 */
public class ChartActivity extends Activity {

    LinkedHashMap<Float, Float> memoryMap;//预加载内存数据
    LinkedHashMap<Float, Float>  cpuMap = new LinkedHashMap<>();//预加载cpu数据
    SimpleDateFormat format =  new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
    Gson gson = new Gson();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.console_server_chart);

        Intent intent = getIntent();
        String serverName = intent.getStringExtra("serverName");  //对应的服务器名称


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

        /**
         *内存/CPU图表
         */
        LineChart chart = (LineChart) findViewById(R.id.chart_memory);


        //自定义轴显示格式
        YAxis left = chart.getAxisLeft();  //显示于左边Y轴
        left.setValueFormatter(new MyYAxisValueFormatter());
        YAxis right = chart.getAxisRight();   //显示于右边的Y轴
        right.setValueFormatter(new MyYAxisValueFormatter());
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f); // 间隔限制为一个单位，禁止复制重复轴标签

//        String[]  values = new String[] {"1:00","2:00","3:00","4:00","5:00"};


        //装填X轴时间标签
        String[] values = intent.getStringArrayExtra("values");
//        values = new String[] {"1:00","2:00","3:00","4:00","5:00"};

        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));

        //装填memoryMap数据
        MapIntent mi = (MapIntent) intent.getSerializableExtra("memoryMapIntent");
        memoryMap = mi.getMap();
        System.out.println("mapcd:"+memoryMap.size());
        System.out.println("valcd:"+values.length);

        //将内存数据注入List<Entry>（图表显示的数据）中。
        List<Entry> entriesM = loadList(memoryMap);

//        List<Entry> entriesC = new ArrayList<>();
//
//        Iterator<LinkedHashMap.Entry<Float,Float>> iteratorM = memoryMap.entrySet().iterator();
//        Iterator<LinkedHashMap.Entry<Float,Float>> iteratorC = cpuMap.entrySet().iterator();

//        //迭代CPU数据集，并装填CPU数据
//        while (iteratorC.hasNext()){
//            LinkedHashMap.Entry<Float,Float> entry = iteratorC.next();
//
//            float f1 = entry.getKey();
//            float f2 = entry.getValue();
//
//            entriesC.add(new Entry(f1,f2));
//        }
//
        LineDataSet dataSetM = new LineDataSet(entriesM,"内存占用百分比");
//        LineDataSet dataSetC = new LineDataSet(entriesC,"CPU占用百分比");
        dataSetM.setColor(Color.RED);
        dataSetM.setValueTextColor(Color.rgb(0,40,80));
//        dataSetC.setColor(Color.BLUE);
//        dataSetC.setValueTextColor(Color.rgb(240,20,20));

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSetM);
//        dataSets.add(dataSetC);

        LineData lineData = new LineData(dataSets);

        chart.setData(lineData);
        chart.invalidate();  //refresh
    }

    /**
     * 将map中的数据放入框架使用的list中，以便显示数据
     * @param map 具体的指标数据
     * @return 框架使用的list
     */
    private List<Entry> loadList(LinkedHashMap<Float, Float> map) {
        Iterator<LinkedHashMap.Entry<Float,Float>> iterator = map.entrySet().iterator();
        List<Entry> entries = new ArrayList<>();
        while (iterator.hasNext()){
            LinkedHashMap.Entry<Float,Float> entry = iterator.next();

            float f1 = entry.getKey();
            float f2 = entry.getValue();

            entries.add(new Entry(f1,f2));
        }
        return entries;
    }



//    private static void getMemoryInfo(List<MemoryInfo> list) throws ParseException {
//
//        int i = 0;
//        Iterator<MemoryInfo> iterator = list.iterator();
//
//
//        //迭代memoryList，并装填内存数据
//        while (iterator.hasNext()){
//            MemoryInfo momoryInfo = iterator.next();
//            memoryMap.put((float)i,momoryInfo.getUsed_Percent());
//            long time = momoryInfo.getTime()/1000000000;
//            Date date = format.parse(format.format(time));
//            String strDate = date.toString();
//            values[i] = strDate;
//            i++;
//        }
//    }


//    //Volley与Activity生命周期关联
//    @Override
//    protected void onStop(){
//        super.onStop();
//        ResInfoApplication.getHttpQueues().cancelAll("ServerInfoPost");
//    }
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
