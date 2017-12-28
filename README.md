# Android key/value backup

Use this app to improve your understanding of Android's key/value backup service, detailed here:

https://developer.android.com/guide/topics/data/keyvaluebackup.html

## Building

* Get an API key for the Android backup service: https://developer.android.com/google/backup/signup.html

* Create `app/src/res/values/keys.xml`. This file is `.gitignore`d already.

* Add a single string value to that file like so:

```
<resources>
   <string name="backup_api_key">YOUR_API_KEY</string>
</resources>
```

* Build and run from Android Studio.

## Testing

* The app will add a single value, a timestamp, to a shared preferences file. The app's backup agent will back up this shared preferences file.

* Backups run, by default, when your device is charging, on WiFi, and has been idle for an hour or two.

* Backup operations are logged to a file and shown on the app UI.

* To kick off a backup manually, or to backup to a file on your host machine instead of Google's cloud, use `adb shell bmgr`. See this page for details:

https://developer.android.com/guide/topics/data/testingbackup.html
