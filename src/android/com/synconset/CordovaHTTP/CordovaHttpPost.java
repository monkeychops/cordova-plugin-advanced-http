/**
 * A HTTP plugin for Cordova / Phonegap
 */
package com.synconset;

import java.util.Map;

import org.apache.cordova.CallbackContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;
 
public class CordovaHttpPost extends CordovaHttp implements Runnable {
    public CordovaHttpPost(String urlString, Map<?, ?> params, Map<String, String> headers, CallbackContext callbackContext) {
        super(urlString, params, headers, callbackContext);
    }
    
    @Override
    public void run() {
        try {
            Log.d(TAG, this.getParams().toString());
            HttpRequest request = HttpRequest.post(this.getUrlString());
            if (this.acceptAllCerts()) {
                request.trustAllCerts();
                request.trustAllHosts();
            }
            if (this.sslPinning()) {
                Log.d(TAG, "ssl pinning");
                request.pinToCerts();
            }
            request.acceptCharset(CHARSET);
            request.headers(this.getHeaders());
            
            request.form(this.getParams());
            
            int code = request.code();
            String body = request.body(CHARSET);
            
            Log.d(TAG, Integer.toString(code));
            Log.d(TAG, body);
            
            JSONObject response = new JSONObject();
            response.put("status", code);
            if (code >= 200 && code < 300) {
                response.put("data", body);
                this.getCallbackContext().success(response);
            } else {
                response.put("error", body);
                this.getCallbackContext().error(response);
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            this.respondWithError("There was an error generating the response");
        }  catch (HttpRequestException e) {
            Log.d(TAG, e.getMessage());
            this.respondWithError("There was an error with the request");
        }
    }
}
