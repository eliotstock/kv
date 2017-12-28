package com.eliotstock.kv;

import android.app.AlertDialog;
import android.app.backup.BackupManager;
import android.app.backup.RestoreObserver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

/**
 * Simple activity to show a timestamp value stored in a shared preferences file and a log of all
 * backup and restore operations.
 */
public class KvActivity extends AppCompatActivity {

    static final String TAG = KvActivity.class.getName();

    // The name of the SharedPreferences file.
    public static final String PREFS = "user_preferences";

    // The key in the SharedPreferences for our only value, a timestamp.
    public static final String KEY_TIMESTAMP = "timestamp";

    private TextView sharedPrefText;

    private TextView logText;

    private BackupManager backupManager;

    private SharedPreferences prefs;

    private AlertDialog missingApiKeyDialog;

    private static final String API_KEY_NAME = "com.google.android.backup.api_key";

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(FileLogger.LOG_ACTION)) {
                // This is a signal from the FileLogger that the log file has changed and needs to
                // be updated on the UI.
                onResume();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefText = findViewById(R.id.text_shared_pref_value);
        logText = findViewById(R.id.text_log);

        backupManager = new BackupManager(this);

        prefs = getSharedPreferences(PREFS, 0);
        String timestamp = prefs.getString(KEY_TIMESTAMP, null);

        // If our single value in the shared preferences doesn't yet exist, create it.
        if (timestamp == null) {
            updateValue(null);
        }

        IntentFilter filter = new IntentFilter(FileLogger.LOG_ACTION);
        registerReceiver(receiver, filter);

        checkForApiKey();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Put the timestamp on the view.
        if (sharedPrefText != null && prefs.getString(KEY_TIMESTAMP, null) != null) {
            sharedPrefText.setText(prefs.getString(KEY_TIMESTAMP, null));
        }

        // Put the log file on the view.
        if (logText != null) {
            String log = FileLogger.getInstance(this).getLog();
            logText.setText(log);
        }
    }

    public void updateValue(View view) {
        Log.i(TAG, "Updating value in shared preferences");
        FileLogger.getInstance(this)
                .logToFile("Updating value in shared preferences", this);

        String timestamp = FileLogger.TIMESTAMP_FORMAT.format(new Date());

        prefs.edit().putString(KEY_TIMESTAMP, timestamp).apply();

        // Update the view to reflect the new value.
        onResume();
    }

    // Not important. Add to an overflow menu later, maybe.
    public void requestBackup(View view) {
        Log.i(TAG, "Telling backup manager that data has changed");

        backupManager.dataChanged();
    }

    // Not important. Add to an overflow menu later, maybe.
    public void requestRestore(View view) {
        Log.i(TAG, "Requesting restore from backup manager");

        backupManager.requestRestore(new RestoreObserver() {
            @Override
            public void restoreStarting(int numPackages) {
                Log.i(TAG, "Restore starting");

                super.restoreStarting(numPackages);
            }

            @Override
            public void onUpdate(int nowBeingRestored, String currentPackage) {
                Log.i(TAG, "Updating");
            }

            public void restoreFinished(int error) {
                Log.i(TAG, "Restore finished");
            }
        });
    }

    private void checkForApiKey() {
        try {
            Bundle metaData = getPackageManager().getApplicationInfo(getPackageName(),
                    PackageManager.GET_META_DATA).metaData;
            String apiKey = metaData.getString(API_KEY_NAME);

            if (apiKey == null || apiKey.isEmpty()) {
                showMissingApiKeyDialog();
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            showMissingApiKeyDialog();
        }
    }

    private void showMissingApiKeyDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.missing_api_key_dialog);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                KvActivity.this.missingApiKeyDialog.dismiss();
            }
        });

        runOnUiThread(new Runnable() {
            public void run() {
                KvActivity.this.missingApiKeyDialog = builder.create();
                KvActivity.this.missingApiKeyDialog.show();
            }
        });
    }

}
