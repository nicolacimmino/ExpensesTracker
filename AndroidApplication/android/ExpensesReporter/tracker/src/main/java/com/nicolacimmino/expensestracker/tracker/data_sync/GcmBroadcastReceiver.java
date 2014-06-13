/* GcmBroadcastReceiver is part of ExpensesTracker and is the receiver for Google Cloud Messaging
 *  system notifications.
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

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/*
 * Google Cloud Messaging notifications receiver.
 *
 *  Self-study note:
 *  this is a wakeful broadcast receiver. We declare in the manifest file the intents for which
 *  this should be invoked (com.google.android.c2dm.intent.RECEIVE). The responsibility of this
 *  class is to allow the GcmIntentService to perform its tasks without risking the phone to go
 *  back to sleep before it's done.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {

    // This is the name of GcmIntentService.
    ComponentName componentName = new ComponentName(
        context.getPackageName(), GcmIntentService.class.getName());

    // This starts the service keeping the device awake till it's done.
    startWakefulService(context, (intent.setComponent(componentName)));

    setResultCode(Activity.RESULT_OK);
  }

}
