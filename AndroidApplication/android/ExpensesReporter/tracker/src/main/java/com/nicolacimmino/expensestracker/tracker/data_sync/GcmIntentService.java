/* GcmIntentService is part of ExpensesTracker and is responsible to take actions on
 *  Google Cloud Messaging intents received.
 *
 *   Copyright (C) 2014 Nicola Cimmino
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 */
package com.nicolacimmino.expensestracker.tracker.data_sync;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nicolacimmino.expensestracker.tracker.R;
import com.nicolacimmino.expensestracker.tracker.data_model.ExpensesDataContentProvider;
import com.nicolacimmino.expensestracker.tracker.ui.ExpensesListActivity;

/*
 * Intent service to process Google Cloud Messaging received intents.
 */
public class GcmIntentService extends IntentService {

  // ID of the notification informing about expenses having changed on the server.
  public static final int EXPENSES_UPDATED_NOTIFICATION_ID = 1;

  // Log tag.
  private static final String TAG = "GcmIntentService";

  // Android Notification Manager
  private NotificationManager mNotificationManager;

  // Builder class to build notifications.
  NotificationCompat.Builder builder;

  public GcmIntentService() {
    super("GcmIntentService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Bundle extras = intent.getExtras();
    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
    // The getMessageType() intent parameter must be the intent you received
    // in your BroadcastReceiver.
    String messageType = gcm.getMessageType(intent);

    if (!extras.isEmpty()) {

      // We ignore other message types (error, deleted etc.) we just process messages.
      if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

        // We know something changed on the server, so we request a sync of the ExpensesDataPRovider
        getContentResolver().requestSync(ExpensesAccountResolver.getInstance().getAccount(),
            ExpensesDataContentProvider.Contract.CONTENT_AUTHORITY, extras);

        // Post notification of received message.
        showExpensesUpdatedNotification();

        Log.i(TAG, "Received: " + extras.toString());
      }
    }

    // Release the wake lock provided by the WakefulBroadcastReceiver. This signals that we are
    // done and the device doesn't need to be kept awake anymore.
    GcmBroadcastReceiver.completeWakefulIntent(intent);
  }

  // Show a notification informing that expenses on the server have changed.
  // Note: this is not a really useful feature if this was a real product. The notification
  //  is here mainly for testing and to experiment with notifications.
  private void showExpensesUpdatedNotification() {
    mNotificationManager = (NotificationManager)
        this.getSystemService(Context.NOTIFICATION_SERVICE);

    PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        new Intent(this, ExpensesListActivity.class), 0);

    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getResources().getString(R.string.app_name))
            .setStyle(new NotificationCompat.BigTextStyle().bigText(getResources().getString(R.string.notification_data_changed)))
            .setContentText(getResources().getString(R.string.notification_data_changed))
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setLights(Color.rgb(219, 70, 0), 700, 1000)
            .setOnlyAlertOnce(true);

    mBuilder.setContentIntent(contentIntent);
    mNotificationManager.notify(EXPENSES_UPDATED_NOTIFICATION_ID, mBuilder.build());
  }
}