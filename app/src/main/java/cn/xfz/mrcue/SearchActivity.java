package cn.xfz.mrcue;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import cn.xfz.mrcue.sql.SQLUtil;
import cn.xfz.mrcue.sql.Schedule;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    private Schedule[] sch;
    private Button button;
    private EditText text;
    private ListView list;
    private SQLUtil connection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        connection=new SQLUtil(this);
        button=findViewById(R.id.search_button);
        button.setOnClickListener(this);
        text=findViewById(R.id.editText2);
        sch=new Schedule[0];
        list=findViewById(R.id.schedule_view2);
        list.setAdapter(new SchAdapter(sch, this));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchActivity.this, LookActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("sch_data", sch[i]);
                intent.putExtra("data", bundle);
                SearchActivity.this.startActivityForResult(intent, 2);
                //设置请求码 启动选择活动
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.search_button:
                String key=text.getText().toString();
                sch=connection.Search(key);
                list.setAdapter(new SchAdapter(sch, this));
                break;
        }
    }
}
