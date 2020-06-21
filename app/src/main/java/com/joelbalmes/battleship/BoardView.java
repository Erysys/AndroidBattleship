package com.joelbalmes.battleship;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatImageView;

import static com.joelbalmes.battleship.BaseActivity.attackingBoard;
import static com.joelbalmes.battleship.BaseActivity.defendingBoard;
import static com.joelbalmes.battleship.BaseActivity.drawDefending;

public class BoardView extends AppCompatImageView {

  static float cellWidth;
  static float xOffset;
  Paint paint;

  public BoardView(Context context, AttributeSet attrs ) {
    super( context, attrs );
    paint = new Paint();
    paint.setColor(Color.parseColor("#7497e8"));
    paint.setStrokeWidth( 5 );
    paint.setStyle( Paint.Style.FILL_AND_STROKE );

    Typeface typeface = Typeface.create( Typeface.SANS_SERIF, Typeface.NORMAL );
    paint.setTypeface( typeface );
    paint.setTextAlign( Paint.Align.CENTER );
  }

  @Override
  protected void onDraw( Canvas canvas ) {
    float width, height;
    float canvasWidth = getWidth();
    float canvasHeight = getHeight();
    float baseX = 0;
    float baseY = 0;

    Log.i("GRID", "Width: " + canvasWidth + ", Height: " + canvasHeight );
    // Makes sure grid is square regardless of phone dimensions

    if (canvasWidth > canvasHeight) {
      width = canvasHeight;
      height = canvasHeight;

    } else {
      width = canvasWidth;
      height = canvasWidth;

    }

    Log.i("GRID", "Width (after): " + width + ", Height: " + height );

    float step = height / 11;
    cellWidth = step;
    baseX += ( ( canvasWidth - width ) / 2 ) - ( step / 2 );
    width += baseX;
    xOffset = baseX / 110;

    paint.setTextSize( step * 0.7f );

    for( int row = 0; row < 11; row++) {
      for( int col = 0; col < 11; col++ ) {
        attackingBoard[col][row].setTopLeft( new Point( (col + 2) * (int) (cellWidth), (row + 1) * (int) cellWidth ));
        defendingBoard[col][row].setTopLeft( new Point( (col + 2) * (int) (cellWidth), (row + 1) * (int) cellWidth ));

        attackingBoard[col][row].setBottomRight( new Point( (col + 3) * (int) cellWidth, (row + 2) * (int) cellWidth ));
        defendingBoard[col][row].setBottomRight( new Point( (col + 3) * (int) cellWidth, (row + 2) * (int) cellWidth ));
      }
    }

    super.onDraw( canvas );

    // Grid
    float gridX = baseX + step;
    float gridY = baseY + step;

    paint.setColor( Color.parseColor("#7497e8"));

    for (int i=0; i <= 10; i++) {
      canvas.drawLine(gridX, gridY + (step * i), width, gridY + (step * i), paint );
      canvas.drawLine(gridX + (step * i), gridY, gridX + (step * i), height, paint );

    }

    Rect textbounds = new Rect();
    paint.getTextBounds( "A", 0, 1, textbounds );
    paint.setColor( Color.parseColor("#8a99bd") );

    float textHeight = textbounds.height();
    float textWidth = textbounds.width();

    float textX = baseX + ( step / 2 );
    float textY = baseY + (step * 2) - ( textHeight / 2 );


    canvas.drawText( "A", textX, textY, paint);
    textY += step;
    canvas.drawText( "B", textX, textY, paint);
    textY += step;
    canvas.drawText( "C", textX, textY, paint);
    textY += step;
    canvas.drawText( "D", textX, textY, paint);
    textY += step;
    canvas.drawText( "E", textX, textY, paint);
    textY += step;
    canvas.drawText( "F", textX, textY, paint);
    textY += step;
    canvas.drawText( "G", textX, textY, paint);
    textY += step;
    canvas.drawText( "H", textX, textY, paint);
    textY += step;
    canvas.drawText( "I", textX, textY, paint);
    textY += step;
    canvas.drawText( "J", textX, textY, paint);

    textX += step;
    textY = baseY + (step) - ( textHeight / 2 );

    canvas.drawText( "0", textX, textY, paint);
    textX += step;
    canvas.drawText( "1", textX, textY, paint);
    textX += step;
    canvas.drawText( "2", textX, textY, paint);
    textX += step;
    canvas.drawText( "3", textX, textY, paint);
    textX += step;
    canvas.drawText( "4", textX, textY, paint);
    textX += step;
    canvas.drawText( "5", textX, textY, paint);
    textX += step;
    canvas.drawText( "6", textX, textY, paint);
    textX += step;
    canvas.drawText( "7", textX, textY, paint);
    textX += step;
    canvas.drawText( "8", textX, textY, paint);
    textX += step;
    canvas.drawText( "9", textX, textY, paint);

    float w = paint.measureText( "W", 0, 0);
    float center = ( cellWidth / 2 ) - ( w / 2);

    for( int row = 0; row < 11; row++) {
      for( int col = 0; col < 11; col++ ) {

        if( drawDefending ){
          if ( defendingBoard[col][row].getHasShip() ) {
            paint.setColor( Color.parseColor("#43e885"));
            drawCell("S", col, row, center, canvas);

          } else if( defendingBoard[col][row].getHit() ) {
            //Draw Red X
            paint.setColor( Color.parseColor("#F4511E"));
            drawCell("X", col, row, center, canvas);
          } else if( defendingBoard[col][row].getMiss() ) {
            //Draw White O
            paint.setColor( Color.parseColor("#c0c0c0"));
            drawCell("O", col, row, center, canvas);
          } else if( defendingBoard[col][row].getWaiting() ) {
            //Draw '...'
            paint.setColor( Color.parseColor("#42f59b"));
            drawCell("⟪  ⟫", col, row, center, canvas);
          }
        } else {
          if ( attackingBoard[col][row].getHasShip() ) {
            paint.setColor( Color.parseColor("#43e885"));
            drawCell("S", col, row, center, canvas);

          } else if( attackingBoard[col][row].getHit() ) {
            //Draw Red X
            paint.setColor( Color.parseColor("#F4511E"));
            drawCell("X", col, row, center, canvas);
          } else if( attackingBoard[col][row].getMiss() ) {
            //Draw White O
            paint.setColor( Color.parseColor("#c0c0c0"));
            drawCell("O", col, row, center, canvas);
          } else if( attackingBoard[col][row].getWaiting() ) {
            //Draw '...'
            paint.setColor( Color.parseColor("#42f59b"));
            drawCell("⟪  ⟫", col, row, center, canvas);
          }
        }


      }
    }

  }

  void drawCell( String contents, int x, int y, float center, Canvas canvas) {
    if( drawDefending ) {
      canvas.drawText( contents, defendingBoard[x][y].getTopLeft().x + (center / 3), defendingBoard[x][y].getTopLeft().y + (center * 1.5f), paint );
    } else {
      canvas.drawText( contents, attackingBoard[x][y].getTopLeft().x + (center / 3), attackingBoard[x][y].getTopLeft().y + (center * 1.5f), paint );
    }

  }

}
