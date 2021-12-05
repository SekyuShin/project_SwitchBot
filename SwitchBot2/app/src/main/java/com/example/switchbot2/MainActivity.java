package com.example.switchbot2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    TextView recieverText;
    EditText sendText;
    Button sendBtn;
    String sendStr = "";
    Intent intent;
    TextFile textFile;

    EditText posOn;
    EditText posCommon;
    EditText posOff;
    EditText onTimerText;
    EditText offTimerText;
    Button saveBtn;
    Switch onOffSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Test","onCreate ");
        recieverText = (TextView) findViewById(R.id.recieverText);
        sendText = (EditText)findViewById(R.id.sendText);
        sendBtn = (Button)findViewById(R.id.sendBtn);
        posOn = (EditText)findViewById(R.id.posOn);
        posCommon = (EditText)findViewById(R.id.posCommon);
        posOff = (EditText)findViewById(R.id.posOff);
        onTimerText = (EditText)findViewById(R.id.onTimerText);
        offTimerText = (EditText)findViewById(R.id.offTimerText);
        saveBtn = (Button)findViewById(R.id.saveBtn);
        onOffSwitch = (Switch)findViewById(R.id.onOffSwitch);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String onOff;
                if(isChecked)  onOff = "ON";
                else onOff = "OFF";

                intent.putExtra("strText", onOff); //필요시 인텐트에 필요한 데이터를 담아준다
                startService(intent); // 서비스 실행!
            }
        });

        intent = new Intent(getApplicationContext(), BluetoothService.class); // 실행시키고픈 서비스클래스 이름
        textFile = new TextFile();
        Log.d("Test","onCreate ");
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendStr = sendText.getText().toString();
                sendText.setText("");
                //textFile.setSaveText(text);
                intent.putExtra("strText", sendStr); //필요시 인텐트에 필요한 데이터를 담아준다
                startService(intent);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text;
                text = ":"+posOn.getText().toString()+":"+posCommon.getText().toString()+":"+posOff.getText().toString()+":"+onTimerText.getText().toString()+":"+offTimerText.getText().toString();
                textFile.setSaveText(text);
                intent.putExtra("strText", textFile.GetInitText()); //필요시 인텐트에 필요한 데이터를 담아준다
                startService(intent);
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Test","onStart ");
        String text = textFile.getSaveText();
        Log.d("Test","text : "+text);
        if(text == null) {
            text = ":15:50:75:0500:2300";
            textFile.setSaveText(text);
            sendText.setText(textFile.GetInitText());
        }else {
            posOn.setText(textFile.GetPosOn());
            posCommon.setText(textFile.GetPosCommon());
            posOff.setText(textFile.GetPosOff());
            onTimerText.setText(textFile.GetOnTimer());
            offTimerText.setText(textFile.GetOffTimer());
            sendText.setText(textFile.GetInitText());
        }
       // startService(intent);
        //recieverText.setText(text);
    }

}