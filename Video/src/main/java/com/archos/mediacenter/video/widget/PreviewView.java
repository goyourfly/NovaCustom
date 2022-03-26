package com.archos.mediacenter.video.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.archos.mediacenter.video.leanback.settings.VideoSettingsWledActivity;

import java.util.ArrayList;
import java.util.List;

public class PreviewView extends View {
    public PreviewView(Context context) {
        super(context);
    }

    public PreviewView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PreviewView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private List<Rect> list = new ArrayList();


    public static List<Rect> measureRect(int w, int h, int leftNum, int topNum, int rightNum, int bottomNum,
                                         int paddingLeft, int paddingTop, int paddingRight, int paddingBottom,
                                         int leftOffset,int topOffset,int rightOffset,int bottomOffset) {
        List<Rect> list = new ArrayList<>();
        // left
        if (leftNum > 0) {
            float sampleSize = (h - paddingTop - paddingBottom) * 1F / leftNum;
            for (int i = leftNum - 1; i >= 0; i--) {
                int x = leftOffset;
                int y = (int) (paddingTop + i * sampleSize);
                list.add(new Rect(x, y, (int)(x + sampleSize), (int)(y + sampleSize)));
            }
        }
        // top
        if (topNum > 0) {
            float sampleSize = (w - paddingLeft - paddingRight) * 1F / topNum;
            for (int i = 0; i < topNum; i++) {
                int x = (int) (paddingLeft + sampleSize * i);
                int y = topOffset;
                list.add(new Rect(x, y, (int)(x + sampleSize), (int)(y + sampleSize)));
            }
        }
        // right
        if (rightNum > 0) {
            float sampleSize = (h - paddingTop - paddingBottom) * 1F / rightNum;
            for (int i = 0; i < rightNum; i++) {
                int x = (int) (w - sampleSize - rightOffset - 1);
                int y = (int) (paddingTop + i * sampleSize);
                list.add(new Rect(x, y, (int)(x + sampleSize), (int)(y + sampleSize)));
            }
        }
        // bottom
        if (bottomNum > 0) {
            float sampleSize = (w - paddingLeft - paddingRight) * 1F / bottomNum;
            for (int i = bottomNum - 1; i >= 0; i--) {
                int x = (int) (paddingLeft + sampleSize * i);
                int y = (int) (h - sampleSize - bottomOffset - 1);
                list.add(new Rect(x, y, (int)(x + sampleSize), (int)(y + sampleSize)));
            }
        }
        return list;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        VideoSettingsWledActivity.WledInfo info = VideoSettingsWledActivity.read();
        list = measureRect(getMeasuredWidth(), getMeasuredHeight(), info.leftNum, info.topNum, info.rightNum, info.bottomNum,
                info.leftPadding,info.topPadding,info.rightPadding,info.bottomPadding,info.leftOffset,info.topOffset,info.rightOffset,info.bottomOffset);
    }

    public void setList(List<Rect> list) {
        this.list.clear();
        this.list.addAll(list);
        postInvalidate();
    }

    private Paint paint = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(0x44FF0000);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        for (Rect rect : list) {
            canvas.drawRect(rect, paint);
        }
    }
}
