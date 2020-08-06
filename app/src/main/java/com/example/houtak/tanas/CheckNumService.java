package com.example.houtak.tanas;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.houtak.tanas.App.CHANNEL_1_ID;
import static com.example.houtak.tanas.App2.CHANNEL_ID;

public class CheckNumService extends Service {
    private Timer timer;
    private TimerTask timerTask;
    private Handler handler = new Handler();
    private NotificationManagerCompat notificationManager;
    String userid ;

    @Override
    public void onCreate() {
        super.onCreate();

        //startTimer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //sendOnChannel1();
/*
        String input = intent.getStringExtra("inputExtra");

        String Message1 = "Hi! Get your queue number now!";
        String Message2 = "Please wait awhile! You will be served by our staff soon.";

        Intent notificationIntent = new Intent(this, Main2Activity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(Message1)
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        //do heavy work on a background thread
        //stopSelf();
*/
        userid = intent.getExtras().getString("inputExtra");
        //Log.d("lala",userid);
        startTimer();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void stopTimer(){
        if(timer != null){
            timer.cancel();
            timer.purge();
            //sendOnChannel1();

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

                        //String forgotemail = "test@tester.com";
                        checkUserQueue(userid);
                    }
                });
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }



    public void sendOnChannel1() {
        String title = "Your Turn";
        String message = "Please go to counter";
        Log.d("haha","2");

        Intent notificationIntent = new Intent(this, Main2Activity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .build();

        //Log.d("haha","3");
        //notificationManager.notify(1, notification);
        startForeground(1, notification);

        //Log.d("haha","4");


    }

    private void checkUserQueue(final String email) {
        class CheckUserQueue extends AsyncTask<Void,String,String> {

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap();
                hashMap.put("email",email);
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
                if (s.equalsIgnoreCase("success")){
                    //                    //Toast.makeText(timertask.this, "Success. Check your email", Toast.LENGTH_LONG).show();
                    //dialogforgotpass.dismiss();
                    sendOnChannel1();
                    //stopTimer();
                }else if (s.equalsIgnoreCase("close")){
                    Intent intent = new Intent(CheckNumService.this, CheckNumService.class);
                    stopService(intent);
                }else{
                    //Toast.makeText(timertask.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
        CheckUserQueue checkUserQueue = new CheckUserQueue();
        checkUserQueue.execute();
    }
}
