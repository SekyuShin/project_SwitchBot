package com.example.switchbot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.text.SimpleDateFormat;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {


    private TextView textViewReceive; // 수신 된 데이터를 표시하기 위한 텍스트 뷰
    private EditText editTextSend; // 송신 할 데이터를 작성하기 위한 에딧 텍스트
    private Button buttonSend; // 송신하기 위한 버튼
    private Switch ledSwitch;
    private static final int REQUEST_ENABLE_BT = 10; // 블루투스 활성화 상태
    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터

    String initStr;
    Intent passedIntent;
    Intent intent ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passedIntent= getIntent();
        processCommand(passedIntent);
        intent = new Intent(getApplicationContext(), BluetoothService.class); // 실행시키고픈 서비스클래스 이름
        setContentView(R.layout.activity_main);
        // 각 컨테이너들의 id를 매인 xml과 맞춰준다.
        textViewReceive = (TextView) findViewById(R.id.textView_receive);
        editTextSend = (EditText) findViewById(R.id.editText_send);
        buttonSend = (Button) findViewById(R.id.button_send);
        ledSwitch = (Switch) findViewById(R.id.ledSwitch);

        Date dt = new Date();
        SimpleDateFormat hour = new SimpleDateFormat("HH");
        SimpleDateFormat minute = new SimpleDateFormat("mm");
        SimpleDateFormat second = new SimpleDateFormat("ss");
        Log.d("Test",hour.format(dt).toString()+":"+minute.format(dt).toString()+":"+second.format(dt).toString());
        initStr= "INIT"+hour.format(dt).toString()+":"+minute.format(dt).toString()+":"+second.format(dt).toString()+":15:50:75:0500:2300";
        Log.d("Test",initStr);

        // 블루투스 활성화하기
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터를 디폴트 어댑터로 설정
        if (bluetoothAdapter == null) { // 디바이스가 블루투스를 지원하지 않을 때
            // 여기에 처리 할 코드를 작성하세요.
        } else { // 디바이스가 블루투스를 지원 할 때
            if (bluetoothAdapter.isEnabled()) { // 블루투스가 활성화 상태 (기기에 블루투스가 켜져있음)

                intent.putExtra("strText", initStr); //필요시 인텐트에 필요한 데이터를 담아준다
                startService(intent); // 서비스 실행!
                //connectDevice(btDeviceAddress);

                //selectBluetoothDevice(); // 블루투스 디바이스 선택 함수 호출
            } else { // 블루투스가 비 활성화 상태 (기기에 블루투스가 꺼져있음)
                // 블루투스를 활성화 하기 위한 다이얼로그 출력
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // 선택한 값이 onActivityResult 함수에서 콜백된다.
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            }

        }
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent.putExtra("strText", editTextSend.getText().toString()); //필요시 인텐트에 필요한 데이터를 담아준다
                startService(intent); // 서비스 실행!
            }
        });
        ledSwitch.setOnCheckedChangeListener(new OnOffSwitchListener());

    }
    protected void onNewIntent(Intent intent) {
        processCommand(passedIntent);

        super.onNewIntent(intent);
    }

    private void processCommand(Intent intent) {
        if (intent != null) {
            String recievText = intent.getStringExtra("recievText");
            //Toast.makeText(this, "서비스로부터 전달받은 데이터: " + recievText, Toast.LENGTH_LONG).show();
            Log.d("Test","서비스로부터 전달받은 데이터: " + recievText);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (requestCode == RESULT_OK) { // '사용'을 눌렀을 때
                    intent.putExtra("strText", initStr); //필요시 인텐트에 필요한 데이터를 담아준다
                    startService(intent); // 서비스 실행!
                } else { // '취소'를 눌렀을 때
                    // 여기에 처리 할 코드를 작성하세요.
                }
                break;
        }
    }
    class OnOffSwitchListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String onOff;
            if(isChecked)  onOff = "ON";
            else onOff = "OFF";

            intent.putExtra("strText", onOff); //필요시 인텐트에 필요한 데이터를 담아준다
            startService(intent); // 서비스 실행!

        }
    }


}




