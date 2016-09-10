package net.ledii.pokemon;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

public class BattleDisplay extends View {
    Context context;
    private Paint paint;
    private final int WIDTH = 600;
    private final int HEIGHT = 30;
    private final int PADDING = 20;
    private boolean playerDisplay;
    private Pokemon pokemon;
    private float targetHp, currentHp;
    private Handler handler;
    private Runnable smoothTask;
    private final float UPS = 30;
    private boolean animating;

    public BattleDisplay(Context context, boolean playerDisplay) {
        super(context);
        this.context = context;
        this.playerDisplay = playerDisplay;

        paint = new Paint();
        paint.setTextSize(60);
        paint.setStrokeWidth(HEIGHT);

        RelativeLayout battleLayout = (RelativeLayout)((Activity) context).findViewById(R.id.layoutBattle);
        battleLayout.addView(this);

        smoothTask = new Runnable() {
            @Override
            public void run() {
                //Calculate move
                float diffHp = targetHp - currentHp;
                final float TH_END = 0.01f;
                final float SPEED = 0.012f;

                //Check difference
                if (diffHp < TH_END && diffHp > -TH_END) {
                    currentHp = targetHp;
                    animating = false;
                } else {
                    float inc = SPEED;
                    if (diffHp > 0) {
                        currentHp += inc;
                    } else {
                        currentHp -= inc;
                    }
                }

                //Update ui
                postInvalidate();

                //Run again if not done
                if (animating) { handler.postDelayed(this, (long)(1000 / UPS)); }
            }
        };
        handler = new Handler();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float x1 = PADDING;
        float y = paint.getTextSize() + PADDING;
        float x2 = x1 + WIDTH;

        //Draw frame
        float fw = WIDTH + (PADDING * 2);
        int size = 3;
        if (playerDisplay) { size = 3; }
        float fh = (paint.getTextSize() * size) + HEIGHT + (PADDING * 2f);
        paint.setColor(Color.BLACK);
        paint.setAlpha(125);
        paint.setStyle(Paint.Style.FILL);
        paint.setFakeBoldText(false);
        canvas.drawRect(0, 0, fw, fh, paint);

        //Draw name
        paint.setAlpha(255);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(pokemon.getName(), x1, y, paint);

        //Draw gender
        String genderSymbol = "♂";
        paint.setFakeBoldText(true);
        paint.setColor(Global.getColor(pokemon.getGender()));
        if (pokemon.getGender().equals("Female")) { genderSymbol = "♀"; }
        else if (pokemon.getGender().equals("None")) { genderSymbol = ""; }
        float xx1 = x1 + paint.measureText(pokemon.getName() + " ");
        canvas.drawText(genderSymbol, xx1, y, paint);

        //Draw level
        paint.setFakeBoldText(false);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Lv. " + pokemon.getLevel(), x2, y, paint);
        y += (paint.getTextSize() * 0.75);

        //Draw frame
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        canvas.drawLine(x1, y, x2, y, paint);

        //Draw hp
        paint.setColor(Color.GREEN);
        float xx2 = x1 + (WIDTH * currentHp);
        canvas.drawLine(x1, y, xx2, y, paint);
        y += HEIGHT + paint.getTextSize();

        //Draw status
        String statusStr = pokemon.getStatus();
        if (statusStr != null) {
            String shortStatus = "";
            if (statusStr.equals("Burn")) { shortStatus = "BRN"; paint.setColor(Color.RED); }
            if (statusStr.equals("Freeze")) { shortStatus = "FRZ"; paint.setColor(Color.CYAN); }
            if (statusStr.equals("Paralysis")) { shortStatus = "PAR"; paint.setColor(Color.YELLOW); }
            if (statusStr.equals("Poison")) { shortStatus = "PSN"; paint.setColor(Color.MAGENTA); }
            if (statusStr.equals("Badly poisoned")) { shortStatus = "PSN"; paint.setColor(Color.MAGENTA); }
            if (statusStr.equals("Sleep")) { shortStatus = "SLP"; paint.setColor(Color.LTGRAY); }

            paint.setStyle(Paint.Style.FILL);
            paint.setTextAlign(Paint.Align.RIGHT);
            paint.setFakeBoldText(true);
            canvas.drawText(shortStatus, x2, y, paint);
        }

        if (playerDisplay) {
            //Draw hp text
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setFakeBoldText(false);
            String currentHpStr = (int)Math.ceil(pokemon.getStats().hp * currentHp) + "";
            if (!animating) { currentHpStr = pokemon.getCurrentStats().hp + ""; }
            canvas.drawText("HP: " + currentHpStr + " / " + pokemon.getStats().hp, x1, y, paint);
        }
    }

    public void sendIn(Pokemon pokemon) {
        this.pokemon = pokemon;
        targetHp = pokemon.getCurrentStats().hp / (float)pokemon.getStats().hp;
        currentHp = targetHp;
        animating = false;
        invalidate();
    }

    public int getW() {
        return WIDTH + (PADDING * 2);
    }

    public int getH() {
        return (int) (paint.getTextSize() * 3) + HEIGHT + (PADDING * 2);
    }

    public Pokemon getPokemon() {
        return pokemon;
    }

    public void smoothInvalidate() {
        targetHp = pokemon.getCurrentStats().hp / (float) pokemon.getStats().hp;
        if (!animating) {
            animating = true;
            handler.postDelayed(smoothTask, 0);
        }
    }

    public boolean animationDone() {
        return !animating;
    }
}