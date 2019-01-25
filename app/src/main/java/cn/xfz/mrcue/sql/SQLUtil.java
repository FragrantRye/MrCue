package cn.xfz.mrcue.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SQLUtil {
    private static String[] columns={"_id", "content", "create_time", "important_level"};
    private SQLiteDatabase db;
    public SQLUtil(Context c) {
        db = SQLiteDatabase.openOrCreateDatabase(c.getDatabasePath("mrcue.db"),null);
        if(db!=null){
            db.execSQL("create table if not exists sch(_id INTEGER primary key autoincrement, content TEXT, create_time TEXT, important_level INTEGER)");
        }
    }

    public int Insert(String content, String date, int important){
        try {
            ContentValues cValue = new ContentValues();
            cValue.put("content", content);
            cValue.put("create_time", date);
            cValue.put("important_level", important);
            db.insert("sch", null, cValue);
        }catch (Exception e){
            return -1;
        }
        return 0;
    }

    public int Update(String content, String date, int important, int id){
        try {
            ContentValues values = new ContentValues();
            values.put("content", content);
            values.put("create_time", date);
            values.put("important_level", important);
            String whereClause = "_id=?";
            String[] whereArgs={Integer.toString(id)};
            db.update("sch", values, whereClause, whereArgs);
        }catch (Exception e){
            return -1;
        }
        return 0;
    }
    public int Delete(int id){
        try {
        String whereClause = "_id=?";
        String[] whereArgs = {String.valueOf(id)};
        db.delete("sch", whereClause, whereArgs);
        }catch (Exception e){
            return -1;
        }
        return 0;
    }
    public boolean isEmptyDay(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String[] temp = {sdf.format(date)+"%"};
        Cursor c = db.query("sch",columns,"create_time like ?", temp,null,null,"create_time");
        return c.getCount()==0;
    }

    public Schedule[] Search(String key){
        String[] temp = {"%"+key+"%"};
        Cursor c = db.query("sch",columns,"content like ?", temp,null,null,"create_time");
        return getResult(c);
    }
    public Schedule[] Search(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String[] temp = {sdf.format(date)+"%"};
        Cursor c = db.query("sch",columns,"create_time like ?", temp,null,null,"create_time");
        return getResult(c);
    }

    private Schedule[] getResult(Cursor c) {
        ArrayList<Schedule> schs=new ArrayList<>();
        c.moveToFirst();
        while(!c.isAfterLast()){
            Schedule s=new Schedule();
            s.setId(c.getInt(0));
            s.setContent(c.getString(1));
            s.setTime(c.getString(2));
            s.setImportant(c.getInt(3));
            schs.add(s);
            c.moveToNext();
        }
        c.close();
        Schedule[] re = new Schedule[schs.size()];
        schs.toArray(re);
        return re;
    }
}
