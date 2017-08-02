package com.memo.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.memo.R;
import com.memo.bean.SlidingMenuBean;
import com.memo.dbUtil.CheckAction;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 2017/3/6.
 */
public class DeleteAdapter extends BaseAdapter {

    private Context context;
    private int sourceId;
    private ArrayList<SlidingMenuBean> arrayList;
    private CheckAction checkAction;
    private boolean[] checks=null;  //保存CheckBox状态
    private CheckBox[] checkBoxes=null;//保存CheckBox;
    public DeleteAdapter(Context context, int sourceId, ArrayList<SlidingMenuBean> arrayList, CheckAction checkAction){
        this.context=context;
        this.sourceId=sourceId;
        this.arrayList=arrayList;
        this.checkAction=checkAction;
        checks=new boolean[arrayList.size()];
        checkBoxes=new CheckBox[arrayList.size()];
        for(int i=0;i<arrayList.size();i++){
            checks[i]=false;
        }
    }

    @Override
    public int getCount(){
        return arrayList.size();
    }

    @Override
    public Object getItem(int position){
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup){
        View view= LayoutInflater.from(context).inflate(sourceId,null);
        ImageView imageView=(ImageView) view.findViewById(R.id.delete_tag_list_view_image);
        TextView textView=(TextView)view.findViewById(R.id.delete_tag_list_view_text);
        SlidingMenuBean menuBean=(SlidingMenuBean)getItem(position);
        imageView.setImageResource(menuBean.getImage());
        textView.setText(menuBean.getTitle());
        final int pos=position;
        final CheckBox checkBox=(CheckBox)view.findViewById(R.id.delete_tag_checkBox);
        checkBox.setChecked(checks[position]);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkAction.action(isChecked);
                checks[pos]=isChecked;
            }
        });

        checkBox.setTag(menuBean);
        return view;
    }







}
