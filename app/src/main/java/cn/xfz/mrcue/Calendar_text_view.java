package cn.xfz.mrcue;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * 自定义日历中的子项视图
 */
public class Calendar_text_view extends android.support.v7.widget.AppCompatTextView {
    boolean isToday = false;
    boolean isChose = false;
    boolean isEmpty = false;
    static private Paint paintR, paintB, paintPoint;

    static {
        paintR = new Paint();
        paintR.setStrokeWidth(3.0f);
        paintR.setStyle(Paint.Style.STROKE);
        paintR.setColor(Color.RED);

        paintB = new Paint();
        paintB.setStrokeWidth(3.0f);
        paintB.setStyle(Paint.Style.STROKE);
        paintB.setColor(Color.BLUE);

        paintPoint=new Paint();
        paintPoint.setStyle(Paint.Style.FILL);
        paintPoint.setColor(Color.rgb(0.9f, 0.5f,0.5f));
    }

    @Override
    public int getSystemUiVisibility() {
        return super.getSystemUiVisibility();
    }

    public Calendar_text_view(Context context) {
        super(context);
    }

    public Calendar_text_view(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Calendar_text_view(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!isEmpty)
            canvas.drawCircle(getWidth() / 7.0f * 6.0f, getHeight() / 6.0f, getWidth() / 10.0f, paintPoint);
        if (isToday)
            canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, getWidth() / 2.0f, paintR);
        else if (isChose)
            canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, getWidth() / 2.0f, paintB);
    }
}
