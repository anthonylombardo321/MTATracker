package io.anthonylombardo321.github.mtatracker;

import android.os.Parcel;
import android.os.Parcelable;

public class BusInput implements Parcelable {
    private String monitoringRef;
    private String busRoute;

    public BusInput(String monitoringRef, String busRoute){
        this.monitoringRef = monitoringRef;
        this.busRoute = busRoute;
    }

    public BusInput(){

    }

    public BusInput(Parcel source) {
        this.monitoringRef = source.readString();
        this.busRoute = source.readString();
    }

    public String getMonitoringRef() {
        return monitoringRef;
    }

    public void setMonitoringRef(String monitoringRef) {
        this.monitoringRef = monitoringRef;
    }

    public String getBusRoute() {
        return busRoute;
    }

    public void setBusRoute(String busRoute) {
        this.busRoute = busRoute;
    }


    //Parcelable Methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(monitoringRef);
        dest.writeString(busRoute);
    }
    public static final Creator<BusInput> CREATOR = new Creator<BusInput>() {
        @Override
        public BusInput createFromParcel(Parcel source) {
            return new BusInput(source);
        }

        @Override
        public BusInput[] newArray(int size) {
            return new BusInput[size];
        }
    };
}
