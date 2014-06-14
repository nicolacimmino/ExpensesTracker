package com.nicolacimmino.expensestracker.tracker;

/**
 * Created by nicola on 13/06/2014.
 */
public class SharedPreferencesContract {
  // Last used payment source
  public final static String SAVED_STATE_LAST_SOURCE = "last_source";

  // Last used expense type (destination)
  public final static String SAVED_STATE_LAST_DESTINATION = "last_destination";

  // Last Google Cloud Messagin registration id
  public static final String PROPERTY_REG_ID = "registration_id";

  // The application version last time it was run.
  public static final String PROPERTY_APP_VERSION = "appVersion";

}
