package com.memo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.memo.R;
import com.memo.bean.MemoBean;
import com.memo.dbUtil.CheckAction;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by user on 2017/3/27.
 */
public class DeleteMemoAdapter extends BaseAdapter{
    private Executor executor=null;
    private Context context=null;
    private ArrayList<MemoBean> list=null;
    private int resourceId;
    private boolean[] checks;

    private CheckAction checkAction=null;

    private Handler handler=new android.os.Handler(){
        @Override
        public void handleMessage(Message message){
            switch (message.what){
                case 1:{
                    MyHandler myHandler=(MyHandler)message.obj;
                    myHandler.imageView.setImageBitmap(myHandler.bitmap);
                    break;

                }
            }
        }
    };
    public DeleteMemoAdapter(Context context, ArrayList<MemoBean> list, int resourceId, CheckAction checkAction){
        this.context=context;
        this.list=list;
        this.resourceId=resourceId;
        executor= Executors.newSingleThreadExecutor();
        checks=new boolean[list.size()];

        this.checkAction=checkAction;
    }

    @Override
    public int getCount(){
        if(list!=null) {
            return list.size();
        }else {
            return 0;
        }
    }

    @Override

    public long getItemId(int position){
        return 0;
    }

    @Override
    public Object getItem(int position){
        return null;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup){
        View view= LayoutInflater.from(context).inflate(resourceId,null);
        TextView textView=(TextView) view.findViewById(R.id.memo_list_view_content);
        TextView time=(TextView)view.findViewById(R.id.memo_list_view_time);
        ImageView alarm=(ImageView)view.findViewById(R.id.memo_list_view_alarm);
        ImageView daiban=(ImageView)view.findViewById(R.id.memo_list_view_daiban);
        CheckBox checkBox=(CheckBox)view.findViewById(R.id.delete_memo_checkBox);
        checkBox.setChecked(checks[position]);

        final ImageView image=(ImageView)view.findViewById(R.id.memo_list_view_image);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checks[position]=isChecked;
                checkAction.action(isChecked);
            }
        });

        MemoBean memoBean=list.get(position);
        checkBox.setTag(memoBean);
        final String memo=memoBean.getContent().toString();

        if(memo!=null&&!memo.equals("")) {
            image.setImageBitmap(null);
            Runnable task=new Runnable() {
                @Override
                public void run() {
                    Message message=new Message();
                    message.what=1;
                    String[] content_array = memo.split("&");
                    for(int i=0;i<content_array.length;i++){
                        if(content_array[i].split("=")[0].equals("image")){
                            Bitmap bitmap= BitmapFactory.decodeFile(content_array[i].split("=")[1]);
                            MyHandler myHandler=new MyHandler();
                            myHandler.bitmap=bitmap;
                            myHandler.imageView=image;
                            message.obj=myHandler;
                            handler.sendMessage(message);
                            break;
                        }
                    }
                }
            };
            executor.execute(task);
        }

        if(memo!=null&&!"".equals(memo)) {
            String[] content_array = memo.split("&");
            final String array_one_0=content_array[0].split("=")[0];
            final String array_one_1=content_array[0].split("=")[1];
            if (array_one_0.equals("daiban")) {
                String content = array_one_1.split("■")[0];
                if(content!=null&&!"".equals(content)){
                    textView.setText(content);
                }else {
                    textView.setText("待办");
                }
            } else if (array_one_0.equals("content")) {
                textView.setText(array_one_1);
            }else if(array_one_0.equals("alarm_time")){
                //Toast.makeText(context,content_array.length+"",Toast.LENGTH_LONG).show();
                if(content_array.length>1) {
                    if (content_array[1] != null && !"".equals(content_array[1])) {
                        String array_two_0=content_array[1].split("=")[0];
                        String array_two_1=content_array[1].split("=")[1];
                        if (array_two_0.equals("daiban")) {
                            String content = array_two_1.split("■")[0];
                            textView.setText(content);
                        } else if (array_two_0.equals("content")) {
                            textView.setText(array_two_1);
                        }
                    } else {
                        textView.setText("闹钟");
                    }
                }else {
                    textView.setText("闹钟");
                }
            }else if(array_one_0.equals("image")) {
                if (content_array.length > 1) {

                    if (content_array[1] != null && !"".equals(content_array[1])) {
                        String array_two_0=content_array[1].split("=")[0];
                        String array_two_1=content_array[1].split("=")[1];
                        if (array_two_0.equals("daiban")) {
                            String content = array_two_1.split("■")[0];
                            textView.setText(content);
                        } else if (array_two_0.equals("content")) {
                            textView.setText(array_two_1);
                        }
                        //Toast.makeText(context, content_array.length + "", Toast.LENGTH_LONG).show();
                    } else {
                        textView.setText("图片");
                    }
                }else{
                    textView.setText("图片");
                }
            }


        }

        time.setText(memoBean.getTime());
        if(memoBean.getAlarm()==0){
            alarm.setVisibility(View.GONE);
        }
        if(memoBean.getDaiban()==0){
            daiban.setVisibility(View.GONE);
        }


        //view.setTag(memoBean);
        return view;
    }

    class MyHandler{
        Bitmap bitmap;
        ImageView imageView;
    }

   public void UpdateChecks(){
       for (int i=0;i<checks.length;i++){
           checks[i]=false;
       }
   }
}
