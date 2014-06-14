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
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Content Provider for expenses data.
 * Provides CRUD methods that expose the locally stored expenses data.
 *
 * Study note:
 * Content providers expose data for the application but also for other applications. Scope is
 * controlled in the manifest. We could make this provider exported and then other applications
 * could access our expense data just knowing the content provider authority (declared in the
 * manifest as well). This is how Android provides for instance contact data or gallery images
 * to applications. Note that it would be possible to expose directly the SQLLiteHelper in this
 * case as we use the data only internally, anyhow this allows for more loose coupling and
 * eventually t easily expose our data to other applications in the future.
 * Another reason to have a content provider is that it alows us to use the sync adapter framework
 * provided by Android which is based on content providers.
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
    sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, "expenses", ROUTE_EXPENSES);
    sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, "expenses/*", ROUTE_EXPENSES_ID);
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
        return Contract.Expense.CONTENT_TYPE;
      case ROUTE_EXPENSES_ID:
        return Contract.Expense.CONTENT_ITEM_TYPE;
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
  // URI. An activity that shows the result of a query in a list, for instance, could
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
        cursor = db.query(Contract.Expense.TABLE_NAME,
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
        cursor = db.query(Contract.Expense.TABLE_NAME,
            projection,
            "WHERE " + Contract.Expense.COLUMN_NAME_ID + "=?",
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
        long id = db.insertOrThrow(Contract.Expense.TABLE_NAME, null, contentValues);
        result = Uri.parse(Contract.Expense.CONTENT_URI + "/" + id);
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
        rowsAffected = db.delete(Contract.Expense.TABLE_NAME, null, null);
        break;
      case ROUTE_EXPENSES_ID:
        String id = uri.getLastPathSegment();
        rowsAffected = db.delete(Contract.Expense.TABLE_NAME,
            "WHERE " + Contract.Expense.COLUMN_NAME_ID + "=?",
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
        rowsAffected = db.update(Contract.Expense.TABLE_NAME,    // Table
            contentValues,                          // Content values
            null,                                   // Where clause
            null);                                  // Where args
        break;
      case ROUTE_EXPENSES_ID:
        String id = uri.getLastPathSegment();
        rowsAffected = db.update(Contract.Expense.TABLE_NAME,     // Table
            contentValues,                          // Content values
            "WHERE " + Contract.Expense.COLUMN_NAME_ID + "=?",// Where clause
            new String[]{id});                                           // Where args
        break;
      default:
        throw new UnsupportedOperationException("Unknown URI: " + uri);
    }

    getContext().getContentResolver().notifyChange(uri, null, false);

    return rowsAffected;
  }

  /**
   * The contract defining the interface of the ExpenseData data provider.
   * Here we define URIs, columns names and other constants that define the data exposed
   * by the Expense Data Provider.
   *
   * Study note:
   * This is the idea proposed in the examples in the official Android tutorial on content
   * providers. In this way though we expose the raw cursor from database, which is not in
   * itself so bad as SQLite is standard de facto in Android but should this change we now
   * go and have dependencies on cursor all over the code. It might be a good idea to abstract
   * the data through an adapter before exposing it in the content provider. For a simple
   * application where database storage is not likely to change it would be over engineering.
   */
  public static class Contract {

    // This is the content authority that applications will use to invoke our content provider.
    // If you change this you *must* change it also in the manifest in the android:authorities
    // property of the related provider section.
    public static final String CONTENT_AUTHORITY = "com.nicolacimmino.expensestracker.provider";

    // The base URI of all resources exposed by this content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Path of the expenses content
    public static final String PATH_EXPENSES = "expenses";

    public static class Expense implements BaseColumns {

      // MIME type for the Expenses content. "vnd" is for "vendor specific"
      public static final String CONTENT_TYPE =
          ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.expensetracker.expenses";

      // MIME type for the single Expense content. "vnd" is for "vendor specific"
      public static final String CONTENT_ITEM_TYPE =
          ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.expensetracker.expense";

      // URI for expenses resources.
      public static final Uri CONTENT_URI =
          BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_EXPENSES).build();

      // Table where expense data is saved.
      public static final String TABLE_NAME = "expenses";

      // Name of the columns of data in an expense
      public static final String COLUMN_NAME_ID = "_id";
      public static final String COLUMN_NAME_SOURCE = "source";
      public static final String COLUMN_NAME_DESTINATION = "destination";
      public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
      public static final String COLUMN_NAME_AMOUNT = "amount";
      public static final String COLUMN_NAME_CURRENCY = "currency";
      public static final String COLUMN_NAME_SYNC = "sync";
      public static final String COLUMN_NAME_DESCRIPTION = "description";

      // All table columns.
      public static final String[] COLUMN_NAME_ALL = {
          COLUMN_NAME_ID,
          COLUMN_NAME_SOURCE,
          COLUMN_NAME_DESTINATION,
          COLUMN_NAME_TIMESTAMP,
          COLUMN_NAME_AMOUNT,
          COLUMN_NAME_CURRENCY,
          COLUMN_NAME_SYNC,
          COLUMN_NAME_DESCRIPTION};
    }
  }

  /**
   * Wrapper for SQL operations on the Expenses database.
   * We make this a private inner class so nobody gets funny ideas to access directly to
   * database which would prevent side effects of data modification enforced by the
   * ExpensesDataContentProvider.
   */
  private static class ExpensesSQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Expenses";

    // This is the database schema version. If changes are made to the schema
    // this must be increased. On OnUpdate will take care to update eventual
    // existing databases to match the new schema.
    public static final int DATABASE_VERSION = 7;

    // Database creation statement.
    private static final String DATABASE_CREATE = "create table "
        + Contract.Expense.TABLE_NAME + "("
        + Contract.Expense.COLUMN_NAME_ID + " integer primary key autoincrement, "
        + Contract.Expense.COLUMN_NAME_TIMESTAMP + " timestamp default current_timestamp,"
        + Contract.Expense.COLUMN_NAME_SYNC + " text not null default '0',"
        + Contract.Expense.COLUMN_NAME_SOURCE + " text not null,"
        + Contract.Expense.COLUMN_NAME_DESTINATION + " text not null,"
        + Contract.Expense.COLUMN_NAME_DESCRIPTION + " text not null,"
        + Contract.Expense.COLUMN_NAME_CURRENCY + " text not null,"
        + Contract.Expense.COLUMN_NAME_AMOUNT + " text not null);";

    public ExpensesSQLiteHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
      // Create the database.
      sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
      // In this case we have a very simple policy to drop the database
      //  and recreate it if the version is changed. This means user data is
      //  lost. This is fine as the data is backed by the server.
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Contract.Expense.TABLE_NAME);
      onCreate(sqLiteDatabase);
    }
  }
}
