package com.jhy.org.yueqiu.activity;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.jhy.org.yueqiu.R;
import com.jhy.org.yueqiu.adapter.PlaceAdapter;
import com.jhy.org.yueqiu.config.MyApplication;
import com.jhy.org.yueqiu.domain.Place;
import com.jhy.org.yueqiu.test.h.OnReceiveUserLocationListener;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobGeoPoint;

/*
 **********************************************
 * 			所有者 H: (黄振梓)
 **********************************************
 */
public class SearchPlaceActivity extends Activity implements OnGetPoiSearchResultListener, OnReceiveUserLocationListener {
    private PoiSearch poiSearch;
    private PoiNearbySearchOption searchOption;
    private LatLng userLocation = null;

    private List<Place> placeList;
    private PlaceAdapter adapter;
    private ListView lv_places;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            Log.i("ilog:", "handleMessage, 准备填充ListView");
            lv_places.setAdapter(adapter);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);

        this.poiSearch = PoiSearch.newInstance();
        this.searchOption = new PoiNearbySearchOption()
                .radius(10000)
                .keyword("篮球场")
                .pageNum(0);
        this.placeList = new ArrayList<Place>();
        this.adapter = new PlaceAdapter(this, placeList);
        this.lv_places = (ListView) findViewById(R.id.lv_places);

        poiSearch.setOnGetPoiSearchResultListener(this);
        MyApplication.registerReceiveUserLocation(this);
    }

    @Override
    public void onReceiveUserLocation(LatLng userLocation) {
        this.userLocation = userLocation;
        adapter.setUserLocation(userLocation);
        searchOption.location(userLocation);
        poiSearch.searchNearby(searchOption);
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
//        Log.i("ilog:SearchActivity", "onGetPoiResult调用成功");
        if (poiResult == null || poiResult.error != SearchResult.ERRORNO.NO_ERROR) {
            return;
        }
        for (PoiInfo poi : poiResult.getAllPoi()) {
//            Log.i("ilog: SearchActivity", "name:" + poi.name + ", address:" + poi.address + ", uid:" + poi.uid);
//            Log.i("ilog:", "\t\t latitude:" + poi.location.latitude + ", langitude:" + poi.location.longitude);

            BmobGeoPoint point = new BmobGeoPoint();
            point.setLatitude(poi.location.latitude);
            point.setLongitude(poi.location.longitude);

            Place place = new Place();
            place.setName(poi.name);
            place.setAddress(poi.address);
            place.setLocation(point);
            place.setUid(poi.uid);
            placeList.add(place);
        }
        handler.sendEmptyMessage(1);
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    public void finish (View view) {
        finish();
    }
}
