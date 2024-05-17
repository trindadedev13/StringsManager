package com.trindade.stringscreator;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Process;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import com.google.gson.Gson;
import com.trindade.stringscreator.activities.DebugActivity;
import com.trindade.stringscreator.adapters.StringsAdapter;
import com.trindade.stringscreator.fragments.MainFragment;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

public class StringsCreatorApp extends Application {

    private static FragmentManager sFragmentManager;

    public static void init(FragmentManager fragmentManager) {
        sFragmentManager = fragmentManager;
    }

    public static void updateListView(Context ctx, ArrayList<HashMap<String, Object>> listmap, ListView listStrings) {
        listStrings.setAdapter(new StringsAdapter(ctx, listmap, listStrings));
        ((BaseAdapter) listStrings.getAdapter()).notifyDataSetChanged();
        if (sFragmentManager != null) {
            sFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, new MainFragment())
                    .commit();
        }
    }

    public static String getOrientation(Context ctx) {
        Configuration configuration = ctx.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            return "portrait";
        } else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return "landscape";
        } else {
            return "undefined";
        }
    }

    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    @Override
    public void onCreate() {
        this.uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
        @Override
          public void uncaughtException(Thread thread, Throwable ex) {
             Intent intent = new Intent(getApplicationContext(), DebugActivity.class);
             intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
             intent.putExtra("error", getStackTrace(ex)); PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 11111, intent, PendingIntent.FLAG_ONE_SHOT);
             AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
             am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, pendingIntent);
             Process.killProcess(Process.myPid());
             System.exit(2);
             uncaughtExceptionHandler.uncaughtException(thread, ex);
          }
        });
        super.onCreate();
    }

    private String getStackTrace(Throwable th) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        Throwable cause = th;
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        final String stacktraceAsString = result.toString();
        printWriter.close();
        return stacktraceAsString;
    }
}