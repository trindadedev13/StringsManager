package com.trindade.stringscreator.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.transition.MaterialSharedAxis;

import com.trindade.stringscreator.R;
import com.trindade.stringscreator.activities.api.github.GitHubContributorsActivity;
import com.trindade.stringscreator.classes.GlobalConfig;
import com.trindade.stringscreator.databinding.SettingsFragmentBinding;
import com.trindade.stringscreator.classes.FileUtil;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

public class SettingsFragment extends Fragment {

    SettingsFragmentBinding binding;
    SharedPreferences sp;
    Context ctx;

    @Override
    public void onCreate(Bundle bund) {
        super.onCreate(bund);
        setEnterTransition(new MaterialSharedAxis(GlobalConfig.SharedAxisEnter, GlobalConfig.SharedAxisEnterBoolean));
        setExitTransition(new MaterialSharedAxis(GlobalConfig.SharedAxisExit, GlobalConfig.SharedAxisExitBoolean));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = SettingsFragmentBinding.inflate(inflater, container, false);
        ctx = requireContext();
        if (ctx != null) {
            sp = ctx.getSharedPreferences("prefs", Activity.MODE_PRIVATE);
            getData();
        }

        binding.resourcesTag.setOnCheckedChangeListener((compoundButton, isChecked) -> {
           sp.edit().putBoolean("ADD_RES", isChecked).apply();
        });

        binding.githubIssues.setOnClickListener(v -> {
           openURL(getActivity(), "https://github.com/aquilesTrindade/StringsCreator/issues");
        });
        
        binding.githubContributors.setOnClickListener(v -> {
           Intent in = new Intent(ctx, GitHubContributorsActivity.class);
           startActivity(in);
        });
        
        return binding.getRoot();
    }

    private void getData() {
        binding.resourcesTag.setChecked(sp.getBoolean("ADD_RES", false));
    }

    public static void openURL(Context ctx, String url) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            ctx.startActivity(intent);
        } catch (Exception e) {
        }
    }
}
