package io.anthonylombardo321.github.mtatracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    AutoCompleteTextView subwayStationTextInput;
    AutoCompleteTextView busStopTextInput;
    TextView availabilityText;
    Spinner availableSubwaysAtLocation;
    Spinner directionsForBusStop;
    TextView directionText;
    Spinner directionsForSelectedSubway;
    Spinner busStopRoutes;
    RadioButton subwaySearchButton;
    RadioButton busSearchButton;
    Button searchButton;

    LocationManager locationManager;
    FusedLocationProviderClient fusedLocationProviderClient;
    SubwayMap subwayMap = new SubwayMap();
    HashMap<String, String> subwayAPIHashMap = subwayMap.getSubwayAPIHashMap();
    HashMap<String, HashMap<String, String>> nearbyBusStops = new HashMap<>();
    ApplicationExecutors executors = new ApplicationExecutors();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Getting the ID of all the items in the main layout
        subwayStationTextInput = findViewById(R.id.subwayStationInput);
        busStopTextInput = findViewById(R.id.busStopInput);
        availabilityText = findViewById(R.id.MTAAvailabilityText);
        availableSubwaysAtLocation = findViewById(R.id.availableSubways);
        directionsForBusStop = findViewById(R.id.busStopDirection);
        directionText = findViewById(R.id.MTADirectionText);
        directionsForSelectedSubway = findViewById(R.id.subwayDirection);
        busStopRoutes = findViewById(R.id.busRoutes);
        subwaySearchButton = findViewById(R.id.subwayRadioButton);
        busSearchButton = findViewById(R.id.busRadioButton);
        searchButton = findViewById(R.id.searchButton);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        //Checks if User granted Location Permissions to app
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 100);
        }

        //Sets Subway Station Array to AutoCompleteTextView subwayStationTextInput
        ArrayList<String> subwayStations = new ArrayList<>(subwayMap.getSubwayStationServices().keySet());
        InputAdapter subwayStationAdapter = new InputAdapter(MainActivity.this, android.R.layout.simple_dropdown_item_1line, subwayStations);
        subwayStationTextInput.setAdapter(subwayStationAdapter);
        //====================================================================================================================================
        // Subway onClickListeners
        subwaySearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((RadioButton) view).isChecked()) {
                    busStopTextInput.setVisibility(View.GONE);
                    directionsForBusStop.setVisibility(View.GONE);
                    busStopRoutes.setVisibility(View.GONE);

                    subwayStationTextInput.setVisibility(View.VISIBLE);
                    availabilityText.setText("Subways Available at Station:");
                    availableSubwaysAtLocation.setVisibility(View.VISIBLE);
                    directionText.setText("Subway Route:");
                    directionsForSelectedSubway.setVisibility(View.VISIBLE);
                }
            }
        });

        subwayStationTextInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Clear Spinner Adapters whenever a new station is chosen
                availableSubwaysAtLocation.setAdapter(null);
                directionsForSelectedSubway.setAdapter(null);

                //Hide Keyboard from User
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

                //Get array from hashmap and assign adapter to availableSubways spinner
                String chosenStation = subwayStationTextInput.getText().toString();
                String[] services_at_station = subwayMap.getSubwayStationServices().get(chosenStation);
                ArrayAdapter<String> subwayServicesAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, services_at_station);
                availableSubwaysAtLocation.setAdapter(subwayServicesAdapter);
            }
        });

        availableSubwaysAtLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Get array from hashmap and assign adapter to directionsForSelectedSubway spinner
                String selectedSubwayService = availableSubwaysAtLocation.getSelectedItem().toString();
                String[] serviceDirections = subwayMap.getSubwayServiceStops().get(selectedSubwayService);
                ArrayAdapter<String> subwayDirectionAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, serviceDirections);
                subwayDirectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                directionsForSelectedSubway.setAdapter(subwayDirectionAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//====================================================================================================================================
        // Bus onClickListeners
        busSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((RadioButton) view).isChecked()) {
                    //If Location permissions are granted, get user's location before finding nearby bus stops
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        getLocation();
                    }
                    busStopTextInput.setVisibility(View.VISIBLE);
                    availabilityText.setText("Bus Stop Direction:");
                    directionsForBusStop.setVisibility(View.VISIBLE);
                    directionText.setText("Bus Route:");
                    busStopRoutes.setVisibility(View.VISIBLE);

                    subwayStationTextInput.setVisibility(View.GONE);
                    availableSubwaysAtLocation.setVisibility(View.GONE);
                    directionsForSelectedSubway.setVisibility(View.GONE);
                }
            }
        });

        busStopTextInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Clear Spinner Adapters whenever a new station is chosen
                directionsForBusStop.setAdapter(null);
                busStopRoutes.setAdapter(null);

                //Hide Keyboard from User
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

                //Get array from hashmap and assign adapter to directionsForBusStop spinner
                String chosenBusStop = busStopTextInput.getText().toString();
                ArrayList<String> busStopDirections = new ArrayList<>();
                for (String s : nearbyBusStops.get(chosenBusStop).keySet()) {
                    busStopDirections.add(s);
                }
                ArrayAdapter<String> busDirectionsAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, busStopDirections);
                directionsForBusStop.setAdapter(busDirectionsAdapter);
            }
        });

        directionsForBusStop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String busStop = busStopTextInput.getText().toString();
                String busStopDirection = directionsForBusStop.getSelectedItem().toString();
                //Get the monitoringRef of the Bus Stop (Will be used to get the bus routes from the BusAPI)
                String monitoringRef = nearbyBusStops.get(busStop).get(busStopDirection);
                BusAPI busAPI = new BusAPI(MainActivity.this);
                //Get list of Bus Routes found at stop and assign the adapter to the busStopRoutes spinner
                busAPI.getBusRoutesAtBusStop(monitoringRef, new BusAPI.BusRoutesResponseListener() {
                    @Override
                    public void onError(String message) {
                        Log.d("Bus Routes Error", message);
                    }

                    @Override
                    public void onResponse(ArrayList<String> busRoutesAtBusStop) {
                        ArrayAdapter<String> busRoutesAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, busRoutesAtBusStop);
                        busStopRoutes.setAdapter(busRoutesAdapter);
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subwaySearchButton.isChecked()) {
                    String subwayStation = subwayStationTextInput.getText().toString();
                    Object subwayServiceObject = availableSubwaysAtLocation.getSelectedItem();
                    Object subwayDirectionObject = directionsForSelectedSubway.getSelectedItem();
                    if (subwayStation.equals("") || subwayServiceObject == null || subwayDirectionObject == null) {
                        Toast.makeText(MainActivity.this, "Please Fill in All Missing Fields", Toast.LENGTH_LONG).show();
                    } else {
                        //Gets the equivalent subway station that is understood by the SubwayAPI
                        String subwayAPIStation = subwayAPIHashMap.get(subwayStation);
                        //Changes subwayService to match the service that the SubwayAPI uses
                        String subwayService = availableSubwaysAtLocation.getSelectedItem().toString();
                        if (subwayService.equals("Rockaway Park Shuttle")) {
                            subwayService = "H";
                        }
                        if (subwayService.equals("Franklin Avenue Shuttle")) {
                            subwayService = "FS";
                        }
                        if (subwayService.equals("42nd Street Shuttle")) {
                            subwayService = "GS";
                        }
                        //Gets the subway route direction (NORTH/SOUTH) to get the right subway service (Ex: N - Coney Island-Stillwell Avenue = SOUTH)
                        int subwayDirectionIndex = directionsForSelectedSubway.getSelectedItemPosition();
                        String subwayDirection = subwayDirectionIndex == 0 ? "NORTH" : "SOUTH";
                        //Assigns values to SubwayInput object (Will be used in the ResultsActivity when getting new results upon refresh)
                        SubwayInput subwayInput = new SubwayInput(subwayAPIStation, subwayService, subwayDirection);
                        //Get results before sending results to ResultsActivity
                        SubwayAPI subwayAPI = new SubwayAPI(subwayService, MainActivity.this);
                        subwayAPI.getData(subwayInput, new SubwayAPI.subwayResponseListener() {
                            @Override
                            public void onResponse(ArrayList<SubwayResult> subwayResults) {
                                if (subwayResults.size() == 0) {
                                    executors.getMainThread().execute(() -> {Toast.makeText(MainActivity.this, "No Results Found", Toast.LENGTH_SHORT).show();});
                                } else {
                                    SubwayMap subwayMap = new SubwayMap();
                                    String subwayStation = subwayStationTextInput.getText().toString();
                                    ArrayList<String> subwayStationServices = new ArrayList<>(Arrays.asList(subwayMap.getSubwayStationServices().get(subwayStation)));
                                    String subwayService = subwayInput.getSubwayAPIService();
                                    String subwayDirection = directionsForSelectedSubway.getSelectedItem().toString();
                                    if (subwayService.equals("H") || subwayService.equals("FS") || subwayService.equals("GS")) {
                                        subwayService = "S";
                                    }
                                    Intent resultsIntent = new Intent(MainActivity.this, ResultsActivity.class);
                                    resultsIntent.putExtra("resultType", "Subway");
                                    resultsIntent.putExtra("subwayStation", subwayStation);
                                    resultsIntent.putStringArrayListExtra("subwayStationServices", subwayStationServices);
                                    resultsIntent.putExtra("subwayService", subwayService);
                                    resultsIntent.putExtra("subwayDirection", subwayDirection);
                                    resultsIntent.putParcelableArrayListExtra("subwayResults", subwayResults);
                                    resultsIntent.putExtra("subwayInput", subwayInput);
                                    startActivity(resultsIntent);
                                }
                            }
                        });
                    }
                } else {
                    String busStop = busStopTextInput.getText().toString();
                    Object busStopDirectionObject = directionsForBusStop.getSelectedItem();
                    Object busStopRouteObject = busStopRoutes.getSelectedItem();
                    if (busStop.equals("") || busStopDirectionObject == null || busStopRouteObject == null) {
                        Toast.makeText(MainActivity.this, "Please Fill in All Missing Fields", Toast.LENGTH_LONG).show();
                    } else {
                        //Gets MonitoringRef to search for find the bus route schedule for the chosen bus stop
                        String monitoringRef = nearbyBusStops.get(busStop).get(busStopDirectionObject.toString());
                        //Assigns values to BusInput object (Will be used in the ResultsActivity when getting new results upon refresh)
                        BusInput busInput = new BusInput(monitoringRef, busStopRouteObject.toString());
                        //Get results before sending results to ResultsActivity
                        BusAPI busAPI = new BusAPI(MainActivity.this);
                        busAPI.getBusResults(busInput, new BusAPI.BusResultsResponseListener() {
                            @Override
                            public void onError(String message) {
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(ArrayList<BusResult> busResults) {
                                if (busResults.size() == 0) {
                                    executors.getMainThread().execute(() -> {Toast.makeText(MainActivity.this, "No Results Found", Toast.LENGTH_SHORT).show();});
                                } else {
                                    String busStop = busStopTextInput.getText().toString();
                                    String chosenBusStopRoute = busStopRoutes.getSelectedItem().toString();
                                    String busService = chosenBusStopRoute.split(" - ")[0];
                                    String busDirection = chosenBusStopRoute.split(" - ")[1];

                                    //Getting Bus Services from busStopRoutes Spinner (Ex: Q19, M60-SBS)
                                    ArrayList<String> busStopServices = new ArrayList<>();
                                    Adapter busServicesAdapter = busStopRoutes.getAdapter();
                                    for (int i = 0; i < busServicesAdapter.getCount(); i++) {
                                        String busServiceFromAdapter = (String) busServicesAdapter.getItem(i);
                                        busStopServices.add(busServiceFromAdapter.split(" - ")[0]);
                                    }
                                    Intent resultsIntent = new Intent(MainActivity.this, ResultsActivity.class);
                                    resultsIntent.putExtra("resultType", "Bus");
                                    resultsIntent.putExtra("busStop", busStop);
                                    resultsIntent.putStringArrayListExtra("busStopServices", busStopServices);
                                    resultsIntent.putExtra("busService", busService);
                                    resultsIntent.putExtra("busDirection", busDirection);
                                    resultsIntent.putParcelableArrayListExtra("busResults", busResults);
                                    resultsIntent.putExtra("busInput", busInput);
                                    startActivity(resultsIntent);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        //Initialize Location Manager
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        //Check if Providers are Enabled
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //If Location Service is enabled, get last location
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    //If location is present, send Latitude and Longitude to get bus stops near user's location
                    if (location != null) {
                        getNearbyBusStops(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                    } else {
                        //If Location result is null, create a locationRequest and callback
                        LocationRequest locationRequest = LocationRequest.create()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(5000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                //Upon getting a result, send Latitude and Longitude to get bus stops near user's location
                                Location location = locationResult.getLastLocation();
                                getNearbyBusStops(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                            }
                        };
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
        } else {
            //If Location Service is not enabled, open Location Setting
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void getNearbyBusStops(String latitude, String longitude) {
        BusAPI busAPI = new BusAPI(MainActivity.this);
        busAPI.getNearbyBusStops(latitude, longitude, new BusAPI.NearbyBusStopsResponseListener() {
            @Override
            public void onError(String message) {
                Log.d("Bus Stops Error", message);
            }

            @Override
            public void onResponse(ArrayList<BusStop> nearbyBusStopsResponse) {
                //Add bus stop names from returned array to Hashmap
                for (int i = 0; i < nearbyBusStopsResponse.size(); i++) {
                    HashMap<String, String> busStopDirections = new HashMap<>();
                    BusStop busStopAtIndex = nearbyBusStopsResponse.get(i);
                    String busStopName = busStopAtIndex.getBusStopName();
                    //If busStopName is in HashMap, override ArrayList with the busStopName's value
                    if (nearbyBusStops.containsKey(busStopName)) {
                        busStopDirections = nearbyBusStops.get(busStopName);
                    }
                    String busStopDirection = busStopAtIndex.getBusStopDirection();
                    String busStopMonitoringRef = busStopAtIndex.getMonitoringRef();
                    busStopDirections.put(busStopDirection, busStopMonitoringRef);
                    nearbyBusStops.put(busStopName, busStopDirections);
                }
                Log.d("Results", String.valueOf(nearbyBusStops.entrySet()));

                //Assign arraylist to the Bus Stop AutoCompleteTextView
                InputAdapter busStopAdapter = new InputAdapter(MainActivity.this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(nearbyBusStops.keySet()));
                busStopTextInput.setAdapter(busStopAdapter);
            }
        });
    }
}
