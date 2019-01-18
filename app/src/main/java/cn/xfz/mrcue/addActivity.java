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

import java.util.List;

public class addActivity extends AppCompatActivity {
    private EditText ct;
    public TimePicker time_choose;
    private schedule sche=new schedule();
    public Button confirm;
    public Button cancel;
    private Boolean aBoolean=false;
    private int hour;
    private int min;
    //判断来自修改还是增加
    private Boolean bBoolean;
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
        //作出作出判断，如果传入intent有数据，则是更改日程
        bBoolean=getIntent().getStringExtra("hour")!=null&&getIntent().getStringExtra("min")!=null;
        if(bBoolean)
        {   hour=Integer.parseInt(getIntent().getStringExtra("hour"));
            min=Integer.parseInt(getIntent().getStringExtra("min"));
            time_choose.setHour(hour);
            time_choose.setMinute(min);
            ct.setText(getIntent().getStringExtra("content"));
        }
        //设置时间选择控件的转换监听
        time_choose.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                sche.time=i+":"+i1+"";
                aBoolean=true;
            }
        });
        //
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!aBoolean)
                {
                    sche.time=hour+":"+min;
                }
                else
                {
                    aBoolean=false;
                }
                sche.content=ct.getText().toString();
                Intent intent=new Intent();
                Bundle bundle=new Bundle();
                bundle.putSerializable("add_data",sche);
                intent.putExtra("add",bundle);
                if(bBoolean)
                {
                    setResult(2,intent);
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
                setResult(RESULT_CANCELED,intent);
                finish();
            }
        });
  }

}
