package com.memo.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.memo.R;
import com.memo.bean.MemoBean;
import com.memo.bean.SlidingMenuBean;
import com.memo.broadcast.AlarmBroadcast;
import com.memo.databases.MyDatabasesHelper;
import com.memo.dbUtil.DB;
import com.memo.dbUtil.GridViewUtil;
import com.memo.dbUtil.UI;
import com.memo.service.AlarmService;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by user on 2017/3/8.
 */
public class AddMemoActivity extends Activity implements View.OnClickListener {

    private GridView gridView=null;
    //添加标签
    private Button add_tag_button=null;
    //记录选择的标签
    private int tag;
    //时间
    private TextView textView_time=null;
    //时间格式化
    SimpleDateFormat format=null;
    //返回键
    private Button button_back=null;
    //位置闹钟是否存在
    private boolean isAlarm=false;
    //备忘录保存按钮
    private Button button_save=null;
   //时间
    private String time;
    private LinearLayout linearLayout=null;
    private EditText content=null;

    //图片路径
    private Uri imageUri=null;

    //数据库
    private SQLiteDatabase db;
    private Executor executor= Executors.newSingleThreadExecutor();
    //上一个活动传递的数据
    private MemoBean intent_memoBean=null;
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
        setContentView(R.layout.add_memo);

        //获取gridView
        gridView=(GridView)findViewById(R.id.add_memo_gridView);
        //获取添加标签按钮
        add_tag_button=(Button)findViewById(R.id.add_memo_tag);
        //获取时间显示
        textView_time=(TextView)findViewById(R.id.add_memo_time);
        //获取返回键
        button_back=(Button)findViewById(R.id.memo_button_back);
        //保存按钮
        button_save=(Button)findViewById(R.id.add_memo_save);
        linearLayout=(LinearLayout)findViewById(R.id.add_memo_linearlayout);
        db=new MyDatabasesHelper(AddMemoActivity.this,"mymemo.db",null,1).getReadableDatabase();
        format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time=format.format(new Date());
        textView_time.setText(time);


