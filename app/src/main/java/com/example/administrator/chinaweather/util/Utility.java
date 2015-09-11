package com.example.administrator.chinaweather.util;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import com.example.administrator.chinaweather.db.ChinaWeatherDB;
import com.example.administrator.chinaweather.model.City;
import com.example.administrator.chinaweather.model.County;
import com.example.administrator.chinaweather.model.Province;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2015/9/4.
 */
public class Utility {
    public synchronized static boolean handleProvinceResponse(ChinaWeatherDB chinaWeatherDB,
                                                              String response){
        if(!TextUtils.isEmpty(response)){
            String [] provinces=response.split(",");
                if(provinces.length>0){
                for(String p:provinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setCode(array[0]);
                    province.setName(array[1]);
                    Log.d(array[0], array[1]);
                    chinaWeatherDB.saveProvince(province);
                }
            }
            return true;
        }
      return  false;
    }
    public synchronized static boolean handleCityResponse(ChinaWeatherDB chinaWeatherDB,String response,
                                                          int provinceId){
        if(!TextUtils.isEmpty(response)) {

            String[] cities = response.split(",");
            if (cities.length > 0) {
                for (String c : cities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    chinaWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }
    public synchronized static boolean handleCountyResponce(ChinaWeatherDB chinaWeatherDB, String response,int cityId ){
        if(!TextUtils.isEmpty(response)) {
            String[] counties = response.split(",");
            if (counties.length > 0) {
                for (String c : counties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    chinaWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
                return false;
    }
    public static void handleWeatherResponse(Context context,String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            JSONObject weatherinfo=jsonObject.getJSONObject("weatherinfo");
            String cityName=weatherinfo.getString("city");
            String weatherCode=weatherinfo.getString("cityid");
            String temp1=weatherinfo.getString("temp1");
            String temp2=weatherinfo.getString("temp2");
            String weatherDesp=weatherinfo.getString("weather");
            String pushTime=weatherinfo.getString("ptime");
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,pushTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public static void saveWeatherInfo(Context context,String cityName,String weatherCode,
                                       String temp1,String temp2,String weatherDesp,String pushTime){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyƒÍM‘¬d»’");
        //SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("weatherCode",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weatherDesp",weatherDesp);
        editor.putString("pushTime",pushTime);
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();
    }
}
