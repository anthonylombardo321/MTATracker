package io.anthonylombardo321.github.mtatracker;

import android.os.Parcel;
import android.os.Parcelable;

public class SubwayInput implements Parcelable {
    private String subwayAPIStopName;
    private String subwayAPIService;
    private String subwayDirection;

    public SubwayInput(String subwayAPIStopName, String subwayAPIService, String subwayDirection) {
        this.subwayAPIStopName = subwayAPIStopName;
        this.subwayAPIService = subwayAPIService;
        this.subwayDirection = subwayDirection;
    }

    public SubwayInput(){

    }

    public SubwayInput(Parcel source) {
        this.subwayAPIStopName = source.readString();
        this.subwayAPIService = source.readString();
        this.subwayDirection = source.readString();
    }

    public String getSubwayAPIStopName() {
        return subwayAPIStopName;
    }

    public void setSubwayAPIName(String subwayAPIStopName) {
        this.subwayAPIStopName = subwayAPIStopName;
    }

    public String getSubwayAPIService() {
        return subwayAPIService;
    }

    public void setSubwayAPIService(String subwayAPIService) {
        this.subwayAPIService = subwayAPIService;
    }

    public String getSubwayDirection() {
        return subwayDirection;
    }

    public void setSubwayDirection(String subwayDirection) {
        this.subwayDirection = subwayDirection;
    }

    //Parcelable Methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(subwayAPIStopName);
        dest.writeString(subwayAPIService);
        dest.writeString(subwayDirection);
    }
    public static final Creator<SubwayInput> CREATOR = new Creator<SubwayInput>() {
        @Override
        public SubwayInput createFromParcel(Parcel source) {
            return new SubwayInput(source);
        }

        @Override
        public SubwayInput[] newArray(int size) {
            return new SubwayInput[size];
        }
    };
}
