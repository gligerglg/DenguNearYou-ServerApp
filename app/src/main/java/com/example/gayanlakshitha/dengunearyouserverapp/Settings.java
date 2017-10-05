package com.example.gayanlakshitha.dengunearyouserverapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Gayan Lakshitha on 9/21/2017.
 */

public class Settings extends Activity{

    Button btn_Sync;
    Button btn_RemoveAcc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        btn_RemoveAcc = (Button)findViewById(R.id.btn_removeAcc);
        btn_Sync = (Button)findViewById(R.id.btn_SyncData);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference location_ref = database.getReferenceFromUrl("https://dengueanalyzis.firebaseio.com/Locations/");
        final SQLiteDatabase location_db = openOrCreateDatabase("locationdb.db",MODE_PRIVATE,null);
        final SQLiteDatabase auth_db = openOrCreateDatabase("auth_db.db",MODE_PRIVATE,null);

        btn_Sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),"Please Switch on Internet Service. \nSync Process May Take a Moment",Toast.LENGTH_SHORT).show();

                //Firebase and SQLite Sync
                location_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Cursor cursor = location_db.rawQuery("SELECT location,risk,patients,deaths from tbl_location",null);

                        if(cursor.getCount()<=dataSnapshot.getChildrenCount())
                        {
                            location_db.execSQL("DELETE FROM tbl_location");
                            for(DataSnapshot child : dataSnapshot.getChildren())
                            {
                                LocationInfo info = child.getValue(LocationInfo.class);
                                try
                                {
                                    location_db.execSQL("INSERT INTO tbl_location (location,latitude,longitude,risk,patients,deaths) VALUES" +
                                            "('"+info.getLocation()+"','"+info.getLatitude()+"','"+info.getLongitude()+"'," +
                                            "'"+info.isRisk()+"','"+info.getPatients()+"','"+info.getDeaths()+"')");

                                }
                                catch (SQLException e)
                                {

                                }
                            }
                        }
                        else
                        {
                            while(cursor.moveToNext())
                            {
                                try {
                                    LocationInfo info = new LocationInfo(cursor.getString(0),Boolean.parseBoolean(cursor.getString(1)),cursor.getDouble(2),cursor.getDouble(3),
                                            cursor.getInt(4),cursor.getInt(5));
                                    location_ref.child(info.getLocation()).setValue(info);
                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(getApplicationContext(),"Sync Error",Toast.LENGTH_SHORT).show();
                                }

                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Toast.makeText(getBaseContext(),"Sync Process Complete\nPlease Restart Application",Toast.LENGTH_SHORT).show();
            }
        });

        btn_RemoveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder aleart = new AlertDialog.Builder(Settings.this);
                aleart.setTitle("Reset Database");
                aleart.setMessage("Do You Want To Remove All Server Accounts?");

                aleart.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            auth_db.execSQL("DELETE FROM tbl_users");
                            Toast.makeText(getApplicationContext(),"All Accounts Deleted Successfully!",Toast.LENGTH_SHORT).show();
                        }catch (SQLException e){
                            Toast.makeText(getApplicationContext(),"Database Error!",Toast.LENGTH_SHORT).show();
                        }
                        finally {
                            dialog.dismiss();
                            startActivity(new Intent(Settings.this,Register.class));
                        }

                    }
                });

                aleart.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                aleart.create().show();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
