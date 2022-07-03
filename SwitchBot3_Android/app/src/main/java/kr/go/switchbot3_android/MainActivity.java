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
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button testBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testBtn = (Button)findViewById(R.id.testBtn);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SwitchBotService.class);
                intent.putExtra("test", "Test Service Intent"); //필요시 인텐트에 필요한 데이터를 담아준다
                startService(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(SwitchBotService.ACTION);
        Log.d("Test","activity resume");
        LocalBroadcastManager.getInstance(this).registerReceiver(testReceiver,filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Test","activity onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(testReceiver);
    }
    private BroadcastReceiver testReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode",RESULT_CANCELED);
            Log.d("Test","result Code : "+resultCode);
            if(resultCode == RESULT_OK) {
                String resultVal = intent.getStringExtra("sendTest");
                Log.d("Test","activity receive : "+resultVal);
                Toast.makeText(MainActivity.this, resultVal,Toast.LENGTH_SHORT).show();
            }
        }
    };
}