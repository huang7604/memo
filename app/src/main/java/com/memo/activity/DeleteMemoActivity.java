package com.memo.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.memo.R;
import com.memo.adapter.DeleteMemoAdapter;
import com.memo.bean.MemoBean;
import com.memo.databases.MyDatabasesHelper;
import com.memo.dbUtil.CheckAction;
import com.memo.dbUtil.DB;
import com.memo.dbUtil.GridViewUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 2017/3/27.
 */
public class DeleteMemoActivity extends Activity {
    //数据库
    private SQLiteDatabase db=null;
    private ListView listView=null;
    private GridView gridView=null;
    private int number=0;
    private TextView textView_num=null;
    private TextView title=null;
    private  boolean isAllSelect=false;
    private Button back;
    //备忘录数据
    private ArrayList<MemoBean> list=null;
    //适配器
    private DeleteMemoAdapter deleteMemoAdapter=null;
    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.delete_memo);

        //获取数据库
        db=new MyDatabasesHelper(this,"mymemo.db",null,1).getWritableDatabase();
        listView=(ListView)findViewById(R.id.delete_memo_listView);
        gridView=(GridView)findViewById(R.id.delete_memo_grid_view);
        textView_num=(TextView) findViewById(R.id.delete_memo_number);
        title=(TextView)findViewById(R.id.title_delete);
        back=(Button)findViewById(R.id.delete_memo_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        Intent intent=getIntent();
        list=(ArrayList<MemoBean>) intent.getSerializableExtra("list");

        initListView();
        initGridView();
        initState();
        initListViewListener();

    }

    private void initListView(){
        deleteMemoAdapter=new DeleteMemoAdapter(DeleteMemoActivity.this,list,R.layout.delete_memo_list_view, new CheckAction(){
            @Override
            public void action(boolean isCheck){
                if(isCheck){
                    number=number+1;
                }else {
                    number=number-1;
                }

                //Toast.makeText(DeleteMemoActivity.this,number+"",Toast.LENGTH_SHORT).show();
                if (number != 0) {
                    title.setText("已选择");
                    textView_num.setVisibility(View.VISIBLE);
                    textView_num.setText(number + "");
                } else {
                    title.setText("未选择");
                    textView_num.setVisibility(View.GONE);
                    textView_num.setText(number + "");
                }
            }
        });
        listView.setAdapter(deleteMemoAdapter);
        listView.setDivider(null);
    }

    private void initListViewListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox=(CheckBox)view.findViewById(R.id.delete_memo_checkBox);
                if(checkBox.isChecked()) {
                    checkBox.setChecked(false);
                }else {
                    checkBox.setChecked(true);
                }

            }
        });

    }


    private void initState(){
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.KITKAT){
            getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
            getWindow().setNavigationBarColor(Color.parseColor("#ffffff"));
        }
    }

    private void initGridView(){
        gridView.setGravity(Gravity.CENTER);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        int[] image=new int[]{R.drawable.delete,R.drawable.all_seclect};
        final String[] title_array=new String[]{"删除","全选"};
        final ArrayList<HashMap<String,Object>> list_one= GridViewUtil.getGridViewArrayList(title_array,image,new String[]{"image","title"});
        gridView.setAdapter(new SimpleAdapter(this,list_one,R.layout.grid_view_menu,new String[]{"image","title"},new int[]{R.id.image_item,R.id.text_item}));


        //添加监听
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:{
                        for (int i=0;i<listView.getChildCount();i++){
                            RelativeLayout relativeLayout=(RelativeLayout) listView.getChildAt(i);
                            if(relativeLayout!=null) {
                                CheckBox checkBox = (CheckBox) relativeLayout.getChildAt(1);
                                if(checkBox.isChecked()){
                                    MemoBean memoBean=(MemoBean)checkBox.getTag();
                                    db.execSQL("delete from memo where id='"+memoBean.getId()+"'");
                                    list.remove(memoBean);

                                }
                            }
                        }
                        deleteMemoAdapter.UpdateChecks();
                        deleteMemoAdapter.notifyDataSetChanged();
                        number=0;
                        title.setText("未选择");
                        textView_num.setVisibility(View.GONE);
                        textView_num.setText(number + "");

                        break;
                    }
                    case 1:{
                        TextView grid_text = (TextView) view.findViewById(R.id.text_item);
                        if(!isAllSelect) {
                            for (int i=0;i<listView.getChildCount();i++){
                                RelativeLayout relativeLayout=(RelativeLayout) listView.getChildAt(i);
                                if(relativeLayout!=null) {
                                    CheckBox checkBox = (CheckBox) relativeLayout.getChildAt(1);
                                    checkBox.setChecked(true);
                                }
                            }
                            grid_text.setText("取消全选");
                            isAllSelect=true;
                        }else {
                            for (int i=0;i<listView.getChildCount();i++){
                                RelativeLayout relativeLayout=(RelativeLayout) listView.getChildAt(i);
                                if(relativeLayout!=null) {
                                    CheckBox checkBox = (CheckBox) relativeLayout.getChildAt(1);
                                    checkBox.setChecked(false);
                                }
                            }
                            grid_text.setText("全选");
                            isAllSelect=false;
                        }

                        break;
                    }
                }
            }
        });
    }

    //退回上一层活动
    private void onBack(){
        Intent intent=new Intent(DeleteMemoActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){
        onBack();
    }



}
