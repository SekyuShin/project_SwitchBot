package kr.go.switchbot3_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

public class TerminalActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

        Intent intent = new Intent(getApplicationContext(), SwitchBotService.class);
        intent.putExtra("Message", "Test Service Intent"); //필요시 인텐트에 필요한 데이터를 담아준다
        startService(intent);

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
        }
    };
}