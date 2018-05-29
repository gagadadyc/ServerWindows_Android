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
 * Created by 邓远超 on 2018/5/25.
 */
public class ProcessActivity extends Activity {
    LinkedHashMap<Float, Float> totalProcessMap;//预加载内存数据
    LinkedHashMap<Float, Float> sleepingProcessMap;//预加载cpu数据
    LinkedHashMap<Float, Float> zombiesProcessMap;//预加载cpu数据


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.console_process_chart);

        Intent intent = getIntent();


        /**
         *磁盘I/O图表
         */
        LineChart chart = (LineChart) findViewById(R.id.chart_process);


        //自定义轴显示格式
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f); // 间隔限制为一个单位，禁止复制重复轴标签



        //装填X轴时间标签
        String[] values = intent.getStringArrayExtra("values");

        System.out.println("values:"+values.length+values.toString());
        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));

        //装填map数据
        MapIntent mi = (MapIntent) intent.getSerializableExtra("totalProcessMap");
        totalProcessMap = mi.getLinkedHashMap();
        mi = (MapIntent) intent.getSerializableExtra("sleepingProcessMap");
        sleepingProcessMap = mi.getLinkedHashMap();
        mi = (MapIntent) intent.getSerializableExtra("zombiesProcessMap");
        zombiesProcessMap = mi.getLinkedHashMap();


        //将内存数据注入List<Entry>（图表显示的数据）中。
        List<Entry> entriesT = loadList(totalProcessMap);
        List<Entry> entriesS = loadList(sleepingProcessMap);
        List<Entry> entriesZ = loadList(zombiesProcessMap);

        LineDataSet dataSetT = new LineDataSet(entriesT,"总进程数量");
        LineDataSet dataSetS = new LineDataSet(entriesS,"睡眠进程数量");
        LineDataSet dataSetZ = new LineDataSet(entriesZ,"僵尸进程数量");
        dataSetT.setColor(Color.RED);
        dataSetT.setValueTextColor(Color.rgb(240,20,20));
        dataSetS.setColor(Color.BLUE);
        dataSetS.setValueTextColor(Color.rgb(43,87,154));
        dataSetZ.setColor(Color.YELLOW);
        dataSetZ.setValueTextColor(Color.rgb(240,150,10));


        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSetT);
        dataSets.add(dataSetS);
        dataSets.add(dataSetZ);

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
