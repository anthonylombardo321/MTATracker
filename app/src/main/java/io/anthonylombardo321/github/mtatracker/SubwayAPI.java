package io.anthonylombardo321.github.mtatracker;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.text.DateFormat;
import java.util.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.io.InputStream;
import java.net.ConnectException;
import java.text.SimpleDateFormat;

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.TripDescriptor;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import com.google.transit.realtime.GtfsRealtime.VehiclePosition;
import com.google.transit.realtime.GtfsRealtime.Alert;

import com.google.protobuf.ExtensionRegistry;
import com.google.transit.realtime.GtfsRealtimeNYCT;
import com.google.transit.realtime.GtfsRealtimeNYCT.NyctTripDescriptor;

import org.apache.commons.text.WordUtils;


public class SubwayAPI {
    private FeedMessage feed;
    private String key;
    private SubwayStops subwayStops;
    private ApplicationExecutors executors = new ApplicationExecutors();

    public SubwayAPI(String subwayService, Context context) {
        key = context.getString(R.string.subway_key);
        subwayStops = new SubwayStops(context.getResources().openRawResource(R.raw.stops));
        executors.getBackground().execute(() -> {
            feed = getFeed(subwayService);
        });
    }

    public interface subwayResponseListener {
        void onResponse(ArrayList<SubwayResult> subwayResults);
    }

