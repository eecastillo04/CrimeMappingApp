package com.example.crimemappingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class AdminLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
    }

    public void handleAdminLogin(View view) {
        String username = ((EditText) findViewById(R.id.usernameInput)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordInput)).getText().toString();

        // check if input is valid admin login credentials
        if(DatabaseHelper.getInstance(this).isValidAdminCredentials(username, password)) {
            Intent intent = new Intent(AdminLoginActivity.this, CrimeMapActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("isAdmin", true);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
