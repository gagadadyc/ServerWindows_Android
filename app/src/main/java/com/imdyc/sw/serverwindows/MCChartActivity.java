package com.imdyc.sw.serverwindows;

import android.app.Activity;
import android.content.Intent;
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
import com.imdyc.sw.serverwindows.utility.MapIntent;

import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.console_memcpu_chart);

        Intent intent = getIntent();


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

}
