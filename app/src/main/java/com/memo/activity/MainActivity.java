package com.memo.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.renderscript.Sampler;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.memo.R;
import com.memo.adapter.MemoAdapter;
import com.memo.adapter.MyAdapter;
import com.memo.adapter.SlidingMenuAdapter;
import com.memo.bean.MemoBean;
import com.memo.bean.SlidingMenuBean;
import com.memo.databases.MyDatabasesHelper;
import com.memo.dbUtil.DB;
import com.memo.dbUtil.GridViewUtil;
import com.memo.dbUtil.UI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class MainActivity extends Activity implements View.OnClickListener {

    //跟布局
    private DrawerLayout drawerLayout=null;
    //侧滑菜单按钮
    private Button show_slide_menu_button=null;
    //增加备忘录按钮组
    private GridView gridView_menu_one=null;
    //gridView_menu_two菜单组
    private  GridView gridView_menu_two=null;
    //侧滑菜单按钮list_view
    private ListView listView_sliding_menu=null;
    //数据库
    private SQLiteDatabase db=null;
    //搜索框
    private EditText search_edit=null;
    //备忘录
    private ListView listView_memo=null;
    //备忘录数据
    private   ArrayList<MemoBean> list_memo=new ArrayList<>();
    //标签数据
    private ArrayList<SlidingMenuBean> list=null;
    //memo
    private MemoAdapter memoAdapter=null;
    private SlidingMenuAdapter adapter=null;
    //线程池
    private final Executor executor= Executors.newFixedThreadPool(10);
    private Button button=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initState();

        //获取DrawerLayout,可以侧滑菜单的布局
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        //获取显示侧滑菜单按钮
        show_slide_menu_button=(Button)findViewById(R.id.show_slide_menu);
        //获取增加备忘录按钮
        gridView_menu_one=(GridView)findViewById(R.id.grid_memo_menu_one);

        //获取侧滑菜单按钮list_view
        listView_sliding_menu=(ListView)findViewById(R.id.list_view_menu);
        //获取搜索框
        search_edit=(EditText)findViewById(R.id.search_edit_frame);
        //备忘录
        listView_memo=(ListView)findViewById(R.id.list_memo_content);
        button=(Button)findViewById(R.id.change_view);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent("com.memo.broadcast.AlarmBroadcast");
                sendBroadcast(intent);

            }
        });

        //获取数据库
        db=new MyDatabasesHelper(this,"mymemo.db",null,1).getWritableDatabase();

        //侧滑菜单按钮添加监听
        show_slide_menu_button.setOnClickListener(this);
        //单击搜索框添加监听事件
        search_edit.setFocusable(false);
        search_edit.setOnClickListener(this);






        //初始化增加备忘录按钮组私有方法
        initGridViewMenuOne();
        initListViewSlidingMenu();
        initSlidingMemoListener();
        initListViewMemo();
        initListViewMemoListener();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //侧滑菜单按钮
            case R.id.show_slide_menu:{
                //如果侧滑菜单没有滑出，则滑出侧滑菜单
                if(!drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    drawerLayout.openDrawer(Gravity.LEFT);
                    search_edit.setFocusable(false);
                }
                break;
            }
            case R.id.search_edit_frame:{
                search_edit.setFocusable(true);
                search_edit.setFocusableInTouchMode(true);
            }

        }

    }

    //初始化增加备忘录按钮组私有方法
    private void initGridViewMenuOne(){
        gridView_menu_one.setGravity(Gravity.CENTER);
        String[] title=new String[]{"新建"};
        int[] image=new int[]{R.drawable.ic_add_black_24dp};
        ArrayList<HashMap<String,Object>> list= GridViewUtil.getGridViewArrayList(title,image,new String[]{"image","title"});
        gridView_menu_one.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView_menu_one.setAdapter(new SimpleAdapter(this,list,R.layout.grid_view_menu,new String[]{"image","title"},new int[]{R.id.image_item,R.id.text_item}));

        gridView_menu_one.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:{
                        Intent intent=new Intent(MainActivity.this,AddMemoActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }




    //初始化侧滑菜单按钮list_view的私有方法
    private void initListViewSlidingMenu(){
        list=new ArrayList<>();
        Runnable task=new Runnable() {
            @Override
            public void run() {
                DB.getTagResult(db,list);
            }
        };
        executor.execute(task);

        listView_sliding_menu.setDivider(null);
        adapter=new SlidingMenuAdapter(MainActivity.this,list,db);
        if(list!=null) {
            listView_sliding_menu.setAdapter(adapter);
        }
    }

    private void initSlidingMemoListener(){
        //单击事件
        listView_sliding_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getCount() - 1 == position) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    Intent intent=new Intent(MainActivity.this,AddTagActivity.class);
                    startActivity(intent);
                }else if(position==2){
                    Runnable task=new Runnable() {
                        @Override
                        public void run() {
                            DB.getMemoArrayList(db,null,null,list_memo);
                        }
                    };
                    executor.execute(task);
                    if(list_memo!=null) {
                        memoAdapter.notifyDataSetChanged();
                    }
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }else if(position==3){
                    Runnable task=new Runnable() {
                        @Override
                        public void run() {
                            DB.getMemoArrayList(db,"daiban=?",new String[]{"1"},list_memo);
                        }
                    };
                    executor.execute(task);

                    if(list_memo!=null) {
                        memoAdapter.notifyDataSetChanged();
                    }
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }else if(position==4){
                    Runnable task=new Runnable() {
                        @Override
                        public void run() {
                            DB.getMemoArrayList(db,"alarm=?",new String[]{"1"},list_memo);
                        }
                    };
                    executor.execute(task);

                    if(list_memo!=null) {
                        memoAdapter.notifyDataSetChanged();
                    }
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }else if(position==5){
                    Runnable task=new Runnable() {
                        @Override
                        public void run() {
                            DB.getMemoArrayList(db,"shoucang=?",new String[]{"1"},list_memo);
                        }
                    };
                    executor.execute(task);

                    if(list_memo!=null) {
                        memoAdapter.notifyDataSetChanged();
                    }
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }else if((position==0||position==1)||position==6){

                }else if(adapter.getCount()-2==position){
                    Runnable task=new Runnable() {
                        @Override
                        public void run() {
                            DB.getMemoArrayList(db,"tag_id=?",new String[]{"-1"},list_memo);
                        }
                    };
                    executor.execute(task);
                    if(list_memo!=null) {
                        memoAdapter.notifyDataSetChanged();
                    }
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }else {
                    SlidingMenuBean slidingMenuBean=(SlidingMenuBean)view.getTag();
                    final String tag_id=String.valueOf(slidingMenuBean.getId());
                    Runnable task=new Runnable() {
                        @Override
                        public void run() {
                            DB.getMemoArrayList(db,"tag_id=?",new String[]{tag_id},list_memo);
                        }
                    };
                    executor.execute(task);
                    if(list_memo!=null) {
                        memoAdapter.notifyDataSetChanged();
                    }
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
            }
        });

    }

    //初始化备忘录
    private void initListViewMemo(){
        Runnable task=new Runnable() {
            @Override
            public void run() {
                DB.getMemoArrayList(db,null,null,list_memo);
            }
        };
        executor.execute(task);
        memoAdapter=new MemoAdapter(MainActivity.this, list_memo, R.layout.memo_list_view);
        if(list_memo!=null) {
            listView_memo.setAdapter(memoAdapter);
            listView_memo.setDivider(null);
        }
    }

    //备忘录listView长按监听监听
    private void initListViewMemoListener(){
        listView_memo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(list_memo!=null){
                    Intent intent=new Intent(MainActivity.this,DeleteMemoActivity.class);
                    intent.putExtra("list",(Serializable)list_memo);
                    startActivity(intent);
                }
                return false;
            }
        });

        listView_memo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MemoBean memoBean=(MemoBean)view.getTag();
                Intent intent=new Intent(MainActivity.this,DisplayMemoActivity.class);
                intent.putExtra("memoBean",(Serializable)memoBean);
                startActivity(intent);
            }
        });
    }

    private void initState() {
        //Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.KITKAT) {
              getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
              getWindow().setNavigationBarColor(Color.parseColor("#ffffff"));

              getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

      // 获取点击事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (UI.isHideInput(view, ev)) {
                UI.HideSoftInput(view.getWindowToken(),this);
                search_edit.setFocusable(false);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onRestart(){
        super.onRestart();
        Runnable task=new Runnable() {
            @Override
            public void run() {
                DB.getTagResult(db,list);
                DB.getMemoArrayList(db,null,null,list_memo);
            }
        };
        executor.execute(task);
        adapter.notifyDataSetChanged();
        memoAdapter.notifyDataSetChanged();
    }




}
