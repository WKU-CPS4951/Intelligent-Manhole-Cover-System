package com.wku.game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wku.unitylib.UDP;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    WifiManager.MulticastLock lock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //authority
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        WifiManager.MulticastLock lock = wifiManager.createMulticastLock("test wifi");
        lock.acquire();

        //UI
        setContentView(R.layout.activity_main);
        Button btn_send = findViewById(R.id.btn_send);
        TextView output = findViewById(R.id.txt_output);
        EditText input = findViewById(R.id.txt_input);
        UDP udp = new UDP();

        //handler to handle received messages
        Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String str = new String(msg.getData().getByteArray("bytes"));
                output.setText(str);
            }
        };

        //click event
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String sstr[] = {input.getText().toString()};
                    udp.send(sstr);
                    udp.new ReceiveThread(handler).start();
                }catch (IOException e){
                    Log.d(TAG, "run: "+e.getMessage());
                }catch (InterruptedException e1){
                    Log.d(TAG, "run: "+e1.getMessage());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lock.release();
    }
}