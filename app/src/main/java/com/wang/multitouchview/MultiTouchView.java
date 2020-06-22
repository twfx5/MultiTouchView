package com.wang.multitouchview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 多点触控demo：两个手指交替移动
 *
 * 事件是针对view的，不是针对point的
 *
 * 每个触摸的点 point(x, y, index, id)
 * x, y 表示坐标
 * index 只是用来遍历point
 * id 用来区分point
 *
 * id会复用：手指抬起时，id被回收，重新按下后，
 * 系统会认为是刚才那个手指重新按下，所以id会再给它
 */
public class MultiTouchView extends View {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final float WIDTH = Utils.dp2px(200);
    private Bitmap bitmap;

    float downX;
    float downY;

    float offsetX;
    float offsetY;

    float originalX;
    float originalY;

    // 当前正在响应的Point的id
    int trackingPointId;

    public MultiTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bitmap = Utils.getAvatar(getResources(), (int) WIDTH);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // getActionMasked()可以获取多点触控的事件
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: // 第一个点被按下 index = 0
                trackingPointId = event.getPointerId(0);
                downX = event.getX(); // event.getX()就是event.getX(0)
                downY = event.getY();
                originalX = offsetX;
                originalY = offsetY;
                break;
            case MotionEvent.ACTION_MOVE: // 手指移动需要跟随当前正在响应的point
                int index = event.findPointerIndex(trackingPointId); // 通过id来获取index
                offsetX = originalX + event.getX(index) - downX; // 通过index获取正在响应点的距离event.getX(index),index为正在响应点的坐标
                offsetY = originalY + event.getY(index) - downY;
                invalidate();
                break;
            case MotionEvent.ACTION_POINTER_DOWN: // 一个point被按下
                int actionIndex = event.getActionIndex(); // 响应的point的index
                trackingPointId = event.getPointerId(actionIndex);
                downX = event.getX(actionIndex);
                downY = event.getY(actionIndex);
                originalX = offsetX;
                originalY = offsetY;
                break;
            case MotionEvent.ACTION_POINTER_UP: // 一个point被抬起
                actionIndex = event.getActionIndex(); // 响应的point的index
                int pointId = event.getPointerId(actionIndex);

                if (pointId == trackingPointId) { // 自己定义的规则：如果抬起的点是正在响应的点，把正在响应的点设置为剩下来最大的点
                    int newIndex;
                    if (actionIndex == event.getPointerCount() - 1) {
                        newIndex = event.getPointerCount() - 2;
                    } else {
                        newIndex = event.getPointerCount() - 1;
                    }
                    trackingPointId = event.getPointerId(newIndex);
                    downX = event.getX(actionIndex);
                    downY = event.getY(actionIndex);
                    originalX = offsetX;
                    originalY = offsetY;
                }
                // 如果抬起的点不是正在响应的点，则不处理
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
