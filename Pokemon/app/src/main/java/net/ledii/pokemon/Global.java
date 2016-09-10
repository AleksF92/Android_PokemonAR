package net.ledii.pokemon;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.RunnableFuture;

public class Global {
    static void print(String txt) {
        Log.println(Log.ASSERT, "Debug", txt);
    }

    public static int randomInt(int min, int max) {
        Random rand = new Random();
        int result = min + (rand.nextInt(max - min + 1));
        return result;
    }

    public static String choose(String[] data) {
        int roll = randomInt(0, data.length - 1);
        return data[roll];
    }

    public static float getDistance(LatLng pos1, LatLng pos2) {
        Location loc1 = new Location("");
        loc1.setLatitude(pos1.latitude);
        loc1.setLongitude(pos1.longitude);
        Location loc2 = new Location("");
        loc2.setLatitude(pos2.latitude);
        loc2.setLongitude(pos2.longitude);
        return loc1.distanceTo(loc2);
    }

    public static int parseSymbol(String symbol) {
        int result = 0;
        if (symbol.equals("∞")) { result = -2; }
        else if (symbol.equals("-")) { result = -1; }
        else { result = Integer.parseInt(symbol); }
        return result;
    }

    public static String parseValue(String dataStr) {
        return dataStr.substring(dataStr.indexOf(":") + 2);
    }

    public static LatLng getRandomLocation(LatLng myPos, int minRadius, int maxRadius) {
        Random random = new Random();

        // Convert radius from meters to degrees
        int radius = randomInt(minRadius, maxRadius);
        double radiusInDegrees = radius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(myPos.latitude);

        double foundLongitude = new_x + myPos.longitude;
        double foundLatitude = y + myPos.latitude;
        return new LatLng(foundLatitude, foundLongitude);
    }

    public static Point getScreenSize(Context context) {
        Point result = new Point();

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getSize(result);

        return result;
    }

    public static int getColor(String code) {
        int result = 0;
        switch (code) {
            case "Male": { result = Color.rgb(150, 150, 255); break; }
            case "Female": { result = Color.rgb(255, 150, 150); break; }

            case "Normal": { result = Color.rgb(138, 138, 89); break; }
            case "Fire": { result = Color.rgb(240, 128, 48); break; }
            case "Water": { result = Color.rgb(104, 144, 240); break; }
            case "Electric": { result = Color.rgb(248, 208, 48); break; }
            case "Grass": { result = Color.rgb(120, 200, 80); break; }
            case "Ice": { result = Color.rgb(152, 216, 216); break; }
            case "Fighting": { result = Color.rgb(192, 48, 40); break; }
            case "Poison": { result = Color.rgb(160, 64, 160); break; }
            case "Ground": { result = Color.rgb(224, 192, 104); break; }
            case "Flying": { result = Color.rgb(168, 144, 240); break; }
            case "Psychic": { result = Color.rgb(248, 88, 136); break; }
            case "Bug": { result = Color.rgb(168, 184, 32); break; }
            case "Rock": { result = Color.rgb(184, 160, 56); break; }
            case "Ghost": { result = Color.rgb(112, 88, 152); break; }
            case "Dragon": { result = Color.rgb(112, 56, 248); break; }
            case "Dark": { result = Color.rgb(112, 88, 72); break; }
            case "Steel": { result = Color.rgb(184, 184, 208); break; }
            case "Fairy": { result = Color.rgb(232, 152, 232); break; }
            case "Empty": { result = Color.rgb(80, 80, 80); break; }
        }
        return result;
    }

    public static String getData(String dataStr) {
        return dataStr.substring(dataStr.indexOf(":") + 2);
    }

    public static void clearStatus(final StatusText status) {
        ((Activity) status.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                status.setText("");
            }
        });
    }

    public static void setStatus(final StatusText status, final String text, final boolean append) {
        ((Activity) status.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                status.setStatusText(text, append);
            }
        });
    }

    public static BitmapDescriptor getUnknownIcon(Context context) {
        //Get bitmap
        BitmapDescriptor bmpResult = null;
        try {
            InputStream is = context.getResources().getAssets().open("000ic.png");
            Bitmap bmp = BitmapFactory.decodeStream(is);
            int SCALE = 7;
            int scaleW = bmp.getWidth() * SCALE;
            int scaleH = bmp.getHeight() * SCALE;
            Bitmap bmpScaled = Bitmap.createScaledBitmap(bmp, scaleW, scaleH, true);
            bmpResult = BitmapDescriptorFactory.fromBitmap(bmpScaled);

            is.close();
            bmp.recycle();
            bmpScaled.recycle();
        }
        catch (IOException exception) {}

        return bmpResult;
    }

    public static boolean isTimedOut(Date startDate, int durMinutes) {
        Date now = new Date();
        long diff = now.getTime() - startDate.getTime();
        int msInMin = 1000 * 60;
        long minutes = diff / msInMin;
        return false; //(minutes >= durMinutes);
    }
}