package com.trindade.stringscreator.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.trindade.stringscreator.fragments.MainFragment;
import com.trindade.stringscreator.fragments.SettingsFragment;
import com.trindade.stringscreator.R;
import com.trindade.stringscreator.databinding.ActivityMainBinding;
import com.trindade.stringscreator.utils.ThemedActivity;

import java.io.IOException;

public class MainActivity extends ThemedActivity {

    ActivityMainBinding binding;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1000);
            } else {
                init();
            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.toString(), 4000).show();
            Log.e("Error", e.toString());
        }
    }
    
    public void init(){
        sp = getSharedPreferences("prefs", Activity.MODE_PRIVATE);
                binding = ActivityMainBinding.inflate(getLayoutInflater());
                setContentView(binding.getRoot());
                binding.bottomNavigation.setOnNavigationItemSelectedListener(navListener);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new MainFragment())
                        .commit();
    }

    @Override
    public void onRequestPermissionsResult(int rc, String[] perm, int[] gr) {
        super.onRequestPermissionsResult(rc, perm, gr);
        if (rc == 1000) {
            init();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment;
                    if (item.getItemId() == R.id.nav_home) {
                        selectedFragment = new MainFragment();
                    } else {
                        selectedFragment = new SettingsFragment();
                    }

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();

                    return true;
                }
            };
}
