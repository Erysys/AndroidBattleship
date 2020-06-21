package com.joelbalmes.battleship;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Results extends BaseActivity {

  TextView lblVictor, lblTurns, lblShips;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_results);

    lblVictor = findViewById( R.id.lblVictor );
    lblTurns = findViewById( R.id.lblTurns );
    lblShips = findViewById( R.id.lblShips );

    lblVictor.setText( resultsVictor );
    lblTurns.setText( String.valueOf(resultsTurns) );
    lblShips.setText( String.valueOf(resultsShips) );
  }

  @Override
  public void onBackPressed()
  {
    super.onBackPressed();
    startActivity(new Intent(Results.this, GameLobby.class));
    finish();
  }

  public void homeButtonOnClick( View v ) {
    Intent intent = new Intent( getApplicationContext(), GameLobby.class);
    startActivity( intent );
  }
}
