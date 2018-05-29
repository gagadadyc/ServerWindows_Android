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
import com.imdyc.sw.serverwindows.chartsetting.DiskYAxisValueFormatter;
import com.imdyc.sw.serverwindows.chartsetting.MyXAxisValueFormatter;
import com.imdyc.sw.serverwindows.utility.MapIntent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by 邓远超 on 2018/5/24.
 */
public class DiskChartActivity extends Activity {
    LinkedHashMap<Float, Float> readDiskMap;//预加载内存数据
    LinkedHashMap<Float, Float> writeDiskMap = new LinkedHashMap<>();//预加载cpu数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.console_disk_chart);

        Intent intent = getIntent();


        /**
         *磁盘I/O图表
         */
        LineChart chart = (LineChart) findViewById(R.id.chart_disk);


        //自定义轴显示格式
        YAxis left = chart.getAxisLeft();  //显示于左边Y轴
        left.setValueFormatter(new DiskYAxisValueFormatter());
        YAxis right = chart.getAxisRight();   //显示于右边的Y轴
        right.setValueFormatter(new DiskYAxisValueFormatter());
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f); // 间隔限制为一个单位，禁止复制重复轴标签



        //装填X轴时间标签
        String[] values = intent.getStringArrayExtra("values");
        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));

        //装填磁盘读取map数据
        MapIntent mi = (MapIntent) intent.getSerializableExtra("readDiskMapIntent");
        readDiskMap = mi.getLinkedHashMap();
        //装填磁盘写入map数据
        mi = (MapIntent) intent.getSerializableExtra("writeDiskMapIntent");
        writeDiskMap = mi.getLinkedHashMap();


        //将内存数据注入List<Entry>（图表显示的数据）中。
        List<Entry> entriesR = loadList(readDiskMap);
        List<Entry> entriesW = loadList(writeDiskMap);

        LineDataSet dataSetR = new LineDataSet(entriesR,"磁盘读取速率");
        LineDataSet dataSetW = new LineDataSet(entriesW,"磁盘写入速率");
        dataSetR.setColor(Color.RED);
        dataSetR.setValueTextColor(Color.rgb(0,40,80));
        dataSetW.setColor(Color.BLUE);
        dataSetW.setValueTextColor(Color.rgb(240,20,20));



        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSetR);
        dataSets.add(dataSetW);

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
