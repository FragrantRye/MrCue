package cn.xfz.mrcue;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LookActivity extends AppCompatActivity {
    public String content;
    public String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look);
        content = getIntent().getStringExtra("content");
        time = getIntent().getStringExtra("time");
        ((TextView) findViewById(R.id.accontent)).setText(content);
        ((TextView) findViewById(R.id.acttime)).setText(time);
        Toast.makeText(this, time, Toast.LENGTH_SHORT).show();
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
