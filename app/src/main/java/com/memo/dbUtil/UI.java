package com.memo.dbUtil;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.IBinder;
import android.text.Layout;
import android.text.Selection;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.memo.R;
import com.memo.activity.AddMemoActivity;
import com.memo.activity.AddTagActivity;
import com.memo.adapter.selectTagAdapter;
import com.memo.bean.SlidingMenuBean;
import com.memo.broadcast.AlarmBroadcast;
import com.memo.databases.MyDatabasesHelper;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by user on 2017/3/2.
 */
public class UI {


    // 判定是否需要隐藏
    public static boolean isHideInput(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (ev.getX() > left && ev.getX() < right && ev.getY() > top
                    && ev.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    //隐藏弹出框
    public static boolean isHideDialog(View v,MotionEvent ev){
        if(v!=null&&(v instanceof Button)){
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (ev.getX() > left && ev.getX() < right && ev.getY() > top
                    && ev.getY() < bottom) {
                return false;
            }else {
                return true;
            }
        }
        return false;
    }


    // 隐藏软键盘
    public static void HideSoftInput(IBinder token,Context context) {
        if (token != null) {
            //InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            //manager.hideSoftInputFromWindow(token,InputMethodManager.HIDE_NOT_ALWAYS);
            InputMethodManager manager=(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(token,InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //生成便签选择弹出框
    public static void CreateTagSelectDialog(Context context, View v,final UiAction uiAction){
        //创建标签选择弹出窗口
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        //获取弹出窗口布局
        final View dialog= LayoutInflater.from(context).inflate(R.layout.dialog,null);
        //获取弹出窗口gridView
        GridView gridView=(GridView)dialog.findViewById(R.id.dialog_grid_view);
        ArrayList<HashMap<String,Object>> list=new ArrayList<HashMap<String, Object>>();

        int[] image=new int[]{R.drawable.ic_lens_blue,R.drawable.ic_lens_green,R.drawable.ic_lens_hotpink,R.drawable.ic_lens_orchid,
                R.drawable.ic_lens_red,R.drawable.ic_lens_skyblue,R.drawable.ic_lens_teal,R.drawable.ic_lens_yellow};

        final int[] tag_image=new int[]{R.drawable.ic_tag_blue,R.drawable.ic_tag_green,R.drawable.ic_tag_hotpink,R.drawable.ic_tag_orchid,
                R.drawable.ic_tag_red,R.drawable.ic_tag_skyblue,R.drawable.ic_tag_teal,R.drawable.ic_tag_yellow};

        for (int i=0;i<image.length;i++){
            HashMap<String,Object> hashMap=new HashMap<String, Object>();
            hashMap.put("image",image[i]);
            list.add(hashMap);
        }
        //初始化gridView
        gridView.setAdapter(new SimpleAdapter(builder.getContext(),list,R.layout.dialg_grid_view,new String[]{"image"},new int[]{R.id.dialg_image}));
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        //builder.setView(dialog);
        builder.setCancelable(true);

        final AlertDialog myDialog=builder.create();
        myDialog.setCanceledOnTouchOutside(true);
        myDialog.show();
        int[] l=new int[]{0,0};
        //获取v控件的坐标
        v.getLocationInWindow(l);
        //设置弹出窗口的位置和大小
        Window window=myDialog.getWindow();
        WindowManager.LayoutParams params=window.getAttributes();
        params.width=650;
        params.height=390;
        params.x=l[0]-750;
        params.y=l[1]-700;
        params.dimAmount=0f;
        window.setAttributes(params);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               uiAction.action(position,tag_image,myDialog);
            }
        });


    }

    //生成便签选择弹出框
    public static void createTagDialog(final Context context,Button button){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view=LayoutInflater.from(context).inflate(R.layout.selecttagdialog,null);
        ListView listView=(ListView)view.findViewById(R.id.select_tag_dialog_listView);
        SQLiteDatabase db=new MyDatabasesHelper(context,"mymemo.db",null,1).getWritableDatabase();

        ArrayList<SlidingMenuBean> list=DB.getTagResult(db);
        SlidingMenuBean slidingMenuBean=(SlidingMenuBean)button.getTag();
        if(slidingMenuBean!=null){
            int i=list.indexOf(slidingMenuBean);
            if(i<2){
                selectTagAdapter.setIsSelect(i);

            }else if(i>=2){
                selectTagAdapter.setIsSelect(i+1);

            }
           // Toast.makeText(context,i+"",Toast.LENGTH_LONG).show();
        }
        final selectTagAdapter tagAdapter=new selectTagAdapter(context,R.layout.select_tag_dialog_list,list);



        listView.setAdapter(tagAdapter);
        listView.setDivider(null);

        builder.setView(view);

        final AlertDialog dialog=builder.create();
        dialog.setCancelable(true);
        dialog.show();
        tagAdapter.setDialog(dialog);
        tagAdapter.setButton(button);
        Window window=dialog.getWindow();
        WindowManager.LayoutParams params=window.getAttributes();
        params.dimAmount=0f;
        window.setAttributes(params);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position!=tagAdapter.getCount()-1&&position!=tagAdapter.getCount()-2) {
                    tagAdapter.setIsSelect(position);
                    tagAdapter.setClose(true);
                    tagAdapter.notifyDataSetChanged();

                }else if(position==tagAdapter.getCount()-1){

                }else if(position==tagAdapter.getCount()-2){
                    Intent intent=new Intent(context,AddTagActivity.class);
                    context.startActivity(intent);
                    dialog.dismiss();
                }
            }
        });





    }

    //生成日期选择框
    public static void createDateDialog(Context context){
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);



        DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            }
        };
        DatePickerDialog datePickerDialog=new DatePickerDialog(context,listener,year,month,day);
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        datePickerDialog.show();
        WindowManager.LayoutParams layoutParams=datePickerDialog.getWindow().getAttributes();
        layoutParams.dimAmount=0f;
        datePickerDialog.getWindow().setAttributes(layoutParams);

    }
    //生成日期选择框
    public static void createDateTimeDialog(final Context context, final LinearLayout linearLayout, final boolean isAlarm){

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.date_time_dialog, null);

        final DatePicker datePicker=(DatePicker)view.findViewById(R.id.datePicker);
        final TimePicker timePicker=(TimePicker)view.findViewById(R.id.timePicker);

        timePicker.setIs24HourView(true);
        builder.setView(view);
        builder.setNegativeButton("取消",null);
        builder.setPositiveButton("完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String timeMin=null;
                if(timePicker.getMinute()<10){
                    timeMin="0"+timePicker.getMinute();
                }else {
                    timeMin=timePicker.getMinute()+"";
                }
               String s=datePicker.getYear()+"年"+datePicker.getMonth()+"月"+datePicker.getDayOfMonth()+"日 "+timePicker.getHour()+":"+timeMin;
                if(!isAlarm){
                    final View view_alarm = LayoutInflater.from(context).inflate(R.layout.add_memo_time, null);
                    TextView textView = (TextView) view_alarm.findViewById(R.id.add_memo_alarm_time);
                    Button button=(Button)view_alarm.findViewById(R.id.add_memo_alarm_delete);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            linearLayout.removeView(view_alarm);
                        }
                    });
                    textView.setText(s);
                    linearLayout.addView(view_alarm,1);

                }else {
                    LinearLayout layout=(LinearLayout)linearLayout.getChildAt(1);
                    TextView textView=(TextView)layout.getChildAt(1);
                    textView.setText(s);
                }
                Calendar calendar=Calendar.getInstance();

                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.YEAR,datePicker.getYear());
                calendar.set(Calendar.MONTH,datePicker.getMonth());
                calendar.set(Calendar.DAY_OF_MONTH,datePicker.getDayOfMonth());
                calendar.set(Calendar.HOUR_OF_DAY,timePicker.getHour());
                calendar.set(Calendar.MINUTE,timePicker.getMinute());
                //Toast.makeText(context,calendar.get(Calendar.HOUR_OF_DAY)+"",Toast.LENGTH_LONG).show();
                linearLayout.setTag(calendar);

            }

        });
        builder.show();
    }


    //创建输入待办面板
    public static void createEditView(Context context, final EditText content,final LinearLayout linearLayout){
        final View view_radio = LayoutInflater.from(context).inflate(R.layout.add_memo_radio, null);
        final EditText editText = (EditText) view_radio.findViewById(R.id.radio_edit_text);
        CheckBox checkBox=(CheckBox)view_radio.findViewById(R.id.add_memo_checkbox) ;
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
                            linearLayout.removeView(view_radio);
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
        //当前行数等于1，添加面板
        if(getCurrentCursorLine(content)==1) {
            editText.setText(content.getText());
            content.setText(null);
            linearLayout.addView(view_radio, linearLayout.getChildCount() - 1);
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
            //当前行数不等一1，添加edit面板和待办面板
        }else if(getCurrentCursorLine(content)!=1){
            String text=content.getText().toString().trim();
            content.setText(null);
            final View view_content= LayoutInflater.from(context).inflate(R.layout.add_textvie,null);
            final EditText content_edit=(EditText)view_content.findViewById(R.id.add_memo_content) ;
            content_edit.setText(text);
            content_edit.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if(event.getAction()==KeyEvent.ACTION_DOWN) {
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
            linearLayout.addView(view_content, linearLayout.getChildCount()-1);
            linearLayout.addView(view_radio, linearLayout.getChildCount()-1);

            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
        }

    }

    //光标所在的行数
    public static int getCurrentCursorLine(EditText editText) {
        int selectionStart = Selection.getSelectionStart(editText.getText());
        Layout layout = editText.getLayout();
        if (selectionStart != -1) {
            return layout.getLineForOffset(selectionStart) + 1;
        }
        return -1;
    }

}








