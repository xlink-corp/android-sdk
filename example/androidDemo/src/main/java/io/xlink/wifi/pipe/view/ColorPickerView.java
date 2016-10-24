package io.xlink.wifi.pipe.view;

import io.xlink.wifi.pipe.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * @author LiuXinYi
 * @Date 2015年5月21日 下午2:44:49
 * @Description [自定义取色器]
 * @version 1.0.0
 */
public class ColorPickerView extends ImageView {
    Context context;
    private Bitmap iconBitMap;
    float iconRadius;// 图片圆的半径
    PointF iconPoint;// 点击位置坐标

    public ColorPickerView(Context context) {
	this(context, null);
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	this.context = context;
	init();
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
	this(context, attrs, 0);

    }

    Paint mBitmapPaint;
    Bitmap imageBitmap;
    float radius;

    /**
     * 初始化画笔
     */
    private void init() {
	iconBitMap = BitmapFactory.decodeResource(context.getResources(),
		R.drawable.icon_pickcolor);// 吸管的图片
	iconRadius = iconBitMap.getWidth() / 2;
	mBitmapPaint = new Paint();
	iconPoint = new PointF();
	imageBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
	radius = imageBitmap.getHeight() / 2;
	// // 初始化
	iconPoint.x = radius;
	iconPoint.y = radius;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	// TODO Auto-generated method stub
	super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
	// TODO Auto-generated method stub
	super.onDraw(canvas);
	if (isVisibilityIcon) {
	    canvas.drawBitmap(iconBitMap, iconPoint.x - iconRadius, iconPoint.y
		    - iconRadius, mBitmapPaint);
	}
    }

    boolean isVisibilityIcon = false;

    /**
     * 设置小图片是否隐藏
     * 
     * @param v
     */
    public void setIconVisibility(int v) {
	switch (v) {
	case View.VISIBLE:
	    isVisibilityIcon = true;
	    break;
	default:
	    isVisibilityIcon = false;
	    break;
	}
	invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
	float x = event.getX();
	float y = event.getY();
	int pixel;
	int r;
	int g;
	int b;
	switch (event.getAction()) {
	case MotionEvent.ACTION_MOVE:
	    proofLeft(x, y);
	    pixel = getImagePixel(iconPoint.x, iconPoint.y);
	    r = Color.red(pixel);
	    g = Color.green(pixel);
	    b = Color.blue(pixel);
	    if (mChangedListener != null) {
		mChangedListener.onMoveColor(r, g, b);
	    }
	    if (isMove && isVisibilityIcon) {
		isMove = !isMove;
		invalidate();
	    }
	    break;
	case MotionEvent.ACTION_UP:
	    pixel = getImagePixel(iconPoint.x, iconPoint.y);
	    r = Color.red(pixel);
	    g = Color.green(pixel);
	    b = Color.blue(pixel);
	    if (mChangedListener != null) {
		mChangedListener.onColorChanged(r, g, b);
	    }
	    break;

	default:
	    break;
	}
	return true;
    }

    public int getImagePixel(float x, float y) {

	Bitmap bitmap = imageBitmap;
	// 为了防止越界
	int intX = (int) x;
	int intY = (int) y;
	if (intX < 0)
	    intX = 0;
	if (intY < 0)
	    intY = 0;
	if (intX >= bitmap.getWidth()) {
	    intX = bitmap.getWidth() - 1;
	}
	if (intY >= bitmap.getHeight()) {
	    intY = bitmap.getHeight() - 1;
	}
	int pixel = bitmap.getPixel(intX, intY);
	return pixel;

    }

    /**
     * R = sqrt(x * x + y * y)<br>
     * point.x = x * r / R + r <br>
     * point.y = y * r / R + r
     */
    private void proofLeft(float x, float y) {

	float h = x - radius; // 取xy点和圆点 的三角形宽
	float h2 = h * h;
	float w = y - radius;// 取xy点和圆点 的三角形长
	float w2 = w * w;
	float distance = (float) Math.sqrt((h2 + w2)); // 勾股定理求 斜边距离

	if (distance > radius) { // 如果斜边距离大于半径，则取点和圆最近的一个点为x,y
	    float maxX = x - radius;
	    float maxY = y - radius;
	    x = ((radius * maxX) / distance) + radius; // 通过三角形一边平行原理求出x,y
	    y = ((radius * maxY) / distance) + radius;
	}
	if (x < 0) {
	    iconPoint.x = 0;
	} else if (x > (imageBitmap.getWidth())) {
	    iconPoint.x = imageBitmap.getWidth();
	} else {
	    iconPoint.x = x;
	}
	if (y < 0) {
	    iconPoint.y = 0;
	} else if (y > (imageBitmap.getHeight() - 0)) {
	    iconPoint.y = imageBitmap.getHeight() - 0;
	} else {
	    iconPoint.y = y;
	}
	isMove = true;
    }

    boolean isMove;

    public void setOnColorChangedListenner(OnColorChangedListener l) {
	this.mChangedListener = l;
    }

    private OnColorChangedListener mChangedListener;

    // 内部接口 回调颜色 rgb值
    public interface OnColorChangedListener {
	// 手指抬起，确定颜色回调
	void onColorChanged(int r, int g, int b);

	// 移动时颜色回调
	void onMoveColor(int r, int g, int b);
    }

}
