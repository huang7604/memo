package com.memo.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.memo.R;
import com.memo.bean.SlidingMenuBean;
import com.memo.databases.MyDatabasesHelper;
import com.memo.dbUtil.DB;
import com.memo.dbUtil.UI;
import com.memo.dbUtil.UiAction;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 2017/3/1.
 */
public class MyAdapter extends BaseAdapter {

    private Context context;
    private int sourceId;
    private ArrayList arrayList;
    private SQLiteDatabase db=null;

    public MyAdapter(Context context,int sourceId,ArrayList<SlidingMenuBean> arrayList){
        this.context=context;
        this.sourceId=sourceId;
        this.arrayList=arrayList;
        db=new MyDatabasesHelper(context,"mymemo.db",null,1).getWritableDatabase();
    }

    @Override
    public int getCount(){
        if(arrayList!=null) {
            return arrayList.size();
        }else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position){
        if(arrayList!=null) {
            return arrayList.get(position);
        }else {
            return null;
        }
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup viewGroup){

        final View view = LayoutInflater.from(context).inflate(sourceId, null);
        final EditText editText = (EditText) view.findViewById(R.id.add_tag_list_view_text);
        final Button button = (Button) view.findViewById(R.id.add_tag_list_view_select);
        final SlidingMenuBean menuBean= (SlidingMenuBean) getItem(position);
        editText.setText(menuBean.getTitle());
        button.setBackgroundResource(menuBean.getImage());
        editText.setTag(menuBean);
        button.setTag(menuBean);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                SlidingMenuBean menuBean_one=(SlidingMenuBean)editText.getTag();
                String title=editText.getText().toString().trim();
                if(title!=null&&!title.equals(menuBean_one.getTitle())){
                    String sql="update tag set title='"+title+"' where id='"+menuBean_one.getId()+"'";
                    db.execSQL(sql);
                }
            }
        });


        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){

                    UI.CreateTagSelectDialog(context, v, new UiAction() {
                        @Override
                        public void action(int position, int[] tag_image, Dialog dialog) {
                            SlidingMenuBean menuBean_two=(SlidingMenuBean) button.getTag();
                            if(!(tag_image[position]==menuBean_two.getImage())) {
                                button.setBackgroundResource(tag_image[position]);
                                String sql = "update tag set image='" + tag_image[position] + "' where id='" + menuBean_two.getId() + "'";
                                db.execSQL(sql);
                            }
                            dialog.dismiss();
                        }
                    });
                }

                return false;
            }
        });



        return view;
    }


}
