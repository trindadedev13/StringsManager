package com.trindade.stringscreator.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.transition.MaterialSharedAxis;
import com.trindade.stringscreator.R;
import com.trindade.stringscreator.databinding.SettingsFragmentBinding;

public class SettingsFragment extends Fragment {

    SettingsFragmentBinding binding;
    SharedPreferences sp;
    Context ctx;

    @Override
    public void onCreate(Bundle bund) {
        super.onCreate(bund);
        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
        setExitTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        ctx = requireContext();
        binding = SettingsFragmentBinding.inflate(inflater);
        sp = ctx.getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        getData();
        
        binding.resourcesTag.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            sp.edit().putBoolean("ADD_RES", isChecked).apply();
        });
        
        return binding.getRoot();
    }
    
    private void getData(){
        binding.resourcesTag.setChecked(sp.getBoolean("ADD_RES",false));
    }
    
    private void putData(){
        
    }
}
