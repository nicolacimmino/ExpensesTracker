package com.nicolacimmino.expensestracker.tracker;

import android.app.Application;
import android.content.Context;
import com.nicolacimmino.expensestracker.tracker.data_sync.ExpensesAccountResolver;
import com.nicolacimmino.expensestracker.tracker.data_sync.GcmRegistration;
import com.nicolacimmino.expensestracker.tracker.ui.TransactionsInputActivity;

/**
 * Subclass of Application.
 * We use this to get some general initialization on application startup done.
 * Often this is done in the onCreate of a view that is considered the "main one", I wanted
 * to avoid having to have a view like that so that application can be extended more easily
 * in the future (for instance to allow external intents to start application on another view).
 */
public class ExpensesTrackerApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    // Inject context in the Expenses account resolver.
    ExpensesAccountResolver.getInstance().SetContext(getApplicationContext());

    // Make sure we are registered with Google Cloud Messaging so we can receive notifications.
    GcmRegistration gcmRegistration = new GcmRegistration(getApplicationContext(),
        getSharedPreferences(TransactionsInputActivity.class.getSimpleName(), Context.MODE_PRIVATE));

    gcmRegistration.Register();

    if (ExpensesAccountResolver.getInstance().getAccount() != null) {
      // Associate our Google Cloud Messaging registration id with this account so that the
      //  backend can send relevant notifications to us.
      gcmRegistration.SendRegistrationIdToBackend(ExpensesAccountResolver.getInstance().getAccount());
    }
  }
}
