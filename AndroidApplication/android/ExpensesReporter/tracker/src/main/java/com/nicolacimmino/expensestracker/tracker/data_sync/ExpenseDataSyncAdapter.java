/* ExpenseDataSyncAdapter is part of ExpensesReporter and is the sync adapter to sync data to
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

import com.nicolacimmino.expensestracker.tracker.data_model.ExpenseDataContract;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

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


    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {

        Log.i(TAG, "Sync for: " + account.name);
        String authToken="";
        try {
            authToken = mAccountManager.blockingGetAuthToken(account, ExpenseDataAuthenticatorContract.AUTHTOKEN_TYPE_FULL_ACCESS, true);
        }
        catch(Exception e) {
            //syncResult.stats.numAuthExceptions++;
            Log.i("","Exception on get auth token");
            syncResult.stats.numAuthExceptions++;
            return;
        }

        // Get expenses that are not yet synced with the server.
        Cursor expenses = getContext().getContentResolver().query(ExpenseDataContract.Expense.CONTENT_URI,
                                                ExpenseDataContract.Expense.COLUMN_NAME_ALL,
                                                ExpenseDataContract.Expense.COLUMN_NAME_SYNC + "=?",
                                                new String[]{"0"},
                                                null);
        Log.i(TAG, "Starting sync");

        while(expenses.moveToNext()) {
            HttpURLConnection connection = null;
            try {
                Log.i(TAG, "Posting one item");

                JSONObject authenticationData = new JSONObject();
                authenticationData.put("notes", expenses.getString(expenses.getColumnIndex(ExpenseDataContract.Expense.COLUMN_NAME_DESCRIPTION)));
                authenticationData.put("currency", expenses.getString(expenses.getColumnIndex(ExpenseDataContract.Expense.COLUMN_NAME_CURRENCY)));
                authenticationData.put("amount", expenses.getString(expenses.getColumnIndex(ExpenseDataContract.Expense.COLUMN_NAME_AMOUNT)));
                authenticationData.put("to", expenses.getString(expenses.getColumnIndex(ExpenseDataContract.Expense.COLUMN_NAME_DESTINATION)));
                authenticationData.put("from", expenses.getString(expenses.getColumnIndex(ExpenseDataContract.Expense.COLUMN_NAME_SOURCE)));
                authenticationData.put("timestamp", expenses.getString(expenses.getColumnIndex(ExpenseDataContract.Expense.COLUMN_NAME_TIMESTAMP)));

                byte[] postDataBytes = authenticationData.toString(0).getBytes("UTF-8");

                URL url = new URL("http://expensesapi.nicolacimmino.com/expenses/nicola?auth_token=" + authToken);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Content-Length", "" + Integer.toString(postDataBytes.length));
                connection.setUseCaches(false);

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
                wr.write(postDataBytes);
                wr.flush();
                wr.close();

                int response = connection.getResponseCode();
                Log.i(TAG, String.valueOf(response));
                connection.disconnect();

                if(response == 200) {
                    syncResult.stats.numEntries++;
                    ContentValues values = new ContentValues();
                    values.put(ExpenseDataContract.Expense.COLUMN_NAME_SYNC, "1");
                    getContext().getContentResolver().update(ExpenseDataContract.Expense.CONTENT_URI,
                            values,
                            ExpenseDataContract.Expense.COLUMN_NAME_ID + "=?",
                            new String[]{expenses.getString(expenses.getColumnIndex(ExpenseDataContract.Expense.COLUMN_NAME_ID))});
                }
                else {
                    syncResult.stats.numIoExceptions++;
                }
            } catch (MalformedURLException e) {
                Log.e(TAG, "URL is malformed", e);
                syncResult.stats.numParseExceptions++;
                return;
            } catch (IOException e) {
                Log.e(TAG, "Error reading from network: " + e.toString());
                syncResult.stats.numIoExceptions++;
                return;
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
        Log.i(TAG, "Sync done");
    }
}
