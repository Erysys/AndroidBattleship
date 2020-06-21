package com.joelbalmes.battleship;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Preferences extends BaseActivity {

  TextView lblAvatar, lblLevel, lblExp, lblWin, lblLose, lblDraw, lblCoins;
  TextView lblFirstName, lblLastName, lblEmail, lblAvailable, lblOnline, lblGaming;
  ImageView imgAvatar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_preferences);

    lblAvatar = findViewById( R.id.lblAvatar );
    lblLevel = findViewById( R.id.lblLevel );
    lblExp = findViewById( R.id.lblExp );
    lblWin = findViewById( R.id.lblWin );
    lblLose = findViewById( R.id.lblLose );
    lblDraw = findViewById( R.id.lblDraw );
    lblCoins = findViewById( R.id.lblCoins );

    lblFirstName = findViewById( R.id.lblFirstName );
    lblLastName = findViewById( R.id.lblLastName );
    lblEmail = findViewById( R.id.lblEmail );
    lblAvailable = findViewById( R.id.lblAvailable );
    lblOnline = findViewById( R.id.lblOnline );
    lblGaming = findViewById( R.id.lblGaming );

    imgAvatar = findViewById( R.id.imgAvatar );

    lblAvatar.setText( userPrefs.getAvatarName() );
    lblLevel.setText( userPrefs.getLevel().toString() );
    lblExp.setText( userPrefs.getExperiencePoints().toString() );
    lblWin.setText( userPrefs.getBattlesWon().toString() );
    lblLose.setText( userPrefs.getBattlesLost().toString() );
    lblDraw.setText( userPrefs.getBattlesTied().toString() );
    lblCoins.setText( userPrefs.getCoins().toString() );

    lblFirstName.setText( userPrefs.getFirstName() );
    lblLastName.setText( userPrefs.getLastName() );
    lblEmail.setText( userPrefs.getEmail() );

    if( userPrefs.getAvailable() ) {
      lblAvailable.setText( "Yes" );
      lblAvailable.setTextColor(Color.parseColor("#61FF3E"));
    } else {
      lblAvailable.setText( "No" );
      lblAvailable.setTextColor(Color.parseColor("#FF3E3E"));
    }

    if( userPrefs.getOnline() ) {
      lblOnline.setText( "Yes" );
      lblOnline.setTextColor(Color.parseColor("#61FF3E"));
    } else {
      lblOnline.setText( "No" );
      lblOnline.setTextColor(Color.parseColor("#FF3E3E"));
    }

    if( userPrefs.getGaming() ) {
      lblGaming.setText( "Yes" );
      lblGaming.setTextColor(Color.parseColor("#61FF3E"));
    } else {
      lblGaming.setText( "No" );
      lblGaming.setTextColor(Color.parseColor("#FF3E3E"));
    }

    Picasso.get().load( APIurl + userPrefs.getAvatarImage() ).into( imgAvatar );
  }

  public void backOnClick( View v ) {
    Intent intent = new Intent( getApplicationContext(), GameLobby.class);
    startActivity( intent );
  }

}
