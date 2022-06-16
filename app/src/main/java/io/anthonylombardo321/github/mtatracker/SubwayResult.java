package io.anthonylombardo321.github.mtatracker;

import android.os.Parcel;
import android.os.Parcelable;

public class SubwayResult implements Comparable, Parcelable {
    private String stopsUntilDestination;
    private String arrivalTime;
    private String waitTime;
    private String currentStatus;
    private String trainID;
    private String routeID;
    private String routeDirection;
    private boolean expanded;

    public SubwayResult() {
        stopsUntilDestination = "";
        arrivalTime = "";
        waitTime = "";
        currentStatus = "Unknown";
        trainID = "";
        routeID = "";
        routeDirection = "";
        expanded = false;
    }

    public SubwayResult(Parcel source) {
        this.stopsUntilDestination = source.readString();
        this.arrivalTime = source.readString();
        this.waitTime = source.readString();
        this.currentStatus = source.readString();
        this.trainID = source.readString();
        this.routeID = source.readString();
        this.routeDirection = source.readString();
    }

    public String getStopsUntilDestination() {
        return stopsUntilDestination;
    }

    public void setStopsUntilDestination(String stopsUntilDestination) {
        this.stopsUntilDestination = stopsUntilDestination;
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

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getTrainID() {
        return trainID;
    }

    public void setTrainID(String trainID) {
        this.trainID = trainID;
    }
    public String getRouteID() {
        return routeID;
    }

    public void setRouteID(String routeID) {
        this.routeID = routeID;
    }

    public String getRouteDirection() {
        return routeDirection;
    }

    public void setRouteDirection(String routeDirection) {
        this.routeDirection = routeDirection;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    //Sorts by Wait Time
    @Override
    public int compareTo(Object o) {
        Long currentTrainWaitTime = Long.valueOf(this.getWaitTime());
        Long inputtedTrainWaitTime = Long.valueOf(((SubwayResult) o).getWaitTime());
        return currentTrainWaitTime.compareTo(inputtedTrainWaitTime);
    }

    //Parcelable Methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stopsUntilDestination);
        dest.writeString(arrivalTime);
        dest.writeString(waitTime);
        dest.writeString(currentStatus);
        dest.writeString(trainID);
        dest.writeString(routeID);
        dest.writeString(routeDirection);
    }
    public static final Creator<SubwayResult> CREATOR = new Creator<SubwayResult>() {
        @Override
        public SubwayResult createFromParcel(Parcel source) {
            return new SubwayResult(source);
        }

        @Override
        public SubwayResult[] newArray(int size) {
            return new SubwayResult[size];
        }
    };
}
