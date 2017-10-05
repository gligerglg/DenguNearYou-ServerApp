package com.example.gayanlakshitha.dengunearyouserverapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Gayan Lakshitha on 9/21/2017.
 */

public class AddNewEntry extends Activity {

    EditText txt_Location;
    EditText txt_Patients;
    EditText txt_Deaths;
    TextView txt_latitude;
    TextView txt_longitude;
    Button btn_Map;
    Button btn_Sync;

    String location;
    int patients;
    int deaths;
    double latitude;
    double longitude;
    boolean risky = false;
    boolean isLocationExists = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_entry);

        txt_Location = (EditText)findViewById(R.id.txt_maplocation);
        txt_Deaths = (EditText)findViewById(R.id.txt_deaths);
        txt_Patients = (EditText)findViewById(R.id.txt_patients);
        txt_latitude = (TextView)findViewById(R.id.txt_latitude);
        txt_longitude = (TextView)findViewById(R.id.txt_longitude);
        btn_Map = (Button)findViewById(R.id.btnMap);
        btn_Sync = (Button)findViewById(R.id.btn_sync);

        final SQLiteDatabase location_db = openOrCreateDatabase("locationdb.db",MODE_PRIVATE,null);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference location_ref = database.getReferenceFromUrl("https://dengueanalyzis.firebaseio.com/Locations/");


        btn_Sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    location = txt_Location.getText().toString();
                    patients = Integer.parseInt(txt_Patients.getText().toString());
                    deaths = Integer.parseInt(txt_Deaths.getText().toString());

                if(location.isEmpty())
                    txt_Location.setError("Location cannot be Empty");
                if(txt_Patients.toString().isEmpty())
                    txt_Patients.setError("Number of Patients cannot be Empty");
                if(txt_Deaths.toString().isEmpty())
                    txt_Deaths.setError("Number of Deaths cannot be Empty");

                try {
                    if(!location.isEmpty() && !txt_Patients.toString().isEmpty() && !txt_Deaths.toString().isEmpty())
                    {
                        Cursor cursor = location_db.rawQuery("SELECT location FROM tbl_location",null);
                        while(cursor.moveToNext())
                        {
                            if(location.equals(cursor.getString(0)))
                            {
                                txt_Location.setError("Location is Already Exists");
                                isLocationExists = true;
                                break;
                            }
                        }

                        if(!isLocationExists)
                        {
                            if(deaths>patients)
                                txt_Deaths.setError("Number of Deaths cannot be higher than Number of Patients");
                            else
                            {

                                if(deaths>0)
                                {
                                    risky = true;
                                }
                                else
                                {
                                    risky = false;
                                }


                                //Update SQLite Database
                                try {
                                    location_db.execSQL("INSERT INTO tbl_location (location,latitude,longitude,risk,patients,deaths) VALUES" +
                                            "('"+location+"','"+latitude+"','"+longitude+"'," +
                                            "'"+risky+"','"+patients+"','"+deaths+"')");
                                }catch (Exception e)
                                {
                                    Toast.makeText(getApplicationContext(),"SQLite failed",Toast.LENGTH_SHORT).show();
                                }


                                //Update Firebase Database
                                try
                                {
                                    LocationInfo newInfo = new LocationInfo(location,risky,longitude,latitude,patients,deaths);
                                    location_ref.child(newInfo.getLocation()).setValue(newInfo);
                                }catch (Exception e)
                                {
                                    Toast.makeText(getApplicationContext(),"Sync Process Failed!\nPlease Check Internet Connection",Toast.LENGTH_SHORT).show();
                                }

                                Toast.makeText(getApplicationContext(),"Data Successfully Updated",Toast.LENGTH_SHORT).show();

                            }
                        }

                    }
                }catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),"Error in Adding Data",Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_Map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddNewEntry.this,MapsActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        try
        {
            Bundle b = getIntent().getExtras();
            latitude = b.getDouble("latitude");
            longitude = b.getDouble("longitude");
            location = (String) b.getCharSequence("location");
            txt_Location.setText(""+location);
            txt_longitude.setText("Longitude of the Location : \n" + longitude);
            txt_latitude.setText("Latitude of the Location : \n" + latitude);
        }
        catch (Exception e){}
    }
}
