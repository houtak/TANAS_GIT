package com.example.houtak.tanas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ViewQueueStatus extends AppCompatActivity {

    TextView yournumtv,companytv,queuenametv,datetv,currentnumtv;
    Button btnWithdraw;
    String userid,yrnum,company,queuename,qdate,currentnum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_queue_status);

        yournumtv = findViewById(R.id.yrnumtv);
        companytv = findViewById(R.id.companytv);
        queuenametv = findViewById(R.id.qnametv);
        datetv = findViewById(R.id.datetv);
        currentnumtv = findViewById(R.id.currentnumtv);

        btnWithdraw = findViewById(R.id.btnwithdraw);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle bundle2 = intent.getExtras();
        userid = bundle2.getString("userid");

        getQueuedata();

        btnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogWithdraw();
            }
        });


    }

    void getQueuedata() {
        class GetQueuedata extends AsyncTask<Void,String,String> {

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap();
                hashMap.put("email",userid);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest("http://wht0912.000webhostapp.com/tanas/php/getqueueinfo.php",hashMap);

                return s;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray restarray = jsonObject.getJSONArray("queueinfo");
                    JSONObject c = restarray.getJSONObject(0);
                    yrnum = c.getString("yrnum");
                    company = c.getString("company");
                    queuename = c.getString("queuename");
                    qdate = c.getString("qdate");
                    currentnum = c.getString("currentnum");
                    //confirmDialog();

                    yournumtv.setText(yrnum);
                    companytv.setText("Company Name: "+company);
                    queuenametv.setText("Queue Name "+queuename);
                    datetv.setText("Date: "+qdate);

                    if(currentnum.equals("null")){
                        currentnumtv.setText("Current served number: - ");
                    }else {
                        currentnumtv.setText("Current served number: " + currentnum);
                    }


                } catch (JSONException e) {

                }
            }
        }
        GetQueuedata getQueuedata = new GetQueuedata();
        getQueuedata.execute();

    }

    private void dialogWithdraw() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Withdraw From The Queue");

        alertDialogBuilder
                .setMessage("Are you sure?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        //updateProfile(newemail, newfname, newlname, oldpass, newpass);
                        withdrawfromqueue();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    void withdrawfromqueue() {
        class Withdrawfromqueue extends AsyncTask<Void,String,String> {

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap();
                hashMap.put("email",userid);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest("http://wht0912.000webhostapp.com/tanas/php/withdrawfromQ.php",hashMap);

                return s;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s.equalsIgnoreCase("success")) {
                    Toast.makeText(ViewQueueStatus.this, "Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ViewQueueStatus.this, Main2Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("userid", userid);
                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                } else {
                    Toast.makeText(ViewQueueStatus.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
        Withdrawfromqueue withdrawfromqueue = new Withdrawfromqueue();
        withdrawfromqueue.execute();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ViewQueueStatus.this,Main2Activity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userid",userid);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
