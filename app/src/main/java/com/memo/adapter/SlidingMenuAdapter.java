package com.memo.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.memo.R;
import com.memo.bean.MemoBean;
import com.memo.bean.SlidingMenuBean;
import com.memo.dbUtil.DB;

import java.util.ArrayList;

/**
 * Created by user on 2017/3/2.
 */
public class SlidingMenuAdapter extends BaseAdapter {

    private Context context;
    private ArrayList arrayList;
    private View view_3=null;
    private SQLiteDatabase db=null;

    public SlidingMenuAdapter(Context context, ArrayList<SlidingMenuBean> arrayList, SQLiteDatabase db){
        this.context=context;
        this.arrayList=arrayList;
        this.db=db;
    }

    @Override
    public int getCount(){
        return 9+arrayList.size();
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
    public View getView(int position, View convertView, ViewGroup viewGroup){
        View view=null;
        if(position==0){
            view= LayoutInflater.from(context).inflate(R.layout.list_view_menu_bg,null);
            return view;
        }else if(position==1){
            view=LayoutInflater.from(context).inflate(R.layout.list_view_menu_divider,null);
            TextView textView=(TextView)view.findViewById(R.id.divider);
            textView.setText("备忘录");
            return view;
            //备忘录按钮
        }else if(position==2){
            view=LayoutInflater.from(context).inflate(R.layout.list_view_menu_memo,null);
            TextView number=(TextView)view.findViewById(R.id.number_menu);
            int num=DB.getMemoNumber(db,null,null);
            number.setText(num+"");
            return view;
        }else if((position==3||position==4)||position==5){
            String[] name=new String[]{"待办","提醒","我的收藏"};
            int[] image=new int[]{R.drawable.daiban,R.drawable.alert,R.drawable.shoucang};
            view=LayoutInflater.from(context).inflate(R.layout.list_view_menu_one,null);
            ImageView imageView=(ImageView)view.findViewById(R.id.image_menu);
            TextView textView=(TextView)view.findViewById(R.id.sliding_menu);
            imageView.setImageResource(image[position-3]);
            textView.setText(name[position-3]);
            return view;
        }else if(position==6){
            view=LayoutInflater.from(context).inflate(R.layout.list_view_menu_divider,null);
            TextView textView=(TextView)view.findViewById(R.id.divider);
            textView.setText("标签");
            return view;
        }else if (position==7+arrayList.size()) {
            view = LayoutInflater.from(context).inflate(R.layout.list_view_menu, null);
            ImageView imageView= (ImageView) view.findViewById(R.id.list_view_sliding_image_tag);
            TextView textView= (TextView) view.findViewById(R.id.list_view_sliding_text_tag);
            TextView number= (TextView) view.findViewById(R.id.list_view_sliding_number_tag);
            imageView.setImageResource(R.drawable.ic_tag_none);
            textView.setText("未标签");
            int num=DB.getMemoNumber(db,"tag_id=?",new String[]{"-1"});
            number.setText(num+"");
            return view;
        }else if(position==8+arrayList.size()){
            view=LayoutInflater.from(context).inflate(R.layout.list_view_menu_one,null);
            TextView textView=(TextView)view.findViewById(R.id.sliding_menu);
            textView.setText("新标签");
            textView.setTextColor(Color.parseColor("#1E90FF"));
            TextPaint pt=textView.getPaint();
            pt.setFakeBoldText(true);
            return view;
        }else {
            view = LayoutInflater.from(context).inflate(R.layout.list_view_menu, null);
            ImageView imageView= (ImageView) view.findViewById(R.id.list_view_sliding_image_tag);
            TextView textView= (TextView) view.findViewById(R.id.list_view_sliding_text_tag);
            TextView number= (TextView) view.findViewById(R.id.list_view_sliding_number_tag);

            SlidingMenuBean menuBean= (SlidingMenuBean)arrayList.get(position-7);

            imageView.setImageResource(menuBean.getImage());
            textView.setText(menuBean.getTitle()+"");
            number.setText(menuBean.getNumber()+"");
            view.setTag(menuBean);
            return view;
        }
    }

    class Helper{
        ImageView imageView;
        TextView textView;
        TextView number;
    }

}