        //按钮监听
        button_back.setOnClickListener(this);
        button_save.setOnClickListener(this);
        //初始化gridView
        initGridView();
        initGridViewListener();
        //初始化add_tag_button
        initOnTouchButton();
        //初始化listview
        Intent intent=getIntent();
        intent_memoBean=(MemoBean) intent.getSerializableExtra("memoBean");
        if(intent_memoBean!=null){
            initLinearLayout(intent_memoBean);
            initButton();
        }else {
            initLinearLayout();
        }
        state();

    }

    //初始化linear
    private void initLinearLayout(){
        final View view= LayoutInflater.from(AddMemoActivity.this).inflate(R.layout.add_textvie,null);
        content=(EditText)view.findViewById(R.id.add_memo_content);
        linearLayout.addView(view,1);
        initContentListener(content);
    }
     //监听content
    private void initContentListener(final EditText editText_content){
        //监听按键变化
        editText_content.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction()==KeyEvent.ACTION_DOWN) {
                    if(keyCode==KeyEvent.KEYCODE_DEL) {
                        if (editText_content.getSelectionStart() == 0&&UI.getCurrentCursorLine(editText_content)==1) {
                            if (linearLayout.getChildCount() == 2) {

                            } else {
                                LinearLayout layout = (LinearLayout) linearLayout.getChildAt(linearLayout.getChildCount() - 2);
                                if (layout.getChildCount() == 2) {
                                    //Toast.makeText(AddMemoActivity.this, layout.getChildCount() + "", Toast.LENGTH_LONG).show();
                                    EditText editText = (EditText) layout.getChildAt(1);
                                    editText.setFocusable(true);
                                    editText.setFocusableInTouchMode(true);
                                    editText.requestFocus();
                                    editText.setSelection(editText.getText().toString().trim().length());
                                } else if (layout.getChildCount() == 1) {
                                    View view_one =layout.getChildAt(0);
                                    if(view_one instanceof EditText){
                                        EditText editText = (EditText) layout.getChildAt(0);
                                        editText.setFocusable(true);
                                        editText.setFocusableInTouchMode(true);
                                        editText.requestFocus();
                                        editText.setSelection(editText.getText().toString().trim().length());
                                    }else if(view_one instanceof  ImageView){
                                        ImageView imageView=(ImageView)layout.getChildAt(0);
                                        String path=(String) imageView.getTag();
                                        if(path.indexOf("MyMemo")!=-1){
                                            File file=new File(path);
                                            file.delete();
                                        }
                                        linearLayout.removeView(layout);
                                    }

                                }
                            }
                        }
                    }
                }
                return false;
            }
        });

    }

    private void initLinearLayout(MemoBean memoBean){
        textView_time.setText(memoBean.getTime());
        final String content_array=memoBean.getContent();
        Runnable task=new Runnable() {
            @Override
            public void run() {
                String[] array=content_array.split("&");
                for(int i=0;i<array.length;i++){
                    switch (array[i].split("=")[0]){
                        case "alarm_time":{
                            final View view_alarm=LayoutInflater.from(AddMemoActivity.this).inflate(R.layout.add_memo_time,null);
                            TextView textView = (TextView) view_alarm.findViewById(R.id.add_memo_alarm_time);
                            Button button=(Button) view_alarm.findViewById(R.id.add_memo_alarm_delete);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    linearLayout.removeView(view_alarm);
                                    linearLayout.setTag(null);
                                }
                            });
                            String alarm_time=array[i].split("=")[1];
                            textView.setText(alarm_time);
                            Message message=new Message();
                            message.what=i+1;
                            message.obj=view_alarm;
                            handler.sendMessage(message);
                            isAlarm=true;
                            break;
                        }
                        case "daiban":{
                            final View view_daiban=LayoutInflater.from(AddMemoActivity.this).inflate(R.layout.add_memo_radio,null);
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

                            //输入动作监听
                            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                @Override
                                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                    //忽略回车符
                                    return (event.getKeyCode()==KeyEvent.KEYCODE_ENTER);
                                }
                            });
                            //键盘监听
                            editText.setOnKeyListener(new View.OnKeyListener() {
                                @Override
                                public boolean onKey(View v, int keyCode, KeyEvent event) {
                                    if(event.getAction()==KeyEvent.ACTION_DOWN){
                                        if(keyCode==KeyEvent.KEYCODE_DEL){

                                            if(editText.getSelectionStart()==0){
                                                linearLayout.removeView(view_daiban);
                                                content.setFocusable(true);
                                                content.setFocusableInTouchMode(true);
                                                content.requestFocus();
                                                content.setSelection(content.getText().toString().trim().length());
                                            }
                                        }else if(keyCode==KeyEvent.KEYCODE_ENTER){
                                            String s=editText.getText().toString().trim();
                                            editText.setText(s);
                                            content.setFocusable(true);
                                            content.setFocusableInTouchMode(true);
                                            content.requestFocus();
                                            if(content.getText().toString()=="") {
                                                content.setSelection(0);
                                            }else {
                                                content.setSelection(content.getText().toString().trim().length());
                                            }
                                        }
                                    }
                                    return false;
                                }
                            });

                            String daiban=array[i].split("=")[1];
                            String s=daiban.split("■")[0];
                            String isCheck=daiban.split("■")[1];
                            if(isCheck.equals("true")){
                                checkBox.setChecked(true);
                            }
                            editText.setText(s);

                            Message message=new Message();
                            message.what=i+1;
                            message.obj=view_daiban;
                            handler.sendMessage(message);
                            break;
                        }
                        case "image":{
                            View view_image=LayoutInflater.from(AddMemoActivity.this).inflate(R.layout.add_memo_image,null);
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
                            final View view= LayoutInflater.from(AddMemoActivity.this).inflate(R.layout.add_textvie,null);
                            final EditText editText=(EditText)view.findViewById(R.id.add_memo_content);
                            String s=array[i].split("=")[1];
                            editText.setText(s);
                            editText.setOnKeyListener(new View.OnKeyListener() {
                                @Override
                                public boolean onKey(View v, int keyCode, KeyEvent event) {
                                    if(event.getAction()==KeyEvent.ACTION_DOWN) {
                                        if (keyCode == KeyEvent.KEYCODE_DEL) {
                                            if (editText.getSelectionStart() == 0) {
                                                linearLayout.removeView(view);
                                                content.setFocusable(true);
                                                content.setFocusableInTouchMode(true);
                                                content.requestFocus();
                                            }
                                        }
                                    }
                                    return false;
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

                View view= LayoutInflater.from(AddMemoActivity.this).inflate(R.layout.add_textvie,null);
                content=(EditText)view.findViewById(R.id.add_memo_content);
                initContentListener(content);
                Message message=new Message();
                message.what=array.length+1;
                message.obj=view;
                handler.sendMessage(message);
            }
        };
        executor.execute(task);


    }

    //初始化gridview
    private void initGridView(){
        String[] title=new String[]{"待办","提醒","图片","相机"};
        int[] image=new int[]{R.drawable.daiban_blue,R.drawable.alert_blue,R.drawable.image,R.drawable.camera};
        ArrayList<HashMap<String,Object>> data=null;
        data=GridViewUtil.getGridViewArrayList(title,image,new String[]{"image","title"});
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setAdapter(new SimpleAdapter(this,data,R.layout.grid_view_menu,new String[]{"image","title"},new int[]{R.id.image_item,R.id.text_item}));
    }

    //监听gridview
    private void initGridViewListener(){
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                switch (position){
                    case 0:{
                        if(getCurrentFocus()==content) {
                            UI.createEditView(AddMemoActivity.this,content,linearLayout);
                        }
                        break;
                    }
                    case 1:{
                        //UI.createDateDialog(AddMemoActivity.this);
                        LinearLayout layout=(LinearLayout) linearLayout.getChildAt(1);
                        if(layout.getChildCount()==3){
                            isAlarm=true;
                        }else {
                            isAlarm=false;
                        }
                        UI.createDateTimeDialog(AddMemoActivity.this,linearLayout,isAlarm);
                        break;
                    }
                    case 2:{
                        //启动相册
                        Intent intent=new Intent("android.intent.action.GET_CONTENT");
                        intent.setType("image/*");
                        startActivityForResult(intent,1);
                        break;
                    }
                    case 3:{
                        //启动相机
                        File MyMemo=new File(Environment.getExternalStorageDirectory(),"MyMemo");
                        File outputImage=null;
                        if(!MyMemo.exists()){
                           MyMemo.mkdir();
                        }
                        if(MyMemo.exists()){
                            Long number=System.currentTimeMillis();
                            outputImage=new File(MyMemo.getPath(),"memo_"+number+".jpg");
                        }

                        imageUri=Uri.fromFile(outputImage);
                        Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                        startActivityForResult(intent,2);
                        break;
                    }
                }
            }
        });
    }

    //数据返回
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent date){
        switch (requestCode){
            case 1:{

                if(resultCode==RESULT_OK){
                    View view=LayoutInflater.from(AddMemoActivity.this).inflate(R.layout.add_memo_image,null);
                    ImageView imageView=(ImageView) view.findViewById(R.id.add_memo_image);
                    String image=handleImageOnKitKat(date);
                    if(image!=null) {
                        Bitmap bitmap = BitmapFactory.decodeFile(image);
                        imageView.setImageBitmap(bitmap);
                        imageView.setTag(image);
                        if (UI.getCurrentCursorLine(content) == 1) {
                            linearLayout.addView(view, linearLayout.getChildCount() - 1);
                            content.setFocusable(true);
                            content.setFocusableInTouchMode(true);
                            content.requestFocus();
                        } else if(UI.getCurrentCursorLine(content)> 1){
                            String text = content.getText().toString().trim();
                            content.setText(null);
                            final View view_content = LayoutInflater.from(this).inflate(R.layout.add_textvie, null);
                            final EditText content_edit = (EditText) view_content.findViewById(R.id.add_memo_content);
                            content_edit.setText(text);
                            content_edit.setOnKeyListener(new View.OnKeyListener() {
                                @Override
                                public boolean onKey(View v, int keyCode, KeyEvent event) {
                                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                        if (keyCode == KeyEvent.KEYCODE_DEL) {
                                            if (content_edit.getSelectionStart() == 0) {
                                                linearLayout.removeView(view_content);
                                                content.setFocusable(true);
                                                content.setFocusableInTouchMode(true);
                                                content.requestFocus();
                                            }
                                        }
                                    }
                                    return false;
                                }
                            });
                            linearLayout.addView(view_content, linearLayout.getChildCount() - 1);
                            linearLayout.addView(view, linearLayout.getChildCount() - 1);
                        }

                    }else {
                        Toast.makeText(this,"选择图片不成功",Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }
            case 2:{

                if(resultCode==RESULT_OK){
                   if(imageUri!=null){
                       View view=LayoutInflater.from(AddMemoActivity.this).inflate(R.layout.add_memo_image,null);
                       ImageView imageView=(ImageView) view.findViewById(R.id.add_memo_image);
                       Bitmap bitmap=BitmapFactory.decodeFile(imageUri.getPath());
                       imageView.setImageBitmap(bitmap);
                       imageView.setTag(imageUri.getPath());
                       linearLayout.addView(view,linearLayout.getChildCount()-1);
                   }
                }
                break;
            }
            default:{

            }

        }
    }

    //解析URL
    private String handleImageOnKitKat(Intent date){
        String imagePath=null;
        Uri uri=date.getData();
        //是document类型url
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];
                String selection= MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);

            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
            //普通url
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath=getImagePath(uri,null);
        }
        return imagePath;
    }

    //获取图片路径
    private String getImagePath(Uri uri,String selection){
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToNext()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


   //触摸监听
    private void initOnTouchButton(){
        add_tag_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                  UI.createTagDialog(AddMemoActivity.this,add_tag_button);
                }
                return false;
            }
        });
    }

    private void initButton(){
        Cursor cursor=db.rawQuery("select * from tag where id='"+intent_memoBean.getTagId()+"'",null);
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

    //导航栏和状态栏
    private void state(){
        getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
        getWindow().setNavigationBarColor(Color.parseColor("#ffffff"));
    }

    private void onBack(){
        Intent intent=new Intent(AddMemoActivity.this,MainActivity.class);
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
           //保存
           case R.id.add_memo_save:{
               //初始化memoBean
               MemoBean memoBean=new MemoBean();
               if(intent_memoBean!=null){
                   memoBean=intent_memoBean;
               }
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

               memoBean.setTime(time);
               //保存备忘录数据
               if(!"".equals(content.toString())&&content.toString()!=null) {
                   if(intent_memoBean!=null){
                       DB.UpdateMemo(db,memoBean);
                       initAlarm(memoBean);
                   }else {
                       DB.insertMemo(db, memoBean);
                       initAlarm(memoBean);
                   }
                   //Toast.makeText(this,content,Toast.LENGTH_LONG).show();
               }else {
                   if(intent_memoBean!=null){
                       db.execSQL("delete from memo where id='"+memoBean.getId()+"'");
                   }
               }
               onBack();
           }
       }
    }

    //活动重新可见
    @Override
    public void onRestart(){
        super.onRestart();
        //UI.createTagDialog(AddMemoActivity.this,add_tag_button);
    }

    //设置闹铃
    private void initAlarm(MemoBean memoBean){
        Calendar calendar=(Calendar)linearLayout.getTag();
        if(calendar!=null) {
            Intent intent = new Intent(AddMemoActivity.this,AlarmService.class);
            intent.putExtra("calendar",calendar);
            intent.putExtra("memoBean",memoBean);
            startService(intent);
        }
    }



}
