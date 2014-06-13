package com.nicolacimmino.expensestracker.tracker.data_sync;

/**
 * Created by nicola on 13/06/2014.
 */
public class ExpensesAPIContract {

  // The base URL of the API
  // NOTE: this is purely for testing, DO NOT deploy a live system on HTTP,
  //  user passwords will be sent in clear over the wire.
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
