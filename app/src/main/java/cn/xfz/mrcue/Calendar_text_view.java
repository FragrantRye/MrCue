package cn.xfz.mrcue;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by weizewei on 17-10-6.
 * 自定义日历中的子项视图
 */
public class Calendar_text_view extends android.support.v7.widget.AppCompatTextView {
    public  boolean isToday=false;
    public Paint paint;
    @Override
    public int getSystemUiVisibility() {
        return super.getSystemUiVisibility();
    }

    public Calendar_text_view(Context context) {
        super(context);
    }

    public Calendar_text_view(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initControl();
    }

    public Calendar_text_view(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl();
    }
    private void initControl()
    {
        paint=new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#FF0000"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isToday)
        {
            canvas.translate(getWidth()/2,getHeight()/2);
           canvas.drawCircle(0,0,getWidth()/2,paint);
        }
    }
}
