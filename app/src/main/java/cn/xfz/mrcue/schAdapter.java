package cn.xfz.mrcue;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;

/**
 * Created by weizewei on 17-10-6.
 * ListVIew的适配器
 * 作出适配
 */
public class schAdapter extends BaseAdapter {
    private List<schedule> schlist;
    private LayoutInflater inflater;
    public TextView time;
    public TextView content;
    public Button btnDel;
    public Context context;
    public schAdapter(List<schedule> sch, Context context)
    {
        this.context=context;
        schlist=sch;
        inflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return schlist.size();
    }

    @Override
    public Object getItem(int i) {
        return schlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(view==null)
        {
            view=inflater.inflate(R.layout.listview_item,viewGroup,false);
        }
        time=(TextView)view.findViewById(R.id.time);
        content=(TextView)view.findViewById(R.id.content);
        btnDel=(Button)view.findViewById(R.id.delete);
        time.setText(schlist.get(i).time);
        content.setEllipsize(TextUtils.TruncateAt.END);
        content.setLineSpacing(1,1);
        content.setMaxLines(2);
        content.setText(schlist.get(i).content);
        return view;
    }
}
