package com.memo.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.memo.bean.MemoBean;
import com.memo.broadcast.AlarmBroadcast;

import java.util.Calendar;

/**
 * Created by user on 2017/3/31.
 */
public class AlarmService extends Service {

    private static MemoBean memoBean=null;
    private static Calendar calendar=null;
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){

    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        calendar=(Calendar) intent.getSerializableExtra("calendar");
        memoBean=(MemoBean)intent.getSerializableExtra("memoBean");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent_one = new Intent(AlarmService.this,AlarmBroadcast.class);
        intent.setAction("com.memo.broadcast.AlarmBroadcast");
        //intent.putExtra("name","name");
        //intent.putExtra("memoBean",memoBean);
        //sendBroadcast(intent);
        AlarmBroadcast.setMemoBean(memoBean);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmService.this, 0, intent_one, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy(){
        Intent intent=new Intent(this,AlarmService.class);
        intent.putExtra("calendar",calendar);
        intent.putExtra("memoBean",memoBean);
        startService(intent);
        super.onDestroy();
    }

}
