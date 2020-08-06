package com.example.houtak.tanas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class CompanyInfoActivity extends AppCompatActivity {
    TextView tvcname,tvcphone,tvcaddress,tvcloc,tvcOhour;
    ImageView imgComp;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info);

        imgComp = findViewById(R.id.imageView3);
        tvcname = findViewById(R.id.companytv);
        tvcphone = findViewById(R.id.phonetv);
        tvcaddress = findViewById(R.id.addresstv);
        tvcloc = findViewById(R.id.locationtv);
        tvcOhour = findViewById(R.id.hourtv);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String restid = bundle.getString("restid");
        String cname = bundle.getString("name");
        String cphone = bundle.getString("phone");
        String caddress = bundle.getString("address");
        String clocation = bundle.getString("location");
        String coperationhour = bundle.getString("operationhour");
        userid = bundle.getString("userid");

        tvcname.setText(cname);
        tvcphone.setText(cphone);
        tvcaddress.setText(caddress);
        tvcloc.setText(clocation);
        tvcOhour.setText(coperationhour);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Picasso.with(this).load("http://wht0912.000webhostapp.com/tanas/images/"+restid+".jpg")
                .memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE)
                .fit().into(imgComp);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(CompanyInfoActivity.this,ListCompanyActivity.class);
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
