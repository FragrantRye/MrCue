package cn.xfz.mrcue;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NewCalender.NewCalendarListener{
    private TextView txt_curDay;
    private String curDay;
    private List<schedule> sch;
    private TextView nullSch;
    private ListView sch_view;
    private int mPosition=-1;
    private int lvPosition=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt_curDay=(TextView)findViewById(R.id.curday);
        sch=new ArrayList<>();
        NewCalender newCalender=(NewCalender)findViewById(R.id.newCalendar);
        nullSch=(TextView)findViewById(R.id.nullSchedule);
        sch_view=(ListView)findViewById(R.id.schedule_view);

        //判断是否已经设置密码
        SharedPreferences preferences=getSharedPreferences("key",MODE_PRIVATE);
        String key = preferences.getString("Key", null);
        if(key !=null)
        {
            Intent intent=new Intent(this,keyActivity.class);
            intent.putExtra("judge",1);
            startActivity(intent);
        }
        //悬浮按钮
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add);
        newCalender.newCalendarListener=this;
        //设置悬浮按钮监听事件
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mPosition!=-1)
                {
                    Intent intent=new Intent(MainActivity.this, addActivity.class);
                    intent.putExtra("position", mPosition);
                    MainActivity.this.startActivityForResult(intent,1);
                }else
                {
                    Toast.makeText(MainActivity.this,"您未选择日期",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //设置行程ListVIew的子项点击事件
        //点击ListView可跳转到选择Activity（chooseActivity）
        sch_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(MainActivity.this, chooseActivity.class);
                lvPosition=i;
                //intent中带入文本内容，便于分享
                //分享这里只设置分享文本内容，时间不分享
                intent.putExtra("content",sch.get(lvPosition).content);
                MainActivity.this.startActivityForResult(intent,2);
                //设置请求码 启动选择活动
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemPress(Date day,int i) {
        //每次点击日期时都要将sch清空 只显示当天的日程
        sch.clear();
        mPosition=i;
        DateFormat sdf=new SimpleDateFormat("yyyy-MMMd");
        curDay=sdf.format(day)+"日";
        txt_curDay.setText(curDay);
        getData(i);
        if(sch.size()==0)
        {   sch_view.setVisibility(View.GONE);
            nullSch.setVisibility(View.VISIBLE);
        }else
        {   sch_view.setVisibility(View.VISIBLE);
            nullSch.setVisibility(View.GONE);
            schAdapter adapter=new schAdapter(sch,this);
            sch_view.setAdapter(adapter);
        }
    }
    //从数据库获取数据,封装成函数 便于多次调用
    //数据存储与读取采用的是SharedPreference
    private void getData(int position)
    {   int i=0;
        SharedPreferences spf=getSharedPreferences("curData"+position,MODE_PRIVATE);
        while(spf.getString("content"+i,null)!=null&&spf.getString("time"+i,null)!=null)
        {   
            boolean isSame=false;
            schedule sche=new schedule();
            sche.content = spf.getString("content"+i, null);
            sche.time = spf.getString("time"+i, null);
            for (schedule sch1 : sch) {
                if (sch1 == sche) {
                    isSame = true;
                    break;
                }
            }
            if(!isSame)
            {
                sch.add(sche);
            }
            i++;
        }
    }
    //保存数据函数
    private void saveData(int mPosition)
    {
        SharedPreferences.Editor editor=getSharedPreferences("curData"+mPosition,MODE_PRIVATE).edit();
        editor.clear();
        for(int i=0;i<sch.size();i++)
        {
            editor.putString("content" + i, sch.get(i).content);
            editor.putString("time" + i, sch.get(i).time);
        }
        editor.apply();
    }
    //设置闹钟
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void SetAlarm()
    {
        Calendar currentTime=Calendar.getInstance();
        new TimePickerDialog(this,0, new TimePickerDialog.OnTimeSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                //指定启动AlarmActivity的intent
                Intent intent=new Intent(MainActivity.this,AlarmActivity.class);
                //创建pendingIntent
                PendingIntent pi=PendingIntent.getActivity(MainActivity.this,0,intent,0);
                Calendar c=Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());
                c.set(Calendar.HOUR,i);
                c.set(Calendar.MINUTE,i1);
                //获取Alarm对象x
                AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pi);
                Toast.makeText(MainActivity.this,"设置成功",Toast.LENGTH_SHORT).show();
            }
        },currentTime.get(Calendar.DAY_OF_MONTH),currentTime.get(Calendar.MINUTE),false).show();
        /*
        Intent alarms = new Intent(AlarmClock.ACTION_SET_ALARM)
            .putExtra(AlarmClock.EXTRA_MESSAGE, sch.get(lvPosition).content)
            .putExtra(AlarmClock.EXTRA_HOUR, a[0])
            .putExtra(AlarmClock.EXTRA_MINUTES, a[1]);
         startActivity(alarms);*/
    }
    //每次活动返回时数据的处理
    //通过返回的判断值作出不同的响应

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case 1:if(resultCode==RESULT_OK&& !Objects.equals(data.getStringExtra("judge"), "cancel"))
            {
                Bundle bundle=data.getBundleExtra("add");
                sch.add((schedule)bundle.getSerializable("add_data"));
                saveData(mPosition);
                sch_view.setVisibility(View.VISIBLE);
                nullSch.setVisibility(View.GONE);
                schAdapter adapter=new schAdapter(sch,this);
                sch_view.setAdapter(adapter);
            }
                if(resultCode==2)
                {
                    Bundle bundle=data.getBundleExtra("add");
                    sch.remove(lvPosition);
                    sch.add((schedule)bundle.getSerializable("add_data"));
                    saveData(mPosition);
                    sch_view.setVisibility(View.VISIBLE);
                    nullSch.setVisibility(View.GONE);
                    schAdapter adapter=new schAdapter(sch,this);
                    sch_view.setAdapter(adapter);
                }
                break;
            case 2:if(resultCode==RESULT_OK)
            {
                int judge=data.getIntExtra("judge",0);
                switch (judge)
                {
                    case 1:Intent intent=new Intent(MainActivity.this,lookActivity.class);
                        intent.putExtra("time",curDay+sch.get(lvPosition).time);
                        intent.putExtra("content",sch.get(lvPosition).content);
                        startActivity(intent);
                        break;
                    case 2: sch.remove(lvPosition);
                        saveData(mPosition);
                        schAdapter adapter=new schAdapter(sch,this);
                        sch_view.setAdapter(adapter);
                        break;
                    //修改日程
                    case 3:
                        Intent intent1=new Intent(MainActivity.this, addActivity.class);
                        String [] a=sch.get(lvPosition).time.split(":");
                        intent1.putExtra("hour",a[0]);
                        intent1.putExtra("min",a[1]);
                        intent1.putExtra("content",sch.get(lvPosition).content);
                        startActivityForResult(intent1,1);
                        break;
                    //闹钟设置
                    case 4:
                        SetAlarm();
                        break;
                }
            }
                break;
        }
    }
    //引入菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
    //菜单点击事件

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.deletall:
                if(sch.size()==0)
                {
                    Toast.makeText(this,"行程为空",Toast.LENGTH_SHORT).show();
                }
                else {
                    sch.clear();
                    saveData(mPosition);
                    sch_view.setVisibility(View.GONE);
                    nullSch.setVisibility(View.VISIBLE);
                    Toast.makeText(this,"清除成功",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.set_key:
                Intent intent2 = new Intent(MainActivity.this, keyActivity.class);
                intent2.putExtra("judge",2);
                startActivity(intent2);
                break;
            case R.id.delete_key:
                Intent intent3 = new Intent(MainActivity.this, keyActivity.class);
                intent3.putExtra("judge",3);
                startActivity(intent3);
                break;
        }
        return true;
    }
}
