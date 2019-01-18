package cn.xfz.mrcue;

import android.content.Context;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.media.Image;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.util.Preconditions;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Date;

import static android.R.attr.data;

/**
 * Created by weizewei on 17-10-6.
 * 自定义日历控件
 * 继承与线性布局
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class NewCalender extends LinearLayout {
    private int chose=-1;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grid;
    private ArrayList<Date> cells=new ArrayList<>();
    NewCalendarListener newCalendarListener;
    private Calendar curDate=Calendar.getInstance();  //minsdk api:24
    public NewCalender(Context context) {
        super(context);
    }

    public NewCalender(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initControl(context);
        bindControlEvent();
        renderCalendar();
    }

    public NewCalender(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context);
    }
    private void initControl(Context context)
    {
        bindControl(context);
    }
    //绑定各控件
    private void bindControl(Context context)
    {
        LayoutInflater inflater=LayoutInflater.from(context);
        inflater.inflate(R.layout.calender_view,this);
        btnPrev=findViewById(R.id.btnPrev);
        btnNext=findViewById(R.id.btnNext);
        txtDate=findViewById(R.id.txtDate);
        grid=findViewById(R.id.calender_grid);
    }
    //设置前后按钮监听事件
    //便于日历月份的变化处理
    private void bindControlEvent()
    {
        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                    curDate.add(Calendar.MONTH,-1);
                  renderCalendar();
            }
        });
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                curDate.add(Calendar.MONTH,+1);
                renderCalendar();
            }
        });
    }
    //渲染函数 对日历控件进行渲染
    private  void renderCalendar()
    {
        SimpleDateFormat sdf=new SimpleDateFormat("MMM YYYY");
        txtDate.setText(sdf.format(curDate.getTime()));
        Calendar calendar=(Calendar) curDate.clone();  //复制一个curDate
        calendar.set(Calendar.DAY_OF_MONTH,1);
        int preDays=calendar.get(Calendar.DAY_OF_WEEK)-1;
        calendar.add(Calendar.DAY_OF_MONTH,-preDays);
        int maxCellCount=6*7;
        cells.clear();
        while(cells.size()<maxCellCount)
        {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH,1);
        }
        grid.setAdapter(new CalendarAdapter(getContext(),cells));
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                chose=i;
                newCalendarListener.onItemPress((Date)adapterView.getItemAtPosition(i),i);
                renderCalendar();
            }
        });

    }
    private class CalendarAdapter extends ArrayAdapter<Date>
    {
           LayoutInflater inflater;
        CalendarAdapter(@NonNull Context context, ArrayList<Date> days) {
            super(context,R.layout.calendar_day,days);
            inflater=LayoutInflater.from(context);
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Date date=getItem(position);
            if(convertView==null)
            {
                convertView = inflater.inflate(R.layout.calendar_day, parent, false);
            }
            int day=date.getDate();
            ((TextView)convertView).setText(String.valueOf(day));
            //设置字体颜色
            Calendar calendar=(Calendar) curDate.clone();
            calendar.set(Calendar.DAY_OF_MONTH,1);
            ((Calendar_text_view)convertView).isChose=(position==chose);

            Date now=new Date();
            if(now.getDate()==date.getDate( )&& now.getMonth()==date.getMonth() && now.getYear()==date.getYear())//如果即将渲染的日期是今天
            {
                ((Calendar_text_view)convertView).setTextColor(Color.RED);//红色
                ((Calendar_text_view)convertView).isToday=true;
            }else{
                if(curDate.get(Calendar.MONTH)==date.getMonth())//如果即将渲染的日期属于当前月
                {
                    ((Calendar_text_view)convertView).setTextColor(Color.BLACK);//黑色
                }else
                {
                    ((Calendar_text_view)convertView).setTextColor(Color.LTGRAY);//灰色
                }
            }
             return convertView;
        }
    }
    //自定义接口 在主活动中可调用
    //设置了position参数 便于对所选日期位置进行记录
    //方便日程的保存
    public  interface  NewCalendarListener
    {
        void onItemPress(Date day, int position);
    }
}
