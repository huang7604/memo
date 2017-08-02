package com.memo.bean;

import android.app.ProgressDialog;

import java.io.Serializable;

/**
 * Created by user on 2017/3/1.
 */
public class MemoBean implements Serializable {
    private int id;
    private String content;
    private String time;
    private int alarm=0;
    private int daiban=0;
    private int shoucahng=0;
    private int tagId=-1;



    public MemoBean(){

    }

    public void setId(int id){
        this.id=id;
    }

    public int getId(){
        return id;
    }

    public void setContent(String content){
        this.content=content;
    }

    public String getContent(){
        return content;
    }

    public void setTime(String time){
        this.time=time;
    }

    public String getTime(){
        return time;
    }

    public void setAlarm(int alarm){
        this.alarm=alarm;
    }
    public int getAlarm(){
        return alarm;
    }

    public void setDaiban(int daiban){
        this.daiban=daiban;
    }
    public int getDaiban(){
        return daiban;
    }


    public void setShoucahng(int shoucahng){
        this.shoucahng=shoucahng;
    }
    public int getShoucahng(){
        return shoucahng;
    }

    public void setTagId(int tagId){
        this.tagId=tagId;
    }

    public int getTagId(){
        return tagId;
    }
}
