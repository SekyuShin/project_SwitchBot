package com.example.testmenu;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class BackGroundControl {//extends AppCompatActivity {
    //LayoutInflater layoutInflater;
    //View layoutExample;
    static boolean checkSwitchActivity = false;
    BackGroundControl() {
        //layoutInflater = this.getLayoutInflater();
        //layoutExample = layoutInflater.inflate(R.layout.imagelayout, null);
    }

//    public void startActivity(Intent intent) {
//        checkSwitchActivity=true;
//        Log.d("Test","startActivity start");
//        startActivity(intent);
//        checkSwitchActivity=false;
//    }

    public void CheckSwitchActivity() {
        checkSwitchActivity = true;
    }
    public void UncheckSwitchActivity() {
        checkSwitchActivity = false;
    }
    public boolean getCheckSwitching(){
        Log.d("Test","check : "+checkSwitchActivity);
        return checkSwitchActivity;
    }
//    public View checkSwitching() {
//        if(!checkSwitchActivity) {
//            return layoutExample;
//        } else {
//            return null;
//        }
//    }
}
