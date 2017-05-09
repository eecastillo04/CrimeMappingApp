package com.example.crimemappingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeAdmin();
    }

    public void openAdminLoginActivity(View view) {
        Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
        startActivity(intent);
    }

    public void openCrimeMapActivityAsUser(View view) {
        Intent intent = new Intent(MainActivity.this, CrimeMapActivity.class);
        startActivity(intent);
    }

    private void initializeAdmin() {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        dbHelper.insertAdmin("admin", "password");
    }
}
