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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.memo.R;
import com.memo.adapter.DeleteAdapter;
import com.memo.bean.SlidingMenuBean;
import com.memo.databases.MyDatabasesHelper;
import com.memo.dbUtil.CheckAction;
import com.memo.dbUtil.DB;
import com.memo.dbUtil.GridViewUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 2017/3/1.
 */
public class DeleteTagActivity extends Activity {

    //数据库
    private SQLiteDatabase db=null;
    private ListView listView=null;
    private GridView gridView=null;
    private int number=0;
    private TextView textView_num=null;
    private TextView title=null;
    private  boolean isAllSelect=false;
    private Button back;
    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.delete_tag);

        //获取数据库
        db=new MyDatabasesHelper(this,"mymemo.db",null,1).getWritableDatabase();
        listView=(ListView)findViewById(R.id.delete_tag_listView);
        gridView=(GridView)findViewById(R.id.delete_tag_grid_view);
        textView_num=(TextView) findViewById(R.id.delete_tag_number);
        title=(TextView)findViewById(R.id.title_delete);
        back=(Button)findViewById(R.id.delete_tag_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });

        initListView();
        initGridView();
        initState();
        initListViewListener();

    }

    private void initListView(){
        final ArrayList<SlidingMenuBean> list= DB.getTagResult(db);
        listView.setDivider(null);
        if(list!=null){
            listView.setAdapter(new DeleteAdapter(this, R.layout.delete_tag_list_view, list, new CheckAction() {
                @Override
                public void action(boolean isCheck) {

                    if(isCheck){
                        number=number+1;
                    }else {
                        number=number-1;
                    }

                    //Toast.makeText(DeleteTagActivity.this,number+"",Toast.LENGTH_SHORT).show();
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
            }));

        }

    }

    private void initListViewListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox=(CheckBox)view.findViewById(R.id.delete_tag_checkBox);
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
        String[] title=new String[]{"删除","全选"};
        final ArrayList<HashMap<String,Object>> list=GridViewUtil.getGridViewArrayList(title,image,new String[]{"image","title"});
        gridView.setAdapter(new SimpleAdapter(this,list,R.layout.grid_view_menu,new String[]{"image","title"},new int[]{R.id.image_item,R.id.text_item}));


        //添加监听
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:{
                        for (int i=0;i<listView.getChildCount();i++){
                            RelativeLayout relativeLayout=(RelativeLayout) listView.getChildAt(i);
                            if(relativeLayout!=null) {
                                CheckBox checkBox = (CheckBox) relativeLayout.getChildAt(2);
                                if(checkBox.isChecked()){
                                    SlidingMenuBean slidingMenuBean=(SlidingMenuBean) checkBox.getTag();
                                    db.execSQL("delete from tag where id='"+slidingMenuBean.getId()+"'");
                                }
                            }
                        }
                        initListView();
                        break;
                    }
                    case 1:{
                        TextView grid_text=(TextView) view.findViewById(R.id.text_item);
                            if (!isAllSelect) {
                                for (int i=0;i<listView.getChildCount();i++){
                                    RelativeLayout relativeLayout=(RelativeLayout) listView.getChildAt(i);
                                    if(relativeLayout!=null) {
                                        CheckBox checkBox = (CheckBox) relativeLayout.getChildAt(2);
                                        checkBox.setChecked(true);
                                    }
                                }
                                isAllSelect = true;
                                grid_text.setText("取消全选");
                            }else {
                                for (int i=0;i<listView.getChildCount();i++){
                                    RelativeLayout relativeLayout=(RelativeLayout) listView.getChildAt(i);
                                    if(relativeLayout!=null) {
                                        CheckBox checkBox = (CheckBox) relativeLayout.getChildAt(2);
                                        checkBox.setChecked(false);
                                    }
                                }
                                isAllSelect = false;
                                grid_text.setText("全选");
                            }


                        break;
                    }
                }
            }
        });
    }

    //退回上一层活动
    private void onBack(){
        Intent intent=new Intent(DeleteTagActivity.this,AddTagActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){
        onBack();
    }



}
