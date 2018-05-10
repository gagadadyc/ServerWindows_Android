package com.imdyc.sw.serverwindows.chartsetting;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by 邓远超 on 2018/5/10.
 * 定义X轴格式
 */
public class MyXAxisValueFormatter implements IAxisValueFormatter  {

    private String[] mValues;

    public MyXAxisValueFormatter(String[] values) {
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // "value" represents the position of the label on the axis (x or y)
        return mValues[(int) value];
    }

    /** this is only needed if numbers are returned, else return 0 */
    public int getDecimalDigits() { return 0; }
}
