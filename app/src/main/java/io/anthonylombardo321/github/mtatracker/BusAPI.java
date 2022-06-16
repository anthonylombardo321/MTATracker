package io.anthonylombardo321.github.mtatracker;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class BusAPI {

    private Context context;
    private String key;

    public BusAPI(Context context) {
        this.context = context;
        key = context.getResources().getString(R.string.bus_key);
    }

    public interface NearbyBusStopsResponseListener{
        void onError(String message);
        void onResponse(ArrayList<BusStop> nearbyBusStops);
    }

    public interface BusRoutesResponseListener{
        void onError(String message);
        void onResponse(ArrayList<String> busRoutesAtBusStop);
    }

    public interface BusResultsResponseListener{
        void onError(String message);
        void onResponse(ArrayList<BusResult> busResults);
    }

    public void getNearbyBusStops(String latitude, String longitude, NearbyBusStopsResponseListener nearbyBusStopsResponseListener){
        String url ="https://bustime.mta.info/api/where/stops-for-location.json?lat=" + latitude + "&lon=" + longitude + "&key=" + key;
        // Request a JSONObject response from the provided URL.
        ArrayList<BusStop> nearbyBusStops = new ArrayList<>();
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject dataObject = response.getJSONObject("data");
                    JSONArray stopArray = dataObject.getJSONArray("stops");
                    for(int i = 0; i < stopArray.length(); i++) {
                        JSONObject busStopObject = stopArray.getJSONObject(i);
                        //Gets the relevant information for the selected bus stop
                        //(Including monitoringRef - Will be used later when looking for bus routes)
                        String monitoringRef = busStopObject.getString("code");
                        String direction = busStopObject.getString("direction");
                        String busStopName = busStopObject.getString("name");
                        //Assigns information to BusStop object for easy access
                        BusStop busStop = new BusStop(monitoringRef, busStopName, direction);
                        nearbyBusStops.add(busStop);
                    }
                    nearbyBusStopsResponseListener.onResponse(nearbyBusStops);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                nearbyBusStopsResponseListener.onError("Data can't be accessed");
            }
        });
        // Add the request to the RequestQueue.
        BusQueueSingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

    public void getBusRoutesAtBusStop(String monitoringRef, BusRoutesResponseListener busRoutesResponseListener){
        String url ="https://bustime.mta.info/api/siri/stop-monitoring.json?key=" + key + "&MonitoringRef=" + monitoringRef;
        // Request a JSONObject response from the provided URL.
        ArrayList<String> busRoutes = new ArrayList<>();
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject SIRIObject = response.getJSONObject("Siri");
                    JSONObject ServiceDeliveryObject = SIRIObject.getJSONObject("ServiceDelivery");
                    JSONArray stopMonitoringArray = ServiceDeliveryObject.getJSONArray("StopMonitoringDelivery");
                    JSONArray monitoredStopsArray = stopMonitoringArray.getJSONObject(0).getJSONArray("MonitoredStopVisit");
                    for(int i = 0; i < monitoredStopsArray.length(); i++){
                        JSONObject routeObject = monitoredStopsArray.getJSONObject(i).getJSONObject("MonitoredVehicleJourney");
                        //Gets the bus route found in this stop
                        String lineName = routeObject.getString("PublishedLineName");
                        String destinationName = routeObject.getString("DestinationName");
                        String busRoute = lineName + " - " + destinationName;
                        //A check to avoid duplicate bus routes
                        if(!busRoutes.contains(busRoute)){
                            busRoutes.add(busRoute);
                        }
                    }
                    busRoutesResponseListener.onResponse(busRoutes);
                } catch (JSONException e) {
                    busRoutesResponseListener.onError("Can't Find Buses at Stop - Try Again Later");
                    Log.d("Bus Routes Exception", "MonitoredStopVisit Object Not Found");
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                busRoutesResponseListener.onError("Data can't be accessed");
            }
        });
        BusQueueSingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

    public void getBusResults(BusInput busInput, BusResultsResponseListener busResultsResponseListener){
        String monitoringRef = busInput.getMonitoringRef();
        String busRoute = busInput.getBusRoute();
        String url ="https://bustime.mta.info/api/siri/stop-monitoring.json?key=" + key + "&MonitoringRef=" + monitoringRef;
        // Request a JSONObject response from the provided URL.
        ArrayList<BusResult> busResults = new ArrayList<>();
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject SIRIObject = response.getJSONObject("Siri");
                    JSONObject ServiceDeliveryObject = SIRIObject.getJSONObject("ServiceDelivery");
                    JSONArray stopMonitoringArray = ServiceDeliveryObject.getJSONArray("StopMonitoringDelivery");
                    JSONArray monitoredStopsArray = stopMonitoringArray.getJSONObject(0).getJSONArray("MonitoredStopVisit");
                    for(int i = 0; i < monitoredStopsArray.length(); i++) {
                        JSONObject routeObject = monitoredStopsArray.getJSONObject(i).getJSONObject("MonitoredVehicleJourney");
                        String lineName = routeObject.getString("PublishedLineName");
                        String destinationName = routeObject.getString("DestinationName");
                        String currentBusRoute = lineName + " - " + destinationName;
                        //If the current Bus Route matches the bus route we're looking for, get bus information
                        if (currentBusRoute.equals(busRoute)) {
                            JSONObject monitoredCall = routeObject.getJSONObject("MonitoredCall");
                            JSONObject distances = monitoredCall.getJSONObject("Extensions").getJSONObject("Distances");
                            //Gets any arrival time that is available in the JSONObject
                            String arrivalTime = "";
                            if (monitoredCall.has("ExpectedArrivalTime")) {
                                arrivalTime = monitoredCall.getString("ExpectedArrivalTime");
                            } else {
                                arrivalTime = monitoredCall.getString("AimedArrivalTime");
                            }
                            // Converts DateTime JSON object to Date
                            DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                            Date arrivalDateTime = dateTimeFormat.parse(arrivalTime);
                            //Removes seconds from timestamp (To limit inconsistencies with Wait Time and Arrival Time)
                            Calendar timeStamp = Calendar.getInstance();
                            timeStamp.setTime(arrivalDateTime);
                            timeStamp.set(Calendar.SECOND, 0);
                            arrivalDateTime = timeStamp.getTime();
                            //Removes seconds from current time (To limit inconsistencies with Wait Time and Arrival Time)
                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.SECOND, 0);
                            Date currentDate = cal.getTime();
                            //Calculating Wait Time
                            Long timeDifference = arrivalDateTime.getTime() - currentDate.getTime();
                            Long waitTime = timeDifference / (60 * 1000);
                            //Converts Date to Arrival Time
                            DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
                            arrivalTime = dateFormat.format(arrivalDateTime);
                            //Gather info for BusResult
                            String busID = routeObject.getString("VehicleRef");
                            String distanceFromStop = distances.getString("PresentableDistance");
                            String stopsUntilBusStop = distances.getString("StopsFromCall");
                            if(waitTime > 0) {
                                //Creating BusResult Object to hold relevant information
                                BusResult busResult = new BusResult();
                                busResult.setBusRoute(currentBusRoute);
                                busResult.setBusID(busID);
                                busResult.setArrivalTime(arrivalTime);
                                busResult.setDistanceFromStop(distanceFromStop);
                                busResult.setWaitTime(String.valueOf(waitTime));
                                busResult.setStopsUntilBusStop(stopsUntilBusStop);
                                busResults.add(busResult);
                            }
                        }
                    }
                    Collections.sort(busResults);
                    busResultsResponseListener.onResponse(busResults);
                } catch (JSONException | ParseException e) {
                    busResultsResponseListener.onError("There Are No Active Buses - Try Again Later");
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                busResultsResponseListener.onError("Data can't be accessed");
            }
        });
        BusQueueSingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }
}
