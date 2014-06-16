/* ExpensesListActivity is part of ExpensesTracker and displays a list of all expenses.
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

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;

import com.nicolacimmino.expensestracker.tracker.R;
import com.nicolacimmino.expensestracker.tracker.data_model.ExpensesDataContentProvider;

/*
 * The activity showing a list of all expenses.
 */
public class ExpensesListActivity extends ListActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Query the Expense Data Content provider and get all expenses sorted by timestamp.
    Cursor cursor = getContentResolver().query(
        ExpensesDataContentProvider.Contract.Expense.CONTENT_URI,
        ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_ALL, null, null,
        ExpensesDataContentProvider.Contract.Expense.COLUMN_NAME_TIMESTAMP + " DESC");

    // Let the Expenses Cursor Adapter render the cursor content into list items.
    ExpensesCursorAdapter adapter = new ExpensesCursorAdapter(
        this, R.layout.expeses_transactions_row, cursor, 0);
    setListAdapter(adapter);

  }

}