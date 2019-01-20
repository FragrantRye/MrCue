package cn.xfz.mrcue;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AlarmActivity extends AppCompatActivity {
    private MediaPlayer alarmmusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarmmusic = MediaPlayer.create(this, R.raw.s);
        alarmmusic.setLooping(true);
        new AlertDialog.Builder(AlarmActivity.this).setTitle("闹钟")
                .setMessage("您当前有一个日程").setPositiveButton("confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alarmmusic.stop();
                AlarmActivity.this.finish();
            }
        }).show();
    }
}
