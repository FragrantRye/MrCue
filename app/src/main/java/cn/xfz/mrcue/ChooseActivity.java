package cn.xfz.mrcue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseActivity extends Activity implements View.OnClickListener {
    /*
     *此活动时通过startActivityforresult启动的
     * 故只会根据用户的选择返回不同的判断值
     * 然后具体在逻辑处理仍旧时在MainActivity中处理
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        findViewById(R.id.alarm_set).setOnClickListener(this);
        findViewById(R.id.delete).setOnClickListener(this);
        findViewById(R.id.modify).setOnClickListener(this);
        findViewById(R.id.look).setOnClickListener(this);
        findViewById(R.id.share).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.look:
                //返回判断值1
                intent.putExtra("judge", 1);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.delete:
                //返回判断值2
                intent.putExtra("judge", 2);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.modify:
                //返回判断值3
                intent.putExtra("judge", 3);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.share:
                Intent intent1 = new Intent(Intent.ACTION_SEND);
                intent1.setType("text/plain");
                //分享文本内容
                intent1.putExtra(Intent.EXTRA_TEXT, getIntent().getStringExtra("content"));
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent1, getTitle()));
                break;
            case R.id.alarm_set:
                intent.putExtra("judge", 4);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}
