package com.example.gayanlakshitha.dengunearyouserverapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Gayan Lakshitha on 9/19/2017.
 */

public class Information extends Activity {

    private TextView txt_Location;
    private TextView txt_risk;
    private TextView txt_Patients;
    private TextView txt_Deaths;
    private ProgressBar progress;
    private Button btnEdit;
    String location;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        Bundle b = getIntent().getExtras();

        txt_Location = (TextView)findViewById(R.id.txt_maplocation);
        txt_risk = (TextView)findViewById(R.id.txt_status);
        txt_Patients = (TextView)findViewById(R.id.txt_patients);
        txt_Deaths = (TextView)findViewById(R.id.txt_deaths);
        progress = (ProgressBar)findViewById(R.id.progress);
        btnEdit = (Button)findViewById(R.id.btn_EditData);

        location = (String) b.getCharSequence("location");
        SQLiteDatabase location_db = openOrCreateDatabase("locationdb.db",MODE_PRIVATE,null);
        Cursor cursor = location_db.rawQuery("SELECT location,risk,patients,deaths from tbl_location WHERE location='"+location+"'",null);
        cursor.moveToFirst();
        txt_Location.setText(cursor.getString(0));
        txt_Patients.setText(""+cursor.getInt(2));
        txt_Deaths.setText(""+cursor.getInt(3));
        if(Boolean.parseBoolean(cursor.getString(1)))
        {
            txt_risk.setText("Risky");
            txt_risk.setTextColor(ContextCompat.getColor(this,R.color.risk));
        }
        else
        {
            txt_risk.setText("No Risky");
            txt_risk.setTextColor(ContextCompat.getColor(this,R.color.riskless));
        }

        if(cursor.getInt(2)==0)
            progress.setProgress(0);
        else
        {
            int progressval = ((cursor.getInt(3)*100)/cursor.getInt(2));
            progress.setProgress(progressval);
        }


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Information.this,UpdateData.class);
                Bundle b = new Bundle();
                b.putString("location",location);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
