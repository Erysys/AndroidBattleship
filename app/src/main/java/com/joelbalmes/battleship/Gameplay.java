package com.joelbalmes.battleship;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.joelbalmes.battleship.BoardView.cellWidth;
import static com.joelbalmes.battleship.BoardView.xOffset;

public class Gameplay extends BaseActivity {

  Button btnToggle, btnAttack;
  TextView lblBoardLabel, lblShipsRemaining;
  ImageView imgGameGrid;
  Spinner spnRow, spnCol;
  ArrayAdapter rowAdapter, colAdapter;

  private boolean defendingShown = true;
  private boolean validAttack = false;
  int touchX, touchY;
  String row, col;
  int rowInt, colInt;
  String winner = "";
  int turn = 0;
  String resultMsg;
  int userShipsRemaining = 5;
  int compShipsRemaining = 5;

  @SuppressLint("ClickableViewAccessibility")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gameplay);

    btnToggle = findViewById( R.id.btnToggle );
    btnAttack = findViewById( R.id.btnAttack );
    lblBoardLabel = findViewById( R.id.lblBoardLabel );
    lblShipsRemaining = findViewById( R.id.lblShipsRemaining );
    imgGameGrid = findViewById( R.id.imgGameGrid );

    spnRow = findViewById( R.id.spnRow2 );
    spnCol = findViewById( R.id.spnCol2 );

    rowAdapter = new ArrayAdapter<>( getApplicationContext(), R.layout.activity_spinner, rows );
    spnRow.setAdapter( rowAdapter );

    colAdapter = new ArrayAdapter<>( getApplicationContext(), R.layout.activity_spinner, cols );
    spnCol.setAdapter( colAdapter );

    btnAttack.setEnabled( false );
    btnAttack.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#888888")));

    lblShipsRemaining.setText( String.valueOf(userShipsRemaining) );

    imgGameGrid.setOnTouchListener( new View.OnTouchListener(){

      @Override
      public boolean onTouch(View v, MotionEvent event ) {

        if( event.getAction() == MotionEvent.ACTION_UP ) {
          touchX = (int) ((event.getX() / cellWidth) - xOffset);
          touchY = (int) (event.getY() / cellWidth);

          if ( !defendingShown ){
            spnRow.setSelection( touchY - 1);
            spnCol.setSelection( touchX - 1);

            for( int row = 0; row < 11; row++) {
              for( int col = 0; col < 11; col++ ) {
                attackingBoard[col][row].setWaiting( false );
              }
            }

            attackingBoard[touchX - 1][touchY - 1].setWaiting( true );
            imgGameGrid.invalidate();
          }
        }
        return true;
      }
    });
  }

  public void toggleBoardOnClick( View v ) {
    toggleBoard();
  }

  private void toggleBoard() {
    if( !defendingShown ) {
      btnToggle.setText("Show Attacking Board");
      lblBoardLabel.setText("Defending Board (You)");
      lblShipsRemaining.setText( String.valueOf( userShipsRemaining ) );
      defendingShown = true;
      drawDefending = true;
      btnAttack.setEnabled( false );
      btnAttack.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#888888")));
      imgGameGrid.invalidate();
    } else {
      btnToggle.setText("Show Defending Board");
      lblBoardLabel.setText("Attacking Board (Opponent)");
      lblShipsRemaining.setText( String.valueOf( compShipsRemaining ) );
      defendingShown = false;
      drawDefending = false;
      btnAttack.setEnabled( true );
      btnAttack.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2A3F94")));
      imgGameGrid.invalidate();
    }
  }

  public void attackOnClick( View v ) {
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
    String url = APIurl + "api/v1/game/" + gameId + "/attack/" + row + "/" + col + ".json";

    if( attackingBoard[colInt][rowInt].getHit() || attackingBoard[colInt][rowInt].getMiss() ) {
      toastIt("You have already attacked that position, try again");
      validAttack = false;
    } else {
      validAttack = true;
    }

    if( validAttack ) {
      turn += 1;
      resultMsg = "";
      JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
          if ( response.has("game_id") ) {
            boolean hit = false;
            boolean compHit = false;
            String compRow = "";
            String compCol = "";
            String compShipSunk = "no";
            String userShipSunk = "no";


            String userHitMsg = "hit";
            String compHitMsg = "hit";

            Log.i( "SHIP", response.toString() );
            try {
              hit = response.getBoolean("hit");
              compHit = response.getBoolean("comp_hit");
              compRow = response.getString("comp_row");
              compCol = response.getString("comp_col");
              compShipSunk = response.getString("comp_ship_sunk");
              userShipSunk = response.getString("user_ship_sunk");
              winner = response.getString("winner");
            } catch (JSONException e) {
              e.printStackTrace();
            }
            int compRowInt = 0;
            int compColInt = Integer.parseInt(compCol);

            switch ( compRow ) {
              case "a":
                compRowInt = 0;
                break;
              case "b":
                compRowInt = 1;
                break;
              case "c":
                compRowInt = 2;
                break;
              case "d":
                compRowInt = 3;
                break;
              case "e":
                compRowInt = 4;
                break;
              case "f":
                compRowInt = 5;
                break;
              case "g":
                compRowInt = 6;
                break;
              case "h":
                compRowInt = 7;
                break;
              case "i":
                compRowInt = 8;
                break;
              case "j":
                compRowInt = 9;
                break;
              default:
                break;
            }

            if( hit ) {
              attackingBoard[colInt][rowInt].setHit( true );
              compHitMsg = "hit";
            } else {
              attackingBoard[colInt][rowInt].setMiss( true );
              compHitMsg = "missed";
            }

            resultMsg += "You attacked the enemy at (" + row.toUpperCase() + ", " + colInt+ ").\nYour attack " + compHitMsg + ".\n";

            if( !compShipSunk.equals("no") ) {
              resultMsg += "You sunk the enemy's " + compShipSunk + "!\n";
              compShipsRemaining -= 1;
              lblShipsRemaining.setText( String.valueOf( compShipsRemaining ) );
            }

            if( compHit ) {
              defendingBoard[compColInt][compRowInt].setHit( true );
              defendingBoard[compColInt][compRowInt].setHasShip( false );
              userHitMsg = "hit";
            } else {
              defendingBoard[compColInt][compRowInt].setMiss( true );
              userHitMsg = "missed";
            }


            resultMsg += "\nThe opponent attacked you at (" + compRow.toUpperCase() + ", " + compCol + ").\nThe attack " + userHitMsg + ".\n";

            if( !userShipSunk.equals("no") ) {
              resultMsg += "The enemy sunk your " + userShipSunk + ".\n";
              userShipsRemaining -= 1;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(Gameplay.this);
            builder.setMessage(resultMsg);
            builder.setTitle( "Results - Turn " + turn );
            builder.setCancelable( false );
            builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                imgGameGrid.invalidate();
                dialog.dismiss();
                if( !winner.equals("") ) {
                  endGame();
                }
              }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

          } else {
            toastIt("Unknown Error: Something went wrong");
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
    }
    validAttack = false;
  }

  private void endGame() {
    resultsTurns = turn;

    if( winner.equals("computer")) {
      resultsVictor = "Congratulations, You Win!";
      resultsShips = userShipsRemaining;
    } else {
      resultsVictor = "The Computer Wins";
      resultsShips = compShipsRemaining;
    }

    Intent intent = new Intent( getApplicationContext(), Results.class);
    startActivity( intent );
  }

}
