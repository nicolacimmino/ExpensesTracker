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

  // Tag used in logging.
  protected final static String TAG = "ExpensesApiRequest";

  // Data to be sent along the request.
  private JSONObject mRequestData;

  // The HTTP method to use for the request.
  private String mRequestMethod;

  // The ReSTful resource URL
  private String mUrl;

  // THe response object and the response array.
  // Note: some resources return single objects others return an array.
  // it would be wise to uniform the interface here and always return
  // an array even if it contains only one object.
  // TODO: change to always return only array.
  protected JSONObject jsonResponseObject = null;
  protected JSONArray jsonResponseArray = null;

  // Perfroms the actual ReSTful request syncronously, returns true on success.
  // TODO: when return type is changed to be always JSONArray this can be changed
  //  to return directly the array or null on fail.
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
      connection.setDoOutput(mRequestMethod == "GET"?false:true); // No body data for GET
      connection.setDoInput(true);
      connection.setInstanceFollowRedirects(true);
      connection.setRequestMethod(mRequestMethod);
      connection.setUseCaches(false);

      // For all methods except GET we need to include data in the body.
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
