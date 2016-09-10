package net.ledii.pokemon;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StatusText extends TextView {
    private Context context;
    private boolean isReady;
    private Paint paint;

    public StatusText(Context context) {
        super(context);
        this.context = context;
        isReady = true;
        setClickable(false);

        paint = new Paint();
        paint.setStrokeWidth(3);

        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isReady = true;
                setClickable(false);
                postInvalidate();
            }
        });

        RelativeLayout battleLayout = (RelativeLayout)((Activity) context).findViewById(R.id.layoutBattle);
        battleLayout.addView(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(60);

        String[] textData = getText().toString().split("\n");
        for (int i = 0; i < textData.length; i++) {
            if (textData[i].contains("fell!")) { paint.setColor(Color.parseColor("#FF0000")); }
            else if (textData[i].contains("rose")) { paint.setColor(Color.parseColor("#00FF00")); }
            else { paint.setColor(Color.parseColor("#FFFFFF")); }
            canvas.drawText(textData[i], 50, 20 + ((paint.getTextSize() + 20) * (i + 1)), paint);
        }

        if (!isReady) {
            paint.setColor(Color.YELLOW);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(0, 0, getWidth() - 1, getHeight() - 1, paint);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(50);
            canvas.drawText("(Click here to continue...)", getWidth() / 2, getHeight() - paint.getTextSize(), paint);
        }
    }

    public void setStatusText(String text, boolean append) {
        String newStr = text;
        if (append) { newStr = getText() + text; }
        setText(newStr);
        isReady = false;
        setClickable(true);
        postInvalidate();
    }

    public boolean isClicked() {
        return isReady;
    }

    public void quickBlock() {
        isReady = false;
    }

    public void quickUnlock() {
        isReady = true;
    }
}