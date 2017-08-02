package com.memo.dbUtil;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.memo.bean.MemoBean;
import com.memo.bean.SlidingMenuBean;
import com.memo.databases.MyDatabasesHelper;

import java.util.ArrayList;

/**
 * Created by user on 2017/3/2.
 */
public class DB {

    public static void insertTag(SQLiteDatabase db,ArrayList<SlidingMenuBean> list){
        for(SlidingMenuBean menuBean:list){
            ContentValues values=new ContentValues();
            values.put("title",menuBean.getTitle());
            values.put("image",menuBean.getImage());
            values.put("number",menuBean.getNumber());
            db.insert("tag",null,values);
        }
    }


    public static void getTagResult(SQLiteDatabase db,ArrayList<SlidingMenuBean> list){
        list.clear();
        Cursor cursor=db.query("tag",null,null,null,null,null,null);
        if(cursor!=null) {
            if (cursor.moveToFirst()) {
                do {
                    SlidingMenuBean menuBean=new SlidingMenuBean();
                    menuBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    menuBean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    menuBean.setImage(cursor.getInt(cursor.getColumnIndex("image")));
                    menuBean.setNumber(getMemoNumber(db,"tag_id=?",new String[]{menuBean.getId()+""}));
                    list.add(menuBean);
                }while (cursor.moveToNext());
            }
        }

    }

    //获取标签
    public static ArrayList<SlidingMenuBean>  getTagResult(SQLiteDatabase db){
        ArrayList<SlidingMenuBean> list=new ArrayList<>();
        Cursor cursor=db.query("tag",null,null,null,null,null,null);
        if(cursor!=null) {
            if (cursor.moveToFirst()) {
                do {
                    SlidingMenuBean menuBean=new SlidingMenuBean();
                    menuBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    menuBean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    menuBean.setImage(cursor.getInt(cursor.getColumnIndex("image")));
                    menuBean.setNumber(cursor.getInt(cursor.getColumnIndex("number")));
                    list.add(menuBean);
                }while (cursor.moveToNext());
            }
        }
       return list;
    }

    //更新标签
    public static void UpdateTag(SQLiteDatabase db,SlidingMenuBean menuBean){
        ContentValues values=new ContentValues();
        values.put("title",menuBean.getTitle());
        values.put("image",menuBean.getImage());
        values.put("image",menuBean.getNumber());
        db.update("tag",values,"id=?",new String[]{String.valueOf(menuBean.getId())});

    }

    //保存备忘录
    public static void insertMemo(SQLiteDatabase db, MemoBean memoBean){
        ContentValues values=new ContentValues();
        values.put("content",memoBean.getContent());
        values.put("time",memoBean.getTime());
        values.put("daiban",memoBean.getDaiban());
        values.put("alarm",memoBean.getAlarm());
        values.put("shoucang",memoBean.getShoucahng());
        values.put("tag_id",memoBean.getTagId());
        db.insert("memo",null,values);
    }

    //获取备忘录
    public static ArrayList<MemoBean> getMemoArrayList(SQLiteDatabase db,String selection,String[] selectionAgar){
        ArrayList<MemoBean> list=new ArrayList<>();
        Cursor cursor=db.query("memo",null,selection,selectionAgar,null,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                do{
                    MemoBean memoBean=new MemoBean();
                    memoBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    memoBean.setContent(cursor.getString(cursor.getColumnIndex("content")));
                    memoBean.setTime(cursor.getString(cursor.getColumnIndex("time")));
                    memoBean.setTagId(cursor.getInt(cursor.getColumnIndex("tag_id")));
                    memoBean.setDaiban(cursor.getInt(cursor.getColumnIndex("daiban")));
                    memoBean.setAlarm(cursor.getInt(cursor.getColumnIndex("alarm")));
                    memoBean.setShoucahng(cursor.getInt(cursor.getColumnIndex("shoucang")));
                    list.add(memoBean);
                }while (cursor.moveToNext());
            }
        }
        return list;
    }

    //获取备忘录数据
    public static void getMemoArrayList(SQLiteDatabase db,String selection,String[] selectionAgar,final ArrayList<MemoBean> list){
        list.clear();
        Cursor cursor=db.query("memo",null,selection,selectionAgar,null,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                do{
                    MemoBean memoBean=new MemoBean();
                    memoBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    memoBean.setContent(cursor.getString(cursor.getColumnIndex("content")));
                    memoBean.setTime(cursor.getString(cursor.getColumnIndex("time")));
                    memoBean.setTagId(cursor.getInt(cursor.getColumnIndex("tag_id")));
                    memoBean.setDaiban(cursor.getInt(cursor.getColumnIndex("daiban")));
                    memoBean.setAlarm(cursor.getInt(cursor.getColumnIndex("alarm")));
                    memoBean.setShoucahng(cursor.getInt(cursor.getColumnIndex("shoucang")));
                    list.add(memoBean);
                }while (cursor.moveToNext());
            }
        }

    }

    //获取备忘录的条数
    public static int getMemoNumber(SQLiteDatabase db,String selection,String[] selectionAgar){
        Cursor cursor=db.query("memo",null,selection,selectionAgar,null,null,null);
        int number=0;
        if(cursor!=null){
            if(cursor.moveToFirst()){
                do{
                 number++;
                }while (cursor.moveToNext());
            }
        }
        return number;
    }

    //更新memo
    public static void UpdateMemo(SQLiteDatabase db,MemoBean memoBean){
        ContentValues values=new ContentValues();
        values.put("content",memoBean.getContent());
        values.put("daiban",memoBean.getDaiban());
        values.put("alarm",memoBean.getAlarm());
        values.put("shoucang",memoBean.getShoucahng());
        values.put("tag_id",memoBean.getTagId());
        db.update("memo",values,"id=?",new String[]{String.valueOf(memoBean.getId())});
    }



}
