package com.dyaco.spirit_commercial.support.test_custom_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class McDonaldsLogoView extends View {

    private Paint paint;
    private Path path;

    public McDonaldsLogoView(Context context) {
        super(context);
        init();
    }

    public McDonaldsLogoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public McDonaldsLogoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 初始化画笔
        paint = new Paint();
        paint.setColor(Color.YELLOW);  // 设置麦当劳标志的黄色
        paint.setStyle(Paint.Style.STROKE);  // 只绘制边框
        paint.setStrokeWidth(40f);  // 设定边框宽度
        paint.setAntiAlias(true);   // 抗锯齿效果

        // 初始化路径
        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 获取视图的宽高
        int width = getWidth();
        int height = getHeight();

        // 中心线位置
        float centerX = width / 2f;
        float bottomY = height * 0.85f;

        // 绘制左侧的 M 弧线
        path.moveTo(centerX - 300, bottomY);  // 起点
        path.quadTo(centerX - 150, height * 0.2f, centerX, bottomY);  // 使用贝塞尔曲线绘制

        // 绘制右侧的 M 弧线
        path.quadTo(centerX + 150, height * 0.2f, centerX + 300, bottomY);

        // 绘制路径
        canvas.drawPath(path, paint);
    }
}