package com.example.gayanlakshitha.dengunearyouserverapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Gayan Lakshitha on 9/21/2017.
 */

public class SignIn extends Activity {

    Button btn_Signin;
    EditText txt_UserName;
    EditText txt_Password;
    String username;
    String password;
    boolean flag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        btn_Signin = (Button)findViewById(R.id.btn_login);
        txt_UserName = (EditText)findViewById(R.id.txt_username);
        txt_Password = (EditText)findViewById(R.id.txt_password);
        final SQLiteDatabase auth_db = openOrCreateDatabase("auth_db.db",MODE_PRIVATE,null);


        btn_Signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = txt_UserName.getText().toString();
                password = txt_Password.getText().toString();

                if(username.isEmpty())
                    txt_UserName.setError("Username cannot be empty");
                if(password.isEmpty())
                    txt_Password.setError("Password cannot be empty");

                if(!username.isEmpty() && !password.isEmpty())
                {
                    Cursor cursor = auth_db.rawQuery("SELECT * FROM tbl_users",null);
                    while(cursor.moveToNext())
                    {
                        if(username.equals(cursor.getString(0)) && password.equals(cursor.getString(1)))
                        {
                            flag = true;
                            break;
                        }
                    }

                    if(flag)
                        startActivity(new Intent(SignIn.this,MainActivity.class));
                    else
                        Toast.makeText(getApplicationContext(),"Incorrect Username or Password",Toast.LENGTH_SHORT).show();
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
