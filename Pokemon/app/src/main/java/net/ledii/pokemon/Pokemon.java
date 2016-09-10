package net.ledii.pokemon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

public class Pokemon {
    //Structures
    public enum NUMBER_ID {
        Empty,
        Bulbasaur, Ivysaur, Venusaur,
        Charmander, Charmeleon, Charizard,
        Squirtle, Wartortle, Blastoise,
        Caterpie, Metapod, Butterfree,
        Weedle, Kakuna, Beedrill,
        Pidgey, Pidgeotto, Pidgeot,
        Rattata, Raticate,
        Spearow, Fearow,
        Ekans, Arbok,
        Pikachu, Raichu,
        Sandshrew, Sandslash,
        Nidoran_F, Nidorina, Nidoqueen,
        Nidoran_M, Nidorino, Nidoking,
        Clefairy, Clefable,
        Vulpix, Ninetales,
        Jigglypuff, Wigglytuff,
        Zubat, Golbat,
        Oddish, Gloom, Vileplume,
        Paras, Parasect,
        Venonat, Venomoth,
        Diglett, Dugtrio,
        Meowth, Persian,
        Psyduck, Golduck,
        Mankey, Primeape,
        Growlithe, Arcanine,
        Poliwag, Poliwhirl, Poliwrath,
        Abra, Kadabra, Alakazam,
        Machop, Machoke, Machamp,
        Bellsprout, Weepinbell, Victreebel,
        Tentacool, Tentacruel,
        Geodude, Graveler, Golem,
        Ponyta, Rapidash,
        Slowpoke, Slowbro,
        Magnemite, Magneton,
        Farfetch_Ad,
        Doduo, Dodrio,
        Seel, Dewgong,
        Grimer, Muk,
        Shellder, Cloyster,
        Gastly, Haunter, Gengar,
        Onix,
        Drowzee, Hypno,
        Krabby, Kingler,
        Voltorb, Electrode,
        Exeggcute, Exeggutor,
        Cubone, Marowak,
        Hitmonlee,
        Hitmonchan,
        Lickitung,
        Koffing, Weezing,
        Rhyhorn, Rhydon,
        Chansey,
        Tangela,
        Kangaskhan,
        Horsea, Seadra,
        Goldeen, Seaking,
        Staryu, Starmie,
        Mr_PMime,
        Scyther,
        Jynx,
        Electabuzz,
        Magmar,
        Pinsir,
        Tauros,
        Magikarp, Gyarados,
        Lapras,
        Ditto,
        Eevee, Vaporeon, Jolteon, Flareon,
        Porygon,
        Omanyte, Omastar,
        Kabuto, Kabutops,
        Aerodactyl,
        Snorlax,
        Articuno, Zapdos, Moltres,
        Dratini, Dragonair, Dragonite,
        Mewtwo,
        Mew
    }
    public class Status {
        public String name;
        public int turnsLeft, turns, turnsDelayed;

        public Status() {
            name = "";
            turns = 0;
            turnsLeft = 0;
            turnsDelayed = 0;
        }
    }
    public class Stats {
        int hp, atk, def, spAtk, spDef, spd;
        public Stats(int hp, int atk, int def, int spAtk, int spDef, int spd) {
            this.hp = hp;
            this.atk = atk;
            this.def = def;
            this.spAtk = spAtk;
            this.spDef = spDef;
            this.spd = spd;
        }

        public Stats(String dataStr) {
            String[] data = dataStr.split(",");
            hp = Integer.parseInt(data[0]);
            atk = Integer.parseInt(data[1]);
            def = Integer.parseInt(data[2]);
            spAtk = Integer.parseInt(data[3]);
            spDef = Integer.parseInt(data[4]);
            spd = Integer.parseInt(data[5]);
        }

        public String getSaveData() {
            return hp + "," + atk + "," + def + "," + spAtk + "," + spDef + "," + spd;
        }
    }
    public class Move {
        String name, type, category, effect;
        int power, accuracy, pp, chance;

        Move() {

        }

        Move(String name) {
            InputStream is = context.getResources().openRawResource(R.raw.data_moves);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String dataStr = null;
            try {
                //Wait until ready
                while (dataStr == null) { dataStr = br.readLine(); }

                //Search for id
                while (!dataStr.contains("[" + name + "]")) { dataStr = br.readLine(); }
                this.name = name;

                dataStr = br.readLine();
                type = Global.parseValue(dataStr);
                dataStr = br.readLine();
                category = Global.parseValue(dataStr);
                dataStr = br.readLine();
                power = Global.parseSymbol(Global.parseValue(dataStr));
                dataStr = br.readLine();
                accuracy = Global.parseSymbol(Global.parseValue(dataStr));
                dataStr = br.readLine();
                pp = Global.parseSymbol(Global.parseValue(dataStr));
                dataStr = br.readLine();
                effect = Global.parseValue(dataStr);
                dataStr = br.readLine();
                chance = Global.parseSymbol(Global.parseValue(dataStr));

                //Close steams
                br.close();
                is.close();
            }
            catch (IOException exception) {}
        }

        public int getPriority() {
            int result = 0;

            //Set up categories
            String priUp5 = "Helping Hand, Transform";
            String priUp4 = "Magic Coat, Snatch";
            String priUp3 = "Detect, Endure, Follow Me, Protect, Wide Guard, Quick Guard";
            String priUp2 = "Feint";
            String priUp1 = "Aqua Jet, Bide, Bullet Punch, Extreme Speed, Fake Out, Ice Shard, Mach Punch, Quick Attack, Shadow Sneak, Sucker Punch, Vacuum Wave, Baby-Doll Eyes";
            String priDown1 = "Vital Throw";
            String priDown2 = "";
            String priDown3= "Focus Punch";
            String priDown4 = "Avalanche, Revenge";
            String priDown5 = "Counter, Mirror Coat";
            String priDown6 = "Roar, Whirlwind";
            String priDown7 = "Trick Room";

            //Check categories
            if (priUp5.contains(name)) { result = 5; }
            if (priUp4.contains(name)) { result = 4; }
            if (priUp3.contains(name)) { result = 3; }
            if (priUp2.contains(name)) { result = 2; }
            if (priUp1.contains(name)) { result = 1; }
            if (priDown1.contains(name)) { result = -1; }
            if (priDown2.contains(name)) { result = -2; }
            if (priDown3.contains(name)) { result = -3; }
            if (priDown4.contains(name)) { result = -4; }
            if (priDown5.contains(name)) { result = -5; }
            if (priDown6.contains(name)) { result = -6; }
            if (priDown7.contains(name)) { result = -7; }

            return result;
        }
    }
    public class LearnedMove {
        Move data;
        int ppCurrent, disabledRounds;

        LearnedMove(Move data) {
            this.data = data;
            ppCurrent = data.pp;
            disabledRounds = 0;
        }

        LearnedMove(String dataStr) {
            String[] data = dataStr.split(",");
            this.data = new Move(data[0]);
            ppCurrent = Integer.parseInt(data[1]);
            disabledRounds = 0;
        }

        String getSaveData() {
            return data.name + "," + ppCurrent;
        }
    }
    public class LevelMove {
        Move data;
        int level;

        LevelMove(String dataStr) {
            String[] data = dataStr.split(",");
            level = Integer.parseInt(data[0]);
            this.data = new Move(data[1]);
        }
    }
    private final int MAX_MOVES = 4;

    //Unique variables (must be saved)
    private String id;
    private String nickname;
    private String nature;
    private int level;
    private Stats ivStats, evStats;
    private Vector<LearnedMove> moves;
    private String gender;
    private Status status;

