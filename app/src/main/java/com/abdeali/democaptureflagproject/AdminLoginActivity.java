package com.abdeali.democaptureflagproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText editTextEmailId, editTextPassword;
    private Switch switchRememberMe;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        btnLogin = findViewById(R.id.btnLoginClick);
        editTextEmailId = findViewById(R.id.editTextEmailId);
        editTextPassword = findViewById(R.id.editTextPassword);
        switchRememberMe = findViewById(R.id.switchRememberMe);

        SharedPreferences sharedPreferences = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("IsLogin",false)){
            editTextEmailId.setText( sharedPreferences.getString("Username",""));
            editTextPassword.setText(sharedPreferences.getString("Password",""));
            switchRememberMe.setChecked(sharedPreferences.getBoolean("IsLogin",false));
        }


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences =
                        getSharedPreferences("LoginInfo",
                                Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(switchRememberMe.isChecked())
                {
                    editor.putString("Username",editTextEmailId.getText().toString());
                    editor.putString("Password",editTextPassword.getText().toString());
                    editor.putBoolean("IsLogin",true);

                }
                else
                {
                    editor.putBoolean("IsLogin",false);
                }

                if(editTextEmailId.getText().toString().equals("admin") && editTextPassword.getText().toString().equals("admin123"))  {
                    startActivity(new Intent(AdminLoginActivity.this,TrackMeActivity.class));
                }
                else{
                    Toast.makeText(AdminLoginActivity.this, "Username or Password incorrect ", Toast.LENGTH_SHORT).show();
                    editor.apply();
                }

            }
        });
    }
}


