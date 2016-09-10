package net.ledii.pokemon;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.RelativeLayout;

public class MoveButton extends View {
    private Context context;
    private Paint paint;
    private Pokemon.LearnedMove move;
    private final int PADDING = 20;

    public MoveButton(Context context) {
        super(context);
        this.context = context;

        paint = new Paint();

        RelativeLayout battleLayout = (RelativeLayout)((Activity) context).findViewById(R.id.layoutBattle);
        battleLayout.addView(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Get text
        String cStr = "Empty";
        String txtStr = "Empty";
        if (move != null) {
            cStr = move.data.type;
            txtStr = move.data.name;
        }

        //Draw frame
        paint.setColor(Global.getColor(cStr));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        //Draw move name
        paint.setTextSize(80);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(txtStr, PADDING, paint.getTextSize(), paint);

        if (move != null) {
            //Draw type
            paint.setTextSize(60);
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(move.data.type, PADDING, getHeight() - PADDING, paint);

            //Draw pp
            paint.setTextAlign(Paint.Align.RIGHT);
            String ppStr = move.ppCurrent + " / " + move.data.pp;
            if (move.disabledRounds > 0) {
                ppStr = "Disabled: " + move.disabledRounds;
            }
            canvas.drawText(ppStr, getWidth() - PADDING, getHeight() - PADDING, paint);
        }
    }

    public void setMove(Pokemon.LearnedMove move) {
        this.move = move;
        invalidate();
    }
}