    //Helper variables
    private Vector<LevelMove> levelMoves;
    private Vector<Move> eggMoves;
    private Context context;
    private Stats baseStats, natureStats, totalStats, combatStats, evYield;
    private int accuracy, evasion;
    private Vector<String> types, eggGroups;
    private String height, weight, species, description;
    private String ability;
    private int catchRate, baseHappiness, baseXp, eggCycles;
    private String growthRate;
    private float genderRateMale;
    private boolean fainted, wild;
    private int lastDamageTaken;
    private int protect, detect, quickGuard, endure;
    private boolean flinched;
    private int critStage;
    private Status confusion;
    private int stockpiled;
    private LearnedMove lastMove;

    //Transform variables
    private boolean transformed;
    private Stats orgIvs, orgEvs, orgBase, orgNature, orgTotal, orgCombat;
    private String orgAbility, orgNat;
    private Vector<LearnedMove> orgMoves;
    private Vector<String> orgTypes;
    private String transformAsset;

    Pokemon(Context context, String dataStr) {
        String[] data = dataStr.split(":");

        //Init unique variables from data
        id = data[0];
        nickname = data[1];
        nature = data[2];
        level = Integer.parseInt(data[3]);
        ivStats = new Stats(data[4]);
        evStats = new Stats(data[5]);
        gender = data[6];
        fainted = Boolean.parseBoolean(data[7]);

        //Init helper variables
        this.context = context;
        initNature();

        //Load base stats and moves
        loadData();
        loadLevelMoves();

        //Init learned moves from data
        int numMoves = Integer.parseInt(data[8]);
        moves = new Vector<>();
        for (int i = 0; i < numMoves; i++) {
            moves.add(new LearnedMove(data[9 + i]));
            //moves.get(i).ppCurrent = moves.get(i).data.pp;
        }

        //Init transform
        initTransform();

        //Reset combat stats
        resetCombatStats();

        //Show debug load info
        //debugData();
    }

    Pokemon(Context context, String id, int level) {
        //Init unique variables
        this.id = id;
        nickname = "";
        nature = "Serious";
        this.level = level;
        initIvAndEvStats();

        //Init helper variables
        this.context = context;
        initNature();

        //Load base stats and moves
        loadData();
        loadLevelMoves();
        if (level == 1) {
            loadEggMoves();
        }

        //Init new random moves
        initLearnedMoves();

        //Init gender
        initGender();

        //Init transform
        initTransform();

        //Reset combat stats
        resetCombatStats();

        //Show debug load info
        //debugData();
    }



    private void initNature() {
        /*
                      - atk       - def       - spAtk     - spDef     - spd
            + atk     HARDY       LONELY      ADAMANT     NAUGHTY     BRAVE
            + def     BOLD        DOCILE      IMPISH      LAX         RELAXED
            + spAtk   MODEST      MILD        BASHFUL     RASH        QUIET
            + spDef   CALM        GENTLE      CAREFUL     QUIRKY      SASSY
            + spd     TIMID       HASTY       JOLLY       NAIVE       SERIOUS
        */
        int N = 100;
        int MOD = 10;
        switch (nature) {
            case "Hardy":     { natureStats = new Stats(0, N, N, N, N, N); break; }
            case "Lonely":    { natureStats = new Stats(0, N + MOD, N - MOD, N, N, N); break; }
            case "Adamant":   { natureStats = new Stats(0, N + MOD, N, N - MOD, N, N); break; }
            case "Naughty":   { natureStats = new Stats(0, N + MOD, N, N, N - MOD, N); break; }
            case "Brave":     { natureStats = new Stats(0, N + MOD, N, N, N, N - MOD); break; }

            case "Bold":      { natureStats = new Stats(0, N - MOD, N + MOD, N, N, N); break; }
            case "Docile":    { natureStats = new Stats(0, N, N, N, N, N); break; }
            case "Impish":    { natureStats = new Stats(0, N, N + MOD, N - MOD, N, N); break; }
            case "Lax":       { natureStats = new Stats(0, N, N + MOD, N, N - MOD, N); break; }
            case "Relaxed":   { natureStats = new Stats(0, N, N + MOD, N, N, N - MOD); break; }

            case "Modest":    { natureStats = new Stats(0, N - MOD, N, N + MOD, N, N); break; }
            case "Mild":      { natureStats = new Stats(0, N, N - MOD, N + MOD, N, N); break; }
            case "Bashful":   { natureStats = new Stats(0, N, N, N, N, N); break; }
            case "Rash":      { natureStats = new Stats(0, N, N, N + MOD, N - MOD, N); break; }
            case "Quiet":     { natureStats = new Stats(0, N, N, N + MOD, N, N - MOD); break; }

            case "Calm":      { natureStats = new Stats(0, N - MOD, N, N, N + MOD, N); break; }
            case "Gentle":    { natureStats = new Stats(0, N, N - MOD, N, N + MOD, N); break; }
            case "Careful":   { natureStats = new Stats(0, N, N, N - MOD, N + MOD, N); break; }
            case "Quirky":    { natureStats = new Stats(0, N, N, N, N, N); break; }
            case "Sassy":     { natureStats = new Stats(0, N, N, N, N + MOD, N - MOD); break; }

            case "Timid":     { natureStats = new Stats(0, N - MOD, N, N, N, N + MOD); break; }
            case "Hasty":     { natureStats = new Stats(0, N, N - MOD, N, N, N + MOD); break; }
            case "Jolly":     { natureStats = new Stats(0, N, N, N - MOD, N, N + MOD); break; }
            case "Naive":     { natureStats = new Stats(0, N, N, N, N - MOD, N + MOD); break; }
            case "Serious":   { natureStats = new Stats(0, N, N, N, N, N); break; }
        }
    }

    private void initIvAndEvStats() {
        //Create random individual values
        int ivHp = Global.randomInt(0, 31);
        int ivAtk = Global.randomInt(0, 31);
        int ivDef = Global.randomInt(0, 31);
        int ivSpAtk = Global.randomInt(0, 31);
        int ivSpDef = Global.randomInt(0, 31);
        int ivSpd = Global.randomInt(0, 31);
        //ivStats = new Stats(ivHp, ivAtk, ivDef, ivSpAtk, ivSpDef, ivSpd);
        ivStats = new Stats(31, 31, 31, 31, 31, 31);

        //No effort values obtained
        evStats = new Stats(0, 0, 0, 0, 0, 0);
    }

    private void initLearnedMoves() {
        //Collect all available moves
        Vector<Move> allMoves = new Vector<>();
        for (LevelMove move : levelMoves) {
            if (level >= move.level) {
                allMoves.add(move.data);
            }
        }
        if (level == 1) {
            allMoves.addAll(eggMoves);
        }

        //Remove duplicate entries
        for (int i = 0; i < allMoves.size(); i++) {
            String name = allMoves.get(i).name;
            for (int j = 0; j < allMoves.size(); j++) {
                if (j != i) {
                    String dupName = allMoves.get(j).name;
                    if (dupName.equals(name)) {
                        allMoves.remove(j);
                    }
                }
            }
        }

        //Select random moves
        while (allMoves.size() > MAX_MOVES) {
            int randId = Global.randomInt(0, allMoves.size() - 1);
            allMoves.remove(randId);
        }

        //Add moves
        moves = new Vector<>();
        for (Move move : allMoves) {
            moves.add(new LearnedMove(move));
        }
    }

    private void initGender() {
        int roll = Global.randomInt(1, 10000);
        if (roll <= genderRateMale * 100) { gender = "Male"; }
        else if (genderRateMale >= 0) { gender = "Female"; }
        else { gender = "None"; }
    }

    private void initTransform() {
        transformed = false;

        orgNat = nature;
        orgAbility = ability;

        orgTypes = new Vector<>();
        orgTypes.addAll(types);

        orgIvs = ivStats;
        orgEvs = evStats;
        orgBase = baseStats;
        orgNature = natureStats;
        orgTotal = totalStats;
        orgCombat = combatStats;

        orgMoves = new Vector<>();
        orgMoves.addAll(moves);
    }

