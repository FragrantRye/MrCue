package cn.xfz.mrcue;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LookActivity extends AppCompatActivity {
    public TextView mTime;
    public TextView mContent;
    public String content;
    public String time;
    public Button btnback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look);
        mContent=(TextView)findViewById(R.id.accontent);
        mTime=(TextView)findViewById(R.id.acttime);
        content=getIntent().getStringExtra("content");
        time=getIntent().getStringExtra("time");
        mContent.setText(content);
        mTime.setText(time);
        Toast.makeText(this,time,Toast.LENGTH_SHORT).show();
        btnback=(Button)findViewById(R.id.back);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
