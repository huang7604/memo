package com.memo.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.memo.R;
import com.memo.bean.MemoBean;

import com.memo.bean.SlidingMenuBean;
import com.memo.databases.MyDatabasesHelper;
import com.memo.dbUtil.DB;
import com.memo.dbUtil.UI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by user on 2017/3/28.
 */
public class DisplayMemoActivity extends Activity implements View.OnClickListener {

    //添加标签
    private Button add_tag_button=null;
    //记录选择的标签
    private int tag;
    //时间
    private TextView textView_time=null;
    //返回键
    private Button button_back=null;
    private LinearLayout linearLayout=null;
    //数据库
    private SQLiteDatabase db;
    //线程池
    private Executor executor= Executors.newSingleThreadExecutor();
    private MemoBean memoBean=null;
    //异步
    private Handler handler=new Handler(){
       @Override
        public void handleMessage(Message message){
           int i=message.what;
           View view=(View)message.obj;
           linearLayout.addView(view,i);
       }
    };
    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.display_memo);


        //获取添加标签按钮
        add_tag_button=(Button)findViewById(R.id.add_memo_tag);
        //获取时间显示
        textView_time=(TextView)findViewById(R.id.add_memo_time);
        //获取返回键
        button_back=(Button)findViewById(R.id.memo_button_back);

        linearLayout=(LinearLayout)findViewById(R.id.add_memo_linearlayout);
        db=new MyDatabasesHelper(DisplayMemoActivity.this,"mymemo.db",null,1).getReadableDatabase();

        //按钮监听
        button_back.setOnClickListener(this);

        Intent intent=getIntent();
        memoBean=(MemoBean)intent.getSerializableExtra("memoBean");

        initOnTouchButton();
        //初始化listview
        initLinearLayout(memoBean);
        initButton();
        state();

        NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(1);
    }
    private void initButton(){
        Cursor cursor=db.rawQuery("select * from tag where id='"+memoBean.getTagId()+"'",null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
               SlidingMenuBean slidingMenuBean=new SlidingMenuBean();
                slidingMenuBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
                slidingMenuBean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                slidingMenuBean.setImage(cursor.getInt(cursor.getColumnIndex("image")));
                slidingMenuBean.setNumber(cursor.getInt(cursor.getColumnIndex("number")));
                add_tag_button.setTag(slidingMenuBean);
                add_tag_button.setBackgroundResource(slidingMenuBean.getImage());
            }
        }
    }

    //初始化linear
    private void initLinearLayout(final MemoBean memoBean){

        textView_time.setText(memoBean.getTime());
        final String content_array=memoBean.getContent();
        Runnable task=new Runnable() {
            @Override
            public void run() {
                String[] array=content_array.split("&");
                for(int i=0;i<array.length;i++){
                    switch (array[i].split("=")[0]){
                        case "alarm_time":{
                            View view_alarm=LayoutInflater.from(DisplayMemoActivity.this).inflate(R.layout.add_memo_time,null);
                            TextView textView = (TextView) view_alarm.findViewById(R.id.add_memo_alarm_time);
                            Button button=(Button) view_alarm.findViewById(R.id.add_memo_alarm_delete);
                            button.setVisibility(View.INVISIBLE);
                            String alarm_time=array[i].split("=")[1];
                            textView.setText(alarm_time);
                            Message message=new Message();
                            message.what=i+1;
                            message.obj=view_alarm;
                            handler.sendMessage(message);
                            break;
                        }
                        case "daiban":{
                            View view_daiban=LayoutInflater.from(DisplayMemoActivity.this).inflate(R.layout.add_memo_radio,null);
                            CheckBox checkBox=(CheckBox) view_daiban.findViewById(R.id.add_memo_checkbox);
                            final EditText editText=(EditText)view_daiban.findViewById(R.id.radio_edit_text);
                            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if(isChecked){
                                        editText.setTextColor(Color.parseColor("#bebaba"));
                                        Paint pt=editText.getPaint();
                                        pt.setStrikeThruText(true);

                                    }else {
                                        editText.setTextColor(Color.parseColor("#000000"));
                                        Paint pt=editText.getPaint();
                                        pt.setStrikeThruText(false);
                                    }
                                }
                            });
                            String daiban=array[i].split("=")[1];
                            String s=daiban.split("■")[0];
                            String isCheck=daiban.split("■")[1];
                            if(isCheck.equals("true")){
                                checkBox.setChecked(true);
                            }
                            editText.setText(s);
                            editText.setFocusable(false);
                            editText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    editText.setFocusable(true);
                                    editText.setFocusableInTouchMode(true);
                                    editText.requestFocus();
                                }
                            });
                            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if(hasFocus){
                                        Intent intent=new Intent(DisplayMemoActivity.this,AddMemoActivity.class);
                                        intent.putExtra("memoBean",(Serializable) memoBean);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                            Message message=new Message();
                            message.what=i+1;
                            message.obj=view_daiban;
                            handler.sendMessage(message);
                            break;
                        }
                        case "image":{
                            View view_image=LayoutInflater.from(DisplayMemoActivity.this).inflate(R.layout.add_memo_image,null);
                            ImageView imageView=(ImageView) view_image.findViewById(R.id.add_memo_image);
                            String image=array[i].split("=")[1];
                            Bitmap bitmap=BitmapFactory.decodeFile(image);
                            imageView.setImageBitmap(bitmap);
                            imageView.setTag(image);
                            Message message=new Message();
                            message.what=i+1;
                            message.obj=view_image;
                            handler.sendMessage(message);
                            break;
                        }
                        case "content":{
                            View view= LayoutInflater.from(DisplayMemoActivity.this).inflate(R.layout.add_textvie,null);
                            final EditText editText=(EditText)view.findViewById(R.id.add_memo_content);
                            String s=array[i].split("=")[1];
                            editText.setText(s);
                            editText.setFocusable(false);
                            editText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    editText.setFocusable(true);
                                    editText.setFocusableInTouchMode(true);
                                    editText.requestFocus();
                                }
                            });
                            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if(hasFocus){
                                        Intent intent=new Intent(DisplayMemoActivity.this,AddMemoActivity.class);
                                        intent.putExtra("memoBean",(Serializable) memoBean);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                            Message message=new Message();
                            message.what=i+1;
                            message.obj=view;
                            handler.sendMessage(message);
                            break;
                        }

                    }
                }

                View view= LayoutInflater.from(DisplayMemoActivity.this).inflate(R.layout.add_textvie,null);
                final EditText editText=(EditText)view.findViewById(R.id.add_memo_content);
                editText.setFocusable(false);
                editText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setFocusable(true);
                        editText.setFocusableInTouchMode(true);
                        editText.requestFocus();
                    }
                });
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(hasFocus){
                            Intent intent=new Intent(DisplayMemoActivity.this,AddMemoActivity.class);
                            intent.putExtra("memoBean",(Serializable) memoBean);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                Message message=new Message();
                message.what=array.length+1;
                message.obj=view;
                handler.sendMessage(message);
            }
        };
        executor.execute(task);


    }

    //触摸监听
    private void initOnTouchButton(){
        add_tag_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    UI.createTagDialog(DisplayMemoActivity.this,add_tag_button);
                }
                return false;
            }
        });
    }

    //导航栏和状态栏
    private void state(){
        getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
        getWindow().setNavigationBarColor(Color.parseColor("#ffffff"));
    }

    private void onBack(){
        StringBuffer content=new StringBuffer();
        for(int i=1;i<linearLayout.getChildCount();i++){
            LinearLayout layout=(LinearLayout) linearLayout.getChildAt(i);
            if(layout.getChildCount()==3){
                TextView textView=(TextView) layout.getChildAt(1);
                String s="alarm_time="+textView.getText().toString()+"&";
                content.append(s);
                memoBean.setAlarm(1);
            }else if(layout.getChildCount()==2){
                EditText editText=(EditText)layout.getChildAt(1);
                CheckBox checkBox=(CheckBox)layout.getChildAt(0);
                String isChecked="";
                if(checkBox.isChecked()){
                    isChecked="true";
                }else {
                    isChecked="false";
                }
                String s="daiban="+editText.getText().toString()+"■"+isChecked+"&";
                content.append(s);
                memoBean.setDaiban(1);

            }else if(layout.getChildCount()==1){
                View view1=layout.getChildAt(0);
                if(view1 instanceof EditText){
                    EditText editText=(EditText)layout.getChildAt(0);
                    if(editText.getText().toString()!=null&&!"".equals(editText.getText().toString())){
                        String s = "content=" + editText.getText().toString() + "&";
                        content.append(s);
                    }

                }else if(view1 instanceof ImageView){
                    ImageView imageView=(ImageView)layout.getChildAt(0);
                    String s="image="+imageView.getTag().toString()+"&";
                    content.append(s);
                }
            }
        }

        memoBean.setContent(content.toString());
        SlidingMenuBean slidingMenuBean=(SlidingMenuBean)add_tag_button.getTag();
        if(slidingMenuBean!=null){
            memoBean.setTagId(slidingMenuBean.getId());
        }else{
            memoBean.setTagId(-1);
        }

        //保存备忘录数据
        if(!"".equals(content.toString())&&content.toString()!=null) {
            DB.UpdateMemo(db,memoBean);
            //Toast.makeText(this,content,Toast.LENGTH_LONG).show();
        }
        Intent intent=new Intent(DisplayMemoActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }



    @Override
    public void onBackPressed(){
        super.onBackPressed();
        onBack();
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.memo_button_back:{
                onBack();
                break;
            }
        }
    }

    //活动重新可见
    @Override
    public void onRestart(){
        super.onRestart();
        //UI.createTagDialog(AddMemoActivity.this,add_tag_button);
    }


}