    private void loadData() {
        InputStream is = context.getResources().openRawResource(R.raw.data_pokemon);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String dataStr = null;
        try {
            //Wait until ready
            while (dataStr == null) { dataStr = br.readLine(); }

            //Search for id
            while (!dataStr.contains("[" + id + "]")) { dataStr = br.readLine(); }

            //Load base stats
            dataStr = br.readLine();
            baseStats = new Stats(Global.getData(dataStr));

            //Load types
            dataStr = br.readLine();
            String[] typeData = Global.getData(dataStr).split(",");
            types = new Vector<>();
            types.add(typeData[0]);
            if (typeData.length > 1) { types.add(typeData[1]); }

            //Load info data
            dataStr = br.readLine();
            species = Global.getData(dataStr);
            dataStr = br.readLine();
            height = Global.getData(dataStr);
            dataStr = br.readLine();
            weight = Global.getData(dataStr);

            //Load ability
            Vector<String> abilities = new Vector<>();
            dataStr = br.readLine();
            String[] abilityData = Global.getData(dataStr).split(",");
            for (String abil : abilityData) {
                abilities.add(abil);
            }
            dataStr = br.readLine();
            String hiddenAbility = Global.getData(dataStr);
            if (!hiddenAbility.equals("None")) {
                int roll = Global.randomInt(1, 100);
                if (roll <= 5) { ability = hiddenAbility; }
            }
            if (ability == null) {
                int roll = Global.randomInt(0, abilities.size() - 1);
                ability = abilities.get(roll);
            }

            //Load ev yield
            dataStr = br.readLine();
            evYield = new Stats(Global.getData(dataStr));

            //Load numbers
            dataStr = br.readLine();
            catchRate = Integer.parseInt(Global.getData(dataStr));
            dataStr = br.readLine();
            baseHappiness = Integer.parseInt(Global.getData(dataStr));
            dataStr = br.readLine();
            baseXp = Integer.parseInt(Global.getData(dataStr));

            //Load growth rate
            dataStr = br.readLine();
            growthRate = Global.getData(dataStr);

            //Load egg group
            eggGroups = new Vector<>();
            dataStr = br.readLine();
            String[] eggGroupData = Global.getData(dataStr).split(",");
            for (String eggGroup : eggGroupData) {
                eggGroups.add(eggGroup);
            }

            //Load gender rate
            dataStr = br.readLine();
            String[] genderData = Global.getData(dataStr).split(",");
            if (genderData[0].equals("None")) { genderRateMale = -1; }
            else { genderRateMale = Float.parseFloat(genderData[0]); }

            //Load egg cycles
            dataStr = br.readLine();
            eggCycles = Integer.parseInt(Global.getData(dataStr));

            //Load description
            dataStr = br.readLine();
            description = Global.getData(dataStr);

            //Close steams
            br.close();
            is.close();
        }
        catch (IOException exception) {}

        if (baseStats != null) {
            calculateTotalStats();
        }
    }

    private void loadLevelMoves() {
        InputStream is = context.getResources().openRawResource(R.raw.data_learn_moves);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String dataStr = null;
        try {
            //Wait until ready
            while (dataStr == null) { dataStr = br.readLine(); }

            //Search for id
            while (!dataStr.contains(id)) { dataStr = br.readLine(); }

            //Search for egg moves
            while (!dataStr.contains("Moves")) { dataStr = br.readLine(); }
            dataStr = br.readLine();

            //Load level moves
            levelMoves = new Vector<>();
            while (!dataStr.contains("Egg Moves")) {
                if (!(dataStr.contains("Ally Switch")
                        || dataStr.contains("Assist")
                        || dataStr.contains("Beat Up")
                        || dataStr.contains("Bestow")
                        || dataStr.contains("Block")
                        || dataStr.contains("Fling")
                        || dataStr.contains("Follow Me")
                        || dataStr.contains("Frustration")
                        || dataStr.contains("Grass Knot")
                        || dataStr.contains("Happy Hour")
                        || dataStr.contains("Heat Crash")
                        || dataStr.contains("Heavy Slam")
                        || dataStr.contains("Helping Hand")
                        || dataStr.contains("Hold Hands")
                        || dataStr.contains("Mat Block")
                        || dataStr.contains("Rage Powder")
                        || dataStr.contains("Trick")
                        || dataStr.contains("Wide Guard")
                )) {
                    levelMoves.add(new LevelMove(dataStr));
                }
                dataStr = br.readLine();
            }

            //Close steams
            br.close();
            is.close();
        }
        catch (IOException exception) {}
    }

    private void loadEggMoves() {
        InputStream is = context.getResources().openRawResource(R.raw.data_learn_moves);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String dataStr = null;
        try {
            //Wait until ready
            while (dataStr == null) { dataStr = br.readLine(); }

            //Search for id
            while (!dataStr.contains(id)) { dataStr = br.readLine(); }

            //Search for egg moves
            while (!dataStr.contains("Egg Moves")) { dataStr = br.readLine(); }
            dataStr = br.readLine();

            //Load egg moves
            eggMoves = new Vector<>();
            while (!dataStr.isEmpty()) {
                if (!(dataStr.contains("Ally Switch")
                        || dataStr.contains("Assist")
                        || dataStr.contains("Beat Up")
                        || dataStr.contains("Bestow")
                        || dataStr.contains("Block")
                        || dataStr.contains("Fling")
                        || dataStr.contains("Follow Me")
                        || dataStr.contains("Frustration")
                        || dataStr.contains("Grass Knot")
                        || dataStr.contains("Happy Hour")
                        || dataStr.contains("Heat Crash")
                        || dataStr.contains("Heavy Slam")
                        || dataStr.contains("Helping Hand")
                        || dataStr.contains("Hold Hands")
                        || dataStr.contains("Mat Block")
                        || dataStr.contains("Rage Powder")
                        || dataStr.contains("Trick")
                )) {
                    eggMoves.add(new Move(dataStr));
                }
                dataStr = br.readLine();
            }

            //Close steams
            br.close();
            is.close();
        }
        catch (IOException exception) {}
    }

    private void calculateTotalStats() {
        //Calculate total stats for this level
        int totHp = calculateHp(baseStats.hp, ivStats.hp, evStats.hp);
        int totAtk = calculateStat(baseStats.atk, ivStats.atk, evStats.atk, natureStats.atk);
        int totDef = calculateStat(baseStats.def, ivStats.def, evStats.def, natureStats.def);
        int totSpAtk = calculateStat(baseStats.spAtk, ivStats.spAtk, evStats.spAtk, natureStats.spAtk);
        int totSpDef = calculateStat(baseStats.spDef, ivStats.spDef, evStats.spDef, natureStats.spDef);
        int totSpd = calculateStat(baseStats.spd, ivStats.spd, evStats.spd, natureStats.spd);
        totalStats = new Stats(totHp, totAtk, totDef, totSpAtk, totSpDef, totSpd);
        combatStats = new Stats(totHp, 0, 0, 0, 0, 0);
    }

    private int calculateStat(int base, int iv, int ev, int nat) {
        float stat = (((((2 * base) + iv + (ev / 4)) * level) / 100) + 5) * (nat / 100.0f);
        return (int) stat;
    }

    private int calculateHp(int base, int iv, int ev) {
        float hp = ((((2 * base) + iv + (ev / 4)) * level) / 100) + level + 10;
        return (int) hp;
    }

