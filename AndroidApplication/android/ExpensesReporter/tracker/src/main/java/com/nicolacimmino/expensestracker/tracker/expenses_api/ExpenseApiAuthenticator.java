/* ExpenseDataAuthenticator is part of ExpensesTracker and is the account authenticator.
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
package com.nicolacimmino.expensestracker.tracker.expenses_api;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.nicolacimmino.expensestracker.tracker.ui.LoginActivity;

/*
 * This class subclasses AbstractAccountAuthenticator and provides concrete implementation
 * of authentication against the Expenses API.
 */
public class ExpenseApiAuthenticator extends AbstractAccountAuthenticator {

  private static final String TAG = "ExpenseDataAuthenticator";

  private Context mContext;

  public ExpenseApiAuthenticator(Context context) {
    super(context);
    mContext = context;
  }

  // Invoked when a new account is to be added.
  @Override
  public Bundle addAccount(
      AccountAuthenticatorResponse response,
      String accountType,
      String authTokenType,
      String[] strings,
      Bundle bundle) throws NetworkErrorException {

    // We want to start the ExpenseDataLoginActivity. We don't start the activity here but return
    // a bundle for the AccountManager to actually start it.
    final Intent intent = new Intent(mContext, LoginActivity.class);
    intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
    final Bundle returnBundle = new Bundle();
    returnBundle.putParcelable(AccountManager.KEY_INTENT, intent);
    return returnBundle;
  }

  @Override
  public Bundle confirmCredentials(AccountAuthenticatorResponse response,
                                   Account account, Bundle bundle) throws NetworkErrorException {
    // Ignore attempts to confirm credentials
    return null;
  }

  @Override
  public Bundle getAuthToken(AccountAuthenticatorResponse response,
                             Account account, String authTokenType, Bundle bundle) throws NetworkErrorException {

    // See if we have a cached authentication token.
    final AccountManager accountManager = AccountManager.get(mContext);
    String authToken = accountManager.peekAuthToken(account, authTokenType);

    // If we don't have a token we try to authenticate with the password stored with the
    //  account manager (if any).
    if (TextUtils.isEmpty(authToken)) {
      final String password = accountManager.getPassword(account);
      if (password != null) {
        authToken = ExpenseApiAuthenticator.signInUser(account.name, password, authTokenType);
      }
    }

    // If we get an authToken we return it in a bundle reporting also the account name and type.
    if (!TextUtils.isEmpty(authToken)) {
      final Bundle result = new Bundle();
      result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
      result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
      result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
      return result;
    }

    // If we got this far it means we couldn't get an auth token with the available
    // credentials. We respond with a bundle containing the intent to start the login
    //  activity so the user can provide new credentials.
    final Intent intent = new Intent(mContext, LoginActivity.class);
    final Bundle responseBundle = new Bundle();
    responseBundle.putParcelable(AccountManager.KEY_INTENT, intent);
    return responseBundle;
  }

  @Override
  public Bundle editProperties(AccountAuthenticatorResponse response, String s) {
    // We don't support account editing.
    throw new UnsupportedOperationException();
  }

  @Override
  public String getAuthTokenLabel(String s) {
    // Getting a label for the auth token is not supported
    throw new UnsupportedOperationException();
  }

  @Override
  public Bundle updateCredentials(AccountAuthenticatorResponse r,
                                  Account account, String s, Bundle bundle) throws NetworkErrorException {
    // Updating user credentials is not supported
    throw new UnsupportedOperationException();
  }

  @Override
  public Bundle hasFeatures(AccountAuthenticatorResponse r,
                            Account account, String[] strings) throws NetworkErrorException {
    // Checking features for the account is not supported
    throw new UnsupportedOperationException();
  }

  // Performs the actual sign in operation on the Expenses API.
  public static String signInUser(String username, String password, String authTokenType) {

    String authToken = null;

    try {
      ExpensesApiGenerateAuthTokenRequest request =
          new ExpensesApiGenerateAuthTokenRequest(username, password);

      if(request.performRequest()){
        authToken = request.getAuthToken();
      }

    } catch (IllegalArgumentException e) {
      Log.e(TAG, "Cannot request authorization token.");
    }

    return authToken;
  }

  /*
   * Contract for the Expenses API authenticator.
   */
  public static class ExpenseAPIAuthenticatorContract {

    public static final String ACCOUNT_TYPE = "expenses.nicolacimmino.com";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "expenses.nicolacimmino.com.full";
  }
}
