package com.eliotstock.kv;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.SharedPreferencesBackupHelper;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.IOException;

/**
 * Backup agent. Hook up a SharedPreferencesBackupHelper and add logging when it gets used.
 */
public class KvBackupAgent extends BackupAgentHelper {

    private static final String TAG = KvBackupAgent.class.getName();

    // A key to uniquely identify the set of backup data
    private static final String PREFS_BACKUP_KEY = "prefs";

    // Allocate a helper and add it to the backup agent
    @Override
    public void onCreate() {
        Log.i(TAG, "Backup agent created");

        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this,
                KvActivity.PREFS);
        addHelper(PREFS_BACKUP_KEY, helper);
    }

    /**
     * Do nothing but add logging to BackupAgentHelper's behaviour.
     */
    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
                         ParcelFileDescriptor newState) throws IOException {
        Log.i(TAG, "Backing up");

        FileLogger.getInstance(this).logToFile("Backing up", this);

        super.onBackup(oldState, data, newState);
    }

    /**
     * Do nothing but add logging to BackupAgentHelper's behaviour.
     */
    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState)
            throws IOException {
        Log.i(TAG, "Restoring");

        FileLogger.getInstance(this).logToFile("Restoring", this);

        super.onRestore(data, appVersionCode, newState);
    }

}
