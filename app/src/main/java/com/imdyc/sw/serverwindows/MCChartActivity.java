package com.imdyc.sw.serverwindows;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.imdyc.sw.serverwindows.bean.CPU;
import com.imdyc.sw.serverwindows.bean.Memory;
import com.imdyc.sw.serverwindows.bean.ServerInfo;
import com.imdyc.sw.serverwindows.chartsetting.DiskYAxisValueFormatter;
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

/**
 * Created by 邓远超 on 2018/5/8.
 * 服务器信息图表
 */
public class MCChartActivity extends Activity {

    LinkedHashMap<Float, Float> memoryMap;//预加载内存数据
    LinkedHashMap<Float, Float>  cpuMap = new LinkedHashMap<>();//预加载cpu数据
    private String[] values ;//定义x轴标签

    Gson gson = new Gson();
    Thread thread;   //定时刷新页面，10S一次
    Thread thread2;  //轮询访问getSharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.console_memcpu_chart);

        Intent intent = getIntent();


        /**
         *内存/CPU图表
         */
        final LineChart chart = (LineChart) findViewById(R.id.chart_memory);


        //自定义轴显示格式
        YAxis left = chart.getAxisLeft();  //显示于左边Y轴
        left.setValueFormatter(new MyYAxisValueFormatter());
        YAxis right = chart.getAxisRight();   //显示于右边的Y轴
        right.setValueFormatter(new MyYAxisValueFormatter());
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f); // 间隔限制为一个单位，禁止复制重复轴标签

        //服务器名
        final String inner_SERVERNAME = intent.getStringExtra("ServerName");

        //装填X轴时间标签
        String[] values = intent.getStringArrayExtra("values");

        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));

        //装填memoryMap数据
        MapIntent mi = (MapIntent) intent.getSerializableExtra("memoryMapIntent");
        memoryMap = mi.getLinkedHashMap();
        mi = (MapIntent) intent.getSerializableExtra("cpuMapIntent");
        cpuMap = mi.getLinkedHashMap();


        //将内存数据注入List<Entry>（图表显示的数据）中。
        List<Entry> entriesM = loadList(memoryMap);
        List<Entry> entriesC = loadList(cpuMap);

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

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                chart.invalidate();  //refresh
            }
        };

        final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);


                volley_Post(inner_SERVERNAME,"ResInfo");
                threadWaitJump(inner_SERVERNAME,chart,handler,"ResInfo");


            }
        };
        thread = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    //Thread.currentThread().isInterrupted()
                    while (true) {

                        sleep(10000);//休眠10秒
                        System.out.println("refresh2");
                        mHandler.sendEmptyMessage(0);

                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                /**
                 * 要执行的操作
                 */
            }
        };
        thread.start();

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

    private void volley_Post(String serverName,final String InfoType) {


        String url = "http://192.168.1.187:8080/sw/"+InfoType;
        //请求数据
        HashMap<String,String> RequstMap = new HashMap<String,String>();
        RequstMap.put("serverName",serverName);

        JSONObject jsonObject = new JSONObject(RequstMap);
        JsonObjectRequest JOrequest = new JsonObjectRequest(Request.Method.POST,url,jsonObject,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {

                //获取存储文件
                SharedPreferences sharedPreferences = getSharedPreferences("ServerWindows", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(InfoType,arg0.toString());//将Json格式的服务器数据存入SharedPreferences中
                editor.commit();
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError arg0){
                Toast.makeText(MCChartActivity.this,arg0.toString()+"请求失败",Toast.LENGTH_SHORT).show();
            }
        });
        JOrequest.setTag("ServerInfoPost");

        ResInfoApplication.getHttpQueues().add(JOrequest);
    }

    private void threadWaitJump(final String inner_SERVERNAME,final LineChart chart,final Handler handler, final String InfoType){

        //若未取回数据，则休眠等待
        thread2 = new Thread(){
            @Override
            public void run(){
                boolean threadbl = false;
                try {
                    int i = 0;

                    //轮询方式查找getSharedPreferences，Server无数据则继续查找
                    while (!threadbl && getSharedPreferences("ServerWindows", Context.MODE_PRIVATE).getString(InfoType, "") == "") {
                        Thread.sleep(1000);//如果找不到，则睡眠1秒钟再访问。
                        i++;
                        if(i>10){
                            Looper.prepare();
                            Toast.makeText(MCChartActivity.this, "刷新超时", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            threadbl = Thread.interrupted();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //若线程未设置中断，则说明访问到了数据
                if(!threadbl) {

                    SharedPreferences preferences = getSharedPreferences("ServerWindows", Context.MODE_PRIVATE);
                    String serverJson=preferences.getString(InfoType, "");  // getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值


                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(InfoType,"");//读取完毕后将SharedPreferences中的值设为空，避免下一次读取由于线程同步问题读到旧数据
                    editor.commit();



                    //自定义轴显示格式
                    YAxis left = chart.getAxisLeft();  //显示于左边Y轴
                    left.setValueFormatter(new MyYAxisValueFormatter());
                    YAxis right = chart.getAxisRight();   //显示于右边的Y轴
                    right.setValueFormatter(new MyYAxisValueFormatter());
                    XAxis xAxis = chart.getXAxis();
                    xAxis.setGranularity(1f); // 间隔限制为一个单位，禁止复制重复轴标签

                    values = getSPMemArr(serverJson);
                    xAxis.setValueFormatter(new MyXAxisValueFormatter(values));



                    memoryMap = getSPMemMap(serverJson);
                    cpuMap = getSPCPUMap(serverJson);

                    //将内存数据注入List<Entry>（图表显示的数据）中。
                    List<Entry> entriesM = loadList(memoryMap);
                    List<Entry> entriesC = loadList(cpuMap);

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

                    handler.sendEmptyMessage(0);

                }
            }
        };
        thread2.start();
    }

    /**
     * 自定义内存/CPU图表的x轴标签
     * @param serverJson 服务器传来的Json数据
     * @return X轴自定义标签数组
     */
    private String[] getSPMemArr(String serverJson){
        ServerInfo serverInfo = gson.fromJson(serverJson, ServerInfo.class);
        List<Memory> memoryList = serverInfo.getMemory(); //接收数据

        values = new String[memoryList.size()];  //接收时间作为x轴坐标

        int i = 0;
        Iterator<Memory> iterator = memoryList.iterator();
        //迭代memoryList，并装填内存时间轴
        while (iterator.hasNext()){
            Memory memory = iterator.next();
            SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
            values[i]=sdf.format(memory.getTime());
            i++;
        }
        return values;
    }

    /**
     * @param serverJson 服务器传来的Json数据
     * 将存放在SharedPreferences中的Json信息进行反序列化，取出内存信息
     */
    private LinkedHashMap<Float, Float> getSPMemMap(String serverJson) {

        //Json反序列化
        ServerInfo serverInfo = gson.fromJson(serverJson, ServerInfo.class);
        List<Memory> memoryList = serverInfo.getMemory();

        memoryMap = new LinkedHashMap<>();

        int i = 0;
        Iterator<Memory> iterator = memoryList.iterator();
        //迭代memoryList，并装填内存数据
        while (iterator.hasNext()){
            Memory memory = iterator.next();
            memoryMap.put((float)i,Float.parseFloat(memory.getUsed_Percent())); //将内存百分比转为float
            i++;
        }

        return memoryMap;
    }

    /**
     * @param serverJson 服务器传来的Json数据
     * 将存放在SharedPreferences中的Json信息进行反序列化，取出cpu信息
     */
    private LinkedHashMap<Float, Float> getSPCPUMap(String serverJson) {

        //Json反序列化
        ServerInfo serverInfo = gson.fromJson(serverJson, ServerInfo.class);

        List<CPU> cpuInfoList = serverInfo.getCPU();

        cpuMap = new LinkedHashMap<>();
        int i = 0;
        Iterator<CPU> iterator = cpuInfoList.iterator();

        //迭代cpuList，装填cpu数据
        while (iterator.hasNext()){
            CPU cpuInfo = iterator.next();
            Float shiyong = 100f-Float.parseFloat(cpuInfo.getUsage_idle()); //将cpu空闲百分比转为float型的使用百分比
            cpuMap.put((float)i,shiyong);
            i++;
        }
        return cpuMap;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        thread.interrupt();  //通知线程中断

    }
}
