package kr.go.switchbot3_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Switch switchOnOff;
    Button terminalBtn;
    Button testBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchOnOff = (Switch) findViewById(R.id.onOffSwitch);
        switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(getApplicationContext(), SwitchBotService.class);
                if(isChecked) intent.putExtra("Message", "ON"); //필요시 인텐트에 필요한 데이터를 담아준다
                else intent.putExtra("Message", "OFF"); //필요시 인텐트에 필요한 데이터를 담아준다
                startService(intent);
            }
        });

        terminalBtn = (Button)findViewById(R.id.terminalBtn);
        terminalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),TerminalActivity.class);
                startActivity(intent);
            }
        });
        testBtn = (Button)findViewById(R.id.testBtn);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SwitchBotService.class);
                intent.putExtra("Message", "INIT"); //필요시 인텐트에 필요한 데이터를 담아준다
                startService(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(getApplicationContext(), SwitchBotService.class);
        intent.putExtra("Message", "INIT"); //필요시 인텐트에 필요한 데이터를 담아준다
        startService(intent);
    }
}