package com.example.houtak.tanas;

import android.app.Notification;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.houtak.tanas.App.CHANNEL_1_ID;

public class timertask extends AppCompatActivity {

    private Timer timer;
    private TimerTask timerTask;
    private Handler handler = new Handler();
    Button startbtn,stopbtn,notifybtn;
    TextView numtv;
    int num=0;
    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timertask);

        startbtn = findViewById(R.id.startTimer);
        stopbtn = findViewById(R.id.stopTimer);
        numtv = findViewById(R.id.textView);
        notifybtn = findViewById(R.id.notify);

        notificationManager = NotificationManagerCompat.from(this);

        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
            }
        });

        notifybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendOnChannel1();
                Intent serviceIntent = new Intent(timertask.this, CheckNumService.class);
                serviceIntent.putExtra("inputExtra", "lala");

                ContextCompat.startForegroundService(timertask.this, serviceIntent);
            }
        });
    }

    private void stopTimer(){
        if(timer != null){
            timer.cancel();
            timer.purge();
            sendOnChannel1();

        }
    }

    //To start timer
    private void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        //Log.d("lala", String.valueOf(num));
                       /* if(num==10) {
                            //sendOnChannel1();
                            Log.d("lala","reach");
                            //stopTimer();
                            sendOnChannel1();
                        }
                        num++;
                        */
                        String forgotemail = "test@tester.com";
                        sendPassword(forgotemail);
                    }
                });
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }

    public void setText(){
        numtv.setText(num);
    }

    public void sendOnChannel1() {
        String title = "Your Turn";
        String message = "Please go to counter";
        Log.d("haha","2");
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        //Log.d("haha","3");
        notificationManager.notify(1, notification);
        //Log.d("haha","4");


    }

    private void sendPassword(final String forgotemail) {
        class SendPassword extends AsyncTask<Void,String,String> {

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap();
                hashMap.put("email",forgotemail);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest("http://wht0912.000webhostapp.com/tanas/php/checkcurrentnum.php",hashMap);
                Log.d("lala",s);
                Log.d("lala","haha");
                //
                //
                // s="11111";
                return s;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s.equalsIgnoreCase("11111")){
                    //Toast.makeText(timertask.this, "Success. Check your email", Toast.LENGTH_LONG).show();
                    //dialogforgotpass.dismiss();
                    sendOnChannel1();
                }else{
                    //Toast.makeText(timertask.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
        SendPassword sendPassword = new SendPassword();
        sendPassword.execute();
    }
}

