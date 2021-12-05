package com.example.wifibroadcasttest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch(intent.getAction()) {
            //와이파이 상태변화
            case WifiManager.WIFI_STATE_CHANGED_ACTION:
                //와이파이 상태값 가져오기
                int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                switch (wifistate) {
                    case WifiManager.WIFI_STATE_DISABLING: //와이파이 비활성화중
                        Log.d("Test","와이파이 비활성화중");
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:  //와이파이 비활성화
                        Log.d("Test","와이파이 비활성화");
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:  //와이파이 활성화중
                        Log.d("Test","와이파이 활성화중");
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:   //와이파이 활성화
                        Log.d("Test","와이파이 활성화");
                        break;
                    default:
                        Log.d("Test","알수없음");
                        break;
                }
                break;


        }


    }
}