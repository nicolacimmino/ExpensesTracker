/* ExpensesApiGenerateAuthTokenRequest is part of ExpensesTracker and represents a specialized request
 *   to the Expenses API.
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
package com.nicolacimmino.expensestracker.tracker.expenses_api;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * Specialization of ExpensesApiRequest to generate an authentication token.
 */
public class ExpensesApiGenerateAuthTokenRequest extends ExpensesApiRequest {

  public ExpensesApiGenerateAuthTokenRequest(String username, String password) throws IllegalArgumentException {

    // API call to generate authentication token is:
    // POST /users/:username/auth_token
    try {
      setRequestMethod("POST");
      setUrl(ExpensesAPIContract.URL + "/users/" + username + "/auth_token");
      setRequestData(new JSONObject("{password:" + password + "}"));
    } catch (JSONException e) {
      throw new IllegalArgumentException();
    }

  }

  // Gets the authentication token returned by the API.
  public String getAuthToken()
  {
    try {
      if (jsonResponseObject != null) {
        return jsonResponseObject.getString("auth_token");
      }
    } catch (JSONException e) {
      Log.e(TAG, "Invalid response: " + e.getMessage());
    }
    return null;
  }
}
