package com.example.crimemappingapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.crimemappingapp.R;
import com.example.crimemappingapp.utils.CrimeMappingUtils;
import com.example.crimemappingapp.utils.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO remove this
//        this.deleteDatabase(DatabaseHelper.DATABASE_NAME);

        initDB();

        // TODO remove this
//        Intent intent = new Intent(MainActivity.this, CrimeMapActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putBoolean("isAdmin", true);
//        intent.putExtras(bundle);
//        startActivity(intent);
    }

    private void initDB() {
        DatabaseHelper.createInstance(getApplicationContext());
        initializeAdmin();
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
}
