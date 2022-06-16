package io.anthonylombardo321.github.mtatracker;

import android.os.Parcel;
import android.os.Parcelable;

public class BusResult implements Comparable, Parcelable {

    private String stopsUntilBusStop;
    private String arrivalTime;
    private String waitTime;
    private String distanceFromStop;
    private String busID;
    private String busRoute;
    private boolean expanded;

    public BusResult(){
        this.stopsUntilBusStop = "";
        this.arrivalTime = "";
        this.waitTime = "";
        this.distanceFromStop = "";
        this.busID = "";
        this.busRoute = "";
        expanded = false;
    }

    public BusResult(Parcel source) {
        this.stopsUntilBusStop = source.readString();
        this.arrivalTime = source.readString();
        this.waitTime = source.readString();
        this.distanceFromStop = source.readString();
        this.busID = source.readString();
        this.busRoute = source.readString();
    }

    public String getStopsUntilBusStop() {
        return stopsUntilBusStop;
    }

    public void setStopsUntilBusStop(String stopsUntilBusStop) {
        this.stopsUntilBusStop = stopsUntilBusStop;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(String waitTime) {
        this.waitTime = waitTime;
    }

    public String getDistanceFromStop() {
        return distanceFromStop;
    }

    public void setDistanceFromStop(String distanceFromStop) {
        this.distanceFromStop = distanceFromStop;
    }

    public String getBusID() {
        return busID;
    }

    public void setBusID(String busID) {
        this.busID = busID;
    }

    public String getBusRoute() {
        return busRoute;
    }

    public void setBusRoute(String busRoute) {
        this.busRoute = busRoute;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    //Sorts by Wait Time
    public int compareTo(Object o) {
        Long currentBusWaitTime = Long.valueOf(this.getWaitTime());
        Long inputtedBusWaitTime = Long.valueOf(((BusResult) o).getWaitTime());
        return currentBusWaitTime.compareTo(inputtedBusWaitTime);
    }

    //Parcelable Methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stopsUntilBusStop);
        dest.writeString(arrivalTime);
        dest.writeString(waitTime);
        dest.writeString(distanceFromStop);
        dest.writeString(busID);
        dest.writeString(busRoute);
    }

    public static final Creator<BusResult> CREATOR = new Creator<BusResult>() {
        @Override
        public BusResult createFromParcel(Parcel source) {
            return new BusResult(source);
        }

        @Override
        public BusResult[] newArray(int size) {
            return new BusResult[size];
        }
    };
}
