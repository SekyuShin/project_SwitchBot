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
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class TerminalActivity extends AppCompatActivity {

    TextView rcvText;
    Button sendBtn;
    EditText sendText;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

        Intent intent = new Intent(getApplicationContext(), SwitchBotService.class);
        intent.putExtra("Message", "TERMINAL.START");
        startService(intent);

        rcvText = (TextView)findViewById(R.id.rcvText);
        sendText = (EditText) findViewById(R.id.sendText);
        sendBtn= (Button) findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmpText = sendText.getText().toString();
                sendText.setText("");
                if(tmpText.length() != 0) {
                    Intent intent = new Intent(getApplicationContext(), SwitchBotService.class);
                    Log.d("Test","TErmianl send text : "+tmpText);
                    intent.putExtra("Message", "TERMINAL."+tmpText);
                    startService(intent);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(SwitchBotService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(testReceiver,filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(testReceiver);
    }
    private BroadcastReceiver testReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String resultVal = intent.getStringExtra("Message");
            Log.d("Test","Terminal Activity receive : "+resultVal);
            rcvText.append(resultVal);
        }
    };
}