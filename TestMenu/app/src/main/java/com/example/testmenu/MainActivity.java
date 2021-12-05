package com.example.testmenu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
   // private Dialog dialog = null;
    BackGroundControl bgc = new BackGroundControl();

    LayoutInflater layoutInflater;
    View layoutExample;
    LayoutInflater layoutInflaterMain ;
    View layoutExampleMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bgc.UncheckSwitchActivity();
        layoutInflater = this.getLayoutInflater();
        layoutExample = layoutInflater.inflate(R.layout.imagelayout, null);
        layoutInflaterMain = this.getLayoutInflater();
        layoutExampleMain = layoutInflaterMain.inflate(R.layout.activity_main, null);
        setContentView(layoutExampleMain);
//        dialog = new Dialog(this, android.R.style.Theme_Light);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.imagelayout);
        //getWindow().setContentView(layoutExample, WindowManager.LayoutParams.FLAG_SECURE);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        //getWindow().setFlags();
        //getWindow().setContentView(layoutExample, WRAP_CONTENT);
        //getWindow().setFlags(ImageJava.class, WindowManager.LayoutParams.FLAG_SECURE);

        Button btn_one = (Button)findViewById(R.id.btn_one);
        Button btn_two = (Button)findViewById(R.id.btn_two);


        btn_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),testOneActivity.class);
                bgc.CheckSwitchActivity();
                startActivity(intent);
            }
        });
        btn_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),testTwoActivity.class);
                bgc.CheckSwitchActivity();
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onPause() {
        Log.d("Test","main Pause");
        if(!bgc.getCheckSwitching()) {
            setContentView(layoutExample);
        }

        //dialog.show();
        super.onPause();
    }

    @Override
    protected void onResume() {
        //dialog.hide();
       // LayoutInflater layoutInflater = this.getLayoutInflater();
       // View layoutExample = layoutInflater.inflate(R.layout.activity_main, null);
        if(!bgc.getCheckSwitching()) {
            setContentView(layoutExampleMain);
        } else {
            bgc.UncheckSwitchActivity();
        }

        super.onResume();
    }
}