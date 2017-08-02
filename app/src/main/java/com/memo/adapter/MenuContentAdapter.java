package com.memo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by user on 2017/3/1.
 */
public class MenuContentAdapter extends BaseAdapter {

    private Context context;
    private int sourceId;
    private ArrayList arrayList;
    public MenuContentAdapter(Context context,int sourceId,ArrayList arrayList){
        this.context=context;
        this.sourceId=sourceId;
        this.arrayList=arrayList;
    }

    @Override
    public int getCount(){
        return arrayList.size();
    }

    @Override
    public Object getItem(int position){
        return null;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup){
        return null;
    }

    class Helper{
        TextView textView_content;
        TextView textView_time;
        CheckBox checkBox;
    }

}
