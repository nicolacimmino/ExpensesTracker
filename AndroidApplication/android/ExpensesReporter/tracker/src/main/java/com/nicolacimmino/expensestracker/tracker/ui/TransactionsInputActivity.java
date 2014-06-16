/* MainActivity is part of ExpensesTracker and provides the expenses input interface.
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

package com.nicolacimmino.expensestracker.tracker.ui;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.nicolacimmino.expensestracker.tracker.R;
import com.nicolacimmino.expensestracker.tracker.SharedPreferencesContract;
import com.nicolacimmino.expensestracker.tracker.data_model.ExpensesDataContentProvider;
import com.nicolacimmino.expensestracker.tracker.expenses_api.ExpenseApiAuthenticator;
import com.nicolacimmino.expensestracker.tracker.data_sync.ExpensesAccountResolver;


public class TransactionsInputActivity extends Activity {

  // Tag used in logs.
  private static final String TAG = "MainActivity";

  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_transactions_input);

    // Add values to the source spinner.
    Spinner sourceSpinner = (Spinner) findViewById(R.id.sourceSpinner);
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.transaction_sources, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    sourceSpinner.setAdapter(adapter);

    // Recover from preferences the last used source.
    String lastSource = getPreferences(MODE_PRIVATE)
        .getString(SharedPreferencesContract.SAVED_STATE_LAST_SOURCE, "");
    if (lastSource != "") {
      sourceSpinner.setSelection(((ArrayAdapter) sourceSpinner.getAdapter())
          .getPosition(lastSource));
    }

    // Add values to the destination spinner.
    Spinner destinationSpinner = (Spinner) findViewById(R.id.destinationSpinner);
    adapter = ArrayAdapter.createFromResource(this,
        R.array.transaction_destinations, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    destinationSpinner.setAdapter(adapter);

    // Recover from preferences the last used destination.
    String lastDestination = getPreferences(MODE_PRIVATE)
        .getString(SharedPreferencesContract.SAVED_STATE_LAST_DESTINATION, "");
    if (lastDestination != "") {
      destinationSpinner.setSelection(((ArrayAdapter) destinationSpinner.getAdapter())
          .getPosition(lastDestination));
    }

    // If we don't have an account yet ask first account manager to have the user to create one.
    // This will show immediatley the ExpensesDataLoginActivity before the user can proceed.
    if (ExpensesAccountResolver.getInstance().getAccount() == null) {
      AccountManager.get(this).addAccount(ExpenseApiAuthenticator.ExpenseAPIAuthenticatorContract.ACCOUNT_TYPE,
          ExpenseApiAuthenticator.ExpenseAPIAuthenticatorContract.AUTHTOKEN_TYPE_FULL_ACCESS,
          null, null, this, null, null);
    }
  }

  @Override
  protected void onStop() {
    super.onStop();

    // Store some values that we want to persist across instance of the activity.
    Spinner sourceSpinner = (Spinner) findViewById(R.id.sourceSpinner);
    Spinner destinationSpinner = (Spinner) findViewById(R.id.destinationSpinner);

    SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
    editor.putString(SharedPreferencesContract.SAVED_STATE_LAST_SOURCE, sourceSpinner.getSelectedItem().toString());
    editor.putString(SharedPreferencesContract.SAVED_STATE_LAST_DESTINATION, destinationSpinner.getSelectedItem().toString());
    editor.commit();
  }

  /*
   * Action bar created.
   * We must here inflate its content.
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_transactions_input, menu);
    return true;
  }

  /*
   * Action bar item clicked.
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    // User confirmed the transaction.
    if (id == R.id.action_submit) {
      onTransactionConfirmClick();
      return true;
    }

    // User wants to see the expenses list.
    if (id == R.id.action_show_expenses) {
      Intent intent = new Intent(this, ExpensesListActivity.class);
      startActivity(intent);
    }

    // Let base class handle other stuff (eg back button etc.)
    return super.onOptionsItemSelected(item);
  }


  /*
   * User clicked to confirm the transaction.
   * Here we save the new transaction and give user visual feedback.
   */
  public void onTransactionConfirmClick() {

    try {

      // Get the input controls.
      TextView amountView = (TextView) findViewById(R.id.textAmount);
      Spinner sourceSpinner = (Spinner) findViewById(R.id.sourceSpinner);
      Spinner destinationSpinner = (Spinner) findViewById(R.id.destinationSpinner);
      TextView notesView = (TextView) findViewById(R.id.textDescription);
      TextView currencyView = (TextView) findViewById(R.id.textAmountCurrency);

      // We cannot accept an empty amount. Give error and don't save.
      // Also set focus on the amount input control so it's faster for the user
      //  to rectify the problem.
      if (amountView.getText().toString().isEmpty()) {
        amountView.setError(getString(R.string.error_amount));
        amountView.requestFocus();
        return;
      }

      // Prepare values and invoke insert on the Expense Data Content Provider
      ContentValues values = new ContentValues();
      values.put(ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_AMOUNT, amountView.getText().toString());
      values.put(ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_SOURCE, sourceSpinner.getSelectedItem().toString());
      values.put(ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_DESTINATION, destinationSpinner.getSelectedItem().toString());
      values.put(ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_DESCRIPTION, notesView.getText().toString());
      values.put(ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_CURRENCY, currencyView.getText().toString());
      getContentResolver().insert(ExpensesDataContentProvider.Contract.Expense.CONTENT_URI, values);

      // Clear the input fields so the interface is ready for another operation.
      amountView.setText("");
      notesView.setText("");
      amountView.requestFocus();

      // We show a toast as visual feedback that something has happened.
      Toast.makeText(getApplicationContext(), getString(R.string.saved), Toast.LENGTH_SHORT).show();

      // Request to sync data with the backend
      ContentResolver.requestSync(ExpensesAccountResolver.getInstance().getAccount(), ExpensesDataContentProvider.Contract.CONTENT_AUTHORITY, new Bundle());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
