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

        initDB();
    }

    private void initDB() {
        DatabaseHelper.createInstance(this);
        initializeAdmin();
        initializeCrimeTypes();
    }

    public void openAdminLoginActivity(View view) {
        Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
        startActivity(intent);
    }

    public void openCrimeMapActivityAsUser(View view) {
        Intent intent = new Intent(MainActivity.this, CrimeMapActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isAdmin", false);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void initializeAdmin() {
        DatabaseHelper.insertAdmin("admin", "password");
    }

    private void initializeCrimeTypes() {
        DatabaseHelper.insertCrimeType("Arson");
        DatabaseHelper.insertCrimeType("Assault");
        DatabaseHelper.insertCrimeType("Burglary");
        DatabaseHelper.insertCrimeType("Murder");
    }
}
