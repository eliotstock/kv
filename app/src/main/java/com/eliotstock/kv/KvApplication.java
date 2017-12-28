package com.eliotstock.kv;

import android.app.Application;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Extend android.app.Application and add some logging, simply to show that this class is NOT
 * instantiated when the backup manager backs us up.
 */
public class KvApplication extends Application {

    static final String TAG = KvApplication.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "Application subclass created");

        // Note: this will NOT happen when the backup runs. The backup manager will instantiate
        // Application, not our Application subclass.
        FileLogger.getInstance(this)
                .logToFile("Application subclass created", this);
    }

}
