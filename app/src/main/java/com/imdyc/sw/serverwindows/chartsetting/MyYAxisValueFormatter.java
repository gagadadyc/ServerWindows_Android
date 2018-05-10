package com.imdyc.sw.serverwindows.chartsetting;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by 邓远超 on 2018/5/10.
 * 自定义Y轴及数据显示格式
 */
public class MyYAxisValueFormatter implements IValueFormatter, IAxisValueFormatter {

    private DecimalFormat mFormat;

    public MyYAxisValueFormatter() {

        // format values to 1 decimal digit
        mFormat = new DecimalFormat("###,###,##0.0");
    }

    // IValueFormatter
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return mFormat.format(value) + " %";
    }
    // IAxisValueFormatter
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mFormat.format(value) + " %";
    }

    /** this is only needed if numbers are returned, else return 0 */
    public int getDecimalDigits() { return 1; }
}
