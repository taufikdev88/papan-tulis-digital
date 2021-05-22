package com.example.papantulisdigital.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {
    private int i = 0;
    private Socket socket;
    private static PrintWriter output;
    private BufferedReader input;
    Thread receiveThread;
    final Timer timer = new Timer();
    boolean startConnecting = false;
    boolean flagConnecting = false;

    public class ConnectThread implements Runnable {
        private static final String IP_ADDR = "192.168.1.4";
        private static final int PORT = 5000;

        @Override
        public void run() {
            try {
                InetSocketAddress inetAddress = new InetSocketAddress(IP_ADDR, PORT);
                socket = new Socket();
                socket.connect(inetAddress);
                output = new PrintWriter(socket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                receiveThread = new Thread(new ReceiveThread());
                receiveThread.start();

                Log.d("DEBUG_","Connected");
                timer.cancel();
                Intent mainActivity = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainActivity);
            } catch (UnknownHostException e) {
                Log.d("DEBUG_",e.getMessage());
                e.printStackTrace();
                runOnUiThread(() -> { Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_LONG).show(); });

            } catch (IOException e) {
                Log.d("DEBUG_",e.getMessage());
                e.printStackTrace();
                runOnUiThread(() -> { Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_LONG).show(); });
            }
        }
    }

    public class ReceiveThread implements Runnable {
        @Override
        public void run() {
            while (true){
                if(receiveThread.isInterrupted()){
                    Log.d("DEBUG_","receive thread is interrupted");
                    break;
                }
                try {
                    final String message = input.readLine();
                    if(message != null){
                        Log.d("DEBUG_", "got message: " + message);
                    }
                } catch (IOException e) {
                    Log.d("DEBUG_",e.getMessage());
                    e.printStackTrace();
                    runOnUiThread(() -> { Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_LONG).show(); });
                }
            }
        }
    }

    public static class SendThread implements Runnable {
        private String message;
        public SendThread(String m){
            this.message = m;
        }

        @Override
        public void run() {
            output.write(message);
            output.flush();
            output.write("end");
            output.flush();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView txtSplashInfo = findViewById(R.id.splashInfo);

        final String[] SPLASH_INFO = {
                getResources().getString(R.string.splash_1),
                getResources().getString(R.string.splash_2),
                getResources().getString(R.string.splash_3)
        };

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> txtSplashInfo.setText(SPLASH_INFO[i]));
                Log.d("DEBUG_",String.valueOf(i));
                if(++i >= SPLASH_INFO.length){
                    startConnecting = true;
                    i = 0;

//                    timer.cancel();
//                    Intent mainActivity = new Intent(SplashActivity.this, MainActivity.class);
//                    startActivity(mainActivity);

                    if(startConnecting && !flagConnecting){
                        flagConnecting = true;
                        new Thread(new ConnectThread()).start();
                    }
                }
            }
        }, 0, 3000);
    }
}
