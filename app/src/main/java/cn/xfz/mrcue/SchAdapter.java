package cn.xfz.mrcue;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.xfz.mrcue.sql.Schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * ListVIew的适配器
 * 作出适配
 */
public class SchAdapter extends BaseAdapter {
    private Schedule[] scharray;
    private LayoutInflater inflater;
    private static SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    private static SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm", Locale.CHINA);
    public TextView time;
    public TextView content;
    public Context context;

    SchAdapter(Schedule[] sch, Context context) {
        this.context = context;
        scharray = sch;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return scharray.length;
    }

    @Override
    public Object getItem(int i) {
        return scharray[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = inflater.inflate(R.layout.listview_item, viewGroup, false);
        }
        time = view.findViewById(R.id.time);
        switch (scharray[i].getImportant()) {
            case 1:
                time.setTextColor(Color.parseColor("#669900"));
                break;
            case 2:
                time.setTextColor(Color.parseColor("#0099cc"));
                break;
            case 3:
                time.setTextColor(Color.parseColor("#ff8800"));
                break;
            case 4:
                time.setTextColor(Color.parseColor("#cc0000"));
                break;
        }
        content = view.findViewById(R.id.content_show);
        Date temp;
        try {
            temp = sdf0.parse(scharray[i].getTime());
        } catch (ParseException e) {
            temp = new Date();
        }
        time.setText(sdf1.format(temp));
        content.setEllipsize(TextUtils.TruncateAt.END);
        content.setLineSpacing(1, 1);
        content.setMaxLines(2);
        content.setText(scharray[i].getContent());
        return view;
    }
}
