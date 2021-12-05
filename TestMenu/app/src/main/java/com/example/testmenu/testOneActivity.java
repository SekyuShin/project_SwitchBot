package com.example.testmenu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class testOneActivity extends AppCompatActivity {
    BackGroundControl bgc = new BackGroundControl();

    LayoutInflater layoutInflaterMain ;
    View layoutExampleMain;
    LayoutInflater layoutInflater ;
    View layoutExample;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutInflater = this.getLayoutInflater();
        layoutExample = layoutInflater.inflate(R.layout.imagelayout, null);
        layoutInflaterMain = this.getLayoutInflater();
        layoutExampleMain = layoutInflaterMain.inflate(R.layout.activity_test_one, null);
        setContentView(layoutExampleMain);

        Button btn = (Button)findViewById(R.id.btn_one_main);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
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