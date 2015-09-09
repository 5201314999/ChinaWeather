package com.example.administrator.chinaweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.chinaweather.R;
import com.example.administrator.chinaweather.db.ChinaWeatherDB;
import com.example.administrator.chinaweather.model.City;
import com.example.administrator.chinaweather.model.County;
import com.example.administrator.chinaweather.model.Province;
import com.example.administrator.chinaweather.util.HttpCallbackListener;
import com.example.administrator.chinaweather.util.HttpUtil;
import com.example.administrator.chinaweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/4.
 */
public class ChooseAreaActivity extends Activity {
    private static final int LEVEL_PROVINCE=0;
    private static final int LEVEL_CITY=1;
    private static final int LEVEL_COUNTY=2;
    private ArrayAdapter<String> adapter;
    private ProgressDialog progressDialog;
    private ChinaWeatherDB chinaWeatherDB;
    private ListView listView;
    private List<String> list;
    private TextView textView;
    private  int level;
    private  List<Province> provinceList=new ArrayList<Province>();
    private List<City> cityList=new ArrayList<City>();
    private List<County> countyList=new ArrayList<County>();
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.setContentView(R.layout.choose_area);
            list=new ArrayList<String>();
            listView=(ListView)findViewById(R.id.listview);
            textView=(TextView)findViewById(R.id.title_text);
            textView.setText("中国");
            adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
            listView.setAdapter(adapter);
            chinaWeatherDB=ChinaWeatherDB.getInstance(this);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(level==LEVEL_PROVINCE){
                        queryCities(provinceList.get(position));
                        selectedProvince=provinceList.get(position);
                    }
                    else if(level==LEVEL_CITY){
                        queryCounties(cityList.get(position));
                        selectedCity=cityList.get(position);
                    }
                }
            });
            queryProvinces();
    }
    private void queryProvinces(){
        provinceList=chinaWeatherDB.loadProvince();
        if(provinceList!=null&&provinceList.size()>0) {
            list.clear();
            for (Province p : provinceList) {
                list.add(p.getName());
                Log.d(p.getCode(),p.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            level = LEVEL_PROVINCE;
        }
        else{
            queryFromSever(null,"province");
        }
    }
    private void queryCities(Province p){
        cityList=chinaWeatherDB.loadcity(p.getId());
        if(cityList.size()>0){
            list.clear();
            for(City c:cityList){
                list.add(c.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(p.getName());
            level=LEVEL_CITY;
        }
        else{
            queryFromSever(p.getCode(), "city");
        }
    }
    private void queryCounties(City city){
        countyList=chinaWeatherDB.loadcounty(city.getCityId());
        if(countyList.size()>0){
            list.clear();
            for(County c:countyList){
                list.add(c.getCountyName());
            }
            textView.setText(city.getCityName());
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            level=LEVEL_COUNTY;
        }
        else{
            queryFromSever(city.getCityCode(), "county");
        }
    }
    private void queryFromSever(String code,final String type){
        String url;
        if(code==null){
            url="http://www.weather.com.cn/data/list3/city.xml";
        }
        else{
            url="http://www.weather.com.cn/data/list3/city"+code+".xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(url,new HttpCallbackListener(){
            @Override
            public void onFinish(String response) {
                Log.d("111111111",response);
                Boolean result;
                if ("province".equals(type))
                    result = Utility.handleProvinceResponse(chinaWeatherDB, response);
                else if ("city".equals((type)))
                    result = Utility.handleCityResponse(chinaWeatherDB, response, selectedProvince.getId());
                else
                    result = Utility.handleCountyResponce(chinaWeatherDB, response, selectedCity.getCityId());
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type))
                                queryProvinces();
                            else if ("city".equals(type))
                                queryCities(selectedProvince);
                            else
                                queryCounties(selectedCity);
                        }
                    });
                }
            }
            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }
        });

    }
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if(level==LEVEL_COUNTY)
            queryCities(selectedProvince);
        else if(level==LEVEL_CITY)
            queryProvinces();
        else
            finish();
    }
}
