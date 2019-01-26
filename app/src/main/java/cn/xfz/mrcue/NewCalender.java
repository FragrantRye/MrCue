package cn.xfz.mrcue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.*;
import android.widget.*;
import cn.xfz.mrcue.sql.SQLUtil;


import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by weizewei on 17-10-6.
 * 自定义日历控件
 * 继承与线性布局
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class NewCalender extends LinearLayout implements GestureDetector.OnGestureListener {
    private int chose = -1;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grid;
    private ArrayList<Date> cells = new ArrayList<>();
    NewCalendarListener newCalendarListener;
    private Calendar curDate = Calendar.getInstance();
    private GestureDetector gesture_detector;
    private SQLUtil connection;

    public NewCalender(Context context) {
        super(context);
        initControl(context);
        renderCalendar();
    }

    public NewCalender(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initControl(context);
        renderCalendar();
    }

    public NewCalender(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context);
        renderCalendar();
    }

    private void initControl(Context context) {
        connection = new SQLUtil(context);
        //绑定各控件
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.calender_view, this);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        txtDate = findViewById(R.id.txtDate);
        grid = findViewById(R.id.calender_grid);
        gesture_detector=new GestureDetector(context, this);
        bindControlEvent();
    }

    @Override
    public boolean onDown(MotionEvent e) { return false; }

    @Override
    public void onShowPress(MotionEvent e) { }

    @Override
    public boolean onSingleTapUp(MotionEvent e) { return false; }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }

    @Override
    public void onLongPress(MotionEvent e) {}

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getX() - e2.getX() >80 && Math.abs(velocityX)>200) {
            btnNext.performClick();
        } else if (e2.getX()- e1.getX() > 80 && Math.abs(velocityX)>200) {
            btnPrev.performClick();
        }
        return true;
    }

    //设置监听事件
    //便于日历月份的变化处理
    @SuppressLint("ClickableViewAccessibility")
    private void bindControlEvent() {
        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                curDate.add(Calendar.MONTH, -1);
                newCalendarListener.onItemPress(curDate.getTime());
                renderCalendar();
                if(chose>=0)
                    grid.performItemClick(grid.getChildAt(chose),chose,grid.getItemIdAtPosition(chose));
            }
        });
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                curDate.add(Calendar.MONTH, +1);
                newCalendarListener.onItemPress(curDate.getTime());
                renderCalendar();
                if(chose>=0)
                    grid.performItemClick(grid.getChildAt(chose),chose,grid.getItemIdAtPosition(chose));
            }
        });
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                chose = i;
                newCalendarListener.onItemPress((Date) adapterView.getItemAtPosition(i));
                renderCalendar();
            }
        });
        grid.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP)
                    grid.performClick();
                return gesture_detector.onTouchEvent(event);
            }
        });
    }

    //渲染函数 对日历控件进行渲染
    public void renderCalendar() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM YYYY", Locale.CHINA);
        txtDate.setText(sdf.format(curDate.getTime()));
        Calendar calendar = (Calendar) curDate.clone();  //复制一个curDate
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int preDays = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.add(Calendar.DAY_OF_MONTH, -preDays);
        int maxCellCount = 6 * 7;
        cells.clear();
        while (cells.size() < maxCellCount) {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        CalendarAdapter adapter = new CalendarAdapter(getContext(), cells);
        grid.setAdapter(adapter);
    }

    private class CalendarAdapter extends ArrayAdapter<Date> {
        LayoutInflater inflater;

        CalendarAdapter(@NonNull Context context, ArrayList<Date> days) {
            super(context, R.layout.calendar_day, days);
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Date date = getItem(position);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.calendar_day, parent, false);
            }
            ((TextView) convertView).setText(String.valueOf(date.getDate()));
            //设置字体颜色
            Calendar calendar = (Calendar) curDate.clone();
            calendar.set(Calendar.DAY_OF_MONTH, 1);

            Date now = new Date();
            if (now.getDate() == date.getDate() && now.getMonth() == date.getMonth() && now.getYear() == date.getYear())//如果即将渲染的日期是今天
            {
                ((Calendar_text_view) convertView).setTextColor(Color.RED);//红色
                ((Calendar_text_view) convertView).isToday = true;
            } else if(position == chose) {
                ((Calendar_text_view) convertView).setTextColor(Color.BLUE);//蓝色
                ((Calendar_text_view) convertView).isChose = true;
            } else {
                if (curDate.get(Calendar.MONTH) == date.getMonth())//如果即将渲染的日期属于当前月
                {
                    ((Calendar_text_view) convertView).setTextColor(Color.BLACK);//黑色
                } else {
                    ((Calendar_text_view) convertView).setTextColor(Color.LTGRAY);//灰色
                }
            }

            ((Calendar_text_view) convertView).mostImportant =connection.getMostImportant(date);
            return convertView;
        }
    }

    //自定义接口 在主活动中可调用
    //方便日程的保存
    public interface NewCalendarListener {
        void onItemPress(Date day);
    }
}
