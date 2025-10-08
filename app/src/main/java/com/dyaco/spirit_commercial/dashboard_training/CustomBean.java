package com.dyaco.spirit_commercial.dashboard_training;

public class CustomBean {
    private double maxSpeedMph;
    private double maxSpeedKmh;
    private int totalTime = -1;

    private String diagramLevelOrSpeed;
    private String diagramIncline; //level, not value

    public double getMaxSpeedMph() {
        return maxSpeedMph;
    }

    public void setMaxSpeedMph(double maxSpeedMph) {
        this.maxSpeedMph = maxSpeedMph;
    }

    public double getMaxSpeedKmh() {
        return maxSpeedKmh;
    }

    public void setMaxSpeedKmh(double maxSpeedKmh) {
        this.maxSpeedKmh = maxSpeedKmh;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public String getDiagramLevelOrSpeed() {
        return diagramLevelOrSpeed;
    }

    public void setDiagramLevelOrSpeed(String diagramLevelOrSpeed) {
        this.diagramLevelOrSpeed = diagramLevelOrSpeed;
    }

    public String getDiagramIncline() {
        return diagramIncline;
    }

    public void setDiagramIncline(String diagramIncline) {
        this.diagramIncline = diagramIncline;
    }
}
