package kr.go.switchbot3_android;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class SwitchBotService extends IntentService {
    private BluetoothDevice bluetoothDevice; // 블루투스 디바이스
    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private Set<BluetoothDevice> devices; // 블루투스 디바이스 데이터 셋
    private BluetoothSocket bluetoothSocket = null; // 블루투스 소켓
    ConnectedThread connectedThread = null;
    private byte[] readBuffer; // 수신 된 문자열을 저장하기 위한 버퍼
    String btDeviceAddress = "FC:A8:9A:00:91:FE"; //hc - 05
    // String btDeviceAddress = "FC:A8:9A:00:58:1D"; //LEDONOFF
    public static final String ACTION = "kr.go.switchbot3_android.SwitchBotService";
    String sendText;
    public SwitchBotService() {
        super("test-service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Test","onStartCommand");
        if (intent == null) {
            Log.d("Test","Intent is null");
            return Service.START_STICKY; //서비스 종료시 다시 시작
        } else {
            sendText =  intent.getStringExtra("test");
            connectDevice(btDeviceAddress);
            connectedThread.write(sendText);
           /* if(connectedThread != null && connectedThread.isAlive()) { //
                Log.d("Test","Thread alive : "+connectedThread);
                connectedThread.write(sendText);
            } else {
                Log.d("Test","connectedThread not alive : ");
                if(connectDevice(btDeviceAddress)) connectedThread.write(sendText);
                else {
                    Log.d("Test","connection fail ");
                    Toast.makeText(getApplicationContext(),"connection failed!",Toast.LENGTH_SHORT).show();
                    //this.onDestroy();
                }
            }*/
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) { //Activity에서 전달받은 데이터 처리
        //String val = intent.getStringExtra("test");
        //Intent sendIntent = new Intent(ACTION);
//        sendIntent.putExtra("resultCode", Activity.RESULT_OK);
//        sendIntent.putExtra("sendTest","service receive: "+val);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(sendIntent);
    }

    @Override
    public void onDestroy() {

       // if(connectedThread != null && connectedThread.isAlive())  connectedThread.cancel();

        super.onDestroy();
        Log.d("Test","Service Destroy");
    }

    public boolean connectDevice(String device) {
        // 페어링 된 디바이스들을 모두 탐색
        Log.d("Test","sel device : "+device);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터를 디폴트 어댑터로 설정
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(btDeviceAddress);
        // UUID 생성
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        boolean flag = true;
        // 해당 디바이스와 연결하는 함수 호출
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
        } catch (IOException e) {
            flag = false;
            Log.d("Test","connection failed!");
        }
        if(flag) {
            connectedThread = new ConnectedThread(bluetoothSocket);
            connectedThread.start();
            connectedThread.write("Test");
        }
        return flag;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }




    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            byte[] buffer;
            int bytes; // bytes returned from read()
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        buffer = new byte[1024];
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.read(buffer); // record how many bytes we actually read
                        readBuffer = new byte[bytes];
                        System.arraycopy(buffer, 0, readBuffer, 0, bytes);
                        final String text = new String(readBuffer, "US-ASCII");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        public void write(String input) {
            byte[] bytes = input.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void cancel() {
            Log.d("Test","Thread cancel");
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}