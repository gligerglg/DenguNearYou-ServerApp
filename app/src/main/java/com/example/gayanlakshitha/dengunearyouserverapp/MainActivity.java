package com.example.gayanlakshitha.dengunearyouserverapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Gayan Lakshitha on 9/21/2017.
 */

public class MainActivity extends Activity {

    Spinner spin_location;
    Button btn_getInfo;
    Button btn_AddNew;
    Button btn_Settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        spin_location = (Spinner)findViewById(R.id.spinner);
        btn_AddNew = (Button)findViewById(R.id.btn_addNewData);
        btn_getInfo = (Button)findViewById(R.id.btn_getInfo);
        btn_Settings = (Button)findViewById(R.id.btn_Settings);

        final SQLiteDatabase location_db = openOrCreateDatabase("locationdb.db",MODE_PRIVATE,null);
        location_db.execSQL("CREATE TABLE IF NOT EXISTS tbl_location(id INTEGER PRIMARY KEY AUTOINCREMENT,location text,latitude double,longitude double,risk boolean,patients int, deaths int)");

        final Cursor cursor = location_db.rawQuery("SELECT * FROM tbl_location",null);
        ArrayList<String> loc_list = new ArrayList<>();
        while(cursor.moveToNext())
        {
            loc_list.add(cursor.getString(1));
        }
        //Create ArrayAdapter and set it to Spinner
        final ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),R.layout.spiner,loc_list);
        spin_location.setAdapter(adapter);

        btn_getInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String location = spin_location.getSelectedItem().toString();
                    if(location.isEmpty())
                        Toast.makeText(getApplicationContext(),"Please Select a Location or Sync Data",Toast.LENGTH_SHORT).show();
                    else
                    {
                        Intent intent = new Intent(MainActivity.this,Information.class);
                        Bundle b = new Bundle();
                        b.putString("location",location);
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),"Please Select a Location or Sync Data",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_AddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MapsActivity.class));
            }
        });

        btn_Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Settings.class));
            }
        });
    }


}


