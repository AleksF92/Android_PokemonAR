package net.ledii.pokemon;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

public class Player {
    private Context context;
    private String nickname;
    private String email;
    private Vector<Pokemon> party;
    private final int MAX_POKEMON = 6;
    private float distBattle, distIdentify, distSpawn;
    //private Pokedex pokedex;

    Player(Context context) {
        this.context = context;
        party = new Vector<>();

        SharedPreferences prefs = context.getSharedPreferences("com.example.app", Context.MODE_PRIVATE);
        String playerStr = prefs.getString("player", null);
        if (playerStr == null || true) {
            //Init player
            nickname = "Ledii";
            email = "aleks_f92@hotmail.com";
            giveRandomPokemon();

            //Save
            save();
        }
        else {
            //Load player
            String playerData[] = playerStr.split("/");
            nickname = playerData[0];
            email = playerData[1];
            int partySize = Integer.parseInt(playerData[2]);
            for (int i = 0; i < partySize; i++) {
                String pokeStr = playerData[3 + i];
                party.add(new Pokemon(context, pokeStr));
            }
        }

        initDistances();
    }

    public void save() {
        String saveStr = nickname + "/" + email + "/" + party.size();
        for (Pokemon pokemon : party) {
            saveStr += "/" + pokemon.getSaveData();
        }

        SharedPreferences prefs = context.getSharedPreferences("com.example.app", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("player", saveStr);
        editor.commit();
    }

    public Pokemon getFirstPokemon() {
        return party.get(0);
    }

    public boolean unableToBattle() {
        boolean result = true;
        for (Pokemon pokemon : party) {
            if (!pokemon.isFainted()) {
                result = false;
            }
        }
        return result;
    }

    public void giveRandomPokemon() {
        //Clear any other pokemon
        party.clear();

        //Give random starter
        party.add(new Pokemon(context, Global.choose(new String[] {"Bulbasaur", "Charmander", "Squirtle"}), 5));

        //Give totally random
        //int roll = Global.randomInt(1, 151);
        //String randId = Pokemon.NUMBER_ID.values()[roll].name();
        //party.add(new Pokemon(context, randId, 5));
    }

    private void initDistances() {
        distBattle = 25;
        distIdentify = 50;
        distSpawn = 400;
    }

    public float getDistance(String type) {
        float result = 0;
        switch (type) {
            case "Battle": { result = distBattle; break; }
            case "Identify": { result = distIdentify; break; }
            case "Spawn": { result = distSpawn; break; }
        }
        return result;
    }
}