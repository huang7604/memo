package com.memo.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.memo.R;
import com.memo.bean.SlidingMenuBean;
import com.memo.dbUtil.UI;
import com.memo.dbUtil.UiAction;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by user on 2017/3/8.
 */
public class selectTagAdapter extends BaseAdapter {

    private Context context=null;
    private int sourceId;
    private static int isSelect=2;
    private AlertDialog dialog=null;
    private Button button=null;
    private ArrayList<SlidingMenuBean> arrayList;
    private boolean isClose=false;
    public selectTagAdapter(Context context, int sourceId, ArrayList<SlidingMenuBean> list){
        this.context=context;
        this.sourceId=sourceId;
        this.arrayList=list;
    }

    @Override
    public int getCount(){
        if(arrayList!=null) {
            return arrayList.size()+3;
        }else {
            return 3;
        }
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
    public View getView(final int position, final View convertView, ViewGroup viewGroup){


        if(position==2){
            View view = LayoutInflater.from(context).inflate(sourceId, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.select_tag_list_view_image);
            TextView textView = (TextView) view.findViewById(R.id.select_tag_list_view_text);
            final RadioButton radioButton=(RadioButton)view.findViewById(R.id.select_tag_radioButton);
            imageView.setImageResource(R.drawable.ic_tag_none);
            textView.setText("未标签");
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isSelect=position;
                    isClose=true;
                    notifyDataSetChanged();
                }
            });

            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        button.setBackgroundResource(R.drawable.ic_tag_none);
                        button.setTag(null);
                        if(isClose){
                            dialog.dismiss();
                        }
                    }
                }
            });
            if(isSelect==position){
                radioButton.setChecked(true);
            }
            return view;
        }else if(position==arrayList.size()+1) {
            View view=LayoutInflater.from(context).inflate(R.layout.list_view_menu_one,null);
            TextView textView=(TextView)view.findViewById(R.id.sliding_menu);
            textView.setText("新标签");
            textView.setTextColor(Color.parseColor("#1E90FF"));
            TextPaint pt=textView.getPaint();
            pt.setFakeBoldText(true);
            return view;
        }else if(position==arrayList.size()+2) {
            final View view=LayoutInflater.from(context).inflate(R.layout.list_view_button,null);
            Button button=(Button)view.findViewById(R.id.select_tag_dialog_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                }
            });
            return view;
        }else {
            View view = LayoutInflater.from(context).inflate(sourceId, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.select_tag_list_view_image);
            TextView textView = (TextView) view.findViewById(R.id.select_tag_list_view_text);
            final RadioButton radioButton=(RadioButton)view.findViewById(R.id.select_tag_radioButton);

            if(position>2){
                final SlidingMenuBean menuBean = (SlidingMenuBean) arrayList.get(position-1);
                imageView.setImageResource(menuBean.getImage());
                textView.setText(menuBean.getTitle());
                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            button.setBackgroundResource(menuBean.getImage());
                            button.setTag(menuBean);
                            if(isClose){
                                dialog.dismiss();
                            }
                        }
                    }
                });
            }else {
                final SlidingMenuBean menuBean=(SlidingMenuBean) arrayList.get(position);
                imageView.setImageResource(menuBean.getImage());
                textView.setText(menuBean.getTitle());
                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            button.setBackgroundResource(menuBean.getImage());
                            button.setTag(menuBean);
                            if(isClose){
                                dialog.dismiss();
                            }
                        }
                    }
                });
            }


            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isSelect=position;
                    isClose=true;
                    notifyDataSetChanged();

                }
            });

             if(isSelect==position){
                 radioButton.setChecked(true);
             }


            return view;
        }

    }

    public static void setIsSelect(int position){
        isSelect=position;
    }

    public void setDialog(AlertDialog dialog){
        this.dialog=dialog;
    }

    public void setButton(Button button){
        this.button=button;

    }

    public void setClose(boolean isClose){
        this.isClose=isClose;
    }



}
