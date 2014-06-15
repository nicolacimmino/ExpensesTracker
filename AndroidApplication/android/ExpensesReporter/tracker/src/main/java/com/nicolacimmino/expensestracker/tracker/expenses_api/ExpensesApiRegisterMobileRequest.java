/* ExpensesApiRegisterMobileRequest is part of ExpensesTracker and represents a specialized request
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

import com.nicolacimmino.expensestracker.tracker.data_sync.ExpensesAccountResolver;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * Specialization of ExpensesApiRequest to register a  mobile with the API so that
 * norifications can be received.
 */
public class ExpensesApiRegisterMobileRequest extends ExpensesApiRequest {

  public ExpensesApiRegisterMobileRequest(String registration_id) throws IllegalArgumentException {

    // API call to register a mobile is:
    // POST /mobiles/:username/auth_token
    try {
      setRequestMethod("POST");
      setUrl(ExpensesApiContract.URL + "/mobiles/"
          + ExpensesAccountResolver.getInstance().getUsername()
          + "?auth_token=" + ExpensesAccountResolver.getInstance().getAuthorizationToken());
      setRequestData(new JSONObject("{gcmRegistrationId:" + registration_id + "}"));
    } catch (JSONException e) {
      throw new IllegalArgumentException();
    }

  }
}

