package com.trindade.stringscreator.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trindade.stringscreator.StringsCreatorApp;
import com.trindade.stringscreator.databinding.ActivityMainBinding;
import com.trindade.stringscreator.databinding.MainFragmentBinding;
import com.trindade.stringscreator.fragments.MainFragment;
import com.trindade.stringscreator.fragments.SettingsFragment;
import com.trindade.stringscreator.R;
import com.trindade.stringscreator.StringsCreatorAppLog;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    MainFragmentBinding mainB;
    SharedPreferences sp;
    ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
    StringsCreatorAppLog logger = new StringsCreatorAppLog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        mainB = MainFragmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bottomNavigation.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new MainFragment())
                .commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    if (item.getItemId() == R.id.nav_home) {
                        selectedFragment = new MainFragment();
                    } else {

                        selectedFragment = new SettingsFragment();
                    }

                    // Substitui o fragmento no container
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();

                    return true;
                }
            };
}
