package com.example.administrator.chinaweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.chinaweather.model.City;
import com.example.administrator.chinaweather.model.County;
import com.example.administrator.chinaweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/3.
 */
public class ChinaWeatherDB {
    public static final String db_name="china_weather";
    public  static int version=1;
    private static ChinaWeatherDB chinaWeatherDB;
    private SQLiteDatabase db;

    private ChinaWeatherDB(Context context) {
        ChinaWeatherOpenHelper chinaWeatherOpenHelper = new ChinaWeatherOpenHelper(context, db_name, null, version);
        db = chinaWeatherOpenHelper.getWritableDatabase();
    }
    public synchronized static ChinaWeatherDB getInstance(Context context){
        if(chinaWeatherDB==null){
            ChinaWeatherDB chinaWeatherDB=new ChinaWeatherDB(context);
        }
        return chinaWeatherDB;
    }
    public void saveProvince(Province province){
        if(province!=null){
            ContentValues values=new ContentValues();
            values.put("province_name",province.getName());
            values.put("province_code",province.getCode());
            db.insert("province",null,values);
        }
    }
    public List<Province> loadProvince(){
        List<Province> provinceList=new ArrayList<Province>();
        Cursor cursor=db.query("province",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            Province province=new Province();
            do{
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setCode(cursor.getString(cursor.getColumnIndex("province_code")));
                provinceList.add(province);
            }while(cursor.moveToNext());
        }
        if(cursor!=null)
         cursor.close();
        return provinceList;
    }
    public void saveCity(City city){
        if(city!=null) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id",city.getProvinceId());
            db.insert("city", null, values);
        }
    }
     public List<City> loadcity(int provinceId){
         List<City> cityList=new ArrayList<City>();
         Cursor cursor=db.query("city",null,"province_id=?",new String[]{String.valueOf(provinceId)},null,null,null);
         if(cursor.moveToFirst()) {
             City city = new City();
             do {
                 city.setCityId(cursor.getInt(cursor.getColumnIndex("id")));
                 city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                 city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                 city.setProvinceId(provinceId);
                 cityList.add(city);
             } while (cursor.moveToNext());
         }
             if(cursor!=null){
                 cursor.close();
             }
         return cityList;
         }
    public void saveCounty(County county){
        if(county!=null){
            ContentValues values=new ContentValues();
            values.put("county_name",county.getCountyName());
            values.put("county_code",county.getCountyCode());
            values.put("city_code",county.getCityId());
            db.insert("county", null, values);
        }
    }
    public List<County> loadcounty(int cityId){
        List<County> countyList=new ArrayList<County>();
        Cursor cursor=db.query("county",null,"city_id=?",
                new String[]{String.valueOf(cityId)},null,null,null);
        if(cursor.moveToFirst()) {
            County county = new County();
            do {
                county.setCountyId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cityId);
                countyList.add(county);
            } while (cursor.moveToNext());
        }
        if(cursor!=null){
            cursor.close();
        }
        return countyList;
    }








}
