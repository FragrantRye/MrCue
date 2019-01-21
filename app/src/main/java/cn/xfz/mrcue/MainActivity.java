package cn.xfz.mrcue;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import cn.xfz.mrcue.sql.SQLUtil;
import cn.xfz.mrcue.sql.Schedule;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NewCalender.NewCalendarListener {
    private Context thisContext;
    private TextView txt_curDay;
    private String curDay;
    private Schedule[] sch;
    private TextView nullSch;
    private ListView sch_view;
    private Date mDate = null;
    private int lvPosition = -1;
    private static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
    private SQLUtil connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext=this;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        setContentView(R.layout.activity_main);
        txt_curDay = findViewById(R.id.curday);
        sch = new Schedule[0];
        connection = new SQLUtil(this);
        NewCalender newCalender = findViewById(R.id.newCalendar);
        nullSch = findViewById(R.id.nullSchedule);
        sch_view = findViewById(R.id.schedule_view);

        //判断是否已经设置密码
        SharedPreferences preferences = getSharedPreferences("key", MODE_PRIVATE);
        String key = preferences.getString("Key", null);
        if (key != null) {
            Intent intent = new Intent(this, KeyActivity.class);
            intent.putExtra("judge", 1);
            startActivity(intent);
        }

        newCalender.newCalendarListener = this;
        //设置悬浮按钮监听事件
        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDate != null) {
                    Intent intent = new Intent(MainActivity.this, AddActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("date_data", mDate);
                    intent.putExtra("data", bundle);
                    MainActivity.this.startActivityForResult(intent, 1);
                } else {
                    Toast.makeText(MainActivity.this, "您未选择日期", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //设置行程ListVIew的子项点击事件
        //点击ListView可跳转到选择Activity（ChooseActivity）
        sch_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, ChooseActivity.class);
                lvPosition = i;
                //intent中带入文本内容，便于分享
                //分享这里只设置分享文本内容，时间不分享
                intent.putExtra("content", sch[lvPosition].getContent());
                MainActivity.this.startActivityForResult(intent, 2);
                //设置请求码 启动选择活动
            }
        });
        sch_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l){
                lvPosition = i;
                AlertDialog.Builder bb = new AlertDialog.Builder(thisContext);
                bb.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeData(sch[lvPosition]);
                        sch=getData(mDate);
                        SchAdapter adapter = new SchAdapter(sch, thisContext);
                        sch_view.setAdapter(adapter);
                    }
                });
                bb.setNegativeButton("取消", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                bb.setMessage("您确定要删除该备忘录吗？");
                bb.setTitle("提示");
                bb.show();
                return true;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemPress(Date day) {
        //每次点击日期时都要将sch清空 只显示当天的日程
        sch = new Schedule[0];
        mDate = day;
        DateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        curDay = sdf.format(day);
        txt_curDay.setText(curDay);
        sch = getData(mDate);
        if (sch.length == 0) {
            sch_view.setVisibility(View.GONE);
            nullSch.setVisibility(View.VISIBLE);
        } else {
            sch_view.setVisibility(View.VISIBLE);
            nullSch.setVisibility(View.GONE);
            SchAdapter adapter = new SchAdapter(sch, this);
            sch_view.setAdapter(adapter);
        }
    }

    //从数据库获取数据,封装成函数 便于多次调用
    //数据存储与读取采用的是SharedPreference
    private Schedule[] getData(Date date)//int position)
    {
        return connection.Search(date);
    }

    //保存或更新，依Schedule.id是否为正判断
    private void saveData(Schedule s) {
        if(s!=null) {
            if (s.getId() < 0) {
                connection.Insert(s.getContent(), s.getTime(), s.getImportant());
            } else {
                connection.Update(s.getContent(), s.getTime(), s.getImportant(), s.getId());
            }
        }
    }

    private void removeData(Schedule s) {
        if (s.getId() >= 0)
            connection.Delete(s.getId());
    }

    //设置闹钟
    private void SetAlarm(Schedule s) {
        Date date;
        try {
            date = sdf.parse(s.getTime());
        }catch(ParseException e){
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Intent alarms = new Intent(AlarmClock.ACTION_SET_ALARM)
            .putExtra(AlarmClock.EXTRA_MESSAGE, s.getContent())
            .putExtra(AlarmClock.EXTRA_HOUR, calendar.get(Calendar.HOUR_OF_DAY))
            .putExtra(AlarmClock.EXTRA_MINUTES, calendar.get(Calendar.MINUTE));
         startActivity(alarms);
    }

    //每次活动返回时数据的处理
    //通过返回的判断值作出不同的响应
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if ((resultCode == RESULT_OK && !"cancel".equals(data.getStringExtra("judge"))) || resultCode == 2) {
                    Bundle bundle = data.getBundleExtra("add");
                    saveData((Schedule) bundle.getSerializable("add_data"));
                    sch = getData(mDate);
                    sch_view.setVisibility(View.VISIBLE);
                    nullSch.setVisibility(View.GONE);
                    SchAdapter adapter = new SchAdapter(sch, this);
                    sch_view.setAdapter(adapter);
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    int judge = data.getIntExtra("judge", 0);
                    switch (judge) {
                        case 1:
                            Intent intent = new Intent(MainActivity.this, LookActivity.class);
                            intent.putExtra("time", curDay + sch[lvPosition].getTime());
                            intent.putExtra("content", sch[lvPosition].getContent());
                            startActivity(intent);
                            break;
                        case 2:
                            removeData(sch[lvPosition]);
                            sch = getData(mDate);
                            SchAdapter adapter = new SchAdapter(sch, this);
                            sch_view.setAdapter(adapter);
                            break;
                        //修改日程
                        case 3:
                            Intent intent1 = new Intent(MainActivity.this, AddActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("sch_data", sch[lvPosition]);
                            intent1.putExtra("data", bundle);
                            startActivityForResult(intent1, 1);
                            break;
                        //闹钟设置
                        case 4:
                            SetAlarm(sch[lvPosition]);
                            break;
                    }
                }
                break;
        }
    }

    //引入菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    //菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deletall:
                if (sch.length == 0) {
                    Toast.makeText(this, "行程为空", Toast.LENGTH_SHORT).show();
                } else {
                    for (Schedule sch1 : sch)
                        removeData(sch1);
                    sch = new Schedule[0];
                    sch_view.setVisibility(View.GONE);
                    nullSch.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "清除成功", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.set_key:
                Intent intent2 = new Intent(MainActivity.this, KeyActivity.class);
                intent2.putExtra("judge", 2);
                startActivity(intent2);
                break;
            case R.id.delete_key:
                Intent intent3 = new Intent(MainActivity.this, KeyActivity.class);
                intent3.putExtra("judge", 3);
                startActivity(intent3);
                break;
        }
        return true;
    }
}
