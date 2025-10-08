package com.dyaco.spirit_commercial.support;

import android.content.Context;
import android.graphics.Typeface;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.dyaco.spirit_commercial.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class LineChartData {
    Context mContext;
    LineChart lineChart;

    public LineChartData(LineChart lineChart, Context context) {
        this.mContext = context;
        this.lineChart = lineChart;
    }

    public void initDataSet(ArrayList<Entry> valuesY) {

        //設定所需特定標籤資料
        // String[] xValue = new String[]{"", "10/31", "", "11/10", "", "11/20", "", "1/24", "", "1/31", "", "2/7"};
        //    List<String> xList = new ArrayList<>(Arrays.asList(xValue));
        lineChart.setViewPortOffsets(138, 56, 20, 86);//上下左右間距
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(false); //設定是否可以觸控，如為false，則不能拖動，縮放等
        lineChart.setDragEnabled(false);  //設定是否可以拖拽，縮放
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);


        // chart.setDrawR
        // don't forget to refresh the drawing

        //list是你這條線的數據，label 是你對這條線的描述
        LineDataSet lineDataSet = new LineDataSet(valuesY, "DataSet 1");
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);//類型為折線
        lineDataSet.setCubicIntensity(0.2f);
        lineDataSet.setDrawFilled(false);//使用範圍背景填充(預設不使用)
        //  lineDataSet.setFillColor(ContextCompat.getColor(mContext, android.R.color.transparent));
        //   lineDataSet.setFillAlpha(0);


        lineDataSet.setLineWidth(3.0f);//折線線寬
        //   set1.setHighLightColor(ContextCompat.getColor(mContext, R.color.color80FFFFFF));
        lineDataSet.setColor(ContextCompat.getColor(mContext, R.color.color1396ef)); //線的顏色
        //  set1.setDrawHorizontalHighlightIndicator(false);

        lineDataSet.setCircleColor(ContextCompat.getColor(mContext, R.color.color1396ef));//圓點顏色
        lineDataSet.setDrawCircles(true); //不顯示相應座標點的小圓圈(預設顯示)
        lineDataSet.setDrawValues(true);//顯示座標點對應Y軸的數字(預設顯示)
        lineDataSet.setCircleRadius(4f);//圓點大小
        lineDataSet.setDrawCircleHole(false);//圓點為實心(預設空心)
        lineDataSet.setValueTextColor(ContextCompat.getColor(mContext, R.color.white));
        lineDataSet.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v) {
                return String.valueOf((int) v);
            }
        });

        lineDataSet.setValueTextSize(12);//座標點數字大小
        //   set1.setCircleHoleRadius(0f); //圓點為實心(預設空心)


//        lineDataSet.setFillFormatter(new IFillFormatter() {
//            @Override
//            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
//                return lineChart.getAxisLeft().getAxisMinimum();
//            }
//        });

        // create a data object with the data sets
        //  data.setValueTypeface(tfLight);
        //   data.setValueTextSize(9f);
        //  data.setDrawValues(false);

        LineData data = new LineData(lineDataSet);
        lineChart.setData(data); //一定要放在最後


//        chart.setDrawGridBackground(false);
//        chart.setMaxHighlightDistance(300);


        lineChart.getAxisRight().setEnabled(false); //讓右邊Y消失
        //  chart.setBackground(ContextCompat.getDrawable(this, R.drawable.panel_border_chart2));
        lineChart.getLegend().setEnabled(false); //不顯示圖例 (預設顯示)
        lineChart.animateXY(1000, 100);

