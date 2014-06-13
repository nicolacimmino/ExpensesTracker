/* ExpensesDataContentProvider is part of Expensestracker is the expenses data provider.
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
package com.nicolacimmino.expensestracker.tracker.data_model;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import com.nicolacimmino.expensestracker.tracker.data_sync.ExpensesAccountResolver;


/**
 * Content Provider for expenses data.
 * Provides CRUD methods that expose the locally stored expenses data.
 *
 * Self-study note:
 * Content providers expose data for the application but also for other applications. Scope is
 * controlled in the manifest. We could make this provider public and then other applications
 * could access our expense data just knowing the content provider authority (declared in the
 * manifest as well). This is how Android provides for instance contact data or gallery images
 * to applications.
 */
public class ExpensesDataContentProvider extends ContentProvider {

  // The database helper performs the actual access to the database.
  ExpensesSQLiteHelper mDataBaseHelper;

  // IDs for the various routes (data URIs).
  public static final int ROUTE_EXPENSES = 1;
  public static final int ROUTE_EXPENSES_ID = 2;

  // This is the URI matcher that is used to resolve a URI route to a numeric ID.
  private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

  static {
    sUriMatcher.addURI(ExpenseDataContract.CONTENT_AUTHORITY, "expenses", ROUTE_EXPENSES);
    sUriMatcher.addURI(ExpenseDataContract.CONTENT_AUTHORITY, "expenses/*", ROUTE_EXPENSES_ID);
  }

  @Override
  public boolean onCreate() {
    mDataBaseHelper = new ExpensesSQLiteHelper(getContext());
    return true;
  }

  /*
   * Gets the MIME type of a given URI.
   */
  @Override
  public String getType(Uri uri) {
    final int match = sUriMatcher.match(uri);
    switch (match) {
      case ROUTE_EXPENSES:
        return ExpenseDataContract.Expense.CONTENT_TYPE;
      case ROUTE_EXPENSES_ID:
        return ExpenseDataContract.Expense.CONTENT_ITEM_TYPE;
      default:
        throw new UnsupportedOperationException("Unknown URI:" + uri);
    }
  }

  // Self-study note:
  // Each of the query, insert, update, delete methods performs one of the CRUD operations
  // either on a specific expense or all expenses (where sensible). The pattern is always
  // to choose the operation to perform based on the URI. Then for query we return a cursor
  // that has the notification URI set to the URI of the data and of the other methods we
  // notify the eventual observers that there has been a change in data originated by that
  // URI. A an activity that shows the result of a query in a list, for instance, could
  // listen for these notifications and refresh itself if the underlying data is changed.
  // Of course this works only if data is changed through the ContentProvider.

  @Override
  public Cursor query(Uri uri, String[] projection, String selection,
                      String[] selectionArgs, String sortOrder) {
    SQLiteDatabase db = mDataBaseHelper.getReadableDatabase();
    Cursor cursor;
    int match = sUriMatcher.match(uri);
    switch (match) {
      case ROUTE_EXPENSES:
        // Caller wants all expenses.
        cursor = db.query(ExpenseDataContract.Expense.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder);

        // Set the URI to watch for eventual data changes.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;

      case ROUTE_EXPENSES_ID:
        // Caller wants a specific expense.
        String id = uri.getLastPathSegment();
        cursor = db.query(ExpenseDataContract.Expense.TABLE_NAME,
            projection,
            "WHERE " + ExpenseDataContract.Expense.COLUMN_NAME_ID + "=?",
            new String[]{id},
            null,
            null,
            null);

        // Set the URI to watch for eventual data changes.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;

      default:
        throw new UnsupportedOperationException("Unknonw URI: " + uri);
    }
  }

  @Override
  public Uri insert(Uri uri, ContentValues contentValues) {
    final SQLiteDatabase db = mDataBaseHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    Uri result;
    switch (match) {
      case ROUTE_EXPENSES:
        // Caller wants to insert one expense.
        long id = db.insertOrThrow(ExpenseDataContract.Expense.TABLE_NAME, null, contentValues);
        result = Uri.parse(ExpenseDataContract.Expense.CONTENT_URI + "/" + id);
        break;
      case ROUTE_EXPENSES_ID:
        throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
      default:
        throw new UnsupportedOperationException("Unknown URI: " + uri);
    }

    // Inform eventual observers that data related to this URI changed.
    getContext().getContentResolver().notifyChange(uri, null, false);

    return result;
  }

  @Override
  public int delete(Uri uri, String s, String[] strings) {
    final SQLiteDatabase db = mDataBaseHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    int rowsAffected;
    switch (match) {
      case ROUTE_EXPENSES:
        rowsAffected = db.delete(ExpenseDataContract.Expense.TABLE_NAME, null, null);
        break;
      case ROUTE_EXPENSES_ID:
        String id = uri.getLastPathSegment();
        rowsAffected = db.delete(ExpenseDataContract.Expense.TABLE_NAME,
            "WHERE " + ExpenseDataContract.Expense.COLUMN_NAME_ID + "=?",
            new String[]{id});
        break;
      default:
        throw new UnsupportedOperationException("Unknonw URI: " + uri);
    }

    getContext().getContentResolver().notifyChange(uri, null, false);

    return rowsAffected;
  }

  @Override
  public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
    final SQLiteDatabase db = mDataBaseHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    int rowsAffected;
    switch (match) {
      case ROUTE_EXPENSES:
        rowsAffected = db.update(ExpenseDataContract.Expense.TABLE_NAME,    // Table
            contentValues,                          // Content values
            null,                                   // Where clause
            null);                                  // Where args
        break;
      case ROUTE_EXPENSES_ID:
        String id = uri.getLastPathSegment();
        rowsAffected = db.update(ExpenseDataContract.Expense.TABLE_NAME,     // Table
            contentValues,                          // Content values
            "WHERE " + ExpenseDataContract.Expense.COLUMN_NAME_ID + "=?",// Where clause
            new String[]{id});                                           // Where args
        break;
      default:
        throw new UnsupportedOperationException("Unknown URI: " + uri);
    }

    getContext().getContentResolver().notifyChange(uri, null, false);

    return rowsAffected;
  }
}
