package net.ledii.pokemon;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.format.Time;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MapMode implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, LocationListener {
    private class AreaData {
        String streetName, cityName, biomeType, weather;
        float temperature;
        boolean isDay, isWater, isWarm, isRain;
        long lastWeatherTick;
        String lineOne;

        public void getData() {
            //Start loading data
            final TextView txtDebug = (TextView)((Activity)context).findViewById(R.id.txtDebug);
            txtDebug.setTextColor(Color.parseColor("#FFFF00"));

            //Load in background
            Thread netThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //Get address and weather
                    String preStreet = streetName;
                    _getAddress();

                    long tick = android.os.SystemClock.uptimeMillis();
                    long ticksPassed = tick - lastWeatherTick;
                    long OUTDATE_WEATHER = (1000 * 60) * 5;
                    if (!streetName.equals(preStreet) || ticksPassed >= OUTDATE_WEATHER) {
                        //Only use data if needed
                        lastWeatherTick = tick;
                        _getWeather();
                    }

                    //Update location conditions
                    if (weather != null) {
                        _getConditions();
                    }

                    //Display discovered data
                    String tempStr = "?";
                    if (temperature != -999) { tempStr = "" + temperature; }
                    final String debugStr = "Place name: " + streetName + " / " + cityName
                            + "\nWeather: " + weather + " (" + tempStr + " °C)"
                            + "\n\nBiome: " + biomeType
                            + "\nDaytime: " + isDay
                            + "\nWarm: " + isWarm
                            + "\nRain: " + isRain
                            + "\nWater: " + isWater
                            + "\n\nLine One: " + lineOne;
                    ;
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtDebug.setText(debugStr);
                            txtDebug.setTextColor(Color.parseColor("#0000FF"));
                        }
                    });
                }
            });
            netThread.start();
        }

        private void _getAddress() {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocation(lastLoc.latitude, lastLoc.longitude, 1);
                if (addressList.size() > 0) {
                    Address address = addressList.get(0);

                    //Get street name, if not read error
                    if (streetName == null || address.getThoroughfare() != null) {
                        streetName = address.getThoroughfare();
                    }

                    //Get city name, if not read error
                    if (cityName == null || address.getLocality() != null) {
                        cityName = address.getLocality();
                    }

                    lineOne = address.getAddressLine(0);

                    //Get nearest city coords
                    if (cityName != null) {
                        List<Address> cityAddressList = geocoder.getFromLocationName(cityName, 1);
                        if (cityAddressList.size() > 0) {
                            Address cityAddress = cityAddressList.get(0);
                            /*
                            Global.print(
                                    "Nearest city: " + cityName
                                    + "\nLat: " + cityAddress.getLatitude()
                                    + "\nLng: " + cityAddress.getLongitude()
                            );*/
                        }
                    }
                }
            }
            catch (IOException e) {}
        }

        private void _getWeather() {
            try {
                //Keep current weather as default
                String preWeather = weather;

                //Connect to weather service
                String weatherUrl = "https://www.wunderground.com/cgi-bin/findweather/getForecast?query=" + lastLoc.latitude + "," + lastLoc.longitude;
                Document doc = Jsoup.connect(weatherUrl).timeout(5000).get();

                if (doc != null) {
                    //Get weather
                    weather = doc.select("#curCond .wx-value").first().ownText();

                    //Get temperature
                    temperature = Float.parseFloat(doc.select("#curTemp .wx-value").first().ownText());
                }

                if (doc == null || weather == null) {
                    //No data found or timeout
                    if (preWeather != null) {
                        weather = preWeather;
                    }
                    else {
                        weather = "Unknown";
                        temperature = -999;
                    }
                }
            }
            catch (IOException exception) {}
        }

        private void _getConditions() {
            //Is it daytime?
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            if (hour >= 6 && hour < 18) { isDay = true; }

            //Is it warm?
            if (temperature > 18) { isWarm = true; }

            //Is it rain?
            if (weather.contains("Rain")) { isRain = true; }

            //Is it water nearby?
            if (false) { isWater = true; }

            //What type of terrain is it?
            if (true) {
                biomeType = "City";
            }
            else if (false) {
                biomeType = "Fields";
            }
            else {
                biomeType = "Forest";
            }
        }
    }

    //System variables
    private Context context;
    private GoogleApiClient apiClient;
    private SensorManager sensorManager;
    private SensorEventListener sensorListener;
    private GoogleMap map;
    private AreaData areaData;

    //Helper variables
    private LatLng lastLoc;
    private float lastBearing;
    private final float ZOOM = 18.0f;
    private boolean readyToShow;

    //Spawning variables
    private class Spawn {
        Marker marker;
        Pokemon pokemon;
        Date date;
        String state;
        int numMinutes;
    }
    private Vector<Spawn> spawns;
    private SimpleDateFormat dateFormat;
    private final int MAX_SPAWN = 3;

    //Other variables
    private Player player;
    private Pokemon battleWild;
    private Player battleTrainer;

    MapMode(Context context) {
        //Set system data
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", context.getResources().getConfiguration().locale);
        spawns = new Vector<>();

        //Load player
        player = new Player(context);

        //Load map in background
        FragmentActivity activity = (FragmentActivity) context;
        FragmentManager fm = activity.getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.fragMap);
        mapFragment.getMapAsync(this);
        readyToShow = false;

        areaData = new AreaData();
    }



    //Events
    public void resume() {

    }

    public void stop() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //Map settings
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setBuildingsEnabled(true);
        map.setIndoorEnabled(false);
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setAllGesturesEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setCompassEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setIndoorLevelPickerEnabled(false);
        map.setOnMapLongClickListener(this);
        map.setOnMarkerClickListener(this);

        //Ask for location updates
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(context);
        builder.addApi(LocationServices.API);
        builder.addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                setLocationUpdates(true);
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        });
        builder.addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {

            }
        });
        apiClient = builder.build();
        apiClient.connect();
    }

    public void setLocationUpdates(boolean enabled) {
        if (enabled) {
            //Check permission
            boolean hasPermission = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    hasPermission = false;
                }
            }

            if (hasPermission) {
                //Check if location is available
                Location location = LocationServices.FusedLocationApi.getLastLocation(apiClient);
                if (location != null) {
                    onLocationChanged(location);
                }
                lastBearing = 0.0f;

                //Set update rules
                LocationRequest locRequest = LocationRequest.create();
                locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locRequest.setInterval(5000);

                //Start updates
                map.setMyLocationEnabled(true);
                LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, locRequest, this);

                //Start rotate sensor
                sensorListener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                            onRotationUpdate(event.values);
                        }
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                };
                Sensor rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
                sensorManager.registerListener(sensorListener, rotation, SensorManager.SENSOR_DELAY_GAME);
            }
        }
        else {
            //Stop updates
            LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
        }
    }

    @Override
    public void onMapLongClick(LatLng position) {
        //Create pokemon
        spawnPokemon(position);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //Find spawn with marker
        for (Spawn spawn : spawns) {
            if (spawn.marker.equals(marker)) {
                //Check range
                if (spawn.state.equals("Very close")) {
                    //Enter battle
                    spawn.pokemon.setWild(true);
                    battleWild = spawn.pokemon;
                }
                break;
            }
        }
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        boolean firstUpdate = (lastLoc == null);

        //Update location
        lastLoc = new LatLng(location.getLatitude(), location.getLongitude());

        if (firstUpdate) {
            //Load recent spawns
            loadSpawns();
        }
        areaData.getData();

        //Update spawns
        if (readyToShow) {
            for (int i = 0; i < spawns.size(); i++) {
                Spawn spawn = spawns.get(i);

                //Check timeout
                if (Global.isTimedOut(spawn.date, spawn.numMinutes)) {
                    despawn(spawn);
                }
                else {
                    //Check distance
                    float dist = Global.getDistance(lastLoc, spawn.marker.getPosition());
                    if (dist < player.getDistance("Battle")) {
                        //Close enough to battle
                        if (!spawn.state.equals("Very close")) {
                            spawn.marker.setAlpha(1);
                            spawn.marker.setIcon(spawn.pokemon.getMarkerIcon(context));
                            spawn.state = "Very close";
                        }
                    } else if (dist < player.getDistance("Identify")) {
                        //Visible
                        if (!spawn.state.equals("Visible")) {
                            spawn.marker.setAlpha(0.25f);
                            spawn.marker.setIcon(spawn.pokemon.getMarkerIcon(context));
                            spawn.state = "Visible";
                        }
                    } else if (dist < player.getDistance("Spawn")) {
                        //Far away
                        if (!spawn.state.equals("Far away")) {
                            spawn.marker.setAlpha(1);
                            spawn.marker.setIcon(Global.getUnknownIcon(context));
                            spawn.state = "Far away";
                        }
                    } else {
                        //Out of spawn boundries
                        despawn(spawn);
                    }
                }
            }
        }
    }

    private void onRotationUpdate(float[] rotationVec) {
        //Find new rotation
        float[] mRotationMatrix = new float[16];
        SensorManager.getRotationMatrixFromVector(mRotationMatrix, rotationVec);
        float[] orientation = new float[3];
        SensorManager.getOrientation(mRotationMatrix, orientation);
        float newBearing = (float) Math.toDegrees(orientation[0]);
        float diffBearing = newBearing - lastBearing;
        lastBearing = newBearing;

        if (lastLoc != null) {
            //Force update
            CameraPosition camPos = new CameraPosition(lastLoc, ZOOM, 0.0f, lastBearing);
            CameraUpdate camUpd = CameraUpdateFactory.newCameraPosition(camPos);
            map.moveCamera(camUpd);

            if (!readyToShow) {
                readyToShow = true;
                setVisible(true);
            }
        }
    }



    //Spawning mechanics
    private String choosePokemon() {
        /*
        final String[] ALL = {
                "Bulbasaur", "Ivysaur", "Venusaur",
                "Charmander", "Charmeleon", "Charizard",
                "Squirtle", "Wartortle", "Blastoise",
                "Caterpie", "Metapod", "Butterfree",
                "Weedle", "Kakuna", "Beedrill",
                "Pidgey", "Pidgeotto", "Pidgeot",
                "Rattata", "Raticate",
                "Spearow", "Fearow",
                "Ekans", "Arbok",
                "Pikachu", "Raichu",
                "Sandshrew", "Sandslash",
                "Nidoran_F", "Nidorina", "Nidoqueen",
                "Nidoran_M", "Nidorino", "Nidoking",
                "Clefairy", "Clefable",
                "Vulpix", "Ninetales",
                "Jigglypuff", "Wigglytuff",
                "Zubat", "Golbat",
                "Oddish", "Gloom", "Vileplume",
                "Paras", "Parasect",
                "Venonat", "Venomoth",
                "Diglett", "Dugtrio",
                "Meowth", "Persian",
                "Psyduck", "Golduck",
                "Mankey", "Primeape",
                "Growlithe", "Arcanine",
                "Poliwag", "Poliwhirl", "Poliwrath",
                "Abra", "Kadabra", "Alakazam",
                "Machop", "Machoke", "Machamp",
                "Bellsprout", "Weepinbell", "Victreebel",
                "Tentacool", "Tentacruel",
                "Geodude", "Graveler", "Golem",
                "Ponyta", "Rapidash",
                "Slowpoke", "Slowbro",
                "Magnemite", "Magneton",
                "Farfetch_Ad",
                "Doduo", "Dodrio",
                "Seel", "Dewgong",
                "Grimer", "Muk",
                "Shellder", "Cloyster",
                "Gastly", "Haunter", "Gengar",
                "Onix",
                "Drowzee", "Hypno",
                "Krabby", "Kingler",
                "Voltorb", "Electrode",
                "Exeggcute", "Exeggutor",
                "Cubone", "Marowak",
                "Hitmonlee",
                "Hitmonchan",
                "Lickitung",
                "Koffing", "Weezing",
                "Rhyhorn", "Rhydon",
                "Chansey",
                "Tangela",
                "Kanhaskhan",
                "Horsea", "Seadra",
                "Goldeen", "Seaking",
                "Staryu", "Starmie",
                "Mr_PMime",
                "Scyther",
                "Jynx",
                "Electabuzz",
                "Magmar",
                "Pinsir",
                "Tauros",
                "Magikarp", "Gyarados",
                "Lapras",
                "Ditto",
                "Eevee", "Vaporeon", "Jolteon", "Flareon",
                "Porygon",
                "Omanyte", "Omastar",
                "Kabuto", "Kabutops",
                "Aerodactyl",
                "Snorlax",
                "Articuno", "Zapdos", "Moltres",
                "Dratini", "Dragonair", "Dragonite",
                "Mewtwo",
                "Mew"
        };
        */

        final String[] FOREST = {
                "Bulbasaur", "Ivysaur", "Venusaur",
                "Squirtle", "Wartortle", "Blastoise",
                "Caterpie", "Metapod", "Butterfree",
                "Weedle", "Kakuna", "Beedrill",
                "Pidgey", "Pidgeotto", "Pidgeot",
                "Rattata", "Raticate",
                "Spearow", "Fearow",
                "Ekans", "Arbok",
                "Pikachu", "Raichu",
                "Jigglypuff", "Wigglytuff",
                "Zubat", "Golbat",
                "Oddish", "Gloom", "Vileplume",
                "Paras", "Parasect",
                "Venonat", "Venomoth",
                "Diglett", "Dugtrio",
                "Psyduck", "Golduck",
                "Poliwag", "Poliwhirl", "Poliwrath",
                "Bellsprout", "Weepinbell", "Victreebel",
                "Farfetch_Ad",
                "Grimer", "Muk",
                "Gastly", "Haunter", "Gengar",
                "Exeggcute", "Exeggutor",
                "Koffing", "Weezing",
                "Tangela",
                "Goldeen", "Seaking",
                "Scyther",
                "Pinsir",
                "Magikarp", "Gyarados",
                "Lapras",
                "Omanyte", "Omastar",
                "Kabuto", "Kabutops",
                "Dratini", "Dragonair", "Dragonite"
        };

        final String[] FIELDS = {
                "Bulbasaur", "Ivysaur", "Venusaur",
                "Charmander", "Charmeleon", "Charizard",
                "Pidgey", "Pidgeotto", "Pidgeot",
                "Rattata", "Raticate",
                "Spearow", "Fearow",
                "Sandshrew", "Sandslash",
                "Nidoran_F", "Nidorina", "Nidoqueen",
                "Nidoran_M", "Nidorino", "Nidoking",
                "Clefairy", "Clefable",
                "Vulpix", "Ninetales",
                "Jigglypuff", "Wigglytuff",
                "Zubat", "Golbat",
                "Diglett", "Dugtrio",
                "Meowth", "Persian",
                "Mankey", "Primeape",
                "Growlithe", "Arcanine",
                "Machop", "Machoke", "Machamp",
                "Geodude", "Graveler", "Golem",
                "Ponyta", "Rapidash",
                "Magnemite", "Magneton",
                "Doduo", "Dodrio",
                "Grimer", "Muk",
                "Gastly", "Haunter", "Gengar",
                "Onix",
                "Voltorb", "Electrode",
                "Cubone", "Marowak",
                "Hitmonlee",
                "Hitmonchan",
                "Lickitung",
                "Rhyhorn", "Rhydon",
                "Chansey",
                "Tangela",
                "Kanhaskhan",
                "Goldeen", "Seaking",
                "Mr_PMime",
                "Scyther",
                "Jynx",
                "Electabuzz",
                "Magmar",
                "Tauros",
                "Magikarp",
                "Ditto",
                "Eevee", "Vaporeon", "Jolteon", "Flareon",
                "Porygon",
                "Omanyte", "Omastar",
                "Kabuto", "Kabutops",
                "Aerodactyl",
                "Snorlax",
                "Dratini", "Dragonair", "Dragonite"
        };

        final String[] CITY = {
                "Charmander", "Charmeleon", "Charizard",
                "Squirtle", "Wartortle", "Blastoise",
                "Caterpie", "Metapod", "Butterfree",
                "Weedle", "Kakuna", "Beedrill",
                "Pidgey",
                "Rattata", "Raticate",
                "Pikachu",
                "Sandshrew",
                "Nidoran_F",
                "Nidoran_M",
                "Vulpix", "Ninetales",
                "Venonat", "Venomoth",
                "Meowth", "Persian",
                "Psyduck", "Golduck",
                "Growlithe", "Arcanine",
                "Abra", "Kadabra", "Alakazam",
                "Machop", "Machoke", "Machamp",
                "Bellsprout", "Weepinbell", "Victreebel",
                "Tentacool", "Tentacruel",
                "Slowpoke", "Slowbro",
                "Magnemite", "Magneton",
                "Seel", "Dewgong",
                "Shellder", "Cloyster",
                "Gastly", "Haunter", "Gengar",
                "Drowzee", "Hypno",
                "Krabby", "Kingler",
                "Voltorb", "Electrode",
                "Hitmonlee",
                "Hitmonchan",
                "Lickitung",
                "Chansey",
                "Horsea", "Seadra",
                "Staryu", "Starmie",
                "Mr_PMime",
                "Jynx",
                "Electabuzz",
                "Magikarp", "Gyarados",
                "Lapras",
                "Eevee", "Vaporeon", "Jolteon", "Flareon",
                "Porygon",
        };

        final String[] WATER_ONLY = {
                "Squirtle", "Wartortle", "Blastoise",
                "Psyduck", "Golduck",
                "Poliwag", "Poliwhirl", "Poliwrath",
                "Tentacool", "Tentacruel",
                "Slowpoke", "Slowbro",
                "Seel", "Dewgong",
                "Shellder", "Cloyster",
                "Krabby", "Kingler",
                "Horsea", "Seadra",
                "Goldeen", "Seaking",
                "Staryu", "Starmie",
                "Magikarp", "Gyarados",
                "Lapras",
                "Vaporeon",
                "Omanyte", "Omastar",
                "Kabuto", "Kabutops",
                "Dratini", "Dragonair", "Dragonite"
        };

        final String[] WARM_ONLY = {
                "Charmander", "Charmeleon", "Charizard",
                "Sandshrew", "Sandslash",
                "Vulpix", "Ninetales",
                "Growlithe", "Arcanine",
                "Ponyta", "Rapidash",
                "Farfetch_Ad",
                "Onix",
                "Exeggcute", "Exeggutor",
                "Magmar",
                "Lapras",
                "Ditto",
                "Flareon",
                "Porygon",
                "Aerodactyl",
        };

        final String[] RAIN_ONLY = {
                "Paras", "Parasect",
                "Seel", "Dewgong",
                "Grimer", "Muk",
                "Tangela",
                "Dratini", "Dragonair", "Dragonite"
        };

        final String[] NIGHT_ONLY = {
                "Clefairy", "Clefable",
                "Venonat", "Venomoth",
                "Persian",
                "Poliwrath",
                "Alakazam",
                "Golem",
                "Gastly", "Haunter", "Gengar",
                "Drowzee", "Hypno",
                "Koffing", "Weezing",
                "Pinsir",
                "Omanyte", "Omastar",
                "Kabuto", "Kabutops",
        };

        final String[] DAY_ONLY = {
                "Bulbasaur", "Ivysaur", "Venusaur",
                "Squirtle", "Wartortle", "Blastoise",
                "Butterfree",
                "Beedrill",
                "Pidgey", "Pidgeotto", "Pidgeot",
                "Spearow", "Fearow",
                "Sandshrew", "Sandslash",
                "Vulpix", "Ninetales",
                "Jigglypuff", "Wigglytuff",
                "Oddish", "Gloom", "Vileplume",
                "Diglett", "Dugtrio",
                "Mankey", "Primeape",
                "Growlithe", "Arcanine",
                "Bellsprout", "Weepinbell", "Victreebel",
                "Tentacool", "Tentacruel",
                "Ponyta", "Rapidash",
                "Magnemite", "Magneton",
                "Farfetch_Ad",
                "Doduo", "Dodrio",
                "Seel", "Dewgong",
                "Shellder", "Cloyster",
                "Voltorb", "Electrode",
                "Exeggcute", "Exeggutor",
                "Lickitung",
                "Rhyhorn", "Rhydon",
                "Chansey",
                "Kanhaskhan",
                "Horsea", "Seadra",
                "Mr_PMime",
                "Jynx",
                "Tauros",
                "Gyarados",
                "Lapras",
                "Eevee", "Vaporeon", "Jolteon", "Flareon",
                "Aerodactyl",
                "Snorlax",
        };

        int specForest = 0, specFields = 0, specCity = 0;
        for (String p1 : FOREST) {
            boolean unique = true;
            for (String p2 : FIELDS) {
                if (p2.equals(p1)) {
                    unique = false;
                    break;
                }
            }
            if (unique) {
                for (String p2 : CITY) {
                    if (p2.equals(p1)) {
                        unique = false;
                        break;
                    }
                }
            }
            if (unique) {
                specForest++;
            }
        }

        for (String p1 : FIELDS) {
            boolean unique = true;
            for (String p2 : FOREST) {
                if (p2.equals(p1)) {
                    unique = false;
                    break;
                }
            }
            if (unique) {
                for (String p2 : CITY) {
                    if (p2.equals(p1)) {
                        unique = false;
                        break;
                    }
                }
            }
            if (unique) {
                specFields++;
            }
        }

        for (String p1 : CITY) {
            boolean unique = true;
            for (String p2 : FOREST) {
                if (p2.equals(p1)) {
                    unique = false;
                    break;
                }
            }
            if (unique) {
                for (String p2 : FIELDS) {
                    if (p2.equals(p1)) {
                        unique = false;
                        break;
                    }
                }
            }
            if (unique) {
                specCity++;
            }
        }

        String debugStr = "[SPAWN DATA]"
                + "\nForest: " + FOREST.length + " (" + specForest + " unique)"
                + "\nFields: " + FIELDS.length + " (" + specFields + " unique)"
                + "\nCity: " + CITY.length + " (" + specCity + " unique)"
                + "\n" + DAY_ONLY.length + " requires daytime!"
                + "\n" + NIGHT_ONLY.length + " requires nighttime!"
                + "\n" + RAIN_ONLY.length + " requires rain!"
                + "\n" + WATER_ONLY.length + " requires water!"
                + "\n" + WARM_ONLY.length + " requires warm temperature!";
        //Global.print(debugStr);

        //Return random choice
        return "";
    }

    private void spawnPokemon(LatLng position) {
        //Remove old pokemon
        while (spawns.size() > MAX_SPAWN - 1) {
            spawns.get(0).marker.remove();
            spawns.remove(0);
        }

        //Choose and add pokemon
        int roll = 96; //Global.randomInt(1, 151);
        String randId = Pokemon.NUMBER_ID.values()[roll].name();
        int randLvl = Global.randomInt(2, 6);
        Pokemon pokemon = new Pokemon(context, randId, randLvl);
        int randMins = Global.randomInt(4, 10);

        //Create spawn
        createSpawn(new Date(), randMins, pokemon, position);
        saveSpawns();

        choosePokemon();
    }

    private void saveSpawns() {
        String spawnStr = "" + spawns.size();
        for (Spawn spawn : spawns) {
            String posStr = spawn.marker.getPosition().latitude + "," + spawn.marker.getPosition().longitude;
            String dateStr = dateFormat.format(spawn.date);
            String numMinsStr = "" + spawn.numMinutes;
            String pokeStr = spawn.pokemon.getSaveData();
            spawnStr += "/" + dateStr + "/" + numMinsStr + "/" + posStr + "/" + pokeStr;
        }

        SharedPreferences prefs = context.getSharedPreferences("com.example.app", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("spawns", spawnStr);
        edit.commit();
    }

    private void loadSpawns() {
        SharedPreferences prefs = context.getSharedPreferences("com.example.app", Context.MODE_PRIVATE);
        String spawnStr = prefs.getString("spawns", null);
        if (spawnStr != null) {
            String spawnData[] = spawnStr.split("/");
            int spawnSize = Integer.parseInt(spawnData[0]);
            for (int i = 0; i < spawnSize; i++) {
                int dataStart = (i * 4) + 1;

                //Get date
                Date date = dateFormat.parse(spawnData[dataStart], new ParsePosition(0));
                int numMins = Integer.parseInt(spawnData[dataStart + 1]);
                if (!Global.isTimedOut(date, numMins)) {
                    //Get position
                    String[] posData = spawnData[dataStart + 2].split(",");
                    LatLng position = new LatLng(Double.parseDouble(posData[0]), Double.parseDouble(posData[1]));

                    //Get pokemon
                    String pokeData = spawnData[dataStart + 3];
                    Pokemon pokemon = new Pokemon(context, pokeData);

                    //Create spawn
                    createSpawn(date, numMins, pokemon, position);
                }
            }

            //Update saved spawns
            saveSpawns();
        }
    }

    private void createSpawn(Date date, int numMinutes, Pokemon pokemon, LatLng position) {
        //Get random position
        //LatLng randPos = Global.getRandomLocation(position, BATTLE_RANGE, SPAWN_RANGE);
        //float alpha = 0.5f;
        //int dist = (int) Global.getDistance(lastLoc, position);
        //if (dist <= BATTLE_RANGE) { alpha = 1; }

        //Create new marker
        MarkerOptions options = new MarkerOptions();
        options.position(position);
        options.title(pokemon.getName() + " (Lvl: " + pokemon.getLevel() + ")");
        options.icon(Global.getUnknownIcon(context));
        options.anchor(0.5f, 0.75f);
        options.alpha(0);
        Marker marker = map.addMarker(options);

        //Store spawn
        Spawn spawn = new Spawn();
        spawn.marker = marker;
        spawn.pokemon = pokemon;
        spawn.date = date;
        spawn.state = "";
        spawn.numMinutes = numMinutes;
        spawns.add(spawn);
    }

    private void despawn(Spawn spawn) {
        spawn.marker.remove();
        spawns.remove(spawn);
        saveSpawns();
    }



    //Battle connection
    public void setVisible(boolean visible) {
        if (readyToShow) {
            View fragMap = ((FragmentActivity) context).findViewById(R.id.fragMap);
            View mapLayout = ((FragmentActivity) context).findViewById(R.id.layoutMap);

            if (visible) {
                fragMap.setVisibility(View.VISIBLE);
                mapLayout.setVisibility(View.VISIBLE);
            }
            else {
                fragMap.setVisibility(View.INVISIBLE);
                mapLayout.setVisibility(View.INVISIBLE);
            }
        }
    }

    public String isBattleBegan() {
        String result = null;
        if (battleWild != null) {
            result = "Wild";
        }
        if (battleTrainer != null) {
            result = "Trainer";
        }
        return result;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getOpponent() {
        return battleTrainer;
    }

    public Pokemon getWild() {
        return battleWild;
    }

    public void battleEnd(String result) {
        if (battleWild != null) {
            if (result.equals("Caught") || result.equals("Won")) {
                for (int i = 0; i < spawns.size(); i++) {
                    Spawn spawn = spawns.get(i);
                    if (spawn.pokemon.equals(battleWild)) {
                        despawn(spawn);
                        Global.print(spawn.pokemon.getName() + " was defeated!");
                        saveSpawns();
                    }
                }
            }

            battleWild = null;
            player.save();
        }
        if (battleTrainer != null) {
            battleTrainer = null;
        }
    }
}