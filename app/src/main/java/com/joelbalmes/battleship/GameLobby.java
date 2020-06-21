package com.joelbalmes.battleship;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GameLobby extends BaseActivity {

  ImageView imgAvatar;
  TextView txtUsername;
  Button btnAllUsers, btnLogOut, btnPlayGame;
  ListView lstAllUsers;
  RelativeLayout loadingPanel;

  ArrayAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_game_lobby);


    imgAvatar = findViewById( R.id.imgAvatar );
    txtUsername = findViewById( R.id.txtUsername );
    btnAllUsers = findViewById( R.id.btnAllUsers );
    btnLogOut = findViewById( R.id.btnLogOut );
    btnPlayGame = findViewById( R.id.btnPlayGame );
    lstAllUsers = findViewById( R.id.lstAllUsers );
    loadingPanel = findViewById( R.id.loadingPanel );

    loadingPanel.setVisibility(View.GONE);

    txtUsername.setText( userPrefs.getAvatarName() );
    Picasso.get().load( APIurl + userPrefs.getAvatarImage() ).into( imgAvatar );
  }

  public void allUsersOnClick( View v ) {
    String url = APIurl + "api/v1/all_users.json";
    StringRequest request = new StringRequest(
        url, new Response.Listener<String>() {
      @Override
      public void onResponse(String response) {
        users = gson.fromJson( response, UserPreferences[].class);
        loggedIn = true;
        lstAllUsers.setBackgroundColor(Color.parseColor("#252A3F"));

        for ( int i = 0; i < users.length; ++i) {
          if (users[i].getAvatarName() != null && users[i].getAvatarName().length() < 40) {
            userNames.add(users[i].getAvatarName());
          }
        }

        adapter = new ArrayAdapter<>( getApplicationContext(), R.layout.activity_list_item, userNames );

        lstAllUsers.setAdapter( adapter );

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

  public void logOutOnClick( View v ) {
    logOut();
  }

  public void prefsOnClick( View v ) {
    Intent intent = new Intent( getApplicationContext(), Preferences.class );
    startActivity( intent );
  }

  public void playGameOnClick( View v ) {
    String url = APIurl + "api/v1/challenge_computer.json";
    loadingPanel.setVisibility(View.VISIBLE);

      JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
          try {
            gameId = response.getInt( "game_id" );
          } catch (JSONException e) {
            e.printStackTrace();
          }
          loadingPanel.setVisibility(View.GONE);
          Intent intent = new Intent( getApplicationContext(), BoardSetup.class);
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
      request.setRetryPolicy(new DefaultRetryPolicy(
          20000,
          DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
          DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
      ));
      requestQueue.add( request );
    }

}
