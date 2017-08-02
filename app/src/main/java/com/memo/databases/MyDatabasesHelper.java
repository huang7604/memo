package com.memo.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.memo.R;
import com.memo.bean.SlidingMenuBean;
import com.memo.dbUtil.DB;

import java.util.ArrayList;

/**
 * Created by user on 2017/3/1.
 */
public class MyDatabasesHelper extends SQLiteOpenHelper {

    public static final String CREATE_TAG_BOOK="create table tag(" +
            "id integer primary key autoincrement," +
            "title text," +
            "image integer," +
            "number integer)";
    public static final String CREATE_MEMO_BOOK="create table memo(" +
            "id integer primary key autoincrement," +
            "content text," +
            "time text," +
            "daiban integer default '0',"+
            "alarm integer default '0',"+
            "shoucang integer default '0',"+
            "tag_id integer," +
            "foreign key(tag_id) references tag(id))";

    private Context context;
    public MyDatabasesHelper(Context context, String name, SQLiteDatabase.CursorFactory cursorFactory,int version){
        super(context,name,cursorFactory,version);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TAG_BOOK);
        db.execSQL(CREATE_MEMO_BOOK);
        initTag(db);
    }

    @Override
    public  void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){

    }

    private void initTag(SQLiteDatabase db){
        ArrayList<SlidingMenuBean> list=new ArrayList<SlidingMenuBean>();
        String[] name=new String[]{"个人","生活","工作","旅游"};
        int[] color=new int[]{R.drawable.ic_tag_yellow,R.drawable.ic_tag_red,R.drawable.ic_tag_blue,R.drawable.ic_tag_green};
        for(int i=0;i<name.length;i++){
            SlidingMenuBean menuBean=new SlidingMenuBean();
            menuBean.setTitle(name[i]);
            menuBean.setImage(color[i]);
            menuBean.setNumber(0);
            list.add(menuBean);
        }
        DB.insertTag(db,list);
    }

}
