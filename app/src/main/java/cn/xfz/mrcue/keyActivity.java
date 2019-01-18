package cn.xfz.mrcue;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

/*
* 实现解锁控件的一些处理操作等
*
*/
public class keyActivity extends AppCompatActivity {
    public String key;
    public boolean aBoolean=true;
    public TextView mText;
    public PatternLockView mPatternLockView;
    public int judge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key);
        mPatternLockView=(PatternLockView)findViewById(R.id.lockview);
        mText=(TextView)findViewById(R.id.testview);
        //判断活动的启动来自哪里
        //并做出不同的响应
        judge=getIntent().getIntExtra("judge",0);
        switch (judge)
        {
            case 1:
                mText.setText("请绘制解锁图案");
                mPatternLockView.addPatternLockListener(mPatternLockViewListener);
                break;
            case 2:
                mText.setText("请绘制解锁图案");
                mPatternLockView.addPatternLockListener(mPatternLockViewListener);
                break;
            case 3:
                mText.setText("请绘制原解锁图案");
                mPatternLockView.addPatternLockListener(mPatternLockViewListener);
                break;
        }
    }
    public void savekey(String key) {
        SharedPreferences.Editor editor = getSharedPreferences("key", MODE_PRIVATE).edit();
        editor.putString("Key", key);
        editor.apply();
    }
    public String getKey()
    {  String s;
        SharedPreferences sharedPreferences=getSharedPreferences("key",MODE_PRIVATE);
        s=sharedPreferences.getString("Key",null);
        return s;
    }
    private PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {

            Log.d(getClass().getName(), "Pattern drawing started");
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
            Log.d(getClass().getName(), "Pattern progress: " +
                    PatternLockUtils.patternToString(mPatternLockView, progressPattern));
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            Log.d(getClass().getName(), "Pattern complete: " +
                    PatternLockUtils.patternToString(mPatternLockView, pattern));
            key=PatternLockUtils.patternToString(mPatternLockView, pattern);
            if(judge==1)
            {
                if(!key.equals(getKey())) {
                    mText.setText("图案错误,请重新绘制");
                }else
                {
                    Toast.makeText(keyActivity.this,"解锁成功",Toast.LENGTH_LONG).show();
                    finish();
                }
            }else if(judge==2)
            {
                if(aBoolean)
                {
                    mText.setText("请确认解锁图案");
                    savekey(key);
                    aBoolean=false;
                }else
                {
                    if(!key.equals(getKey()))
                    {
                        mText.setText("两次图案不一致，请重新输入");
                    }
                    else
                    {
                        Toast.makeText(keyActivity.this,"设置成功",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }else if(judge==3)
            {
                if(key.equals(getKey()))
                {
                    Toast.makeText(keyActivity.this,"清除成功",Toast.LENGTH_SHORT).show();
                    key=null;
                    savekey(key);
                    finish();
                }else
                {
                    mText.setText("图案错误，请重新绘制");
                }
            }
            pattern.clear();
        }
        @Override
        public void onCleared() {
            Log.d(getClass().getName(), "Pattern has been cleared");
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&judge==1)
        {
            return  false;
        }
        return super.onKeyDown(keyCode, event);
    }
}

