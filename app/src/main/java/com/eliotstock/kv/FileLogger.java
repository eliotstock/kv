package com.eliotstock.kv;

import android.content.Context;
import android.content.Intent;
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
 * Singleton used by both the activity and the backup agent to log stuff to a file under local data.
 */
class FileLogger {

    static final String TAG = FileLogger.class.getName();

    private File logFile;
    private FileWriter logFileWriter;

    private static final String LOG_FILENAME = "log.txt";

    private static FileLogger instance;

    static final SimpleDateFormat TIMESTAMP_FORMAT =
            new SimpleDateFormat("YYYY-MM-dd HH:mm", Locale.ENGLISH);

    static final String LOG_ACTION = "LOG_ACTION";

    private FileLogger(Context c) {
        logFile = new File(c.getNoBackupFilesDir(), LOG_FILENAME);

        try {
            if (!logFile.exists()) {
                boolean created = logFile.createNewFile();

                Log.d(TAG, "File created: " + created);
            }

            logFileWriter = new FileWriter(logFile);

            logToFile("Application created", c);
        }
        catch (IOException ioe) {
            Log.e(TAG, ioe.getMessage());
        }
    }

    static FileLogger getInstance(Context c) {
        // context = c;

        if (instance == null) {
            instance = new FileLogger(c);
        }

        return instance;
    }

    void logToFile(String message, Context context) {
        if (logFile == null) {
            Log.e(TAG, "No log file.");

            return;
        }

        String timestamp = TIMESTAMP_FORMAT.format(new Date());
        String line = timestamp + ": " + message + "\n";

        try {
            logFileWriter.append(line);
            logFileWriter.flush();
        }
        catch (IOException ioe) {
            Log.e(TAG, ioe.getMessage());
        }

        // Send broadcast to activity to get it to refresh its TextView of the log.
        Intent logIntent = new Intent(LOG_ACTION);
        context.sendBroadcast(logIntent);
    }

    String getLog() {
        if (logFile == null) {
            Log.e(TAG, "No log file.");

            return "";
        }

        try {
            byte[] log = Files.readAllBytes(Paths.get(logFile.getPath()));
            return new String(log);
        }
        catch (IOException ioe) {
            Log.e(TAG, ioe.getMessage());

            return ioe.getMessage();
        }
    }

}
