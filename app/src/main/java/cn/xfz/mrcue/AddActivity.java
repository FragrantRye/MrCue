package cn.xfz.mrcue;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import cn.xfz.mrcue.sql.Schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddActivity extends AppCompatActivity {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private EditText ct;
    private Date day=null;
    public TimePicker time_choose;
    private Schedule sche=new Schedule();
    public Button confirm;
    public Button cancel;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //实例化各控件
        ct= findViewById(R.id.content);
        confirm= findViewById(R.id.confirm);
        cancel= findViewById(R.id.cancel);
        time_choose= findViewById(R.id.time_choose);
        Bundle bundle=getIntent().getBundleExtra("data");

        if(bundle!=null && (day=(Date)bundle.getSerializable("date_data"))!=null ){
            time_choose.setHour(day.getHours());
            time_choose.setMinute(day.getMinutes());
        }
        if(bundle!=null && bundle.getSerializable("sch_data")!=null ){
            sche=(Schedule)bundle.getSerializable("sch_data");
            ct.setText(sche.getContent());
            try{
                day=sdf.parse(sche.getTime());
            }catch(ParseException e){
                day=new Date();
            }

        }
        //设置时间选择控件的转换监听
        time_choose.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                day.setHours(i);
                day.setMinutes(i1);
            }
        });
        //
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(day!=null) {
                    sche.setTime(sdf.format(day));
                }
                sche.setContent(ct.getText().toString());
                Intent intent=new Intent();
                Bundle bundle=new Bundle();
                bundle.putSerializable("add_data",sche);
                intent.putExtra("add", bundle);
                if(sche.getId()>=0)
                {
                    setResult(2, intent);
                    finish();
                } //返回的resultCode不同
                else {
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        //
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
  }

}
