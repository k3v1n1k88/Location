package com.example.lap10581_local.map;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {
    private Button btnSignIn;
    private EditText edtUsername;
    private EditText edtPassword;
    private TextView txtNotify;
    String userName;
    String passWord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        txtNotify = (TextView) findViewById(R.id.txtNotification);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = edtUsername.getText().toString();
                passWord = edtPassword.getText().toString();
                if(userName.equals("")){
                    txtNotify.setText("Empty user name");
                    txtNotify.setVisibility(View.VISIBLE);
                    return;
                }
                if(passWord.equals("")){
                    txtNotify.setText("Empty password");
                    txtNotify.setVisibility(View.VISIBLE);
                    return;
                }
                UserModel userModel = new UserModel();
                try {
                    User user = userModel.getUserFromUsername(userName);
                    if(!user.getPassWord().trim().equals(passWord)){
                        txtNotify.setText("Password not match");
                        txtNotify.setVisibility(View.VISIBLE);
                        return;
                    }
                    txtNotify.setText("Login success");
                    txtNotify.setVisibility(View.VISIBLE);
                } catch (SQLException e) {
                    txtNotify.setText("Cannot find your ID");
                    txtNotify.setVisibility(View.VISIBLE);
                }
            }
        });
//
        edtUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                txtNotify.setVisibility(View.INVISIBLE);
            }
        });
        edtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                txtNotify.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
