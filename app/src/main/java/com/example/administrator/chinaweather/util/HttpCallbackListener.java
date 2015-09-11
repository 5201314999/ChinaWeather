package com.example.administrator.chinaweather.util;

/**
 * Created by Administrator on 2015/9/4.
 */
public interface  HttpCallbackListener{
    public  void onFinish(String response);

    public void onError(Exception e);

}
