package net.ledii.pokemon;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import java.util.Vector;

public class BattleMode {
    private class Turn {
        BattleDisplay display;
        Pokemon pokemon;
        Pokemon.Move move;
        boolean player;
    }
    private Context context;
    private Player player;
    private Player opponent;
    private Pokemon wild;
    private AnimatedGif playerGif;
    private AnimatedGif battleGif;
    private BattleDisplay displayNear;
    private BattleDisplay displayFar;
    private Vector<MoveButton> moveButtons;
    private final int PADDING = 50;
    private boolean run, battleDone;
    private StatusText statusText;

    BattleMode(Context context) {
        this.context = context;

        battleGif = new AnimatedGif(context);
        playerGif = new AnimatedGif(context);
        displayFar = new BattleDisplay(context, false);
        displayNear = new BattleDisplay(context, true);

        Point screenSize = Global.getScreenSize(context);
        float xLeft = PADDING;
        float yTop = PADDING;
        float xRight = screenSize.x - PADDING;
        float yBottom = (screenSize.y / 2f) + (PADDING / 1.5f);

        displayFar.setX(xLeft);
        displayFar.setY(yTop);
        displayFar.setVisibility(View.INVISIBLE);
        displayNear.setX(xRight - displayNear.getW());
        displayNear.setY(yBottom - displayNear.getH());
        displayNear.setVisibility(View.INVISIBLE);
        battleGif.setVisibility(View.INVISIBLE);
        battleGif.setOffset(xRight - (displayFar.getW() / 2f), (screenSize.y / 3.1f));
        playerGif.setVisibility(View.INVISIBLE);
        playerGif.setOffset(xLeft + (displayNear.getW() / 2f), yBottom);

        int w = (screenSize.x - (PADDING * 3)) / 2;
        int h = 200;
        float x1 = PADDING;
        float y1 = screenSize.y - ((h + PADDING) * 2);
        moveButtons = new Vector<>();
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                final int id = (y * 2) + x;
                MoveButton moveButton = new MoveButton(context);
                moveButton.getLayoutParams().width = w;
                moveButton.getLayoutParams().height = h;
                moveButton.setX(x1 + ((w + PADDING) * x));
                moveButton.setY(y1 + ((h + PADDING) * y));
                moveButtons.add(moveButton);
                moveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        moveClick(id);
                    }
                });
            }
        }

        statusText = new StatusText(context);
        statusText.setX(0);
        statusText.setY(1300 + PADDING);
        statusText.getLayoutParams().width = screenSize.x;
        statusText.getLayoutParams().height = 96 * 10;
    }

    public void stop() {

    }

    public void resume() {

    }



    //Map connection
    public void setVisible(boolean visible) {
        View battleLayout = ((FragmentActivity) context).findViewById(R.id.layoutBattle);

        if (visible) {
            battleLayout.setVisibility(View.VISIBLE);
        }
        else {
            player = null;

            playerGif.setGIF(null);
            playerGif.setVisibility(View.INVISIBLE);
            battleGif.setGIF(null);
            battleGif.setVisibility(View.INVISIBLE);
            displayFar.setVisibility(View.INVISIBLE);
            displayNear.setVisibility(View.INVISIBLE);

            setMoveButtons(false);

            battleLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void startTrainerBattle(Player player, Player opponent) {
        this.player = player;
        this.opponent = opponent;
        run = false;
    }

    public void startWildBattle(Player player, Pokemon wild) {
        this.player = player;
        this.wild = wild;
        run = false;
        battleDone = false;
        setMoveButtons(false);

        //Load pokemon
        loadOpponent(wild);
        loadPlayer(player.getFirstPokemon());

        //Wait for click
        if (wild != null) {
            String battleTxt = "A wild " + displayFar.getPokemon().getName() + " appeared!";
            statusText.setStatusText(battleTxt, false);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (statusText.isClicked()) {
                            //Transform instant if imposter
                            if (displayNear.getPokemon().getAbility().equals("Imposter")) {
                                //Transform
                                displayNear.getPokemon().transform(displayFar.getPokemon());

                                //Load player and wait
                                ((Activity) context).runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {
                                           loadPlayer(displayNear.getPokemon());
                                       }
                                });
                                _waitForResponce(displayNear.getPokemon().getName(true) + " transformed!", false);
                            }
                            if (displayFar.getPokemon().getAbility().equals("Imposter")) {
                                //Transform
                                displayFar.getPokemon().transform(displayNear.getPokemon());

                                //Load opponent and wait
                                ((Activity) context).runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {
                                           loadOpponent(displayFar.getPokemon());
                                       }
                                });
                                _waitForResponce(displayFar.getPokemon().getName(true) + " transformed!", false);
                            }

                            //Choose action
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setMoveButtons(true);
                                    Global.clearStatus(statusText);
                                }
                            });
                            break;
                        }
                    }
                }
            });
            thread.start();
        }
    }

    private void loadPlayer(Pokemon pokemon) {
        //Send in pokemon
        displayNear.sendIn(pokemon);
        displayNear.setVisibility(View.VISIBLE);
        playerGif.setGIF(displayNear.getPokemon().getBackGif());
        playerGif.setVisibility(View.VISIBLE);
        playerGif.play(true);

        //Update move buttons
        for (int i = 0; i < moveButtons.size(); i++) {
            Pokemon.LearnedMove move = displayNear.getPokemon().getLearnedMove(i);
            moveButtons.get(i).setMove(move);
        }
    }

    private void loadOpponent(Pokemon pokemon) {
        //Send in pokemon
        displayFar.sendIn(pokemon);
        displayFar.setVisibility(View.VISIBLE);
        battleGif.setGIF(wild.getGif());
        battleGif.setVisibility(View.VISIBLE);
        battleGif.play(true);
    }

    public void runFromBattle() {
        run = true;
        statusText.quickBlock();
        Global.setStatus(statusText, "You successfully ran away!", false);
        setMoveButtons(false);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (statusText.isClicked()) {
                        battleDone = true;
                        break;
                    }
                }
            }
        });
        thread.start();
    }

    public String isBattleDone() {
        String result = null;

        if (battleDone) {
            if (wild != null) {
                if (wild.isFainted()) {
                    result = "Won";
                }
                if (wild.isCaught()) {
                    result = "Caught";
                    Global.print("Wild " + wild.getName() + " was caught!");
                }
                if (wild.ranAway()) {
                    result = "Fled";
                    Global.print("Wild " + wild.getName() + " ran away!");
                }
                if (run) {
                    result = "Run";
                }
                if (player.unableToBattle()) {
                    result = "Unable to Battle";
                }
            }

            //Check if combat is ended
            if (result != null) {
                //Hide buttons
                setMoveButtons(false);

                //Disengage pokemon from combat
                displayFar.getPokemon().resetCombatStats();
                displayNear.getPokemon().resetCombatStats();

                //Heal player's pokemon if fainted
                if (result.contains("Unable")) {
                    displayNear.getPokemon().recover();
                }

                //Heal wild pokemon if escaped
                if (!(wild.isFainted() || wild.isCaught())) {
                    displayFar.getPokemon().recover();
                }
            }
        }

        return result;
    }

    private void moveClick(int moveId) {
        //Hide buttons
        setMoveButtons(false);

        //Move selected check if valid move is selected
        Pokemon.Move nearMove = displayNear.getPokemon().useMove(moveId);
        if (nearMove != null) {
            //Init turns and select opponent move
            Turn first = new Turn();
            first.display = displayNear;
            first.pokemon = displayNear.getPokemon();
            first.move = nearMove;
            first.player = true;
            Turn second = new Turn();
            second.display = displayFar;
            second.pokemon = displayFar.getPokemon();
            second.move = displayFar.getPokemon().useRandomMove();

            //Decide who goes first
            if (second.move.getPriority() > first.move.getPriority()) {
                //Swap
                Turn tmp = first;
                first = second;
                second = tmp;
            }
            else if (second.move.getPriority() == first.move.getPriority()) {
                if (second.pokemon.getStats().spd > first.pokemon.getStats().spd) {
                    //Swap
                    Turn tmp = first;
                    first = second;
                    second = tmp;
                }
                else if (second.pokemon.getStats().spd == first.pokemon.getStats().spd) {
                    //Random swap
                    int roll = Global.randomInt(0, 1);
                    if (roll == 1) {
                        Turn tmp = first;
                        first = second;
                        second = tmp;
                    }
                }
            }

            final Turn firstTurn = first;
            final Turn secondTurn = second;

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //First move
                    _preformAttack(firstTurn, secondTurn);

                    //Second move
                    if (!secondTurn.pokemon.isFainted()) {
                        _preformAttack(secondTurn, firstTurn);
                    }

                    if (!(firstTurn.pokemon.isFainted() || secondTurn.pokemon.isFainted())) {
                        //Update delayed status
                        firstTurn.pokemon.updateStatusDelay();
                        secondTurn.pokemon.updateStatusDelay();

                        //Done attacking, show attacks
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setMoveButtons(true);
                            }
                        });
                    }

                    firstTurn.pokemon.endOfTurn();
                    secondTurn.pokemon.endOfTurn();
                }
            });
            thread.start();
        }
        else {
            //Invalid move choice
            if (displayNear.getPokemon().getLearnedMove(moveId) != null) {
                //Non-EMPTY move selected
                final int invalidMoveId = moveId;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //Check if disabled
                        Pokemon.LearnedMove useMove = displayNear.getPokemon().getLearnedMove(invalidMoveId);
                        if (useMove.disabledRounds > 0) {
                            _waitForResponce(useMove.data.name + " is currently disabled!", false);
                            Global.clearStatus(statusText);
                        }

                        //Done attacking, show attacks
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setMoveButtons(true);
                            }
                        });
                    }
                });
                thread.start();
            }
            else {
                //EMPTY move selected, do nothing
                setMoveButtons(true);
            }
        }
    }

    private void _preformAttack(final Turn attacker, final Turn defender) {
        //Use move
        boolean moveSuccessful = false;
        String moveStr = attacker.pokemon.getName(true) + " used " + attacker.move.name + "...";
        Global.setStatus(statusText, moveStr, false);

        //Check status
        String startStatus = attacker.pokemon.startMove();
        boolean hurtSelf = false;
        if (startStatus.contains("confus")) {
            //May prevent attack
            _waitForResponce(startStatus, false);

            if (startStatus.contains("confused")) {
                int roll = Global.randomInt(0, 1);
                if (roll == 1) {
                    hurtSelf = true;
                    attacker.pokemon.hurtSelf();
                    startStatus = attacker.pokemon.getName(true) + " hurt itself in it's confusion!";
                }
            }
            Global.setStatus(statusText, moveStr, false);
        }

        if (startStatus.equals("") || startStatus.contains("woke up") || (startStatus.contains("confus") && !hurtSelf)) {
            if (startStatus.contains("woke up")) {
                //Status depleted
                _waitForResponce(startStatus, false);
                Global.setStatus(statusText, moveStr, false);
            }

            //Check hit
            float hitRate = attacker.pokemon.getStageValue("Accuracy") / defender.pokemon.getStageValue("Evasion");
            int totAccuracy = (int) (attacker.move.accuracy * hitRate);

            boolean halfChance = false;
            if (attacker.pokemon.haveUsed("Protection")) {
                if (attacker.move.name.equals("Protect")
                        || attacker.move.name.equals("Detect")
                        || attacker.move.name.equals("Quick Guard")
                        || attacker.move.name.equals("Wide Guard")
                        || attacker.move.name.equals("Endure")) {
                    totAccuracy = (int) (50 * hitRate);
                    halfChance = true;
                }
            }

            //Check for transform
            boolean usedTransform = false;
            if (attacker.move.name.equals("Transform")) {
                usedTransform = true;
            }

            if (!usedTransform) {
                //Check for flinch
                boolean flinched = attacker.pokemon.isFlinched();
                if (attacker.move.name.equals("Focus Punch") && attacker.pokemon.hitThisTurn()) {
                    flinched = true;
                }
                if (!flinched) {
                    //Check for protection
                    boolean isProtected = false;
                    if (defender.pokemon.haveProtection("Protect")
                            || defender.pokemon.haveProtection("Protect")
                            || (defender.pokemon.haveProtection("Quick Guard") && attacker.move.getPriority() > 0)) {
                        isProtected = true;
                    }

                    if (!isProtected) {
                        //Nothing is preventing attack
                        moveSuccessful = true;
                        int roll = Global.randomInt(1, 100);
                        if (roll <= totAccuracy || (attacker.move.accuracy < 0 && !halfChance)) {
                            //Get num hits
                            String searchStr = attacker.move.effect.toLowerCase();
                            int times = 1;
                            int counter = 0;
                            if (searchStr.contains("2-5 times")) {
                                int hitRoll = Global.randomInt(1, 8);
                                if (hitRoll <= 6) {
                                    times = Global.randomInt(2, 3);
                                } else {
                                    times = Global.randomInt(4, 5);
                                }
                            } else if (searchStr.contains("twice")) {
                                times = 2;
                            } else if (searchStr.contains("thrice")) {
                                times = 3;
                            }

                            for (int i = 0; i < times; i++) {
                                counter++;
                                int orgPower = attacker.move.power;
                                if (searchStr.contains("thrice")) {
                                    attacker.move.power += (orgPower * i);
                                }

                                //Hit
                                String result = defender.pokemon.attack(attacker.pokemon, attacker.move);
                                if (i == 0) {
                                    Global.setStatus(statusText, result, true);
                                } else {
                                    Global.setStatus(statusText, attacker.move.name + " hits again..." + result, false);
                                }

                                if (searchStr.contains("thrice")) {
                                    attacker.move.power = orgPower;
                                }

                                //Wait for animation
                                _waitForAnimation();

                                //If has recover effect
                                if (searchStr.contains("recovers")) {
                                    float amount = 0;
                                    if (searchStr.contains("inflicted")) {
                                        if (searchStr.contains("half")) {
                                            amount = 0.5f;
                                        }
                                        if (searchStr.contains("most")) {
                                            amount = 0.75f;
                                        }

                                        if (attacker.move.name.equals("Dream Eater")) {
                                            String status = defender.pokemon.getStatus();
                                            if (status != null) {
                                                if (status.equals("Sleep")) {
                                                    amount = 0.5f;
                                                }
                                            } else {
                                                amount = 0;
                                            }
                                        }

                                        if (amount > 0) {
                                            //Heal for damage
                                            int totHeal = (int) (defender.pokemon.getLastDamageTaken() * amount);
                                            if (totHeal < 1) {
                                                totHeal = 1;
                                            }
                                            boolean fullHp = false;
                                            if (attacker.pokemon.getCurrentStats().hp == attacker.pokemon.getStats().hp) {
                                                fullHp = true;
                                            }
                                            attacker.pokemon.healHealth(totHeal);

                                            if (!fullHp) {
                                                //Wait for animation
                                                Global.setStatus(statusText, attacker.pokemon.getName(true) + " recovered some hp!", false);
                                                _waitForAnimation();
                                            }
                                        }
                                    }
                                }

                                if (defender.pokemon.isFainted()) {
                                    break;
                                }
                            }

                            if (searchStr.contains("2-5 times") || searchStr.contains("twice") || searchStr.contains("thrice")) {
                                _waitForResponce(attacker.move.name + " hit " + counter + " times!", false);
                            }
                        } else {
                            if (attacker.move.name.equals("Protect")
                                    || attacker.move.name.equals("Detect")
                                    || attacker.move.name.equals("Quick Guard")
                                    || attacker.move.name.equals("Wide Guard")
                                    || attacker.move.name.equals("Endure")) {
                                //Failed
                                _waitForResponce("\nBut it failed!", true);
                            } else {
                                //Miss
                                _waitForResponce("\n" + defender.pokemon.getName(true) + " evaded the attack!", true);
                            }
                        }
                    } else {
                        //Is protected
                        _waitForResponce("\n" + defender.pokemon.getName(true) + " blocked the attack!", true);
                    }
                } else {
                    //Flinched
                    _waitForResponce(attacker.pokemon.getName(true) + " flinched and is unable to move!", false);
                }
            } else {
                //Used transform
                attacker.pokemon.transform(defender.pokemon);

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (attacker.player) {
                            loadPlayer(attacker.pokemon);
                        }
                        else {
                            loadOpponent(attacker.pokemon);
                        }
                    }
                });

                _waitForResponce(attacker.pokemon.getName(true) + " transformed!", false);
            }
        } else {
            //Status prevented attack
            Global.setStatus(statusText, startStatus, false);
            _waitForAnimation();
        }

        if (_pokemonAlive(defender) && _pokemonAlive(attacker)) {
            //End attack status
            String endStatus = attacker.pokemon.endMove(attacker.move, moveSuccessful);
            if (!endStatus.equals("")) {
                //Attacker took status damage
                Global.setStatus(statusText, endStatus, false);
                _waitForAnimation();
                _pokemonAlive(attacker);
            }
        }
        //Move done, clear data
        Global.clearStatus(statusText);
    }

    private void setMoveButtons(boolean enabled) {
        for (int i = 0; i < moveButtons.size(); i++) {
            MoveButton button = moveButtons.get(i);
            button.setClickable(enabled);
            if (enabled) {
                button.setVisibility(View.VISIBLE);
                button.invalidate();
            }
            else {
                button.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void _waitForResponce(String text, boolean append) {
        statusText.quickBlock();
        Global.setStatus(statusText, text, append);
        while (true) {
            if (statusText.isClicked()) {
                break;
            }
        }
    }

    private void _playerPokemonFainted() {
        if (player.unableToBattle()) {
            _waitForResponce("Your last pokemon fainted!", false);
            battleDone = true;
        }
    }

    private void _waitForAnimation() {
        //Animate damage
        displayFar.smoothInvalidate();
        displayNear.smoothInvalidate();

        while (true) {
            //Wait for animation to finish
            if (displayFar.animationDone()
                    && displayNear.animationDone()
                    && statusText.isClicked()) {
                break;
            }
        }
    }

    private boolean _pokemonAlive(Turn turn) {
        boolean result = true;
        if (turn.pokemon.isFainted()) {
            result = false;

            //Defender fainted
            _waitForResponce(turn.pokemon.getName(true) + " fainted!", false);
            if (turn.player) {
                _playerPokemonFainted();
            }
            else {
                //Wild pokemon defeated
                battleDone = true;
            }
        }
        return result;
    }
}