/* ExpenseDataLoginActivity is part of ExpensesTracker and provides the login interface.
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

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.nicolacimmino.expensestracker.tracker.R;
import com.nicolacimmino.expensestracker.tracker.data_model.ExpenseDataContract;
import com.nicolacimmino.expensestracker.tracker.data_sync.ExpenseDataAuthenticator;
import com.nicolacimmino.expensestracker.tracker.data_sync.ExpenseDataAuthenticatorContract;

public class ExpenseDataLoginActivity extends AccountAuthenticatorActivity {

  // Instance of the user login task.
  private UserLoginTask mAuthTask = null;

  // UI references.
  private EditText mUsernameView;
  private EditText mPasswordView;
  private View mProgressView;
  private View mLoginFormView;
  private Button mSignInButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_expese_data_login);

    // Set up the login form.
    mUsernameView = (EditText) findViewById(R.id.username);
    mPasswordView = (EditText) findViewById(R.id.password);
    mSignInButton = (Button) findViewById(R.id.sign_in_button);
    mSignInButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        attemptLogin();
      }
    });

    mLoginFormView = findViewById(R.id.login_form);
    mProgressView = findViewById(R.id.login_progress);
  }



  /*
   * Attempts the login.
   */
  public void attemptLogin() {

    // A previous login attempt is ongoing do nothing.
    if (mAuthTask != null) {
      return;
    }

    // Reset errors.
    mUsernameView.setError(null);
    mPasswordView.setError(null);

    // Store values at the time of the login attempt.
    String email = mUsernameView.getText().toString();
    String password = mPasswordView.getText().toString();

    boolean cancel = false;
    View focusView = null;

    // Username is compulsory.
    if (TextUtils.isEmpty(email)) {
      mUsernameView.setError(getString(R.string.error_field_required));
      focusView = mUsernameView;
      cancel = true;
    }

    if (cancel) {
      // There was an error; don't attempt login and focus the first form field with an error.
      focusView.requestFocus();
    } else {
      // Show a progress spinner, and start authentication in a background task.
      showProgress(true);
      mAuthTask = new UserLoginTask(email, password);
      mAuthTask.execute((Void) null);
    }
  }

  /*
   * Show the progress UI and hide the login form.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  public void showProgress(final boolean show) {
    // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
    // for very easy animations. If available, use these APIs to fade-in
    // the progress spinner.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
      int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
      mLoginFormView.animate().setDuration(shortAnimTime).alpha(
          show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
      });

      mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
      mProgressView.animate().setDuration(shortAnimTime).alpha(
          show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
      });
    } else {
      // The ViewPropertyAnimator APIs are not available, so simply show
      // and hide the relevant UI components.
      mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
  }

  /*
   * Represents an asynchronous login/registration task used to authenticate the user.
   */
  public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

    private final String mUsername;
    private final String mPassword;

    UserLoginTask(String username, String password) {
      mUsername = username;
      mPassword = password;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
      String authToken = ExpenseDataAuthenticator.SignInUser(mUsername, mPassword, "");
      if (authToken == null || authToken.isEmpty()) {
        return false;
      }

      Account newAccount = new Account(mUsername, ExpenseDataAuthenticatorContract.ACCOUNT_TYPE);
      AccountManager accountManager = (AccountManager) getApplicationContext().getSystemService(ACCOUNT_SERVICE);

      if (accountManager.addAccountExplicitly(newAccount, mPassword, null)) {
        accountManager.setAuthToken(newAccount, ExpenseDataAuthenticatorContract.AUTHTOKEN_TYPE_FULL_ACCESS, authToken);

        // Turn on automatic syncing for the new account.
        ContentResolver.setIsSyncable(newAccount, ExpenseDataContract.CONTENT_AUTHORITY, 1);
        ContentResolver.setSyncAutomatically(newAccount, ExpenseDataContract.CONTENT_AUTHORITY, true);

        return true;
      } else {
        return false;
      }
    }

    @Override
    protected void onPostExecute(final Boolean success) {
      mAuthTask = null;
      showProgress(false);

      if (success) {
        finish();
      } else {
        mPasswordView.setError(getString(R.string.error_incorrect_password));
        mPasswordView.requestFocus();
      }
    }

    @Override
    protected void onCancelled() {
      mAuthTask = null;
      showProgress(false);
    }
  }
}



