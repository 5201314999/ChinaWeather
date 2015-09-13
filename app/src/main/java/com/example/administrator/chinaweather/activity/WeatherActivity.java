package com.example.administrator.chinaweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.administrator.chinaweather.R;
import com.example.administrator.chinaweather.service.AutoUpdateService;
import com.example.administrator.chinaweather.util.HttpCallbackListener;
import com.example.administrator.chinaweather.util.HttpUtil;
import com.example.administrator.chinaweather.util.Utility;
/**
 * Created by Administrator on 2015/9/10.
 */
public class WeatherActivity extends Activity {
    private LinearLayout weatherLayout;
    private TextView citynameText;
    private TextView publishTimeText;
    private TextView weatherDspText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentTimeText;
    private ImageView refresh;
    private ImageView hostpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather);
        weatherLayout=(LinearLayout)findViewById(R.id.contentPanel);
        citynameText=(TextView)findViewById(R.id.city_name);
        publishTimeText=(TextView)findViewById(R.id.pushTime);
        weatherDspText=(TextView)weatherLayout.findViewById(R.id.weather);
        temp1Text=(TextView)weatherLayout.findViewById(R.id.maxdegree);
        temp2Text=(TextView)weatherLayout.findViewById(R.id.mindegree);
        currentTimeText=(TextView)weatherLayout.findViewById(R.id.date);
        String countyCode=getIntent().getStringExtra("county_code");
        if(!TextUtils.isEmpty(countyCode)){
            Log.d("pogai", countyCode);
            publishTimeText.setText("同步中");
            weatherLayout.setVisibility(View.INVISIBLE);
            citynameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);

        }
        else{
            showWeather();
        }
        refresh=(ImageView)findViewById(R.id.update);
        hostpage=(ImageView)findViewById(R.id.hostpage);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        hostpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_hostpage();
            }
        });


    }
    private void queryWeatherCode(String countyCode){
        String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        queryFromSever(address,"countyCode");
    }
    private void queryWeatherInfo(String weatherCode){
        String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        queryFromSever(address,"weatherCode");
    }
    private void queryFromSever(String address,final String type){
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                publishTimeText.setText("同步失败");
            }
        });
    }
    private void showWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        citynameText.setText(prefs.getString("city_name",""));
        temp1Text.setText(prefs.getString("temp1",""));
        temp2Text.setText(prefs.getString("temp2",""));
        weatherDspText.setText(prefs.getString("weatherDesp",""));
        publishTimeText.setText("今天"+prefs.getString("pushTime","")+"发布");
        currentTimeText.setText(prefs.getString("current_date", ""));
        weatherLayout.setVisibility(View.VISIBLE);
        citynameText.setVisibility(View.VISIBLE);
        Intent i=new Intent(WeatherActivity.this, AutoUpdateService.class);
        startService(i);
    }
    private void refresh(){
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode=prefs.getString("weaterCode","");
        queryWeatherInfo(weatherCode);
    }
    private void btn_hostpage(){
        Intent i=new Intent(WeatherActivity.this,ChooseAreaActivity.class);
        i.putExtra("isFromWeatherActivity",true);
        startActivity(i);
        finish();
    }
}
