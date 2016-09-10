package net.ledii.pokemon;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Movie;
import android.graphics.Paint;
import android.view.View;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.io.InputStream;

public class AnimatedGif extends View {
    private Movie animation;
    private long startTick;
    private Paint paint;
    private boolean looping;
    private boolean paused;
    private Context context;
    private InputStream is;
    private float scale = 4;
    private float speed = 1.5f;
    private final int CENTER_SIZE = 192;
    private float width, height;
    private float anchorX, anchorY;
    private float offX, offY;

    public AnimatedGif(Context context) {
        super(context);

        //Init variables
        paused = true;
        looping = true;

        //Init paint
        paint = new Paint();
        setLayerType(LAYER_TYPE_SOFTWARE, paint);

        //Store context
        this.context = context;
        RelativeLayout battleLayout = (RelativeLayout) ((Activity) context).findViewById(R.id.layoutBattle);
        battleLayout.addView(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.scale(scale, scale);
        super.onDraw(canvas);

        if (animation != null && !paused) {
            //Set animation frame
            long tick = android.os.SystemClock.uptimeMillis();
            if (startTick == 0) {
                //Start of animation
                startTick = tick;
            }
            int ticksPassed = (int)((tick - startTick) * speed);
            if (ticksPassed >= animation.duration()) {
                //End of animation
                startTick = 0;
                ticksPassed = 0;
                if (!looping) {
                    paused = true;
                }
            }
            animation.setTime(ticksPassed);

            //Draw animation
            paint.setStyle(Paint.Style.FILL);
            animation.draw(canvas, 0, 0, paint);
            invalidate();

            /*
            //Draw frame
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            canvas.drawRect(0, 0, width, height, paint);

            //Draw anchor cross
            paint.setColor(Color.BLUE);
            canvas.drawLine(anchorX, 0, anchorX, height, paint);
            canvas.drawLine(0, anchorY, width, anchorY, paint);
            */
        }
    }

    public void play(boolean looping) {
        paused = false;
        this.looping = looping;
    }

    public void stop() {
        startTick = 0;
        paused = true;
    }

    public void pause() {
        paused = true;
    }

    public void setGIF(String asset) {
        if (animation != null) {
            try { is.close(); }
            catch (IOException exception) {}
            is = null;
            animation = null;
        }

        if (asset != null) {
            //Try to open animation
            try { is = context.getResources().getAssets().open(asset); }
            catch (IOException exception) {}
            if (is != null) {
                animation = Movie.decodeStream(is);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                width = bmp.getWidth();
                height = bmp.getHeight();
                bmp.recycle();

                //Set anchor
                String searchStr = asset.replace(".gif", "");
                switch (searchStr) {
                    case "001b": { setAnchor(66, height); break; }
                    case "002b": { setAnchor(99, height); break; }
                    case "003b": { setAnchor(120, height); break; }
                    case "004b": { setAnchor(93, height); break; }
                    case "005b": { setAnchor(109, height); break; }
                    case "006b": { setAnchor(247, height); break; }
                    case "007b": { setAnchor(75, height); break; }
                    case "008b": { setAnchor(82, height); break; }
                    case "009b": { setAnchor(108, height); break; }
                    case "010b": { setAnchor(61, height); break; }
                    case "011b": { setAnchor(62, height); break; }
                    case "012b": { setAnchor(100, height); break; }
                    case "013b": { setAnchor(69, height); break; }
                    case "014b": { setAnchor(34, height); break; }
                    case "015b": { setAnchor(88, height); break; }
                    case "016b": { setAnchor(67, height); break; }
                    case "017b": { setAnchor(91, height); break; }
                    case "018b": { setAnchor(101, height); break; }
                    case "019b": { setAnchor(43, height); break; }
                    case "020b": { setAnchor(106, height); break; }
                    case "021b": { setAnchor(61, height); break; }
                    case "022b": { setAnchor(160, height); break; }
                    case "023b": { setAnchor(62, height); break; }
                    case "024b": { setAnchor(163, height); break; }
                    case "025b": { setAnchor(98, height); break; }
                    case "026b": { setAnchor(135, height); break; }
                    case "027b": { setAnchor(85, height); break; }
                    case "028b": { setAnchor(82, height); break; }
                    case "029b": { setAnchor(55, height); break; }
                    case "030b": { setAnchor(61, height); break; }
                    case "031b": { setAnchor(120, height); break; }
                    case "032b": { setAnchor(59, height); break; }
                    case "033b": { setAnchor(60, height); break; }
                    case "034b": { setAnchor(176, height); break; }
                    case "035b": { setAnchor(76, height); break; }
                    case "036b": { setAnchor(87, height); break; }
                    case "037b": { setAnchor(75, height); break; }
                    case "038b": { setAnchor(130, height); break; }
                    case "039b": { setAnchor(49, height); break; }
                    case "040b": { setAnchor(61, height); break; }
                    case "041b": { setAnchor(123, height); break; }
                    case "042b": { setAnchor(151, height); break; }
                    case "043b": { setAnchor(64, height); break; }
                    case "044b": { setAnchor(80, height); break; }
                    case "045b": { setAnchor(90, height); break; }
                    case "046b": { setAnchor(55, height); break; }
                    case "047b": { setAnchor(77, height); break; }
                    case "048b": { setAnchor(58, height); break; }
                    case "049b": { setAnchor(101, height); break; }
                    case "050b": { setAnchor(47, height); break; }
                    case "051b": { setAnchor(76, height); break; }
                    case "052b": { setAnchor(63, height); break; }
                    case "053b": { setAnchor(148, height); break; }
                    case "054b": { setAnchor(49, height); break; }
                    case "055b": { setAnchor(87, height); break; }
                    case "056b": { setAnchor(76, height); break; }
                    case "057b": { setAnchor(72, height); break; }
                    case "058b": { setAnchor(87, height); break; }
                    case "059b": { setAnchor(170, height); break; }
                    case "060b": { setAnchor(99, height); break; }
                    case "061b": { setAnchor(66, height); break; }
                    case "062b": { setAnchor(64, height); break; }
                    case "063b": { setAnchor(94, height); break; }
                    case "064b": { setAnchor(110, height); break; }
                    case "065b": { setAnchor(74, height); break; }
                    case "066b": { setAnchor(50, height); break; }
                    case "067b": { setAnchor(70, height); break; }
                    case "068b": { setAnchor(70, height); break; }
                    case "069b": { setAnchor(46, height); break; }
                    case "070b": { setAnchor(64, height); break; }
                    case "071b": { setAnchor(85, height); break; }
                    case "072b": { setAnchor(46, height); break; }
                    case "073b": { setAnchor(87, height); break; }
                    case "074b": { setAnchor(53, height); break; }
                    case "075b": { setAnchor(80, height); break; }
                    case "076b": { setAnchor(87, height); break; }
                    case "077b": { setAnchor(73, height); break; }
                    case "078b": { setAnchor(195, height); break; }
                    case "079b": { setAnchor(75, height); break; }
                    case "080b": { setAnchor(130, height); break; }
                    case "081b": { setAnchor(44, height); break; }
                    case "082b": { setAnchor(72, height); break; }
                    case "083b": { setAnchor(48, height); break; }
                    case "084b": { setAnchor(38, height); break; }
                    case "085b": { setAnchor(95, height); break; }
                    case "086b": { setAnchor(98, height); break; }
                    case "087b": { setAnchor(192, height); break; }
                    case "088b": { setAnchor(80, height); break; }
                    case "089b": { setAnchor(160, height); break; }
                    case "090b": { setAnchor(62, height); break; }
                    case "091b": { setAnchor(92, height); break; }
                    case "092b": { setAnchor(67, height); break; }
                    case "093b": { setAnchor(100, height); break; }
                    case "094b": { setAnchor(84, height); break; }
                    case "095b": { setAnchor(105, height); break; }
                    case "096b": { setAnchor(63, height); break; }
                    case "097b": { setAnchor(72, height); break; }
                    case "098b": { setAnchor(71, height); break; }
                    case "099b": { setAnchor(92, height); break; }
                    case "100b": { setAnchor(51, height); break; }
                    case "101b": { setAnchor(83, height); break; }
                    case "102b": { setAnchor(89, height); break; }
                    case "103b": { setAnchor(146, height); break; }
                    case "104b": { setAnchor(48, height); break; }
                    case "105b": { setAnchor(66, height); break; }
                    case "106b": { setAnchor(38, height); break; }
                    case "107b": { setAnchor(60, height); break; }
                    case "108b": { setAnchor(77, height); break; }
                    case "109b": { setAnchor(78, height); break; }
                    case "110b": { setAnchor(102, height); break; }
                    case "111b": { setAnchor(80, height); break; }
                    case "112b": { setAnchor(127, height); break; }
                    case "113b": { setAnchor(78, height); break; }
                    case "114b": { setAnchor(65, height); break; }
                    case "115b": { setAnchor(160, height); break; }
                    case "116b": { setAnchor(44, height); break; }
                    case "117b": { setAnchor(89, height); break; }
                    case "118b": { setAnchor(108, height); break; }
                    case "119b": { setAnchor(110, height); break; }
                    case "120b": { setAnchor(54, height); break; }
                    case "121b": { setAnchor(70, height); break; }
                    case "122b": { setAnchor(71, height); break; }
                    case "123b": { setAnchor(99, height); break; }
                    case "124b": { setAnchor(79, height); break; }
                    case "125b": { setAnchor(116, height); break; }
                    case "126b": { setAnchor(137, height); break; }
                    case "127b": { setAnchor(64, height); break; }
                    case "128b": { setAnchor(189, height); break; }
                    case "129b": { setAnchor(79, height); break; }
                    case "130b": { setAnchor(226, height); break; }
                    case "131b": { setAnchor(123, height); break; }
                    case "132b": { setAnchor(44, height); break; }
                    case "133b": { setAnchor(92, height); break; }
                    case "134b": { setAnchor(194, height); break; }
                    case "135b": { setAnchor(75, height); break; }
                    case "136b": { setAnchor(100, height); break; }
                    case "137b": { setAnchor(69, height); break; }
                    case "138b": { setAnchor(63, height); break; }
                    case "139b": { setAnchor(82, height); break; }
                    case "140b": { setAnchor(56, height); break; }
                    case "141b": { setAnchor(62, height); break; }
                    case "142b": { setAnchor(167, height); break; }
                    case "143b": { setAnchor(83, height); break; }
                    case "144b": { setAnchor(191, height); break; }
                    case "145b": { setAnchor(146, height); break; }
                    case "146b": { setAnchor(199, height); break; }
                    case "147b": { setAnchor(63, height); break; }
                    case "148b": { setAnchor(70, height); break; }
                    case "149b": { setAnchor(160, height); break; }
                    case "150b": { setAnchor(255, height); break; }
                    case "151b": { setAnchor(55, height); break; }

                    default: { setAnchor(width / 2f, height); }
                }

                if (height == CENTER_SIZE) {
                    setAnchor(anchorX, anchorY - 28);
                }

                //Set animation speed
                if (searchStr.contains("41") || searchStr.contains("129")) {
                    speed = 1f;
                }
                else {
                    speed = 1.5f;
                }
            }
        }
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setAnchor(float x, float y) {
        //Reset last anchor
        setOffset(offX, offY);

        //Set new anchor
        anchorX = x;
        anchorY = y;
        setX(getX() - (anchorX * scale));
        setY(getY() - (anchorY * scale));
    }

    public void setOffset(float x, float y) {
        //Set new offset
        offX = x;
        offY = y;
        setX(offX);
        setY(offY);
    }

    public float getSize() {
        return CENTER_SIZE * scale;
    }
}