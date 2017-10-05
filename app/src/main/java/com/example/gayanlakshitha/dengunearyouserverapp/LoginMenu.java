package com.example.gayanlakshitha.dengunearyouserverapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginMenu extends AppCompatActivity {

    Button btn_signin;
    Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_menu);

        btn_register = (Button)findViewById(R.id.btn_register);
        btn_signin = (Button)findViewById(R.id.btn_signin);
        final SQLiteDatabase auth_db = openOrCreateDatabase("auth_db.db",MODE_PRIVATE,null);
        auth_db.execSQL("CREATE TABLE IF NOT EXISTS tbl_users(username text,password text)");
        Cursor cursor = auth_db.rawQuery("SELECT * FROM tbl_users",null);

        if(cursor.getCount()==0)
            btn_signin.setVisibility(View.GONE);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginMenu.this,Register.class));
            }
        });

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginMenu.this,SignIn.class));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
