/* ExpensesAccountResolver is part of ExpensesTracker and is responsible to provide resolution of
 *  the expenses account currently in use.
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

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

/*
 * Takes care to provide globally to the application the current account in use.
 * At the moment the application supports a single account only, should several exist
 * the first one would be used. This is here so in the future we can support multiple
 * accounts without changing the code all over the place. This is a singleton.
 */
public class ExpensesAccountResolver {

  // The only instance.
  private static ExpensesAccountResolver mInstance;

  // The application context.
  private static Context mContext;

  // Private constructor so nobody can instantiate us.
  private ExpensesAccountResolver() {
  }

  // Sets the application context.
  public void SetContext(Context context) {
    mContext = context;
  }

  // Gets the only instance.
  public static synchronized ExpensesAccountResolver getInstance() {
    if (mInstance == null) {
      mInstance = new ExpensesAccountResolver();
    }
    return mInstance;
  }

  // Gets the currently used account.
  public Account getAccount() {

    // At the moment we resolve the current account just by taking the first account of the
    // expenses accounts type.
    Account[] accounts = AccountManager.get(mContext).getAccountsByType(ExpenseAPIAuthenticator.ExpenseAPIAuthenticatorContract.ACCOUNT_TYPE);

    if (accounts.length > 0) {
      return accounts[0];
    } else {
      return null;
    }
  }

}
