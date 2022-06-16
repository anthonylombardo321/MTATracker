package io.anthonylombardo321.github.mtatracker;

public class BusStop {
    private String monitoringRef;
    private String busStopName;
    private String busStopDirection;

    public BusStop(String monitoringRef, String busStopName, String busStopDirection){
        this.monitoringRef = monitoringRef;
        this.busStopName = busStopName;
        this.busStopDirection = busStopDirection;
    }

    public String getMonitoringRef() {
        return monitoringRef;
    }

    public void setMonitoringRef(String monitoringRef) {
        this.monitoringRef = monitoringRef;
    }

    public String getBusStopName() {
        return busStopName;
    }

    public void setBusStopName(String busStopName) {
        this.busStopName = busStopName;
    }

    public String getBusStopDirection() {
        return busStopDirection;
    }

    public void setBusStopDirection(String busStopDirection) {
        this.busStopDirection = busStopDirection;
    }
}