    public void resetCombatStats() {
        if (transformed) {
            transformed = false;
            transformAsset = "";

            //Reset variables
            nature = orgNat;
            ability = orgAbility;

            //Reset types
            types.clear();
            types.addAll(orgTypes);

            //Reset stats, except hp
            int ivHp = ivStats.hp;
            int evHp = evStats.hp;
            int baseHp = baseStats.hp;
            int natureHp = natureStats.hp;
            int totalHp = totalStats.hp;
            int combatHp = combatStats.hp;
            ivStats = orgIvs;
            evStats = orgEvs;
            baseStats = orgBase;
            natureStats = orgNature;
            totalStats = orgTotal;
            combatStats = orgCombat;
            ivStats.hp = ivHp;
            evStats.hp = evHp;
            baseStats.hp = baseHp;
            natureStats.hp = natureHp;
            totalStats.hp = totalHp;
            combatStats.hp = combatHp;

            //Reset moves
            moves.clear();
            moves.addAll(orgMoves);
        }

        accuracy = 0;
        evasion = 0;
        combatStats.atk = 0;
        combatStats.def = 0;
        combatStats.spAtk = 0;
        combatStats.spDef = 0;
        combatStats.spd = 0;
        critStage = 0;
        if (status != null) {
            if (status.name.equals("Badly poisoned")) {
                status.name = "Poison";
            }
        }
        if (confusion != null) {
            confusion = null;
        }

        fainted = false;
        wild = false;
        lastDamageTaken = 0;
        protect = 0;
        detect = 0;
        quickGuard = 0;
        endure = 0;
        flinched = false;
        stockpiled = 0;

        lastMove = null;
        for (LearnedMove move : moves) {
            move.disabledRounds = 0;
        }
    }

    public int restoreHp(Integer... amount) {
        int missingHp = totalStats.hp - combatStats.hp;
        int addHp = missingHp;
        if (amount.length > 0) { addHp = Math.min(missingHp, amount[0]); }
        combatStats.hp += addHp;
        return addHp;
    }

    public int restorePp(LearnedMove move, Integer... amount) {
        int missingPp = move.data.pp - move.ppCurrent;
        int addPp = missingPp;
        if (amount.length > 0) { addPp = Math.min(missingPp, amount[0]); }
        move.ppCurrent += addPp;
        return addPp;
    }

    public void recover() {
        fainted = false;
        restoreHp();
        for (LearnedMove move : moves) {
            restorePp(move);
        }
        status = null;
    }



    public String getSaveData() {
        String data = id
                + ":" + nickname
                + ":" + nature
                + ":" + level
                + ":" + ivStats.getSaveData()
                + ":" + evStats.getSaveData()
                + ":" + gender
                + ":" + fainted
                + ":" + moves.size();
        for (LearnedMove learnedMove : moves) {
            data += ":" + learnedMove.getSaveData();
        }
        return data;
    }

    public String getName(boolean... prefix) {
        String result = id;
        if (result.contains("_M")) { result = result.replace("_M", ""); }
        if (result.contains("_F")) { result = result.replace("_F", ""); }
        if (result.contains("_A")) { result = result.replace("_A", "'"); }
        if (result.contains("_P")) { result = result.replace("_P", ". "); }
        if (prefix.length > 0) {
            if (prefix[0] && wild) {
                result = "Wild " + result;
            }
        }
        return result;
    }

    private String getAssetName() {
        String result;
        int number = NUMBER_ID.valueOf(id).ordinal();
        if (number < 10) { result = "00" + number; }
        else if (number < 100) { result = "0" + number; }
        else { result = "" + number; }

        if (transformed) { result = transformAsset; }
        return result;
    }

    public String getGif() {
        return getAssetName() + ".gif";
    }

    public String getBackGif() {
        return getAssetName() + "b.gif";
    }

    public String getNature() {
        return nature;
    }

