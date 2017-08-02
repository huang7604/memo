package com.memo.broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.memo.R;
import com.memo.activity.AddMemoActivity;
import com.memo.activity.DisplayMemoActivity;
import com.memo.bean.MemoBean;

/**
 * Created by user on 2017/3/31.
 */
public class AlarmBroadcast extends BroadcastReceiver {

    private static MemoBean memoBean=null;
    @Override
    public void onReceive(Context context, Intent intent){

        NotificationManager manager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setTicker("备忘录消息");
        builder.setContentTitle("明天上课");
        builder.setContentText("");
        builder.setSmallIcon(R.drawable.memo);
        //MemoBean memoBean=(MemoBean)intent.getSerializableExtra("memoBean");
        //String name=intent.getStringExtra("name");
        if(memoBean!=null){
            Intent intent1=new Intent(context, DisplayMemoActivity.class);
            intent1.putExtra("memoBean",memoBean);
            PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent1,PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(pendingIntent);
        }else {
            Toast.makeText(context, "huang",Toast.LENGTH_LONG).show();
        }
        Notification notification = builder.build();
        manager.notify(1,notification);
    }

    public static void setMemoBean(MemoBean memoBeans){
        memoBean=memoBeans;
    }


}
