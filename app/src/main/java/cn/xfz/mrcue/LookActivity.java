package cn.xfz.mrcue;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.xfz.mrcue.sql.Schedule;

public class LookActivity extends AppCompatActivity implements View.OnClickListener{
    private Schedule sch;
    private Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look);
        Bundle bundle = getIntent().getBundleExtra("data");
        if((sch=(Schedule)bundle.getSerializable("sch_data"))!=null) {
            ((TextView) findViewById(R.id.accontent)).setText(sch.getContent());
            ((TextView) findViewById(R.id.acttime)).setText(sch.getTime());
            TextView im = findViewById(R.id.look_importance);
            switch (sch.getImportant()){
                case 1:
                    im.setText("提示");
                    im.setTextColor(Color.parseColor("#669900"));
                    break;
                case 2:
                    im.setText("一般");
                    im.setTextColor(Color.parseColor("#0099cc"));
                    break;
                case 3:
                    im.setText("重要");
                    im.setTextColor(Color.parseColor("#ff8800"));
                    break;
                case 4:
                    im.setText("紧急");
                    im.setTextColor(Color.parseColor("#cc0000"));
                    break;
            }
            Toast.makeText(this, sch.getTime(), Toast.LENGTH_SHORT).show();
            findViewById(R.id.back).setOnClickListener(this);
            findViewById(R.id.look_delete).setOnClickListener(this);
            findViewById(R.id.look_edit).setOnClickListener(this);
            findViewById(R.id.look_share).setOnClickListener(this);
            findViewById(R.id.look_setAlarm).setOnClickListener(this);
        }else{
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("sch_data", sch);
        intent.putExtra("data", bundle);
        switch(v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.look_delete:
                AlertDialog.Builder bb = new AlertDialog.Builder(this);
                bb.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intent.putExtra("judge", 2);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
                bb.setNegativeButton("取消", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                bb.setMessage("您确定要删除该备忘录吗？");
                bb.setTitle("提示");
                bb.show();
                break;
            case R.id.look_edit:
                intent.putExtra("judge", 3);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.look_share:
                Intent intent1 = new Intent(Intent.ACTION_SEND);
                intent1.setType("text/plain");
                //分享文本内容
                intent1.putExtra(Intent.EXTRA_TEXT, sch.getContent());
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent1, getTitle()));
                break;
            case R.id.look_setAlarm:
                intent.putExtra("judge", 4);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}
