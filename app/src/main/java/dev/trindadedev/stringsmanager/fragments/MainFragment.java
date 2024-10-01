package dev.trindadedev.stringsmanager.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.transition.MaterialSharedAxis;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import dev.trindadedev.stringsmanager.StringsCreatorApp;
import dev.trindadedev.stringsmanager.classes.GlobalConfig;
import dev.trindadedev.stringsmanager.classes.SimpleHighlighter;
import dev.trindadedev.stringsmanager.*;
import dev.trindadedev.stringsmanager.classes.copyToClipboard;
import dev.trindadedev.stringsmanager.databinding.MainFragmentBinding;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MainFragment extends Fragment {

    MainFragmentBinding binding;
    copyToClipboard copyToClipboard = new copyToClipboard();
    boolean ADD_RES;
    HashMap<String, Object> map = new HashMap<>();
    int p = 0;
    int wrap = LinearLayout.LayoutParams.WRAP_CONTENT;
    int match = LinearLayout.LayoutParams.MATCH_PARENT;
    private static final int CREATE_FILE = 1;
    ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
    SharedPreferences sp;
    Context ctx;
    StringsCreatorAppLog logger = new StringsCreatorAppLog();

    @Override
    public void onCreate(Bundle bund) {
        super.onCreate(bund);
        setEnterTransition(new MaterialSharedAxis(GlobalConfig.SharedAxisEnter, GlobalConfig.SharedAxisEnterBoolean));
        setExitTransition(new MaterialSharedAxis(GlobalConfig.SharedAxisExit, GlobalConfig.SharedAxisExitBoolean));
    }

    @Override
    public void onActivityResult(int rC, int rsC, Intent dt) {
        super.onActivityResult(rC, rsC, dt);
        if (rC == 1 && rsC == Activity.RESULT_OK) {
            if (dt != null) {
                Uri uri = dt.getData();
                try (OutputStream outputStream = getActivity().getContentResolver().openOutputStream(uri)) {
                    outputStream.write(generateCodeFull().getBytes());
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = MainFragmentBinding.inflate(inflater);
        ctx = getActivity();
        
        if (ctx != null) {
            sp = ctx.getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        }

        initializeViews();
        clicks();

        return binding.getRoot();
    }

    private void initializeViews() {
        if (ctx != null) {
            sp = ctx.getSharedPreferences("prefs", Context.MODE_PRIVATE);
            if (!sp.contains("JSON")) {
                sp.edit().putString("JSON", "[]").apply();
                sp.edit().putBoolean("ADD_RES", true).apply();
            } else {
                getData(binding.listStrings);
            }
        }
    }

    private void clicks() {

        binding.toolbar.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.view_code) {
                        dialogCode();
                    } else if (item.getItemId() == R.id.save_to_file) {
                        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("xml/plain");
                        intent.putExtra(Intent.EXTRA_TITLE, "strings.xml");
                        startActivityForResult(intent, CREATE_FILE);
                    } else if (item.getItemId() == R.id.add_new_string) {
                        if (binding.listStrings != null) {
                            MaterialAlertDialogBuilder dialog =
                                    new MaterialAlertDialogBuilder(getActivity());
                            View alertD =
                                    getLayoutInflater()
                                            .inflate(R.layout.content_dialog_create_edit, null);
                            dialog.setView(alertD);

                            final TextInputEditText stringName =
                                    alertD.findViewById(R.id.stringName);
                            final TextInputEditText stringValue =
                                    alertD.findViewById(R.id.stringValue);

                            stringName.setFocusableInTouchMode(true);
                            stringValue.setFocusableInTouchMode(true);

                            dialog.setTitle(getResources().getString(R.string.main_fab_text));
                            dialog.setPositiveButton(
                                    getResources().getString(R.string.create),
                                    (d, w) -> {
                                        newString(
                                                stringName.getText().toString(),
                                                stringValue.getText().toString());
                                    });
                            dialog.setNegativeButton(
                                    getResources().getString(R.string.cancel), null);
                            dialog.show();
                        } else {
                            logger.add("null list");
                        }
                    }
                    return false;
                });
    }

    private void updateList() {
        StringsCreatorApp.updateListView(getActivity(), listmap, binding.listStrings);
    }

    private void newString(String name, String value) {
        map.clear();
        map.put("val", "<string name=\"" + name + "\">" + value + "</string>");
        map.put("name", name);
        map.put("value", value);
        listmap.add(map);
        updateList();
        putData();
    }

    private String forEach() {
        StringBuilder result = new StringBuilder();
        Collections.reverse(listmap);
        p = listmap.size() - 1;
        for (int r8 = 0; r8 < (int) (listmap.size()); r8++) {
            result.append("\n   ").append(listmap.get(r8).get("val").toString());
            p--;
        }
        return result.toString();
    }

    private String generateCodeFull() {
        if (sp.getBoolean("ADD_RES", false)) {
            return "<resources>" + forEach() + "\n</resources>";
        } else {
            return forEach();
        }
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

        TextView txt = new TextView(getActivity());
        LinearLayout.LayoutParams txtParams = new LinearLayout.LayoutParams(wrap, wrap);
        txtParams.setMargins(18, 20, 20, 20);
        txt.setLayoutParams(txtParams);
        txt.setTextSize(10);

        layout.addView(txt);

        txt.setText(generateCodeFull());
        new SimpleHighlighter(txt, "xml");
        dialog.setView(layout);
        dialog.setPositiveButton(
                getResources().getString(R.string.copy),
                (d, w) -> {
                    copyText(generateCodeFull());
                });
        dialog.setNegativeButton(getResources().getString(R.string.cancel), (d, w) -> {});

        dialog.show();
    }

    private void updateList(ListView ctc) {
        StringsCreatorApp.updateListView(ctx, listmap, ctc);
        Log.d("Utils", "List updated successfully.");
    }

    private void putData() {
        sp.edit().putString("JSON", new Gson().toJson(listmap)).apply();
        Log.d("Utils", "Data saved successfully.");
    }

    private void getData(ListView listV) {
        if (ctx != null) {
            boolean ADD_RES = sp.getBoolean("ADD_RES", false);
            listmap = new Gson().fromJson(sp.getString("JSON", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType()); 
            updateList(binding.listStrings);
        }
    }
}
