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


    public static List<Rect> measureRect(int w, int h, int leftNum, int topNum, int rightNum, int bottomNum, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom, int inset) {
        List<Rect> list = new ArrayList<>();
        // left
        if (leftNum > 0) {
            float sampleSize = (h - paddingTop - paddingBottom) * 1F / leftNum;
            for (int i = leftNum - 1; i >= 0; i--) {
                int x = inset;
                int y = (int) (paddingTop + i * sampleSize);
                list.add(new Rect(x, y, (int)(x + sampleSize), (int)(y + sampleSize)));
            }
        }
        Log.d("fkdlsaf:", "" + list.size());
        // top
        if (topNum > 0) {
            float sampleSize = (w - paddingLeft - paddingRight) * 1F / topNum;
            for (int i = 0; i < topNum; i++) {
                int x = (int) (paddingLeft + sampleSize * i);
                int y = inset;
                list.add(new Rect(x, y, (int)(x + sampleSize), (int)(y + sampleSize)));
            }
        }
        Log.d("fkdlsaf:", "" + list.size());
        // right
        if (rightNum > 0) {
            float sampleSize = (h - paddingTop - paddingBottom) * 1F / rightNum;
            for (int i = 0; i < rightNum; i++) {
                int x = (int) (w - sampleSize - inset - 1);
                int y = (int) (paddingTop + i * sampleSize);
                list.add(new Rect(x, y, (int)(x + sampleSize), (int)(y + sampleSize)));
            }
        }
        Log.d("fkdlsaf:", "" + list.size());
        // bottom
        if (bottomNum > 0) {
            float sampleSize = (w - paddingLeft - paddingRight) * 1F / bottomNum;
            for (int i = bottomNum - 1; i >= 0; i--) {
                int x = (int) (paddingLeft + sampleSize * i);
                int y = (int) (h - sampleSize - inset - 1);
                list.add(new Rect(x, y, (int)(x + sampleSize), (int)(y + sampleSize)));
            }
        }
        Log.d("fkdlsaf:", "" + list.size());
        return list;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        list = measureRect(getMeasuredWidth(), getMeasuredHeight(), 40, 60, 40, 60,40,40,40,40,0);
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
        paint.setStrokeWidth(2);
        for (Rect rect : list) {
            canvas.drawRect(rect, paint);
        }
    }
}
