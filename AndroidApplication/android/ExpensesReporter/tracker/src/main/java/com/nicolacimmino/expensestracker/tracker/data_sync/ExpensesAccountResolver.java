/* ExpensesAccountResolver is part of ExpensesTracker and is responsible to provide resolution of
 *  the expenses account currently in use and related authentication token and GCM registration id.
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
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.nicolacimmino.expensestracker.tracker.expenses_api.ExpenseApiAuthenticator;

import java.io.IOException;

/*
 * Takes care to provide globally to the application the current account in use.
 * At the moment the application supports a single account only, should several exist
 * the first one would be used. This is here so in the future we can support multiple
 * accounts without changing the code all over the place. This is a singleton.
 */
public class ExpensesAccountResolver extends Application {

  // The only instance.
  private static ExpensesAccountResolver mInstance;

  // The application context.
  private static Context mContext;

  // Tag used in logging.
  private static final String TAG = "ExpensesAccountResolver";

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
    Account[] accounts = AccountManager.get(mContext).getAccountsByType(ExpenseApiAuthenticator.ExpenseAPIAuthenticatorContract.ACCOUNT_TYPE);

    if (accounts.length > 0) {
      return accounts[0];
    } else {
      return null;
    }
  }

  // Gets the username for the currently used account.
  public String getUsername() {
    return getAccount().name;
  }

  // Gets the authorization token related to the curretly used account.
  public String getAuthorizationToken() {
    String authToken = "";
    try {
      authToken = AccountManager.get(mContext).blockingGetAuthToken(getAccount(),
          ExpenseApiAuthenticator.ExpenseAPIAuthenticatorContract.AUTHTOKEN_TYPE_FULL_ACCESS, true);
    } catch (OperationCanceledException e) {
    } catch (IOException e) {
    } catch (AuthenticatorException e) {
      Log.i(TAG, "Exception on get auth token");
      return null;
    }

    return authToken;
  }
}
