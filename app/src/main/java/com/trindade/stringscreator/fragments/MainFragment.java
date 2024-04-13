package com.trindade.stringscreator.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.transition.MaterialFade;
import com.google.android.material.transition.MaterialSharedAxis;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trindade.stringscreator.StringsCreatorApp;
import com.trindade.stringscreator.R;
import com.trindade.stringscreator.adapters.StringsAdapter;
import com.trindade.stringscreator.classes.copyToClipboard;
import com.trindade.stringscreator.databinding.ActivityMainBinding;
import com.trindade.stringscreator.databinding.MainFragmentBinding;
import java.util.ArrayList;
import java.util.HashMap;

public class MainFragment extends Fragment {
    
    MainFragmentBinding binding;
    ActivityMainBinding bindMain;
    boolean ADD_RES;
    HashMap<String, Object> map = new HashMap<>();
    int p = 0;
    int wrap = LinearLayout.LayoutParams.WRAP_CONTENT;
    int match = LinearLayout.LayoutParams.MATCH_PARENT;
    private static final int CREATE_FILE = 1;
    ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
    SharedPreferences sp;
    Context ctx;
    
    @Override
    public void onCreate(Bundle bund){
        super.onCreate(bund);
        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
        setExitTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = MainFragmentBinding.inflate(inflater);
        bindMain = ActivityMainBinding.inflate(getLayoutInflater());
        ctx = getActivity();

        initializeViews();
        clicks();

        return binding.getRoot();
    }

    private void initializeViews() {
        sp = ctx.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        if (!sp.contains("JSON")) {
            sp.edit().putString("JSON", "[]").apply();
            sp.edit().putBoolean("ADD_RES", true).apply();
        } else {
            getData();
        }
    }

    private void clicks() {
        bindMain.fab.setOnClickListener(
                v -> {
                    MaterialAlertDialogBuilder dialog =
                            new MaterialAlertDialogBuilder(getActivity());
                    View alertD = getLayoutInflater().inflate(R.layout.edittext, null);
                    dialog.setView(alertD);

                    final TextInputEditText stringName = alertD.findViewById(R.id.stringName);
                    final TextInputEditText stringValue = alertD.findViewById(R.id.stringValue);
                    final TextInputLayout til1 = alertD.findViewById(R.id.til1);
                    final TextInputLayout til2 = alertD.findViewById(R.id.til2);
                    stringName.setFocusableInTouchMode(true);
                    stringValue.setFocusableInTouchMode(true);
                    til1.setBoxCornerRadii((float) 12, (float) 12, (float) 12, (float) 12);
                    til2.setBoxCornerRadii((float) 12, (float) 12, (float) 12, (float) 12);
                    dialog.setTitle(getResources().getString(R.string.main_fab_text));
                    dialog.setPositiveButton(
                            getResources().getString(R.string.create),
                            (d, w) -> {
                                newString(
                                        stringName.getText().toString(),
                                        stringValue.getText().toString());
                            });
                    dialog.setNegativeButton(
                            getResources().getString(R.string.cancel), (d, w) -> {});
                    dialog.show();
                });

        binding.toolbar.setOnMenuItemClickListener(
                item -> {
                    if (item.getItemId() == R.id.view_code) {
                        dialogCode();
                    } else if (item.getItemId() == R.id.save_to_file) {
                        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("xml/plain");
                        intent.putExtra(Intent.EXTRA_TITLE, "strings.xml");
                        startActivityForResult(intent, CREATE_FILE);
                    }
                    return false;
                });
    }

    private void updateList() {
        StringsCreatorApp.updateListView(getActivity(), listmap, binding.listStrings);
    }

    private void putData() {
        sp.edit().putString("JSON", new Gson().toJson(listmap)).apply();
    }

    private void getData() {
        ADD_RES = sp.getBoolean("ADD_RES", false);
        listmap =
                new Gson()
                        .fromJson(
                                sp.getString("JSON", ""),
                                new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());
        updateList();
    }

    private void newString(String name, String value) {
        map = new HashMap();
        map.put("val", "<string name=\"" + name + "\">" + value + "</string>");
        map.put("name", name);
        map.put("value", value);
        listmap.add(map);
        updateList();
        putData();
    }

    private String forEach() {
        StringBuilder result = new StringBuilder();
        p = listmap.size() - 1;
        for (int r8 = 0; r8 < (int) (listmap.size()); r8++) {
            result.append("\n   ").append(listmap.get((int) p).get("val").toString());
            p--;
        }
        return result.toString();
    }

    private String generateCodeFull() {
        return "<resources>" + forEach() + "\n</resources>";
    }

    private void copyText(String text) {
        copyToClipboard.copy(getActivity(), text);
        Toast.makeText(getActivity(), "Copiado", Toast.LENGTH_SHORT).show();
        System.out.println("Copiado " + text);
    }

    private void dialogCode() {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getActivity());
        dialog.setTitle(getResources().getString(R.string.view_code));

        LinearLayout layout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(match, wrap);
        layout.setPadding(12, 12, 12, 12);
        layout.setLayoutParams(layoutParams);

        TextInputEditText txt = new TextInputEditText(getActivity());
        LinearLayout.LayoutParams txtParams = new LinearLayout.LayoutParams(wrap, wrap);
        txtParams.setMargins(18, 20, 20, 20);
        txt.setLayoutParams(txtParams);
        txt.setTextSize((int) 10);

        layout.addView(txt);

        txt.setText(generateCodeFull());
        dialog.setView(layout);
        dialog.setPositiveButton(
                getResources().getString(R.string.copy),
                (d, w) -> {
                    copyText(generateCodeFull());
                });
        dialog.setNegativeButton(getResources().getString(R.string.cancel), (d, w) -> {});

        dialog.show();
    }
}