/* ExpenseDataAuthenticatorContract is part of ExpensesTracker.
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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class ExpenseApiAuthenticatorService extends Service {
  // Instance field that stores the authenticator object
  private ExpenseApiAuthenticator mAuthenticator;

  @Override
  public void onCreate() {
    // Create a new authenticator object
    mAuthenticator = new ExpenseApiAuthenticator(this);
  }

  /*
   * When the system binds to this Service to make the RPC call
   * return the authenticator's IBinder.
   */
  @Override
  public IBinder onBind(Intent intent) {
    return mAuthenticator.getIBinder();
  }
}