//        chart.notifyDataSetChanged();

        lineChart.invalidate();//繪製圖表
    }

    public void initX(ArrayList<String> xData) {
        Typeface m_tfMtMedium = ResourcesCompat.getFont(mContext, R.font.inter_regular);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setEnabled(true); //軸數值顯示否
        xAxis.setDrawGridLines(true); //是否將X軸格子消失掉

        //  x.setAxisLineColor(ContextCompat.getColor(mContext, R.color.color1396ef));

        xAxis.setLabelCount(xData.size(), true); //X軸標籤個數 被限制在25

        xAxis.setAvoidFirstLastClipping(true); //如果設定為true，則在繪製時會避免“剪掉”在x軸上的圖表或螢幕邊緣的第一個和最後一個座標軸標籤項。
        xAxis.setGridColor(ContextCompat.getColor(mContext, R.color.color323f4b));
        xAxis.setGridLineWidth(2); //x線寬
//        xAxis.setAxisLineWidth(2); //下面
//        xAxis.setAxisLineColor(ContextCompat.getColor(mContext,R.color.colorCd5bff));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//X軸標籤顯示位置(預設顯示在上方，分為上方內/外側、下方內/外側及上下同時顯示)
        xAxis.setTextColor(ContextCompat.getColor(mContext, R.color.color5a7085));//X軸標籤顏色
        xAxis.setTextSize(24);//X軸標籤大小
        xAxis.setTypeface(m_tfMtMedium);// X軸標籤文字字型
//        xAxis.setSpaceMin(0.0f);//讓左側x軸不從0點開始
//        xAxis.setSpaceMax(0.0f);//x軸最左多出空n個座標
        xAxis.setYOffset(25f); //下面的字離chart的距離
        //   xAxis.setAxisMinimum(0);                        // X軸標籤最小值
        //    xAxis.setAxisMaximum(10);           // X軸標籤最大值
        //   xAxis.setLabelRotationAngle(0);                 // X軸標籤旋轉角度


        final int[] x = {0};
        // 獲取到資料後，格式化x軸的資料
        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v) {
                String data;
                 int i = (int) v;
             //   int i = x[0];
                try {

//                    if (xData.size() > 7) { //日期超過8筆，不要全部顯示
//                        data = (i % 2 == 0) ? xData.get(i) : "";
//                    } else {
//                        data = xData.get(i);
//                    }

                    if (xData.size() > 7 && xData.size() < 14) { //日期超過8筆，不要全部顯示
                        data = (i % 2 == 0) ? xData.get(i) : "";
                    } else if (xData.size() >= 14 && xData.size() < 21) {
                        data = (i % 3 == 0) ? xData.get(i) : "";
                    } else if (xData.size() >= 21) { // && xData.size() < 28
                        data = (i % 4 == 0) ? xData.get(i) : "";
//                    } else if (xData.size() >= 28 && xData.size() < 35) {
//                        data = (i % 5 == 0) ? xData.get(i) : "";
//                    } else if (xData.size() >= 35) {
//                        data = (i % 6 == 0) ? xData.get(i) : "";
                    } else {
                        data = xData.get(i);
                    }

//                    x[0] += 1;
//                    if (x[0] >= xData.size()) x[0] = 0;
                } catch (Exception e) {
                    data = "";
                }

                return data;
            }
        });
    }

    public void initY(ArrayList<Entry> yData) {
        YAxis rightAxis = lineChart.getAxisRight();//獲取右側的軸線
        rightAxis.setEnabled(false);//不顯示右側Y軸

        YAxis axisLeft = lineChart.getAxisLeft();
        //獲取左側的軸線
        //  y.setAxisLineColor(ContextCompat.getColor(mContext, R.color.color323f4b));
        axisLeft.setEnabled(true);
        axisLeft.setDrawTopYLabelEntry(true);                   // 顯示Y軸最上方數值 (預設顯示)

        axisLeft.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART); //文字位置
        axisLeft.setDrawGridLines(true); //顯示每個座標點對應X軸的線 (預設顯示)
        axisLeft.setTextColor(ContextCompat.getColor(mContext, R.color.color5a7085));//X軸標籤顏色
        axisLeft.setTextSize(24);//X軸標籤大小
        axisLeft.setGridColor(ContextCompat.getColor(mContext, R.color.color323f4b));
        axisLeft.setGridLineWidth(2.0f); //線的寬度
        axisLeft.setXOffset(30f); // 左邊字跟chart的距離

        //  leftY_Axis.setYOffset(25f); 左邊字跟上面的距離
        //  leftY_Axis.setGranularity(28);                          // Y軸數值的間隔
        //     leftY_Axis.setSpaceTop(120f);


        List<Float> list = new ArrayList<>();
        for (Entry entry : yData) {
            list.add(entry.getY());
        }
        float max = Collections.max(list) + 2;
        float min = Collections.min(list) - 2;
        axisLeft.setAxisMaximum(max); //Y軸標籤最大值
        axisLeft.setAxisMinimum(min); //Y軸標籤最小值
        axisLeft.setLabelCount((int) (max - min), true); //Y軸標籤個數


        final int[] x = {0};
        // DecimalFormat mFormat = new DecimalFormat("###,###.0");//Y軸數值格式及小數點
        DecimalFormat mFormat = new DecimalFormat("###,###");//Y軸數值格式及小數點

        axisLeft.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v) {
                String s = (x[0] % 2 == 0) ? mFormat.format(v) : "";
                x[0] += 1;
                if (x[0] >= axisLeft.getLabelCount()) x[0] = 0;
                return s;
            }
        });

    }

    public static boolean isDecimal(String decimal) {
        // .0 不算小數 ([1-9]+)
        Pattern pattern = Pattern.compile("[+-]?[0-9]+\\.([1-9]+)?");
        return pattern.matcher(decimal).matches();
    }


}
