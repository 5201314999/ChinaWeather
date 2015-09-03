package com.example.administrator.chinaweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/9/2.
 */
public class ChinaWeatherOpenHelper extends SQLiteOpenHelper {
    public final static  String CREATE_PROVINCE="create table province(id integer primary key autoincrement," +
            "province_name text,province_code text)";
    public final  static String CREATE_CITY="create table city(id integer primary " +
            "key autoincrement city_name text, city_code text, province_id text)";
    public final static String CREATE_COUNTY="create table county(id integer primary key" +
            "autoincrement,county_name text,county_code text,city_id integer)";


    public ChinaWeatherOpenHelper(Context context,String name,SQLiteDatabase.CursorFactory factory
    ,int version){
        super(context, name, factory, version);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
