package com.example.switchbot2;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothService extends Service {
    String text;
    TextFile textFile;
    String btDeviceAddress = "FC:A8:9A:00:91:FE"; //hc - 05
   // String btDeviceAddress = "FC:A8:9A:00:58:1D"; //LEDONOFF

    private BluetoothDevice bluetoothDevice; // 블루투스 디바이스
    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private Set<BluetoothDevice> devices; // 블루투스 디바이스 데이터 셋
    private BluetoothSocket bluetoothSocket = null; // 블루투스 소켓
    ConnectedThread connectedThread;
    private byte[] readBuffer; // 수신 된 문자열을 저장하기 위한 버퍼
    String strText;


    public BluetoothService() {
        Log.d("Test","Service BluetoothService!");
        textFile = new TextFile();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Test","Service onCreate start!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        text = textFile.getSaveText();
        Log.d("Test","Service onStartCommand : "+text);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터를 디폴트 어댑터로 설정
        //Log.d("Test","Service onStartCommand2 : ");//bluetoothAdapter.isEnabled());
        if (intent == null) {
            return Service.START_STICKY; //서비스가 종료되어도 자동으로 다시 실행시켜줘!
        } else {

            // intent가 null이 아니다.
            // 액티비티에서 intent를 통해 전달한 내용을 뽑아낸다.(if exists)
            strText= intent.getStringExtra("strText");
            Log.d("Test", "test  : "+strText);
            Log.d("Test", "test2  : ");
            if(connectedThread != null && connectedThread.isAlive()) {
                connectedThread.write(strText);
            } else {
                connectDevice(btDeviceAddress);
            }

            Log.d("Test", "전달받은 데이터: " + strText);
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Test","Service  destroy!");
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void connectDevice(String device) {
        // 페어링 된 디바이스들을 모두 탐색
        Log.d("Test","sel device : "+device);
        devices = bluetoothAdapter.getBondedDevices();
        for(BluetoothDevice tempDevice : devices) {
            // 사용자가 선택한 이름과 같은 디바이스로 설정하고 반복문 종료
            if(device.equals(tempDevice.getAddress())) {
                bluetoothDevice = tempDevice;
                break;
            }
        }

        // UUID 생성
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        Log.d("Test","sel device : "+bluetoothDevice);
        // Rfcomm 채널을 통해 블루투스 디바이스와 통신하는 소켓 생성
        boolean flag = true;
        // 해당 디바이스와 연결하는 함수 호출
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();

        } catch (IOException e) {
            flag = false;
            Log.d("Test","connection failed!");
            e.printStackTrace();
        }
        if(flag) {
            connectedThread = new ConnectedThread(bluetoothSocket);
            connectedThread.start();
            connectedThread.write(strText);
        }


    }
    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;


            write(strText);
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        buffer = new byte[1024];
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        //bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer); // record how many bytes we actually read
//                        byte[] encodedBytes = new byte[bytes];
                        readBuffer = new byte[bytes];
                        System.arraycopy(buffer, 0, readBuffer, 0, bytes);
                        final String text = new String(readBuffer, "US-ASCII");
                        //bytes = 0;
                        Intent showIntent = new Intent(getApplicationContext(), MainActivity.class);

                        /**
                         화면이 띄워진 상황에서 다른 액티비티를 호출하는 것은 문제가없으나,
                         지금은 따로 띄워진 화면이 없는 상태에서 백그라운드에서 실행중인 서비스가 액티비티를 호출하는 상황이다.
                         이러한 경우에는 FLAG_ACTIVITY_NEW_TASK 옵션(플래그)을 사용해줘야 한다.
                         **/
                        showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        showIntent.putExtra("recievText", text);
                        // *** 이제 완성된 인텐트와 startActivity()메소드를 사용하여 MainActivity 액티비티를 호출한다. ***
                        startActivity(showIntent);
                        Log.d("Test","REcieve : "+text);
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
                Log.d("Test", "write: " + bytes);
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

}