    public BitmapDescriptor getMarkerIcon(Context context) {
        //Get bitmap
        BitmapDescriptor bmpResult = null;
        try {
            InputStream is = context.getResources().getAssets().open(getAssetName() + "ic.png");
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

    public String getNickname() {
        String result = nickname;
        if (nickname.equals("")) { result = id; }
        return result;
    }

    public int getLevel() {
        return level;
    }

    public Stats getStats() {
        return totalStats;
    }

    public Stats getCurrentStats() {
        return combatStats;
    }

    public String getGender() {
        return gender;
    }

    public LearnedMove getLearnedMove(int moveId) {
        LearnedMove result = null;
        if (moveId < moves.size()) {
            LearnedMove move = moves.get(moveId);
            result = move;
        }
        return result;
    }

    public boolean isFainted() {
        return fainted;
    }

    public boolean isCaught() {
        return false;
    }

    public boolean ranAway() {
        return false;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public int getEvasion() {
        return evasion;
    }

    public String getStatus() {
        String result = null;
        if (status != null) {
            if (status.turnsDelayed <= 0) {
                result = status.name;
            }
        }
        return result;
    }

    public String setStage(String code, int stageChange) {
        String result = "";
        //Find relevant value
        int stage = 0;
        if (code.equals("Attack")) { stage = combatStats.atk; }
        if (code.equals("Defence")) { stage = combatStats.def; }
        if (code.equals("Special Attack")) { stage = combatStats.spAtk; }
        if (code.equals("Special Defence")) { stage = combatStats.spDef; }
        if (code.equals("Speed")) { stage = combatStats.spd; }
        if (code.equals("Accuracy")) { stage = accuracy; }
        if (code.equals("Evasion")) { stage = evasion; }

        //Clamp result
        if (stage + stageChange > 6) { stageChange = 6 - stage; }
        if (stage + stageChange < -6) { stageChange = -6 - stage; }

        //Apply change
        if (code.equals("Attack")) { combatStats.atk += stageChange; }
        if (code.equals("Defence")) { combatStats.def += stageChange; }
        if (code.equals("Special Attack")) { combatStats.spAtk += stageChange; }
        if (code.equals("Special Defence")) { combatStats.spDef += stageChange; }
        if (code.equals("Speed")) { combatStats.spd += stageChange; }
        if (code.equals("Accuracy")) { accuracy += stageChange; }
        if (code.equals("Evasion")) { evasion += stageChange; }

        //Print change result
        int newStage = stage + stageChange;
        String showName = getName() + "'s ";
        if (wild) { showName = "Wild " + showName; }
        if (stageChange <= -3) { result = showName + code + " severely fell!"; }
        if (stageChange == -2) { result = showName + code + " harshly fell!"; }
        if (stageChange == -1) { result = showName + code + " fell!"; }
        if (stageChange == 0) {
            if (stage == 6) { result = showName + code + " won't go any higher!"; }
            if (stage == -6) { result = showName + code + " won't go any lower!"; }
        }
        if (stageChange == 1) { result = showName + code + " rose!"; }
        if (stageChange == 2) { result = showName + code + " rose sharply!"; }
        if (stageChange >= 3) { result = showName + code + " rose drastically!"; }
        //Global.print("Apply " + stageChange + " to " + getName(true) + "'s " + code + ". New value is: " + newStage);
        return result;
    }

    public float getStageValue(String code) {
        float result = 0;
        int stage = 0;
        if (code.equals("Attack")) {
            result = totalStats.atk;
            stage = combatStats.atk;
        }
        if (code.equals("Defence")) {
            result = totalStats.def;
            stage = combatStats.def;
        }
        if (code.equals("Special Attack")) {
            result = totalStats.spAtk;
            stage = combatStats.spAtk;
        }
        if (code.equals("Special Defence")) {
            result = totalStats.spDef;
            stage = combatStats.spDef;
        }
        if (code.equals("Speed")) {
            result = totalStats.spd;
            stage = combatStats.spd;
        }
        if (code.equals("Accuracy")) {
            result = 100;
            stage = accuracy;
        }
        if (code.equals("Evasion")) {
            result = 100;
            stage = evasion;
        }

        //Find result
        if (code.equals("Accuracy") || code.equals("Evasion")) {
            int top = Math.max(2, 2 + stage);
            int bottom = Math.max(2, 2 - stage);
            float scale = top / (float)bottom;
            result *= scale;
            //Global.print(code + ": " + top + " / " + bottom + " = " + result);
        }
        else {
            int top = Math.max(3, 3 + stage);
            int bottom = Math.max(3, 3 - stage);
            float scale = top / (float)bottom;
            result *= scale;
            //Global.print(code + ": " + top + " / " + bottom + " = " + result);
        }
        return result;
    }

    public void setWild(boolean state) {
        wild = state;
    }

    public String getAbility() {
        return ability;
    }



    private void debugData() {
        String txt =
            "\nName: " + getName()
                    /*
            + "\nDescription: " + description
            + "\nLevel: " + level
            + "\nNature: " + getNature()
            + "\nGender: " + gender
            */
            + "\nAbility: " + ability;

                    /*
            + "\n\n\t\t HP,    ATK,    DEF,  SPATK,  SPDEF,    SPD"
            + "\nBas:\t" + debugStats(baseStats)
            + "\nNat:\t" + debugStats(natureStats)
            + "\nIVs:\t" + debugStats(ivStats)
            + "\nEVs:\t" + debugStats(evStats)
            + "\nTot:\t" + debugStats(totalStats)
            + "\nCom:\t" + debugStats(combatStats)
            + "\nYIL:\t" + debugStats(evYield)

            + "\n\nMoves:"
        ;
        for (int i = 0; i < MAX_MOVES; i++) {
            if (i < moves.size()) {
                LearnedMove move = moves.get(i);
                txt += "\n" + i + ": " + move.data.name + " (PP: " + move.ppCurrent + " / " + move.data.pp + ")";
            }
            else {
                txt += "\n" + i + ": Empty";
            }
        }

        txt += "\nSpecies: " + species
                + "\nHeight: " + height
                + "\nWeight: " + weight
                + "\nCatch rate: " + catchRate
                + "\nBase happiness: " + baseHappiness
                + "\nBase XP: " + baseXp
                + "\nGrowth rate: " + growthRate
                + "\nMale gender rate: " + genderRateMale + "%"
                + "\nEgg cycles: " + eggCycles;

        String typeStr = "";
        for (String type : types) {
            if (!typeStr.equals("")) { typeStr += ", "; }
            typeStr += type;
        }
        txt += "\nType: " + typeStr;

        String eggStr = "";
        for (String egg : eggGroups) {
            if (!eggStr.equals("")) { eggStr += ", "; }
            eggStr += egg;
        }
        txt += "\nEgg groups: " + eggStr;
        */

        Global.print(txt);
    }

    private String debugStats(Stats stats) {
        String result = "";

        for (int i = 0; i < 6; i++) {
            String tmp = "";
            if (i == 0) { tmp += stats.hp; }
            if (i == 1) { tmp += stats.atk; }
            if (i == 2) { tmp += stats.def; }
            if (i == 3) { tmp += stats.spAtk; }
            if (i == 4) { tmp += stats.spDef; }
            if (i == 5) { tmp += stats.spd; }

            while (tmp.length() < 3) { tmp = " " + tmp; }
            result += tmp + ",\t";
        }

        return result;
    }



    public String attack(Pokemon attacker, Move move) {
        String result = "";

        result += attackDamage(attacker, move);
        result += attackEffect(attacker, move);

        if (fainted && haveProtection("Endure")) {
            combatStats.hp = 1;
            fainted = false;
            result += "\n" + getName(true) + " endured the hit!";
        }

        if (result.equals("") && move.category.equals("Status")) { result = "\nNothing happened!"; }
        return result;
    }

    private String attackDamage(Pokemon attacker, Move move) {
        String result = "";
        //Prepare power
        if (move.name.equals("Spit Up")) {
            if (attacker.stockpiled > 0) {
                move.power = 100 * attacker.stockpiled;
                result += "\n" + attacker.getName(true) + " used it stored power to attack!";
            }
            else {
                result += "\nNo power was stored!";
            }
        }
        else if (move.name.equals("Low Kick")) {
            float w = Float.parseFloat(weight.substring(0, weight.indexOf(" kg")));
            if (w >= 200) { move.power = 120; }
            else if (w >= 100) { move.power = 100; }
            else if (w >= 50) { move.power = 80; }
            else if (w >= 25) { move.power = 60; }
            else if (w >= 10) { move.power = 40; }
            else { move.power = 20; }
        }

        //Check power
        if (move.power > 0) {
            //Get modifier information
            float typeAdv = 1;
            for (String type : types) {
                if (move.name.equals("Freeze-Dry") && type.equals("Water")) {
                    typeAdv *= 2;
                }
                else {
                    typeAdv *= getTypeAdvantage(move.type, type);
                }
            }
            float typeBoost = attacker.getTypeBoost(move.type);
            int bonusCritStages = 0;
            if (move.effect.contains("High critical")) { bonusCritStages = 1; }
            if (move.effect.contains("Always") && move.effect.contains("critical hit")) { bonusCritStages = 3; }
            float crit = attacker.rollCriticalHit(bonusCritStages);
            float other = 1;

            float attack = 0;
            float defence = 0;
            if (move.category.equals("Physical")) {
                attack = attacker.getStageValue("Attack");
                defence = getStageValue("Defence");
                if (move.name.equals("Foul Play")) { attack = getStageValue("Attack"); }
            }
            if (move.category.equals("Special")) {
                attack = attacker.getStageValue("Special Attack");
                defence = getStageValue("Special Defence");
            }
            int basePower = move.power;

            //Calculate damage
            float totPower = ((2 * attacker.getLevel() + 10) / 250f) * (attack / defence) * basePower + 2f;
            float randMod = typeBoost * typeAdv * crit * other * (Global.randomInt(85, 100) / 100.0f);
            float damage = totPower * randMod;
            if (damage < 1) { damage = 1; }

            //Apply damage
            dealDamage((int) damage);

            //Report effect
            if (typeAdv == 0) {
                result += "\nIt had no effect on " + getName(true) + "!";
            }
            else if (typeAdv < 1) {
                result += "\nIt was not very effective!";
            }
            else if (typeAdv > 1) {
                result += "\nIt was super effective!";
            }
            if (crit > 1) {
                result += "\nIt was a critical hit!";
            }
            //result += "\nIt did " + (int)damage + " damage!";

            /*
            float totMod = typeAdv * typeBoost * other;
            float totCritMod = totMod * 1.5f;

            float minDmg = totPower * (totMod * 0.85f);
            float maxDmg = totPower * (totMod * 1f);
            float minCritDmg = totPower * (totCritMod * 0.85f);
            float maxCritDmg = totPower * (totCritMod * 1f);

            String txt = move.name
                    + "\nFormula: ((2 * " + attacker.getLevel() + " + 10) / 250.0) * (" + attack + " / " + defence + ") * " + basePower + " + 2.0"
                    + "\nHit: " + (int)minDmg + " - " + (int)maxDmg + " (" + minDmg + " - " + maxDmg + ")"
                    + "\nCritical hit: " + (int)minCritDmg + " - " + (int)maxCritDmg + " (" + minCritDmg + " - " + maxCritDmg + ")"
                    + "\nDamage: " + (int)damage + " (" + damage + ")";
            Global.print(txt);
            */

            //Reset move power
            if (move.name.equals("Spit Up")) {
                //Drain stockpile
                move.power = 0;
                result += "\n" + attacker.setStage("Defence", -attacker.stockpiled);
                result += "\n" + attacker.setStage("Special Defence", -attacker.stockpiled);
                attacker.stockpiled = 0;
            }
            else if (move.name.equals("Low Kick")) {
                move.power = 0;
            }
        }

        return result;
    }

    private String attackEffect(Pokemon attacker, Move move) {
        String result = "";

        //Apply effects
        String searchStr = move.effect.toLowerCase();

        int effectChance = 100;
        if (searchStr.contains("may") && move.chance > 0) { effectChance = move.chance; }
        int effectRoll = Global.randomInt(1, 100);
        if (effectRoll <= effectChance) {
            if (move.name.equals("Dragon Rage")) {
                dealDamage(40);
            }
            else if (move.name.equals("Sonic Boom")) {
                dealDamage(20);
            }
            else if (move.name.equals("Curse")) {
                if (attacker.hasType("Ghost")) {
                    float damage = attacker.getStats().hp / 2f;
                    attacker.dealDamage((int)damage);
                    result += "\n" + attacker.getName(true) + " lost half of it's health!";
                    result += "\n" + attacker.getName(true) + " put a curse on " + getName(true) + "!";
                    //Todo: Curse
                }
                else {
                    result += "\n" + attacker.setStage("Attack", 1);
                    result += "\n" + attacker.setStage("Defence", 1);
                    result += "\n" + attacker.setStage("Speed", -1);
                }
            }
            else if (move.name.equals("Stockpile")) {
                if (attacker.stockpiled < 3) {
                    result += "\n" + attacker.getName(true) + " charges up power!";
                    result += "\n" + attacker.setStage("Defence", 1);
                    result += "\n" + attacker.setStage("Special Defence", 1);
                    attacker.stockpiled++;
                }
                else {
                    result += "\n" + attacker.getName(true) + " is unable to store more power!";
                }
            }
            else if (move.name.equals("Swallow")) {
                if (attacker.stockpiled > 0) {
                    //Determine power
                    float healPercent = 0;
                    switch (attacker.stockpiled) {
                        case 1: { healPercent = 0.25f; break; }
                        case 2: { healPercent = 0.5f; break; }
                        case 3: { healPercent = 1; break; }
                    }

                    //Recover
                    attacker.healHealth((int)(attacker.getStats().hp * healPercent));
                    result += "\n" + attacker.getName(true) + " used it stored power to recover!";

                    //Drain stockpile
                    result += "\n" + attacker.setStage("Defence", -attacker.stockpiled);
                    result += "\n" + attacker.setStage("Special Defence", -attacker.stockpiled);
                    attacker.stockpiled = 0;
                }
                else {
                    result += "\nNo power was stored!";
                }
            }
            else if (move.name.equals("Wish")) {

            }
            else if (move.name.equals("Captivate")) {
                if (!gender.equals(attacker.gender)) {
                    if (!gender.equals("") && !attacker.gender.equals("")) {
                        if (!ability.equals("Oblivious")) {
                            result += "\n" + setStage("Special Attack", -2);
                        }
                    }
                }
            }
            else if (move.name.equals("Flower Shield")) {
                if (attacker.hasType("Grass")) {
                    result += "\n" + attacker.setStage("Defence", 2);
                }
                if (hasType("Grass")) {
                    result += "\n" + setStage("Defence", 1);
                }
            }
            else if (move.name.equals("Protect")) {
                attacker.protect = 1;
                result += "\n" + attacker.getName(true) + " prepares to block an attack!";
            }
            else if (move.name.equals("Detect")) {
                attacker.detect = 1;
                result += "\n" + attacker.getName(true) + " prepares to block an attack!";
            }
            else if (move.name.equals("Quick Guard")) {
                attacker.quickGuard = 1;
                result += "\n" + attacker.getName(true) + " prepares to block a fast attack!";
            }
            else if (move.name.equals("Endure")) {
                attacker.endure = 1;
                result += "\n" + attacker.getName(true) + " prepares to take a hit!";
            }
            else if (move.name.equals("Acupressure")) {
                String stat = Global.choose(new String[] {"Attack", "Defence", "Special Attack", "Special Defence", "Speed"});
                result += "\n" + setStage(stat, 2);
            }
            else if (move.name.equals("Safeguard")) {
                if (attacker.status == null) {
                    attacker.status = new Status();
                    attacker.status.name = move.name;
                    attacker.status.turns = 5;
                    attacker.status.turnsLeft = attacker.status.turns;
                }
                else {
                    result += "\n" + attacker.getName(true) + " already has a status condition!";
                    result += "\n" + move.name + " had no effect!";
                }
            }
            else if (move.name.equals("Fell Stringer")) {

            }
            else if (move.name.equals("Focus Energy")) {
                int totCritStage = critStage;
                if (ability.equals("Super Luck")) { totCritStage++; }
                totCritStage = Math.min(totCritStage, 3);

                if (totCritStage == 3) {
                    result += "\n" + attacker.getName(true) + " is already at max focus!";
                }
                else {
                    critStage++;
                    result += "\n" + attacker.getName(true) + " is gathering focus!";
                    result += "\nCritical hits land more easily!";
                }
            }
            else if (move.name.equals("Flatter")) {
                result += confuse();
                if (!result.equals("")) {
                    setStage("Special Attack", 2);
                }
            }
            else if (move.name.equals("Swagger")) {
                result += confuse();
                if (!result.equals("")) {
                    setStage("Attack", 2);
                }
            }
            else if (move.name.equals("Outrage") || move.name.equals("Pedal Dance") || move.name.equals("Thrash")) {

            }
            else if (move.name.equals("Teeter Dance")) {
                result += confuse();
            }
            else if (move.name.equals("Disable")) {
                boolean canDisable = true;
                for (LearnedMove m : moves) {
                    if (m.disabledRounds > 0) {
                        canDisable = false;
                        break;
                    }
                }

                if (lastMove != null && canDisable) {
                    lastMove.disabledRounds = Global.randomInt(1, 8);
                    result += "\n" + lastMove.data.name + " was disabled!";
                }
                else {
                    result += "\nBut it failed!";
                }
            }
            else {
                //General move check
                if (searchStr.contains("confuse")) {
                    result += confuse();
                }
                if (searchStr.contains("flinching")) {
                    flinched = true;
                }
                if (searchStr.contains("raise")) {
                    //Effect has stat increase, find level
                    int stages = 1;
                    if (searchStr.contains("sharply")) {
                        stages = 2;
                    }
                    if (searchStr.contains("drastically")) {
                        stages = 3;
                    }

                    //Do the stat changes apply to the user?
                    boolean user = true;
                    if (searchStr.contains("opponent") || searchStr.contains("target")) {
                        user = false;
                    }

                    //Find chance of effect
                    int chance = 100;
                    if (move.chance > 0) {
                        chance = move.chance;
                    }

                    int roll = Global.randomInt(1, 100);
                    if (roll <= chance) {
                        //Find affected stats
                        int start = searchStr.indexOf("raise");
                        int end = searchStr.indexOf("lower");
                        String findStr;
                        if (end >= 0) {
                            findStr = searchStr.substring(start, end);
                        }
                        else {
                            findStr = searchStr.substring(start);
                        }
                        Vector<String> stats = findStatsIn(findStr);
                        for (String stat : stats) {
                            //Apply change
                            if (user) { result += "\n" + attacker.setStage(stat, stages); }
                            else { result += "\n" + setStage(stat, stages); }
                        }
                    }
                }
                if (searchStr.contains("lower")) {
                    //Effect has stat decrease, find level
                    int stages = -1;
                    if (searchStr.contains("sharply")) {
                        stages = -2;
                    }

                    //Do the stat changes apply to the user?
                    boolean user = false;
                    if (searchStr.contains("user")) {
                        user = true;
                    }

                    //Find chance of effect
                    int chance = 100;
                    if (move.chance > 0) {
                        chance = move.chance;
                    }

                    int roll = Global.randomInt(1, 100);
                    if (roll <= chance) {
                        //Find affected stats
                        String findStr = searchStr.substring(searchStr.indexOf("lower"));
                        Vector<String> stats = findStatsIn(findStr);
                        for (String stat : stats) {
                            //Apply change
                            if (user) { result += "\n" + attacker.setStage(stat, stages); }
                            else { result += "\n" + setStage(stat, stages); }
                        }
                    }
                }
                if (status == null) {
                    if (searchStr.contains("freeze")) {
                        status = new Status();
                        status.name = "Freeze";
                        result += "\n" + getName(true) + " was frozen solid!";
                    }
                    else if (searchStr.contains("sleep")) {
                        status = new Status();
                        status.name = "Sleep";
                        status.turns = Global.randomInt(1, 5);
                        status.turnsLeft = status.turns;
                        if (move.name.equals("Yawn")) {
                            status.turnsDelayed = 2;
                            result += "\n" + getName(true) + " began to feel sleepy!";
                        }
                        else if (move.name.equals("Rest")) {
                            status.turns = 2;
                            status.turnsLeft = status.turns;
                            attacker.status = status;
                            status = null;
                            attacker.healHealth(attacker.getStats().hp);
                            result += "\n" + attacker.getName(true) + " recovered it's hp by resting!";
                        }
                        else {
                            result += "\n" + getName(true) + " fell asleep!";
                        }
                    }
                    else if (searchStr.contains("burn")) {
                        status = new Status();
                        status.name = "Burn";
                        result += "\n" + getName(true) + " started burning!";
                    }
                    else if (searchStr.contains("badly poison")) {
                        status = new Status();
                        status.name = "Badly poisoned";
                        result += "\n" + getName(true) + " was badly poisoned!";
                    }
                    else if (searchStr.contains("poison")) {
                        status = new Status();
                        status.name = "Poison";
                        result += "\n" + getName(true) + " was poisoned!";
                    }
                    else if (searchStr.contains("paralyze")) {
                        status = new Status();
                        status.name = "Paralysis";
                        result += "\n" + getName(true) + " became paralyzed!";
                    }
                }
                else if (searchStr.contains("One-Hit-KO")) {
                    int hitChance = attacker.level - level + 30;
                    int roll = Global.randomInt(1, 100);
                    if (roll <= hitChance) {
                        result += "\nIt was a One-Hit-KO!";
                        dealDamage(totalStats.hp);
                    }
                    else {
                        result += "\nBut the attack failed!";
                    }
                }
            }
        }

        return result;
    }

    private float getTypeAdvantage(String atkType, String defType) {
        final float I = 0;
        final float H = 0.5f;
        final float N = 1;
        final float D = 2;
        final float[][] table = {
            //   n, f, w, e, g, i, f, p, g, f, p, b, r, g, d, d, s, f
                {N, N, N, N, N, N, N, N, N, N, N, N, H, I, N, N, H, N}, // n
                {N, H, H, N, D, D, N, N, N, N, N, D, H, N, H, N, D, N}, // f
                {N, D, H, N, H, N, N, N, D, N, N, N, D, N, H, N, N, N}, // w
                {N, N, D, H, H, N, N, N, I, D, N, N, N, N, H, N, N, N}, // e
                {N, H, D, N, H, N, N, H, D, H, N, H, D, N, H, N, H, N}, // g
                {N, H, H, N, D, H, N, N, D, D, N, N, N, N, D, N, H, N}, // i
                {D, N, N, N, N, D, N, H, N, H, H, H, D, I, N, D, D, H}, // f
                {N, N, N, N, D, N, N, H, H, N, N, N, H, H, N, N, I, D}, // p
                {N, D, N, D, H, N, N, D, N, I, N, H, D, N, N, N, D, N}, // g
                {N, N, N, H, D, N, D, N, N, N, N, D, H, N, N, N, H, N}, // f
                {N, N, N, N, N, N, D, D, N, N, H, N, N, N, N, I, H, N}, // p
                {N, H, N, N, D, N, H, H, N, H, D, N, N, H, N, D, H, H}, // b
                {N, D, N, N, N, D, H, N, H, D, N, D, N, N, N, N, H, N}, // r
                {I, N, N, N, N, N, N, N, N, N, D, N, N, D, N, H, N, N}, // g
                {N, N, N, N, N, N, N, N, N, N, N, N, N, N, D, N, H, I}, // d
                {N, N, N, N, N, N, H, N, N, N, D, N, N, D, N, H, N, H}, // d
                {N, H, H, H, N, D, N, N, N, N, N, N, D, N, N, N, H, D}, // s
                {N, H, N, N, N, N, D, H, N, N, N, N, N, N, D, D, H, N}  // f
        };

        //Get attack type array
        final int atkId = getTypeId(atkType);
        float[] array = table[atkId];

        //Print array
        /*
        String arrayStr = "";
        for (float v : array) {
            arrayStr += ", " + v;
        }
        Global.print("       Nor, Fir, Wat, Ele, Gra, Ice, Fig, Poi, Gro, Fly, Psy, Bug, Roc, Gho, Dra, Dar, Ste, Fai");
        Global.print("Array: " + arrayStr.substring(2));
        */

        //Get defend type slot
        final int defId = getTypeId(defType);
        float result = array[defId];

        //Return type adv.
        return result;
    }

    private int getTypeId(String type) {
        int result = 0;
        if (type.equals("Normal"))   { result = 0; }
        if (type.equals("Fire"))     { result = 1; }
        if (type.equals("Water"))    { result = 2; }
        if (type.equals("Electric")) { result = 3; }
        if (type.equals("Grass"))    { result = 4; }
        if (type.equals("Ice"))      { result = 5; }
        if (type.equals("Fighting")) { result = 6; }
        if (type.equals("Poison"))   { result = 7; }
        if (type.equals("Ground"))   { result = 8; }
        if (type.equals("Flying"))   { result = 9; }
        if (type.equals("Psychic"))  { result = 10; }
        if (type.equals("Bug"))      { result = 11; }
        if (type.equals("Rock"))     { result = 12; }
        if (type.equals("Ghost"))    { result = 13; }
        if (type.equals("Dragon"))   { result = 14; }
        if (type.equals("Dark"))     { result = 15; }
        if (type.equals("Steel"))    { result = 16; }
        if (type.equals("Fairy"))    { result = 17; }
        return result;
    }

    private float getTypeBoost(String type) {
        float result = 1;
        for (String t : types) {
            if (t.equals(type)) { result = 1.5f; }
        }
        return result;
    }

    private float rollCriticalHit(int bonusStages) {
        final int[] STAGES = {16, 8, 2, 1};
        int totCritStage = critStage + bonusStages;
        if (ability.equals("Super Luck")) { totCritStage++; }
        totCritStage = Math.min(totCritStage, 3);

        float result = 1;
        int roll = Global.randomInt(1, STAGES[totCritStage]);
        if (roll == 1) {
            result = 1.5f;
            if (ability.equals("Sniper")) { result *= 1.5f; }
        }
        return result;
    }

    private Vector<String> findStatsIn(String searchStr) {
        Vector<String> result = new Vector<>();

        for (int i = 0; i < searchStr.length(); i++) {
            if (searchStr.indexOf("attack") == i) { result.add("Attack"); i += result.lastElement().length(); }
            if (searchStr.indexOf("defense") == i) { result.add("Defence"); i += result.lastElement().length(); }
            if (searchStr.indexOf("special attack") == i) { result.add("Special Attack"); i += result.lastElement().length(); }
            if (searchStr.indexOf("special defense") == i) { result.add("Special Defence"); i += result.lastElement().length(); }
            if (searchStr.indexOf("speed") == i) { result.add("Speed"); i += result.lastElement().length(); }
            if (searchStr.indexOf("accuracy") == i) { result.add("Accuracy"); i += result.lastElement().length(); }
            if (searchStr.indexOf("evasiveness") == i) { result.add("Evasion"); i += result.lastElement().length(); }
        }

        return result;
    }

    public Move useMove(int moveId) {
        Move result = null;

        if (moveId < moves.size()) {
            LearnedMove move = moves.get(moveId);
            if (move.ppCurrent > 0 && move.disabledRounds == 0) {
                result = move.data;
            }

            if (result == null) {
                boolean struggle = false;
                if (move.disabledRounds > 0) {
                    boolean hasValidMove = false;
                    for (LearnedMove m : moves) {
                        if (m.ppCurrent > 0 && m.disabledRounds == 0) {
                            hasValidMove = true;
                            break;
                        }
                    }

                    if (!hasValidMove) {
                        struggle = true;
                    }
                }
                else {
                    struggle = true;
                }

                if (struggle) {
                    //Struggle
                    result = new Move();
                    result.name = "Struggle";
                    result.power = 40;
                    result.accuracy = 100;
                    result.pp = 1;
                    result.type = "";
                }
            }
        }

        return result;
    }

    public Move useRandomMove() {
        Move result = null;

        Vector<LearnedMove> moveChoice = new Vector<>();
        moveChoice.addAll(moves);

        while (moveChoice.size() > 0 && result == null) {
            int randId = Global.randomInt(0, moveChoice.size() - 1);
            LearnedMove move = moveChoice.remove(randId);
            if (move.ppCurrent > 0) {
                move.ppCurrent--;
                result = move.data;
            }
        }

        if (result == null) {
            //Struggle
        }

        return result;
    }

    public String endMove(Move move, boolean used) {
        String result = "";

        //Check for status pain
        if (status != null) {
            if (status.name.equals("Poison")) {
                float damage = totalStats.hp / 8f;
                dealDamage((int)damage);
                result = getName(true) + " was hurt by poison!";
            } else if (status.name.equals("Badly poisoned")) {
                if (status.turns < 16) {
                    status.turns++;
                }
                float damage = (totalStats.hp / 16f) * status.turns;
                dealDamage((int)damage);
                result = getName(true) + " was hurt by poison!\n(The poison seems to intensify!)";
            } else if (status.name.equals("Burn")) {
                float damage = totalStats.hp / 8f;
                dealDamage((int)damage);
                result = getName(true) + " was hurt by burn!";
            } else if (status.name.equals("Sleep")) {
                if (status.turnsDelayed > 0) {
                    status.turnsDelayed--;
                    if (status.turnsDelayed <= 0) {
                        result = getName(true) + " fell asleep!";
                    }
                }
            }
        }

        if (used) {
            lastMove = null;
            for (LearnedMove m : moves) {
                //Check if move used
                if (m.data.equals(move)) {
                    lastMove = m;
                    lastMove.ppCurrent--;
                }

                //Check for disable
                if (m.disabledRounds > 0) {
                    m.disabledRounds--;
                    if (m.disabledRounds == 0) {
                        if (!result.equals("")) { result += "\n"; }
                        result += getName(true) + "'s " + m.data.name + " is no longer disabled!";
                    }
                }
            }
        }

        return result;
    }

    public String startMove() {
        String result = "";

        if (status != null) {
            if (status.name.equals("Sleep")) {
                if (status.turnsDelayed <= 0) {
                    status.turnsLeft--;
                    if (status.turnsLeft < 0) {
                        status = null;
                        result = getName(true) + " woke up!";
                    } else {
                        result = getName(true) + " is still asleep!";
                    }
                }
            } else if (status.name.equals("Freeze")) {
                int roll = Global.randomInt(1, 5);
                if (roll == 1) {
                    status = null;
                } else {
                    result = getName(true) + " is still frozen!";
                }
            } else if (status.name.equals("Paralysis")) {
                int roll = Global.randomInt(1, 4);
                if (roll == 1) {
                    result = getName(true) + " is paralyzed and unable to move!";
                }
            }
        }
        if (confusion != null) {
            confusion.turnsLeft--;
            if (confusion.turnsLeft <= -1) {
                confusion = null;
                result = getName(true) + " snapped out of the confusion!";
            }
            else {
                result = getName(true) + " is confused!";
            }
        }

        protect = 0;
        detect = 0;
        quickGuard = 0;
        endure = 0;

        return result;
    }

    private void dealDamage(int amount) {
        //Apply damage
        combatStats.hp -= amount;
        lastDamageTaken = amount;

        //Check for faint
        if (combatStats.hp <= 0) {
            combatStats.hp = 0;
            fainted = true;
        }
    }

    public void healHealth(int amount) {
        //Apply min heal
        amount = Math.min(totalStats.hp - combatStats.hp, amount);
        combatStats.hp += amount;
    }

    public void updateStatusDelay() {
        if (status != null) {
            if (status.turnsDelayed > 0) { status.turnsDelayed--; }
        }
    }

    public int getLastDamageTaken() {
        return lastDamageTaken;
    }

    public boolean hasType(String type) {
        boolean result = false;
        for (String t : types) {
            if (t.equals(type)) { result = true; break; }
        }
        return result;
    }

    public boolean haveUsed(String moveGroup) {
        boolean result = false;
        if (moveGroup.equals("Protection")) {
            if (protect > 0 || detect > 0 || quickGuard > 0 || endure > 0) {
                result = true;
            }
        }

        protect = 0;
        detect = 0;
        quickGuard = 0;
        endure = 0;

        return result;
    }

    public boolean haveProtection(String move) {
        boolean result = false;
        if (move.equals("Protect") && protect == 1) {
            result = true;
            protect++;
        }
        if (move.equals("Detect") && detect == 1) {
            result = true;
            detect++;
        }
        if (move.equals("Quick Guard") && quickGuard == 1) {
            result = true;
            quickGuard++;
        }
        if (move.equals("Endure") && endure == 1) {
            result = true;
            endure++;
        }

        return result;
    }

    public void endOfTurn() {
        flinched = false;
        lastDamageTaken = 0;
    }

    public boolean isFlinched() {
        return flinched;
    }

    public boolean hitThisTurn() {
        return lastDamageTaken > 0;
    }

    private String confuse() {
        String result = "";
        if (confusion == null) {
            confusion = new Status();
            confusion.name = "Confusion";
            confusion.turns = Global.randomInt(1, 4);
            confusion.turnsLeft = confusion.turns;
            result += "\n" + getName(true) + " became confused!";
        }
        return result;
    }

    public void hurtSelf() {
        Move hitSelf = new Move();
        hitSelf.name = "Confused Hit";
        hitSelf.power = 40;
        hitSelf.type = "";
        hitSelf.pp = 1;
        hitSelf.effect = "Hurt it self in it's confusion!";
        hitSelf.accuracy = 100;
        hitSelf.chance = 100;
        hitSelf.category = "Physical";

        attack(this, hitSelf);
    }

    public void transform(Pokemon target) {
        resetCombatStats();
        transformed = true;
        transformAsset = target.getAssetName();

        //Copy variables
        nature = target.nature;
        ability = target.ability;

        //Copy type
        types.clear();
        types.addAll(target.types);

        //Copy stats, except hp
        int ivHp = ivStats.hp;
        int evHp = evStats.hp;
        int baseHp = baseStats.hp;
        int natureHp = natureStats.hp;
        int totalHp = totalStats.hp;
        int combatHp = combatStats.hp;
        ivStats = copyStats(target.ivStats);
        evStats = copyStats(target.evStats);
        baseStats = copyStats(target.baseStats);
        natureStats = copyStats(target.natureStats);
        totalStats = copyStats(target.totalStats);
        combatStats = copyStats(target.combatStats);
        ivStats.hp = ivHp;
        evStats.hp = evHp;
        baseStats.hp = baseHp;
        natureStats.hp = natureHp;
        totalStats.hp = totalHp;
        combatStats.hp = combatHp;

        //Backup old moves and copy
        orgMoves.clear();
        orgMoves.addAll(moves);
        moves.clear();
        for (LearnedMove move : target.moves) {
            LearnedMove copy = new LearnedMove(move.data);
            copy.ppCurrent = move.ppCurrent;
            moves.add(copy);
        }
    }

    private Stats copyStats(Stats src) {
        Stats copy = new Stats(0, 0, 0, 0, 0, 0);
        copy.hp = src.hp;
        copy.atk = src.atk;
        copy.def = src.def;
        copy.spAtk = src.spAtk;
        copy.spDef = src.spDef;
        copy.spd = src.spd;
        return copy;
    }

}