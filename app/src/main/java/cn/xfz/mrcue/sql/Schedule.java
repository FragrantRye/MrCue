package cn.xfz.mrcue.sql;

import java.io.Serializable;

public class Schedule implements Serializable {
    private int id = -1;
    private String content;
    private String time;
    private int important;

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public int getImportant() {
        return important;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setImportant(int important) {
        this.important = important;
    }
}
