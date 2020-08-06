package com.example.houtak.tanas;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListCompanyActivity extends AppCompatActivity {
    ListView lvrest;
    ArrayList<HashMap<String, String>> complist;
    Spinner sploc;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_company);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userid = bundle.getString("userid");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvrest = findViewById(R.id.listviewRest);
        sploc = findViewById(R.id.spinner);

        loadCompany(sploc.getSelectedItem().toString());

        sploc.setSelection(0,false);
        sploc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadCompany(sploc.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lvrest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MainActivity.this, restlist.get(position).get("restid"), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ListCompanyActivity.this,CompanyInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("restid",complist.get(position).get("restid"));
                bundle.putString("name",complist.get(position).get("name"));
                bundle.putString("phone",complist.get(position).get("phone"));
                bundle.putString("address",complist.get(position).get("address"));
                bundle.putString("location",complist.get(position).get("location"));
                bundle.putString("operationhour",complist.get(position).get("operationhour"));
                bundle.putString("userid",userid);
                //bundle.putString("userphone",phone);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void loadCompany(final String loc) {
        class LoadRestaurant extends AsyncTask<Void,Void,String> {

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap<>();
                Log.e("haha",loc);
                hashMap.put("location",loc);
                Log.e("haha","1");
                RequestHandler rh = new RequestHandler();
                complist = new ArrayList<>();
                String s = rh.sendPostRequest
                        ("http://wht0912.000webhostapp.com/tanas/php/load_shop.php",hashMap);

                Log.e("haha",s);
                return s;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                complist.clear();
                try{
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray restarray = jsonObject.getJSONArray("rest");
                    Log.e("HANIS",jsonObject.toString());
                    for (int i=0;i<restarray.length();i++){
                        JSONObject c = restarray.getJSONObject(i);
                        String rid = c.getString("restid");
                        String rname = c.getString("name");
                        String rphone = c.getString("phone");
                        String raddress = c.getString("address");
                        String rlocation = c.getString("location");
                        String rhour = c.getString("operationhour");
                        HashMap<String,String> restlisthash = new HashMap<>();
                        restlisthash.put("restid",rid);
                        restlisthash.put("name",rname);
                        restlisthash.put("phone",rphone);
                        restlisthash.put("address",raddress);
                        restlisthash.put("location",rlocation);
                        restlisthash.put("operationhour",rhour);
                        complist.add(restlisthash);
                    }
                }catch (final JSONException e){
                    Log.e("JSONERROR",e.toString());
                }

                ListAdapter adapter = new CustomAdapter(
                        ListCompanyActivity.this, complist,
                        R.layout.cust_list_comp, new String[]
                        {"name","phone","address","location"}, new int[]
                        {R.id.textView,R.id.textView2,R.id.textView3,R.id.textView4});
                lvrest.setAdapter(adapter);
            }

        }
        LoadRestaurant loadRestaurant = new LoadRestaurant();
        loadRestaurant.execute();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ListCompanyActivity.this,Main2Activity.class);
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
