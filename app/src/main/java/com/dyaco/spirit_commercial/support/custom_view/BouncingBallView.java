package com.dyaco.spirit_commercial.support.custom_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;


/**
 * 模擬 球體在旋轉六邊形內彈跳 的  程式，球受到 重力與摩擦力 影響，並能夠真實與旋轉的牆壁發生物理碰撞。
 */
public class BouncingBallView extends View {
    private final Path hexagonPath = new Path();
    // 參數設定
    private final float ballRadius = 20f;          // 球半徑
    private float hexagonRadius;                   // 六邊形半徑 (依螢幕尺寸計算)
    private final float angularVelocity = (float) Math.toRadians(30);  // 六邊形角速度 (30度/秒)
    private float currentRotation = 0f;            // 當前旋轉角度

    private final float gravity = 980f;            // 重力加速度 (像素/秒²)
    private final float damping = 0.98f;            // 碰撞後衰減 (摩擦效果)  將 damping 的數值調高（接近 1.0），碰撞後的速度衰減較少，球的反彈力度會更大，看起來更有彈性。例如：

    // 球體狀態
    private float ballX, ballY;                    // 球心位置
    private float ballVX, ballVY;                  // 球速度

    private long lastTime;                         // 用來計算更新間隔

    // 畫筆
    private Paint ballPaint, hexagonPaint;

    public BouncingBallView(Context context) {
        super(context);
        init();
    }

    public BouncingBallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // 初始化畫筆
        ballPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ballPaint.setColor(Color.RED);

        hexagonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hexagonPaint.setColor(Color.BLUE);
        hexagonPaint.setStyle(Paint.Style.STROKE);
        hexagonPaint.setStrokeWidth(5);

        // 初始球體位置與速度 (位置在 onSizeChanged 中重新設定)
        ballX = 0;
        ballY = 0;
        ballVX = 300;  // 初始水平速度 (像素/秒)
        ballVY = -500; // 初始垂直速度

        lastTime = System.currentTimeMillis();
        // 啟動動畫更新
        post(updateRunnable);
    }

    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            long now = System.currentTimeMillis();
            float dt = (now - lastTime) / 1000f;
            lastTime = now;
            updatePhysics(dt);
            invalidate();
            postDelayed(this, 16); // 約 60 FPS
        }
    };

    private void updatePhysics(float dt) {

        dt = Math.min(dt, 0.05f); // 限制 dt 最大值為 0.05 秒

        // 更新六邊形旋轉角度
        currentRotation += angularVelocity * dt;

        // 更新球的速度與位置 (重力作用)
        ballVY += gravity * dt;
        ballX += ballVX * dt;
        ballY += ballVY * dt;

        // 六邊形中心
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        // 依目前旋轉角度計算六邊形頂點 (依序排列成 6 個點)
        PointF[] vertices = new PointF[6];
        for (int i = 0; i < 6; i++) {
            float angle = (float) (Math.toRadians(60 * i)) + currentRotation;
            float vx = centerX + hexagonRadius * (float) Math.cos(angle);
            float vy = centerY + hexagonRadius * (float) Math.sin(angle);
            vertices[i] = new PointF(vx, vy);
        }

        // 針對每個邊做碰撞檢測與回應
        for (int i = 0; i < 6; i++) {
            PointF p1 = vertices[i];
            PointF p2 = vertices[(i + 1) % 6];

            // 計算球心到邊線的最近點
            PointF closest = getClosestPointOnSegment(p1, p2, new PointF(ballX, ballY));
            float dx = ballX - closest.x;
            float dy = ballY - closest.y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);

            if (dist < ballRadius) {
                // 計算從牆面指向球心的法向量
                float nx = dx / dist;
                float ny = dy / dist;

                // 計算碰撞點的牆壁速度 (牆壁因旋轉產生的切向速度)
                float rx = closest.x - centerX;
                float ry = closest.y - centerY;
                float wallVX = -angularVelocity * ry;
                float wallVY = angularVelocity * rx;

                // 球體相對於牆壁的速度
                float relVX = ballVX - wallVX;
                float relVY = ballVY - wallVY;
                float dot = relVX * nx + relVY * ny;

                // 只有在球體正朝牆壁移動時進行反彈計算
                if (dot < 0) {
                    // 反射相對速度 (簡單反射公式)
                    float relVXAfter = relVX - 2 * dot * nx;
                    float relVYAfter = relVY - 2 * dot * ny;
                    // 更新球的速度，加上牆壁的速度
                    ballVX = relVXAfter + wallVX;
                    ballVY = relVYAfter + wallVY;

                    // 防止球體穿透牆面：將球體推回邊界外
                    float overlap = ballRadius - dist;
                    ballX += nx * overlap;
                    ballY += ny * overlap;

                    // 加上摩擦衰減
                    ballVX *= damping;
                    ballVY *= damping;
                }
            }
        }
    }

    // 輔助方法：求點 p 到線段 ab 的最近點
    private PointF getClosestPointOnSegment(PointF a, PointF b, PointF p) {
        float ax = a.x, ay = a.y;
        float bx = b.x, by = b.y;
        float px = p.x, py = p.y;
        float abx = bx - ax, aby = by - ay;
        float t = ((px - ax) * abx + (py - ay) * aby) / (abx * abx + aby * aby);
        t = Math.max(0, Math.min(1, t));
        return new PointF(ax + t * abx, ay + t * aby);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        hexagonRadius = Math.min(w, h) * 0.4f;
        ballX = w / 2f;
        ballY = h / 2f;
        lastTime = System.currentTimeMillis(); // 重置 lastTime
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        // 重置 Path 物件
        hexagonPath.reset();

        // 依據旋轉角度與半徑建立六邊形路徑
        for (int i = 0; i < 6; i++) {
            float angle = (float) (Math.toRadians(60 * i)) + currentRotation;
            float vx = centerX + hexagonRadius * (float) Math.cos(angle);
            float vy = centerY + hexagonRadius * (float) Math.sin(angle);
            if (i == 0) {
                hexagonPath.moveTo(vx, vy);
            } else {
                hexagonPath.lineTo(vx, vy);
            }
        }
        hexagonPath.close();
        canvas.drawPath(hexagonPath, hexagonPaint);

        // 畫出球體
        canvas.drawCircle(ballX, ballY, ballRadius, ballPaint);
    }
}
