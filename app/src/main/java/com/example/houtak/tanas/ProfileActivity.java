package com.example.houtak.tanas;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    String userid, firstname,lastname,phone, latitude, longitude;
    //Spinner sploc;
    TextView tvphone,tvlocation;
    EditText tvuserid, tvfname,tvlname, edoldpass, ednewpass;
    CircleImageView imgprofile;
    Button btnUpdate;
    ImageButton btnloc;
    Dialog myDialogMap;
    private GoogleMap mMap;
    String slatitude, slongitude;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //Intent intent = getIntent();
        //Bundle bundle = intent.getExtras();
        imgprofile = findViewById(R.id.imageView4);
        tvuserid = findViewById(R.id.txtemail);
        tvfname = findViewById(R.id.txtUsername);
        tvlname = findViewById(R.id.txtUsernamelast);
        tvphone = findViewById(R.id.txtphone);
        edoldpass = findViewById(R.id.txtoldpassword);
        ednewpass = findViewById(R.id.txtnewpassword);
        btnUpdate = findViewById(R.id.button5);
        btnloc = findViewById(R.id.btnloc);
        tvlocation  = findViewById(R.id.tvloc);
        Intent intent = getIntent();
        Bundle bundle2 = intent.getExtras();
        userid = bundle2.getString("userid");
///*
        Log.d("haha","1");

        getUserdata();

        //String image_url = "http://wht0912.000webhostapp.com/tanas/profileimages/" + phone + ".jpg";
        //Picasso.with(this).load(image_url)
        //        .resize(400, 400).into(imgprofile);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newemail = tvuserid.getText().toString();
                String newfname = tvfname.getText().toString();
                String newlname = tvlname.getText().toString();
                String oldpass = edoldpass.getText().toString();
                String newpass = ednewpass.getText().toString();
                dialogUpdate(newemail, newfname, newlname, oldpass, newpass);

            }
        });

        btnloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMapWindow();
            }
        });
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //*/
    }

    void getUserdata() {
        class GetUserdata extends AsyncTask<Void,String,String> {

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap();
                hashMap.put("email",userid);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest("http://wht0912.000webhostapp.com/tanas/php/userdata.php",hashMap);

                return s;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray restarray = jsonObject.getJSONArray("user");
                    JSONObject c = restarray.getJSONObject(0);
                    phone = c.getString("phone");
                    firstname = c.getString("firstname");
                    lastname = c.getString("lastname");
                    latitude = c.getString("latitude");
                    longitude = c.getString("longitude");
                    //confirmDialog();

                    tvuserid.setText(userid);
                    tvphone.setText(phone);
                    tvfname.setText(firstname);
                    tvlname.setText(lastname);
                    tvlocation.setText("https://www.google.com/maps/@"+latitude+","+longitude+",15z");
                    loadimage(phone);


                } catch (JSONException e) {

                }
            }
        }
        GetUserdata getUserdata = new GetUserdata();
        getUserdata.execute();

    }

    private void loadimage(String phone) {
        String image_url = "http://wht0912.000webhostapp.com/tanas/profileimages/" + phone + ".jpg";
        Picasso.with(this).load(image_url)
                .resize(400, 400).into(imgprofile);

    }

    private void loadMapWindow() {
        myDialogMap = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);//Theme_DeviceDefault_Dialog_NoActionBar
        myDialogMap.setContentView(R.layout.map_window);
        myDialogMap.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Button btnsavemap = myDialogMap.findViewById(R.id.btnclosemap);
        MapView mMapView = myDialogMap.findViewById(R.id.mapView);
        MapsInitializer.initialize(this);
        mMapView.onCreate(myDialogMap.onSaveInstanceState());
        mMapView.onResume();
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                LatLng allpos;
                LatLng posisiabsen = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)); ////your lat lng
                googleMap.addMarker(new MarkerOptions().position(posisiabsen).title("HOME").icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).showInfoWindow();
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen));
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
                if (ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        googleMap.clear();
                        slatitude = String.valueOf(latLng.latitude);
                        slongitude = String.valueOf(latLng.longitude);
                        googleMap.addMarker(new MarkerOptions().position(latLng).title("New Home").icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))).showInfoWindow();
                    }
                });
            }
        });
        btnsavemap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slatitude.length()>5){
                    latitude = slatitude;
                    longitude = slongitude;
                    myDialogMap.dismiss();
                    tvlocation.setText("https://www.google.com/maps/@"+latitude+","+longitude+",15z");
                    //locationManager.removeUpdates(ProfileActivity.this);
                }else{
                    Toast.makeText(ProfileActivity.this, "Please select home location", Toast.LENGTH_SHORT).show();
                }

            }
        });
        myDialogMap.show();
        //enableLocation();
    }

    private void dialogUpdate(final String newemail, final String newfname, final String newlname, final String oldpass, final String newpass) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Profile");

        alertDialogBuilder
                .setMessage("Update this profile")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        updateProfile(newemail, newfname, newlname, oldpass, newpass);
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

    void updateProfile(final String newemail, final String newfname, final String newlname, final String oldpass, final String newpass) {
        class UpdateProfile extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("email", newemail);
                hashMap.put("firstname", newfname);
                hashMap.put("lastname", newlname);
                hashMap.put("phone", phone);
                hashMap.put("opassword", oldpass);
                hashMap.put("npassword", newpass);
                hashMap.put("latitude", latitude);
                hashMap.put("longitude", longitude);
                Log.d("haha",latitude);
                RequestHandler rh = new RequestHandler();          ///here
                String s = rh.sendPostRequest("http://wht0912.000webhostapp.com/tanas/php/update_profile.php", hashMap);
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s.equalsIgnoreCase("success")) {
                    Toast.makeText(ProfileActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileActivity.this, Main2Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("userid", userid);
                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
        UpdateProfile updateProfile = new UpdateProfile();
        updateProfile.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ProfileActivity.this,Main2Activity.class);
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
