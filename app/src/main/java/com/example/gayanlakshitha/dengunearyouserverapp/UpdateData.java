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
 * Created by Gayan Lakshitha on 9/22/2017.
 */

public class UpdateData extends Activity {

    String location;
    int patients;
    int deaths;
    double latitude;
    double longitude;
    boolean risky = false;
    boolean isLocationExists = false;

    TextView txt_Location;
    EditText txt_Patients;
    EditText txt_Deaths;
    TextView txt_latitude;
    TextView txt_longitude;
    Button btn_Sync;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editdata);

        Bundle b = getIntent().getExtras();
        location = b.getString("location");

        txt_Location = (TextView) findViewById(R.id.txt_maplocation);
        txt_Deaths = (EditText)findViewById(R.id.txt_deaths);
        txt_Patients = (EditText)findViewById(R.id.txt_patients);
        txt_latitude = (TextView)findViewById(R.id.txt_latitude);
        txt_longitude = (TextView)findViewById(R.id.txt_longitude);
        btn_Sync = (Button)findViewById(R.id.btn_sync);

        final SQLiteDatabase location_db = openOrCreateDatabase("locationdb.db",MODE_PRIVATE,null);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference location_ref = database.getReferenceFromUrl("https://dengueanalyzis.firebaseio.com/Locations/");

        //Fill Data
        Cursor cursor = location_db.rawQuery("SELECT * FROM tbl_location WHERE location='"+location+"'",null);
        while(cursor.moveToNext())
        {
            txt_Location.setText(cursor.getString(1));
            txt_latitude.setText("Latitude of the Location : \n" + cursor.getDouble(2));
            txt_longitude.setText("Longitude of the Location : \n" + cursor.getDouble(3));
            txt_Patients.setText(""+cursor.getInt(5));
            txt_Deaths.setText(""+cursor.getInt(6));
            latitude = cursor.getDouble(2);
            longitude = cursor.getDouble(3);
        }

        btn_Sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    location = txt_Location.getText().toString();
                    patients = Integer.parseInt(txt_Patients.getText().toString());
                    deaths = Integer.parseInt(txt_Deaths.getText().toString());
                }
                catch (Exception e){}


                if(location.isEmpty())
                    txt_Location.setError("Location cannot be Empty");
                if(txt_Patients.toString().isEmpty())
                    txt_Patients.setError("Number of Patients cannot be Empty");
                if(txt_Deaths.toString().isEmpty())
                    txt_Deaths.setError("Number of Deaths cannot be Empty");

                try {
                    if(!location.isEmpty() && !txt_Patients.toString().isEmpty() && !txt_Deaths.toString().isEmpty())
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
                                    location_db.execSQL("UPDATE tbl_location SET location='"+location+"'," +
                                            "latitude='"+latitude+"',longitude='"+longitude+"',risk='"+risky+"'," +
                                            "patients='"+patients+"',deaths='"+deaths+"' WHERE location='"+location+"'");
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
                }catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),"Error in Adding Data",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
