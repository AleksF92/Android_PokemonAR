package net.ledii.pokemon;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.FutureTask;

public class MainActivity extends FragmentActivity {

    private String gameMode;
    private MapMode mapMode;
    private BattleMode battleMode;
    private Timer timer;
    private TimerTask updateTask;
    private final float UPS = 30;
    private GameAudio bgMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Hide map until done loading
        View mapView = findViewById(R.id.fragMap);
        mapView.setVisibility(View.INVISIBLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        gameMode = "";
        bgMusic = new GameAudio(this);

        gameIntro();
        gameAccept();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bgMusic.play();

        switch (gameMode) {
            case "Map": { mapMode.resume(); break; }
            case "Battle": { battleMode.resume(); break; }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            enterTrueFullscreen();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        bgMusic.pause();

        switch (gameMode) {
            case "Map": { mapMode.stop(); break; }
            case "Battle": { battleMode.stop(); break; }
        }
    }

    @Override
    public void onBackPressed() {
        //Do nothing
        if (gameMode == "Battle") {
            battleMode.runFromBattle();
        }
    }

    private void enterTrueFullscreen() {
        if (Build.VERSION.SDK_INT >= 19) {
            //Hide nav and status bar
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
        if (Build.VERSION.SDK_INT < 16) {
            int fullscreen = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setFlags(fullscreen, fullscreen);
        }
    }

    private void gameIntro() {
        //Global.print("Game intro!");
        for (int i = 0; i < 50; i++) {
            Global.choose(new String[] {"Male", "Female"});
        }
    }

    private void gameAccept() {
        //Global.print("Game accept!");
        //Request permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            };
            requestPermissions(permissions, 1);
        }
        else {
            gameStart();
        }
    }

    private void gameStart() {
        //Init game
        //Global.print("Game start!");
        mapMode = new MapMode(this);
        battleMode = new BattleMode(this);
        setGameMode("Map");

        //Start update
        timer = new Timer();
        updateTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gameUpdate();
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(updateTask, 0, (int)(1000 / UPS));
    }

    private void gameUpdate() {
        switch (gameMode) {
            case "Map": {
                String battleCode = mapMode.isBattleBegan();
                if (battleCode != null) {
                    Player player = mapMode.getPlayer();
                    if (battleCode == "Player") { battleMode.startTrainerBattle(player, mapMode.getOpponent()); }
                    if (battleCode == "Wild") { battleMode.startWildBattle(player, mapMode.getWild()); }
                    setGameMode("Battle");
                }
                break;
            }
            case "Battle": {
                String battleCode = battleMode.isBattleDone();
                if (battleCode != null) {
                    mapMode.battleEnd(battleCode);
                    setGameMode("Map");
                }
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0) {
                int numGranted = 0;
                for (int result : grantResults) {
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        numGranted++;
                    }
                }

                if (numGranted == permissions.length) {
                    //Global.print("Permissions granted!");
                    gameStart();
                }
                else {
                    //Global.print("Permissions missing!");
                }
            }
            else {
                //Global.print("Request cancelled!");
            }
        }
    }

    private void setGameMode(String mode) {
        gameMode = mode;
        switch (gameMode) {
            case "Map": {
                mapMode.setVisible(true);
                battleMode.setVisible(false);
                bgMusic.setMusic(GameAudio.Music.WALKING);
                bgMusic.play();
                break;
            }
            case "Battle": {
                mapMode.setVisible(false);
                battleMode.setVisible(true);
                bgMusic.setMusic(GameAudio.Music.BATTLE);
                bgMusic.play();
                break;
            }
        }
    }
}
