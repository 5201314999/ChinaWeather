package com.example.administrator.chinaweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.administrator.chinaweather.db.ChinaWeatherDB;
import com.example.administrator.chinaweather.model.City;
import com.example.administrator.chinaweather.model.County;
import com.example.administrator.chinaweather.model.Province;

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
        if(response.length()>0){
            String [] counties=response.split(",");
            if(counties.length>0){
                for(String c:counties){
                    String []array=c.split("\\|");
                    County county=new County();
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
}
