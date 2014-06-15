package com.nicolacimmino.expensestracker.tracker.data_sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nicolacimmino.expensestracker.tracker.SharedPreferencesContract;
import com.nicolacimmino.expensestracker.tracker.expenses_api.ExpenseApiAuthenticator;
import com.nicolacimmino.expensestracker.tracker.expenses_api.ExpensesApiRegisterMobileRequest;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by nicola on 11/06/2014.
 */
public class GcmRegistration {

  String SENDER_ID = "958439099682";

  GoogleCloudMessaging gcm;
  AtomicInteger msgId = new AtomicInteger();
  Context mContext;
  private static String registration_id;
  SharedPreferences mSharedPreferences;

  static {
    registration_id = "";
  }

  public static String getRegistration_id() {
    return registration_id;
  }

  private static final String TAG = "GcmRegistration";

  public GcmRegistration(Context context, SharedPreferences sharedPreferences) {
    mContext = context;
    mSharedPreferences = sharedPreferences;
  }

  public void Register() {
    gcm = GoogleCloudMessaging.getInstance(mContext);
    registration_id = getRegistrationId();

    if (registration_id.isEmpty()) {
      registerInBackground();
    }
  }

  /**
   * Gets the current registration ID for application on GCM service.
   * If result is empty, the app needs to register.
   */
  private String getRegistrationId() {
    String registrationId = mSharedPreferences.getString(SharedPreferencesContract.PROPERTY_REG_ID, "");
    if (registrationId.isEmpty()) {
      Log.i(TAG, "Registration not found.");
      return "";
    }
    // Check if app was updated; if so, it must clear the registration ID
    // since the existing regID is not guaranteed to work with the new
    // app version.
    int registeredVersion = mSharedPreferences.getInt(SharedPreferencesContract.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
    int currentVersion = getAppVersion(mContext);
    if (registeredVersion != currentVersion) {
      Log.i(TAG, "App version changed.");
      return "";
    }
    return registrationId;
  }

  private void storeRegistrationId(String regId) {
    int appVersion = getAppVersion(mContext);
    Log.i(TAG, "Saving regId on app version " + appVersion);
    SharedPreferences.Editor editor = mSharedPreferences.edit();
    editor.putString(SharedPreferencesContract.PROPERTY_REG_ID, regId);
    editor.putInt(SharedPreferencesContract.PROPERTY_APP_VERSION, appVersion);
    editor.commit();
  }

  private static int getAppVersion(Context context) {
    try {
      PackageInfo packageInfo = context.getPackageManager()
          .getPackageInfo(context.getPackageName(), 0);
      return packageInfo.versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      // should never happen
      throw new RuntimeException("Could not get package name: " + e);
    }
  }


  private void registerInBackground() {
    new AsyncTask() {

      @Override
      protected Object doInBackground(Object[] objects) {
        String msg = "";
        try {
          if (gcm == null) {
            gcm = GoogleCloudMessaging.getInstance(mContext);
          }
          registration_id = gcm.register(SENDER_ID);
          msg = "Device registered, registration ID=" + registration_id;
          // Persist the regID - no need to register again.
          storeRegistrationId(registration_id);
        } catch (IOException ex) {
          msg = "Error :" + ex.getMessage();
          // If there is an error, don't just keep trying to register.
          // Require the user to click a button again, or perform
          // exponential back-off.
        }
        return msg;
      }

      @Override
      protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Log.i(TAG, "onPostExecute:" + o.toString());
      }
    }.execute(null, null, null);

  }

  public void SendRegistrationIdToBackend() {

    new AsyncTask() {

      @Override
      protected Object doInBackground(Object[] objects) {

        new ExpensesApiRegisterMobileRequest(registration_id).performRequest();

        return null;
      }
    }.execute();
  }

}
