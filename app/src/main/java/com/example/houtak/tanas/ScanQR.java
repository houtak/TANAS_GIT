package com.example.houtak.tanas;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class ScanQR extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView zXingScannerView;
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;
    //DatabaseReference ref;
    String company,queuename,currentavailablenum,qdate,userid;
    Dialog dialogconfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        int currentApiVersion = Build.VERSION.SDK_INT;

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userid = bundle.getString("userid");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //ref = FirebaseDatabase.getInstance().getReference().child("Company").child(infront);

        if (currentApiVersion >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                //Toast.makeText(getApplicationContext(), "Permission already granted!", Toast.LENGTH_LONG).show();
            } else {
                requestPermission();
            }
        }
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if (scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(ScanQR.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {
        final String myResult = result.getText();

        int x = myResult.indexOf(",");
        final String infront = myResult.substring(0, x);  //company name
        final String behint = myResult.substring(x + 1, myResult.length()); // queue name

        //test1
        //Log.d("QRCodeScanner", result.getText());
        //Log.d("QRCodeScanner", result.getBarcodeFormat().toString());

        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("Scan Result");
        //builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        //  @Override
        //public void onClick(DialogInterface dialog, int which) {
        //  scannerView.resumeCameraPreview(MainActivity.this);
        // }
        //});

        //builder.setNeutralButton("Visit", new DialogInterface.OnClickListener() {
        // @Override
        //public void onClick(DialogInterface dialog, int which) {
        //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(myResult));
        // startActivity(browserIntent);
        //  }
        //  });

        //builder.setMessage(result.getText());
        //AlertDialog alert1 = builder.create();
        //alert1.show();


        //test2

        //Toast.makeText(getApplicationContext(), infront, Toast.LENGTH_LONG).show();
        scanAndGet(infront,behint);
    }


    private void scanAndGet(final String infront,final String behint) {
        class ScanAndGet extends AsyncTask<Void,String,String> {

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap();
                hashMap.put("company",infront);
                hashMap.put("queuename",behint);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest("http://wht0912.000webhostapp.com/tanas/php/scan_to_get.php",hashMap);
                Log.d("lala",s);
                Log.d("lala","haha");

                return s;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray restarray = jsonObject.getJSONArray("num");
                    JSONObject c = restarray.getJSONObject(0);
                    company = c.getString("company");
                    queuename = c.getString("queuename");
                    currentavailablenum = c.getString("currentavailablenum");
                    qdate = c.getString("qdate");
                    abletakenum();
                    //confirmDialog();

                } catch (JSONException e) {

                }
            }
        }
        ScanAndGet scanAndGet = new ScanAndGet();
        scanAndGet.execute();

    }

    void confirmDialog() {

        dialogconfirm = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);//Theme_DeviceDefault_Dialog_NoActionBar
        dialogconfirm.setContentView(R.layout.confirm_take_queue);
        dialogconfirm.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //final EditText edemail = dialogconfirm.findViewById(R.id.edtEmail);
        final TextView tvnum = dialogconfirm.findViewById(R.id.numtv);
        Button yesBtn = dialogconfirm.findViewById(R.id.btnYes);
        Button cancelBtn = dialogconfirm.findViewById(R.id.btnCancel);
        tvnum.setText(currentavailablenum);
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String forgotemail =  edemail.getText().toString();
                // String forgotemail =  "test@tester.com";
                //sendPassword(forgotemail);
                saveNum();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String forgotemail =  edemail.getText().toString();
                // String forgotemail =  "test@tester.com";
                //sendPassword(forgotemail);

                dialogconfirm.dismiss();
                Intent intent = new Intent(ScanQR.this, Main2Activity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userid",userid);
                intent.putExtras(bundle);
                startActivity(intent);
                //finish();
            }
        });
        dialogconfirm.show();
    }

    private void abletakenum() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure take this number ? ");
        builder.setPositiveButton("Yes",new DialogInterface.OnClickListener()

        {
            @Override
            public void onClick (DialogInterface dialog,int which){

                //scannerView.resumeCameraPreview(MainActivity.this);
                //takenNum();
                //startActivity(new Intent(scanqr.this, ManageProfile.class));
                //updateNum();
                //addtolist();
                //Intent intent = new Intent(ScanQR.this, Main2Activity.class);
                //startActivity(intent);
               // finish();
                saveNum();
            }
        });
        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                dialog.cancel();
                Intent intent = new Intent(ScanQR.this, Main2Activity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userid",userid);
                intent.putExtras(bundle);
                startActivity(intent);
                //finish();
            }
        });

        builder.setMessage(currentavailablenum);
        AlertDialog alert1 = builder.create();
        alert1.show();
    }

    private void saveNum() {
        class SaveNum extends AsyncTask<Void,String,String> {

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap();
                hashMap.put("email",userid);
                hashMap.put("company",company);
                hashMap.put("queuename",queuename);
                hashMap.put("holdnum",currentavailablenum);
                hashMap.put("qdate",qdate);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest("http://wht0912.000webhostapp.com/tanas/php/savequeuenum.php",hashMap);
                Log.d("lala",s);
                //Log.d("lala","haha");

                return s;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(s.equalsIgnoreCase("Success")){
                    Toast.makeText(ScanQR.this, s, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ScanQR.this,Main2Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("userid",userid);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    //dialogconfirm.dismiss();

                   // Intent serviceIntent = new Intent(ScanQR.this, CheckNumService.class);
                   // serviceIntent.putExtra("inputExtra", userid);

                    //ContextCompat.startForegroundService(ScanQR.this, serviceIntent);

                }else{
                    Toast.makeText(ScanQR.this, "You Already have a number!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ScanQR.this,Main2Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("userid",userid);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        }
        SaveNum saveNum = new SaveNum();
        saveNum.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ScanQR.this,Main2Activity.class);
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