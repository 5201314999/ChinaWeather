package com.example.administrator.chinaweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.chinaweather.receiver.AutoUpdateReceiver;
import com.example.administrator.chinaweather.util.HttpCallbackListener;
import com.example.administrator.chinaweather.util.HttpUtil;
import com.example.administrator.chinaweather.util.Utility;

/**
 * Created by Administrator on 2015/9/12.
 */
public class AutoUpdateService extends Service{

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        long triggerTime=SystemClock.elapsedRealtime()+8*60*60*1000;
        Intent i=new Intent(this,AutoUpdateReceiver.class);
        PendingIntent pi=PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }
       private void updateWeather(){
           Log.d("test", "test");
           SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
           String weatherCode=prefs.getString("weatherCode", "");
           String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
           HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
               @Override
               public void onFinish(String response) {
                   Utility.handleWeatherResponse(AutoUpdateService.this,response);
               }
               @Override
               public void onError(Exception e) {
                e.printStackTrace();
               }
           });
    }
}
