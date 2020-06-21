package com.joelbalmes.battleship;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends AppCompatActivity {

  static String username, password;
  static UserPreferences userPrefs;
  static String APIurl = "http://www.battlegameserver.com/";
  static boolean loggedIn = false;
  static UserPreferences[] users;
  static ArrayList<String> userNames = new ArrayList<>();
  static int gameId;
  static String[] rows = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" };
  static String[] cols = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
  public static GameCell[][] defendingBoard;
  public static GameCell[][] attackingBoard;
  public static boolean drawDefending;

  public static int resultsTurns = 0;
  public static int resultsShips = 5;
  public static String resultsVictor = "";

  public RequestQueue requestQueue;
  Gson gson;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_base);

    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.setDateFormat( "yyyy-MM-dd'T'HH:mm:ssX" );
    gson = gsonBuilder.create();

    Cache cache = new DiskBasedCache( getCacheDir(), 1024 * 1024 );
    Network network = new BasicNetwork( new HurlStack() );

    requestQueue = new RequestQueue( cache, network );
    requestQueue.start();

    drawDefending = true;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    //Inflate menu
    getMenuInflater().inflate( R.menu.menu, menu );
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    Intent intent;
    switch ( item.getItemId() ) {
      case R.id.menuHome:
        intent = new Intent( this, GameLobby.class );
        startActivity( intent );
        return true;
      case R.id.menuPrefs:
        intent = new Intent( this, Preferences.class );
        startActivity( intent );
        return true;
      case R.id.menuLogOut:
        logOut();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public void logOut() {
    String url = APIurl + "api/v1/logout.json";
    StringRequest request = new StringRequest(
        url, new Response.Listener<String>() {
      @Override
      public void onResponse(String response) {
        loggedIn = false;

        Intent intent = new Intent( getApplicationContext(), MainActivity.class);
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

  public void toastIt(String msg ){
    Toast.makeText( getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
  }

  public void resetGameStats() {
    resultsShips = 5;
    resultsTurns = 0;
    resultsVictor = "";
    BoardSetup.buildGrid();
    drawDefending = true;
    gameId = 0;
  }
}
