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
import android.widget.Toast;

/**
 * Created by Gayan Lakshitha on 9/21/2017.
 */

public class Register extends Activity {

    Button btn_register;
    EditText txt_username;
    EditText txt_password;
    EditText txt_repeat;

    String username;
    String password;
    String repeat;
    boolean flag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        btn_register = (Button)findViewById(R.id.btn_register);
        txt_username = (EditText)findViewById(R.id.txt_reg_username);
        txt_password = (EditText)findViewById(R.id.txt_reg_password);
        txt_repeat = (EditText)findViewById(R.id.txt_reg_repeat);

        final SQLiteDatabase auth_db = openOrCreateDatabase("auth_db.db",MODE_PRIVATE,null);
        final Cursor cursor = auth_db.rawQuery("SELECT * FROM tbl_users",null);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = txt_username.getText().toString();
                password = txt_password.getText().toString();
                repeat = txt_repeat.getText().toString();

                if(username.isEmpty())
                    txt_username.setError("Username cannot be empty");
                if(password.isEmpty())
                    txt_password.setError("Password cannot be empty");
                if(repeat.isEmpty())
                    txt_repeat.setError("This field cannot be empty");

                if(!username.isEmpty() && !password.isEmpty() && !repeat.isEmpty())
                {
                    while(cursor.moveToNext())
                    {
                        if(username.equals(cursor.getString(0)))
                        {
                            flag = true;
                            break;
                        }
                    }

                    if(flag)
                    {
                        Toast.makeText(getApplicationContext(),"User is Already Exists",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (!password.equals(repeat))
                            txt_repeat.setError("Incorrect Password");
                        else
                        {
                            auth_db.execSQL("INSERT INTO tbl_users (username,password)VALUES ('"+username+"','"+password+"')");
                            startActivity(new Intent(Register.this,SignIn.class));
                        }
                    }
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
