package io.anthonylombardo321.github.mtatracker;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;

//Java Class that gets converts the Subway Stop ID
//to the Station Name from MTA Stops file
public class SubwayStops {
    HashMap<String, String> stops;
    public SubwayStops(InputStream stopInputStream){
        stops = getStopData(stopInputStream);
    }

    private HashMap<String, String> getStopData(InputStream stopInputStream){
        HashMap<String, String> stops = new HashMap<>();
        try{
            BufferedReader stopReader = new BufferedReader(new InputStreamReader(stopInputStream, Charset.forName("UTF-8")));
            String line = "";
            int count = 0;
            while((line = stopReader.readLine()) != null){
                if(count >= 1) {
                    String[] row = line.split(",");
                    String stop_id = row[0];
                    String stop_name = row[2];
                    stops.put(stop_id, stop_name);
                }
                count+=1;
            }
            stopReader.close();
        }
        catch(IOException ioException){
            Log.e("SubwayStop IOException", ioException.toString());
        }
        return stops;
    }

    public String getStationName(String stopID){
        String stationName = stops.get(stopID);
        if(stationName == null){
            Log.e("SubwayStops Error", stopID + " not found in stops.csv");
            return "";
        }
        else{
            return stationName;
        }
    }
}
