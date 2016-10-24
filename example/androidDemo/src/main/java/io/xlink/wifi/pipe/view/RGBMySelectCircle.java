package io.xlink.wifi.pipe.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 
 * @Title: RGBMyRedCircle.java
 * @Package io.xlink.home.view
 * @Description: TODO(常用RGB灯用戶选择圆)
 * @author chendh
 * @date 2014-7-15 上午11:06:31
 */
public class RGBMySelectCircle extends View {
    int color = Color.RED;

    public RGBMySelectCircle(Context context, AttributeSet attrs) {
	super(context, attrs);
	// TODO Auto-generated constructor stub
    }

    Paint redpaint = new Paint();

    protected void onDraw(Canvas canvas) {
	super.onDraw(canvas);
	// // 外围的白色空心圆
	// Paint circlep = new Paint();
	// circlep.setAntiAlias(true);
	// circlep.setColor(getResources().getColor(R.color.circlewhite));
	// circlep.setStyle(Paint.Style.STROKE);
	// circlep.setStrokeWidth(2);
	// canvas.drawCircle(dpsize, dpsize, dpsize, circlep);

	// 红色实心圆

	redpaint.setAntiAlias(true);
	redpaint.setColor(color);
	redpaint.setStyle(Paint.Style.FILL);
	canvas.drawCircle(radius, radius, radius, redpaint);

    }

    int w;
    int h;
    int radius;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	// TODO Auto-generated method stub
	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	// // setMeasuredDimension(measureWidth(widthMeasureSpec),
	// // measureHeight(heightMeasureSpec));
	// int widthMode = MeasureSpec.getMode(widthMeasureSpec);
	// int heightMode = MeasureSpec.getMode(heightMeasureSpec);
	// int widthSize = MeasureSpec.getSize(widthMeasureSpec);
	// int heightSize = MeasureSpec.getSize(heightMeasureSpec);
	// if (widthMode == MeasureSpec.EXACTLY) {
	// w = widthSize;
	// } else if (widthMode == MeasureSpec.AT_MOST) {
	// h = changR(getContext());
	// } else {
	// w = changR(getContext());
	// }
	// if (heightMode == MeasureSpec.EXACTLY) {
	// h = heightSize;
	// } else if (heightMode == MeasureSpec.AT_MOST) {
	// h = changR(getContext());
	// } else {
	// h = changR(getContext());
	// }
	// if (w > h) {
	// radius = h;
	// } else {
	// radius = w;
	// }
	w = changR(getContext());
	h = w;
	radius = w;
	setMeasuredDimension(w * 2, h);
    }

    // 根据不同分辨率画不同大小的圆
    public int changR(Context context) {
	float scale = context.getResources().getDisplayMetrics().density;
	if (scale > 1) {
	    return 33;
	} else if (scale < 1) {
	    return 26;
	} else {
	    return 30;
	}

    }

    public void setColor(int c) {
	this.color = c;
	invalidate();
    }

}
