package com.memo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.memo.R;
import com.memo.adapter.MyAdapter;
import com.memo.bean.SlidingMenuBean;
import com.memo.databases.MyDatabasesHelper;
import com.memo.dbUtil.DB;
import com.memo.dbUtil.GridViewUtil;
import com.memo.dbUtil.UI;
import com.memo.dbUtil.UiAction;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 2017/3/1.
 */
public class AddTagActivity extends Activity implements View.OnClickListener {

    //显示标签
    private ListView listView=null;
    //数据库
    private SQLiteDatabase db=null;
    //编辑标签
    private EditText editText=null;
    //返回按钮
    private Button button_back=null;
    //删除菜单按钮组
    private GridView gridView=null;
    //删除编辑按钮
    private Button button_add_tag_delete=null;
    //编辑组件
    private RelativeLayout add_tag_create_layout=null;
    //标签选择
    private Button tag_button=null;
    //标签添加
    private Button add_button=null;
    //记录选择的标签,默认蓝色
    private int tag=0;
    //记录已经存在的标签
    private ArrayList<SlidingMenuBean> list_one;
    //弹出窗口
    private Dialog mydialog=null;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.add_tag);

        db=new MyDatabasesHelper(this,"mymemo.db",null,1).getWritableDatabase();

        //获取listView
        listView=(ListView)findViewById(R.id.add_tag_list_view);
        //获取编辑标签
        editText=(EditText)findViewById(R.id.add_tag_edit);
        //获取返回按钮
        button_back=(Button)findViewById(R.id.add_tag_back);
        //获取删除菜单按钮组
        gridView=(GridView)findViewById(R.id.add_tag_grid_view);
        //获取删除按钮
        button_add_tag_delete=(Button)findViewById(R.id.add_tag_delete);
        //获取删除组件
        add_tag_create_layout=(RelativeLayout)findViewById(R.id.add_tag_create);
        //获取标签选择
        tag_button=(Button)findViewById(R.id.add_tag_color_select);
        //获取添加标签
        add_button=(Button)findViewById(R.id.add_tag_save);

        //添加监听
        button_back.setOnClickListener(this);
        button_add_tag_delete.setOnClickListener(this);
        add_button.setOnClickListener(this);

        //初始化listView
        initListView();
        //开启沉浸式
        initState();
        //初始化gridView
        initGridViewMenu_one();
        //监听事件
        initGridViewListener();
        //listView监听事件
        initListViewListener();
        //标签选择按钮，触摸监听事件
        initButtonTouchListener();



    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.add_tag_back:{
                //退回上一层活动
                onBack();
                break;
            }
            case R.id.add_tag_delete:{
                add_tag_create_layout.setVisibility(View.GONE);
                initGridViewMenu_two();
                //可以点击
                listView.setEnabled(true);
                //标签选择
                tag=0;
                tag_button.setBackgroundResource(R.drawable.ic_tag_none);
                editText.setText(null);
                break;
            }
            case R.id.add_tag_save:{
                String tag_name=editText.getText().toString().trim();
                if(!(tag_name.equals(""))){
                    if(tag==0){
                        Toast.makeText(this,"标签未选择",Toast.LENGTH_LONG).show();
                    }else {
                        for(SlidingMenuBean menu:list_one){
                            if(tag_name.equals(menu.getTitle())){
                                Toast.makeText(this,"标签名已存在",Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        Toast.makeText(this, tag_name, Toast.LENGTH_LONG).show();
                        SlidingMenuBean menuBean = new SlidingMenuBean();
                        menuBean.setTitle(tag_name);
                        menuBean.setImage(tag);
                        menuBean.setNumber(0);
                        ArrayList<SlidingMenuBean> list = new ArrayList<SlidingMenuBean>();
                        list.add(menuBean);
                        //添加便签
                        DB.insertTag(db, list);
                        //更新listView
                        initListView();
                        tag=0;
                        tag_button.setBackgroundResource(R.drawable.ic_tag_none);
                        editText.setText(null);
                    }
                }else {
                    //隐藏编辑便签框
                    if(tag==0){
                        add_tag_create_layout.setVisibility(View.GONE);
                        initGridViewMenu_two();
                        //可以点击
                        listView.setEnabled(true);
                    }else{
                        Toast.makeText(this,"没有标签名",Toast.LENGTH_LONG).show();
                    }
                }

            }
        }
    }

   //初始化GridView
    private void initGridViewMenu_one(){
        gridView.setGravity(Gravity.CENTER);
        //不可点击
        gridView.setEnabled(false);
        //点击背景
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        String[] title=new String[]{"新建","删除"};
        int[] image=new int[]{R.drawable.ic_add_black_24dp_none,R.drawable.delete_none};
        ArrayList<HashMap<String,Object>> list= GridViewUtil.getGridViewArrayList(title,image,new String[]{"image","title"});
        gridView.setAdapter(new SimpleAdapter(this,list,R.layout.grid_view_menu_none,new String[]{"image","title"},new int[]{R.id.image_item,R.id.text_item}));
    }

    //初始化GridView
    private void initGridViewMenu_two(){

        gridView.setGravity(Gravity.CENTER);
        gridView.setEnabled(true);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        String[] title=new String[]{"新建","删除"};
        int[] image=new int[]{R.drawable.ic_add_black_24dp,R.drawable.delete};
        ArrayList<HashMap<String,Object>> list= GridViewUtil.getGridViewArrayList(title,image,new String[]{"image","title"});
        gridView.setAdapter(new SimpleAdapter(this,list,R.layout.grid_view_menu,new String[]{"image","title"},new int[]{R.id.image_item,R.id.text_item}));
    }

    //添加监听事件
    private void initGridViewListener(){
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:{
                        add_tag_create_layout.setVisibility(View.VISIBLE);
                        //不可以点击
                        listView.setEnabled(false);
                        initGridViewMenu_one();
                        break;
                    }
                    case 1:{
                        Intent intent=new Intent(AddTagActivity.this,DeleteTagActivity.class);
                        startActivity(intent);
                        break;
                    }
                }
            }
        });
    }


    //初始化listView
    private void initListView(){
        list_one= DB.getTagResult(db);
        listView.setDivider(null);
        //不可以点击
        listView.setEnabled(false);
        if(list_one!=null){
            listView.setAdapter(new MyAdapter(this,R.layout.add_tag_list_view,list_one));
        }

    }

    //添加listView事件
    private void initListViewListener(){

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(AddTagActivity.this,DeleteTagActivity.class);
                startActivity(intent);
                return false;
            }
        });

    }

    private void initState() {
        //Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.KITKAT) {
            getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
            getWindow().setNavigationBarColor(Color.parseColor("#ffffff"));
        }
    }

    // 获取点击事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            //获取当前获得焦点的view
            View view = getCurrentFocus();
            //判断是否需要隐藏输入法
            if (UI.isHideInput(view, ev)) {
                //隐藏输入法
                UI.HideSoftInput(view.getWindowToken(),this);
            }else if(UI.isHideDialog(view,ev)){
                mydialog.dismiss();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void  onBackPressed(){
        onBack();
    }

    //退回上一层活动
    private void onBack(){
        //Intent intent=new Intent(AddTagActivity.this,MainActivity.class);
       //startActivity(intent);
        this.finish();
    }

    private void initButtonTouchListener(){
       tag_button.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {

               if (event.getAction() == MotionEvent.ACTION_DOWN) {

                   UI.CreateTagSelectDialog(AddTagActivity.this, v, new UiAction() {
                       @Override
                       public void action(int position, int[] image, Dialog dialog) {
                           tag_button.setBackgroundResource(image[position]);
                           tag = image[position];
                           mydialog = dialog;

                       }
                   });
               }

                   return false;
               }

       });
    }

    @Override
    public void onRestart(){
        super.onRestart();
        initListView();
    }









}
