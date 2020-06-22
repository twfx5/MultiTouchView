package com.wang.multitouchview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 *
 * 单点触控demo
 *
 * 事件序列是针对view的，不是在、针对手指或point的
 *
 * getX()和getY()总是返回index=0的点的坐标
 */
public class SingleTouchView extends View {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final float WIDTH = Utils.dp2px(200);
    private Bitmap bitmap;

    float downX;
    float downY;

    float offsetX;
    float offsetY;

    // 手按下时，图的位置在哪
    float originalX;
    float originalY;

    public SingleTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bitmap = Utils.getAvatar(getResources(), (int) WIDTH);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // getActionMasked()可以获取多点触控的事件
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                // 按下时，图片已经移到到哪儿了，比写在ACTION_UP里面保险，因为不知道程序会发生什么
                // 所以在第二次ACTION_DOWN里面记录
                originalX = offsetX;  // 记录下初始x
                originalY = offsetY; // 记录下初始y
                break;
            case MotionEvent.ACTION_MOVE:
                offsetX = originalX + event.getX() - downX; //偏移的距离 = 初始位置 + 移动位置 - 按下的位置
                offsetY = originalY + event.getY() - downY;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, offsetX, offsetY, paint);
    }
}
