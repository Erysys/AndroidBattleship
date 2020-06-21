package com.joelbalmes.battleship;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import static com.joelbalmes.battleship.BoardView.cellWidth;
import static com.joelbalmes.battleship.BoardView.xOffset;

public class BoardSetup extends BaseActivity {

  Spinner spnShip;
  Spinner spnDirections;
  Spinner spnRow, spnCol;
  TextView lblGameId;
  ImageView imgDefGrid;
  Button btnPlay;

  ArrayAdapter adapter, rowAdapter, colAdapter;
  String[] shipsArray;
  String[] directionsArray;
  static TreeMap<String, Integer> shipsHash = new TreeMap<>();
  static TreeMap<String, Integer> directionsHash = new TreeMap<>();
  int touchX, touchY;

  String row, col, ship, dir;
  int rowInt, colInt;

  ArrayList<String> shipsPlaced = new ArrayList<>();


  @SuppressLint("ClickableViewAccessibility")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_board_setup);

    spnShip = findViewById( R.id.spnShips );
    spnDirections = findViewById( R.id.spnDirections );
    spnRow = findViewById( R.id.spnRow );
    spnCol = findViewById( R.id.spnCol );
    lblGameId = findViewById( R.id.lblGameId );
    lblGameId.setText( "game ID: " + gameId );
    imgDefGrid = findViewById( R.id.imgDefGrid );
    btnPlay = findViewById( R.id.btnPlay );

    btnPlay.setEnabled( false );
    btnPlay.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#888888")));

    rowAdapter = new ArrayAdapter<>( getApplicationContext(), R.layout.activity_spinner, rows );
    spnRow.setAdapter( rowAdapter );

    colAdapter = new ArrayAdapter<>( getApplicationContext(), R.layout.activity_spinner, cols );
    spnCol.setAdapter( colAdapter );

    getAvailableShips();
    getDirections();

    defendingBoard = new GameCell[11][11];
    attackingBoard = new GameCell[11][11];

    buildGrid();

    imgDefGrid.setOnTouchListener( new View.OnTouchListener(){
      @Override
      public boolean onTouch(View v, MotionEvent event ) {

        if( event.getAction() == MotionEvent.ACTION_UP ) {
          touchX = (int) ((event.getX() / cellWidth) - xOffset);
          touchY = (int) (event.getY() / cellWidth);

          for( int row = 0; row < 11; row++) {
            for( int col = 0; col < 11; col++ ) {
              defendingBoard[col][row].setWaiting( false );
            }
          }
          if (touchY >= 1 && touchX >= 1 && touchY <= 10 && touchX <= 10) {
            spnRow.setSelection( touchY - 1);
            spnCol.setSelection( touchX - 1);
            defendingBoard[touchX - 1][touchY - 1].setWaiting( true );
            imgDefGrid.invalidate();
          }
        }
        return true;
      }
    });
  }

  public static void buildGrid() {
    for( int row = 0; row < 11; row++) {
      for( int col = 0; col < 11; col++ ) {
        attackingBoard[col][row] = new GameCell();
        defendingBoard[col][row] = new GameCell();
      }
    }
  }

  public void addShipOnClick( View v ) {
    row = spnRow.getSelectedItem().toString().toLowerCase();
    switch (row) {
      case "a":
        rowInt = 0;
        break;
      case "b":
        rowInt = 1;
        break;
      case "c":
        rowInt = 2;
        break;
      case "d":
        rowInt = 3;
        break;
      case "e":
        rowInt = 4;
        break;
      case "f":
        rowInt = 5;
        break;
      case "g":
        rowInt = 6;
        break;
      case "h":
        rowInt = 7;
        break;
      case "i":
        rowInt = 8;
        break;
      case "j":
        rowInt = 9;
        break;
      default:
        break;
    }
    colInt = ( Integer.parseInt(spnCol.getSelectedItem().toString()) );
    col = String.valueOf( colInt + 1 );
    ship = spnShip.getSelectedItem().toString();
    final String dirStr = spnDirections.getSelectedItem().toString();
    dir = directionsHash.get(dirStr).toString();

    if ( ! shipsPlaced.contains( ship ) ) {

      String url = APIurl + "api/v1/game/" + gameId + "/add_ship/" + ship + "/" + row + "/" + col + "/" + dir + ".json";

      JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
          if ( response.has("status") ) {
            shipsPlaced.add( ship );

            int shipSize = shipsHash.get(ship);
            for( int i = 0; i < shipSize; i++ ) {
              defendingBoard[colInt][rowInt].setHasShip(true);
              switch(dirStr) {
                case "north":
                  rowInt -= 1;
                  break;
                case "south":
                  rowInt += 1;
                  break;
                case "east":
                  colInt += 1;
                  break;
                case "west":
                  colInt -= 1;
                  break;
                default:
                  break;
              }
            }
            imgDefGrid.invalidate();

          } else if ( response.has("error") ) {
            toastIt( "Illegal ship placement (out of bounds)" );
          }

          if( shipsPlaced.size() == 5) {
            btnPlay.setEnabled( true );
            btnPlay.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2A3F94")));
          }

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
    } else {
      toastIt( "You've already placed a " + ship );
    }


  }

  public void playButtonOnClick( View v ) {
    Intent intent = new Intent( getApplicationContext(), Gameplay.class);
    startActivity( intent );
  }

  private void getAvailableShips() {
    String url = APIurl + "api/v1/available_ships.json";

    JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {

        Iterator iter = response.keys();
        while( iter.hasNext() ) {
          String key = (String) iter.next();
          try {
            Integer value = (Integer) response.get( key );
            shipsHash.put( key, value );
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }

        int size = shipsHash.keySet().size();
        shipsArray = new String[ size ];
        shipsArray = shipsHash.keySet().toArray( new String[]{});

        adapter = new ArrayAdapter<String>( getApplicationContext(), R.layout.activity_spinner, shipsArray );

        spnShip.setAdapter( adapter );
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

  private void getDirections() {
    String url = APIurl + "api/v1/available_directions.json";

    JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {

        Iterator iter = response.keys();
        while( iter.hasNext() ) {
          String key = (String) iter.next();
          try {
            Integer value = (Integer) response.get( key );
            directionsHash.put( key, value );
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }

        int size = directionsHash.keySet().size();
        directionsArray = new String[ size ];
        directionsArray = directionsHash.keySet().toArray( new String[]{});

        adapter = new ArrayAdapter<String>( getApplicationContext(), R.layout.activity_spinner, directionsArray );

        spnDirections.setAdapter( adapter );
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
