/* ExpensesApiRequest is part of ExpensesTracker and represents a generic request
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*
 * Abstract class representing a generic request to the Expenses API.
 */
public abstract class ExpensesApiRequest {

  protected final static String TAG = "ExpensesApiRequest";

  private JSONObject mRequestData;

  private String mRequestMethod;

  private String mUrl;

  protected JSONObject jsonResponseObject = null;

  protected JSONArray jsonResponseArray = null;

  public boolean performRequest()
  {

    jsonResponseArray = null;
    jsonResponseObject = null;

    HttpURLConnection connection = null;

    try {

      // Get the request JSON document as U*TF-8 encoded bytes, suitable for HTTP.
      mRequestData = ((mRequestData != null) ? mRequestData : new JSONObject());
      byte[] postDataBytes = mRequestData.toString(0).getBytes("UTF-8");

      URL url = new URL(mUrl);
      connection = (HttpURLConnection) url.openConnection();
      connection.setDoOutput(mRequestMethod == "GET"?false:true);
      connection.setDoInput(true);
      connection.setInstanceFollowRedirects(true);
      connection.setRequestMethod(mRequestMethod);
      connection.setUseCaches(false);
      if(mRequestMethod != "GET"){
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("Content-Length", "" + Integer.toString(postDataBytes.length));

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.write(postDataBytes);
        wr.flush();
        wr.close();
      }

      if(connection.getResponseCode() == 200) {
        InputStreamReader in = new InputStreamReader((InputStream) connection.getContent());
        BufferedReader buff = new BufferedReader(in);
        String line = buff.readLine().toString();
        buff.close();
          Object json = new JSONTokener(line).nextValue();
          if(json.getClass() == JSONObject.class) {
            jsonResponseObject = (JSONObject) json;
          } else if (json.getClass() == JSONArray.class) {
            jsonResponseArray = (JSONArray) json;
          } // else members will be left to null indicating no valid response.
          return true;
      }

    } catch (MalformedURLException e) {
      Log.e(TAG, e.getMessage());
    } catch (IOException e) {
      Log.e(TAG, e.getMessage());
    } catch (JSONException e) {
      Log.e(TAG, e.getMessage());
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }

    return  false;
  }

  public void setRequestData(JSONObject mRequestData) {
    this.mRequestData = mRequestData;
  }

  public void setRequestMethod(String mRequestMethod) {
    this.mRequestMethod = mRequestMethod;
  }

  public void setUrl(String mUrl) {
    this.mUrl = mUrl;
  }
}
