package com.example.houtak.tanas;

import android.app.Notification;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.houtak.tanas.App.CHANNEL_1_ID;

public class Main2Activity extends AppCompatActivity {

    String userid;
    CardView companycard,scanandgetnum,viewstatuscard;

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
        setContentView(R.layout.activity_main2);

        //Intent serviceIntent = new Intent(Main2Activity.this, CheckNumService.class);
        //serviceIntent.putExtra("inputExtra", userid);
        //startService(serviceIntent);
        //ContextCompat.startForegroundService(Main2Activity.this, serviceIntent);



        Intent intent = getIntent();
        Bundle bundle2 = intent.getExtras();
        userid = bundle2.getString("userid");

        Intent serviceIntent = new Intent(Main2Activity.this, CheckNumService.class);
        serviceIntent.putExtra("inputExtra", userid);

        ContextCompat.startForegroundService(Main2Activity.this, serviceIntent);

        //Toast.makeText(getApplicationContext(), userid, Toast.LENGTH_LONG).show();

        companycard = findViewById(R.id.companycard);
        scanandgetnum = findViewById(R.id.scannget);
        viewstatuscard = findViewById(R.id.viewstatuscard);



        companycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(Main2Activity.this,ListCompanyActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userid",userid);
                intent3.putExtras(bundle);
                startActivity(intent3);
                //finish();
            }
        });

        scanandgetnum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(Main2Activity.this, ScanQR.class);
                Bundle bundle = new Bundle();
                bundle.putString("userid",userid);
                intent3.putExtras(bundle);
                startActivity(intent3);
            }
        });

        viewstatuscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(Main2Activity.this, ViewQueueStatus.class);
                Bundle bundle = new Bundle();
                bundle.putString("userid",userid);
                intent3.putExtras(bundle);
                startActivity(intent3);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.myprofile:
                Intent intent = new Intent(Main2Activity.this, ProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userid",userid);
                intent.putExtras(bundle);
                startActivity(intent);;
                return true;

            case R.id.logout:
                Intent intent2 = new Intent(Main2Activity.this, LoginActivity.class);
                startActivity(intent2);;
                finish();
                return true;

            default:

                return super.onOptionsItemSelected(item);
        }
    }


}
