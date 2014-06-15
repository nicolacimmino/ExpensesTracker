/* ExpenseDataSyncAdapter is part of ExpensesTracker and is the sync adapter to sync data to
 *   the server.
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

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import org.json.*;

import com.nicolacimmino.expensestracker.tracker.data_model.ExpensesDataContentProvider;
import com.nicolacimmino.expensestracker.tracker.expenses_api.ExpenseApiAuthenticator;
import com.nicolacimmino.expensestracker.tracker.expenses_api.ExpensesApiContract;
import com.nicolacimmino.expensestracker.tracker.expenses_api.ExpensesApiGetExpensesRequest;
import com.nicolacimmino.expensestracker.tracker.expenses_api.ExpensesApiNewExpenseRequest;

import java.net.HttpURLConnection;

/*
 * Sync adapter for Expense data.
 * This is used by Android sync manager to execute data syncs.
 * Since we extend  AbstractThreadedSyncAdapter our operations will be run in a thread different
 * than UI thread, so it's safe to have long blocking operations here.
 */
public class ExpenseDataSyncAdapter extends AbstractThreadedSyncAdapter {

  // Tag used for logging so we can filter messages from this class.
  public static final String TAG = "ExpenseDataSyncAdapter";

  private AccountManager mAccountManager;

  public ExpenseDataSyncAdapter(Context context, boolean autoInitialize) {
    super(context, autoInitialize);
    mAccountManager = AccountManager.get(context);
  }


  public void onPerformSync(Account account,Bundle extras,
      String authority,ContentProviderClient provider, SyncResult syncResult) {

    // Get expenses that are not yet synced with the server.
    Cursor expenses = getContext().getContentResolver().query(ExpensesDataContentProvider.Contract.Expense.CONTENT_URI,
        ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_ALL,
        ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_SYNC + "=?", new String[]{"0"}, null);

    while (expenses.moveToNext()) {
      HttpURLConnection connection = null;
      try {
        JSONObject expense = new JSONObject();
        expense.put(ExpensesApiContract.Expense.NOTES,
            expenses.getString(expenses.getColumnIndex(ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_DESCRIPTION)));
        expense.put(ExpensesApiContract.Expense.CURRENCY,
            expenses.getString(expenses.getColumnIndex(ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_CURRENCY)));
        expense.put(ExpensesApiContract.Expense.AMOUNT,
            expenses.getString(expenses.getColumnIndex(ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_AMOUNT)));
        expense.put(ExpensesApiContract.Expense.DESTINATION,
            expenses.getString(expenses.getColumnIndex(ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_DESTINATION)));
        expense.put(ExpensesApiContract.Expense.SOURCE,
            expenses.getString(expenses.getColumnIndex(ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_SOURCE)));
        expense.put(ExpensesApiContract.Expense.TIMESTAMP,
            expenses.getString(expenses.getColumnIndex(ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_TIMESTAMP)));
        expense.put(ExpensesApiContract.Expense.REPORTER_GCM_REG_ID,
            GcmRegistration.getRegistration_id());

        ExpensesApiNewExpenseRequest request = new ExpensesApiNewExpenseRequest(expense);
        if (request.performRequest()) {
          syncResult.stats.numEntries++;
          ContentValues values = new ContentValues();
          values.put(ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_SYNC, "1");
          getContext().getContentResolver().update(ExpensesDataContentProvider.Contract.Expense.CONTENT_URI,
              values,
              ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_ID + "=?",
              new String[]{expenses.getString(expenses.getColumnIndex(ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_ID))});
        } else {
          syncResult.stats.numIoExceptions++;
        }
      } catch (JSONException e) {
        Log.e(TAG, "Error building json doc: " + e.toString());
        syncResult.stats.numIoExceptions++;
        return;
      } finally {
        if (connection != null) {
          connection.disconnect();
        }
      }
    }
    expenses.close();

    fetchExpensesFromServer();

    Log.i(TAG, "Sync done");
  }

  public void fetchExpensesFromServer() {

    try {
      ExpensesApiGetExpensesRequest request = new ExpensesApiGetExpensesRequest();

      if(!request.performRequest()) {
        Log.e(TAG, "ExpensesApiGetExpensesRequest failed");
        return;
      }
      JSONArray jsonObject = request.getExpenses();

      getContext().getContentResolver().delete(ExpensesDataContentProvider.Contract.Expense.CONTENT_URI,
          ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_SYNC + "=1", null);

      for (int ix = 0; ix < jsonObject.length(); ix++) {
        Log.i(TAG, "Expense: " + jsonObject.getJSONObject(ix).get("amount"));

        ContentValues values = new ContentValues();
        values.put(ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_SYNC, "1");
        values.put(ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_CURRENCY, "eur");
        values.put(ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_AMOUNT, jsonObject.getJSONObject(ix).getString("amount"));
        values.put(ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_DESCRIPTION, jsonObject.getJSONObject(ix).getString("notes"));
        values.put(ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_SOURCE, jsonObject.getJSONObject(ix).getString("source"));
        values.put(ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_DESTINATION, jsonObject.getJSONObject(ix).getString("destination"));
        if (jsonObject.getJSONObject(ix).has("timestamp")) {
          values.put(ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_TIMESTAMP, jsonObject.getJSONObject(ix).getString("timestamp"));
        } else {
          values.put(ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_TIMESTAMP, "19750620 16:00:00");
        }

        getContext().getContentResolver().insert(ExpensesDataContentProvider.Contract.Expense.CONTENT_URI,
            values);
      }
    } catch (JSONException e) {
      Log.e(TAG, "Invalid JSON: " + e.toString());
      return;
    } finally {
    }

  }
}
