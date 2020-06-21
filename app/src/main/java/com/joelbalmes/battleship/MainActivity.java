package com.joelbalmes.battleship;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

  Button btnLogin;
  EditText edtUsername, edtPassword;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    btnLogin = findViewById( R.id.btnLogin );
    edtUsername = findViewById( R.id.edtUsername );
    edtPassword = findViewById( R.id.edtPassword );

    if (loggedIn) {
      Intent intent = new Intent( this, GameLobby.class);
      startActivity( intent );
    }
  }

  public void LoginOnClick( View v ) {
    username = edtUsername.getText().toString();
    password = edtPassword.getText().toString();

    String url = APIurl + "api/v1/login.json";
    StringRequest request = new StringRequest(
        url, new Response.Listener<String>() {
      @Override
      public void onResponse(String response) {
        userPrefs = gson.fromJson( response, UserPreferences.class);
        loggedIn = true;

        Intent intent = new Intent( getApplicationContext(), GameLobby.class);
        startActivity( intent );
      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        toastIt( "Internet Failure" + error.toString() );
      }
    }) {
      @Override
      public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        String credentials = username + ":" + password;
        String auth = "Basic " + Base64.encodeToString( credentials.getBytes(), Base64.NO_WRAP);
        headers.put( "Authorization", auth );
        return headers;
      }
    };
    requestQueue.add( request );
  }
}
