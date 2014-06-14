/* ExpensesAPIContract is part of ExpensesTracker and is responsible to provide constants
 *   that represent the Expenses API.
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

/*
 * Contract for the Expenses API.
 */
public class ExpensesAPIContract {

  // The base URL of the API
  // NOTE: this is purely for testing, DO NOT deploy a live system on HTTP,
  //  user passwords will be sent in clear over the wire!
  public static final String URL = "http://expensesapi.nicolacimmino.com";

  // The expense object JSON field names.
  public class Expense {
    public static final String NOTES = "notes";
    public static final String CURRENCY = "currency";
    public static final String AMOUNT = "amount";
    public static final String DESTINATION = "destination";
    public static final String SOURCE = "source";
    public static final String TIMESTAMP = "timestamp";
    public static final String REPORTER_GCM_REG_ID = "reporter_gcm_reg_id";
  }
}