    //Hashmap that converts Service Name/ServiceID to Feed URL
    //(A URL that contains information about the selected service)
    private HashMap<String, String> getFeedURLs() {
        HashMap<String, String> feedURLs = new HashMap<>();

        feedURLs.put("1", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs");
        feedURLs.put("2", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs");
        feedURLs.put("3", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs");
        feedURLs.put("4", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs");
        feedURLs.put("5", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs");
        feedURLs.put("6", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs");
        feedURLs.put("7", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs");
        feedURLs.put("S", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs");
        feedURLs.put("GS", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs");
        feedURLs.put("A", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-ace");
        feedURLs.put("C", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-ace");
        feedURLs.put("E", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-ace");
        feedURLs.put("H", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-ace");
        feedURLs.put("FS", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-ace");
        feedURLs.put("SF", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-ace");
        feedURLs.put("SR", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-ace");
        feedURLs.put("B", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-bdfm");
        feedURLs.put("D", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-bdfm");
        feedURLs.put("F", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-bdfm");
        feedURLs.put("M", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-bdfm");
        feedURLs.put("G", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-g");
        feedURLs.put("J", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-jz");
        feedURLs.put("Z", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-jz");
        feedURLs.put("N", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-nqrw");
        feedURLs.put("Q", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-nqrw");
        feedURLs.put("R", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-nqrw");
        feedURLs.put("W", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-nqrw");
        feedURLs.put("L", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-l");
        feedURLs.put("SI", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-si");
        feedURLs.put("SS", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-si");
        feedURLs.put("SIR", "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-si");

        return feedURLs;
    }

    //Registry used to get certain objects in the
    //Feed URL that contain subway information
    private ExtensionRegistry getRegistry() {
        ExtensionRegistry registry = ExtensionRegistry.newInstance();
        registry.add(GtfsRealtimeNYCT.nyctFeedHeader);
        registry.add(GtfsRealtimeNYCT.nyctStopTimeUpdate);
        registry.add(GtfsRealtimeNYCT.nyctTripDescriptor);
        return registry;
    }

    //Extracts FeedMessage (Think of it as JSON Response, but GTFS format)
    //from the Feed URL that is received by the getFeedURLs function
    private FeedMessage getFeed(String subwayService) {
        FeedMessage feed = null;
        URL url = null;
        try {
            url = new URL(getFeedURLs().get(subwayService));
        } catch (MalformedURLException e) {
            Log.e("URL Error", e.toString());
        }
        InputStream subway_stream = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            Log.e("Connection Error", e.toString());
        }
        try {
            connection.addRequestProperty("x-api-key", key);
            connection.setInstanceFollowRedirects(true);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                subway_stream = connection.getInputStream();
            } else {
                throw new ConnectException();
            }
        } catch (MalformedURLException e) {
            Log.e("Problem accessing", url.toString());
        } catch (Exception e) {
            Log.e("Response Error", e.toString());
        }

        try {
            feed = FeedMessage.parseFrom(subway_stream, getRegistry());
        } catch (IOException e) {
            Log.e("FeedMessage Error", e.toString());
        }
        if (connection != null) {
            connection.disconnect();
        }
        return feed;
    }

    public void getData(SubwayInput subwayInput, subwayResponseListener subwayResponseListener) {
        //Runs the getTrips method in the background thread
        executors.getBackground().execute(() -> {
            getTrips(subwayInput, feed.getEntityList(), subwayResponseListener);
        });
    }

    private void getTrips(SubwayInput subwayInput, List<FeedEntity> entityList, subwayResponseListener subwayResponseListener) {
        HashMap<String, SubwayResult> tripResults = new HashMap<>();

        String inputtedRouteID = subwayInput.getSubwayAPIService();
        String inputtedRouteDirection = subwayInput.getSubwayDirection();
        String inputtedStopName = subwayInput.getSubwayAPIStopName();

        for (FeedEntity entity : entityList) {
            if (entity.hasTripUpdate()) {
                String routeID = entity.getTripUpdate().getTrip().getRouteId();
                NyctTripDescriptor tripDescriptor = entity.getTripUpdate().getTrip().getExtension(GtfsRealtimeNYCT.nyctTripDescriptor);
                String routeDirection = tripDescriptor.getDirection().toString();
                //Checks to see that the route and direction of this subway trip matches what we're looking for
                if (routeID.equals(inputtedRouteID) && routeDirection.equals(inputtedRouteDirection)) {
                    String trainID = tripDescriptor.getTrainId();
                    List<StopTimeUpdate> stopTimeUpdates = entity.getTripUpdate().getStopTimeUpdateList();
                    int errorCount = 0;
                    for (int i = 0; i < stopTimeUpdates.size(); i++) {
                        StopTimeUpdate stopTimeUpdate = stopTimeUpdates.get(i);
                        String stopID = stopTimeUpdate.getStopId();
                        String stopName = subwayStops.getStationName(stopID);
                        //This checks if the stop name is empty
                        //(Usually, it's empty because there's an imaginary stop
                        //when the subway train transfers between boroughs)
                        if(stopName == ""){
                            errorCount += 1;
                        }
                        if (stopName.equals(inputtedStopName)) {
                            //When the selected train schedule passes by the stop we're looking for,
                            //the count is converted to string after ignoring the stops with an empty stop name
                            String stopsUntilDestination = String.valueOf(i-errorCount);
                            //Removes seconds from timestamp
                            Date timestampToDatetime = new Date(stopTimeUpdate.getArrival().getTime() * 1000);
                            Calendar timeStamp = Calendar.getInstance();
                            timeStamp.setTime(timestampToDatetime);
                            timeStamp.set(Calendar.SECOND, 0);
                            timestampToDatetime = timeStamp.getTime();
                            //Removes seconds from current time
                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.SECOND, 0);
                            Date currentDate = cal.getTime();
                            //Calculating Wait Time
                            Long timeDifference = timestampToDatetime.getTime() - currentDate.getTime();
                            Long waitTime = timeDifference / (60 * 1000);
                            //Converts Date to Arrival Time
                            DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
                            String arrivalTime = dateFormat.format(timestampToDatetime);
                            if(waitTime > 0) {
                                //Assigns relevant information into SubwayResult object
                                SubwayResult subwayResult = new SubwayResult();
                                subwayResult.setStopsUntilDestination(stopsUntilDestination);
                                subwayResult.setTrainID(trainID);
                                subwayResult.setRouteID(routeID);
                                subwayResult.setRouteDirection(routeDirection);
                                subwayResult.setWaitTime(String.valueOf(waitTime));
                                subwayResult.setArrivalTime(arrivalTime);
                                //Assigns object to hashmap
                                //(Key will be used to get the vehicle status
                                //when a following FeedEntity object has a Vehicle object)
                                tripResults.put(trainID, subwayResult);
                            }
                        }
                    }
                }
            }
            if (entity.hasVehicle()) {
                VehiclePosition vehiclePosition = entity.getVehicle();
                TripDescriptor tripDescriptor = vehiclePosition.getTrip();
                String routeID = tripDescriptor.getRouteId();
                String routeDirection = tripDescriptor.getExtension(GtfsRealtimeNYCT.nyctTripDescriptor).getDirection().toString();
                if (routeID.equals(inputtedRouteID) && routeDirection.equals(inputtedRouteDirection)) {
                    String trainID = tripDescriptor.getExtension(GtfsRealtimeNYCT.nyctTripDescriptor).getTrainId();
                    //If the train has passed our searched subway stop, get the train's current status
                    if (tripResults.containsKey(trainID)) {
                        //Gets Train's Current Status (Initially = Unknown)
                        SubwayResult subwayResult = tripResults.get(trainID);
                        String trainCurrentStatus = vehiclePosition.getCurrentStatus().toString();
                        trainCurrentStatus = WordUtils.capitalizeFully(trainCurrentStatus.replace('_', ' '));
                        String trainStopID = vehiclePosition.getStopId();
                        String currentStatus = trainCurrentStatus + " " + subwayStops.getStationName(trainStopID);
                        subwayResult.setCurrentStatus(currentStatus);
                        tripResults.replace(trainID, subwayResult);
                    }
                }
            }
        }
        ArrayList<SubwayResult> subwayResults = new ArrayList<>(tripResults.values());
        Collections.sort(subwayResults);
        subwayResponseListener.onResponse(subwayResults);
    }
}

