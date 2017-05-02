package com.company;

public class BodyProperties {

    private Double mass;
    private Double radius;
    private Double atmosphereThickness;

    BodyProperties(Double mass, Double radius, Double atmosphereThickness) {
        this.mass = mass;
        this.radius = radius;
        this.atmosphereThickness = atmosphereThickness;
    }

    public Double getMass() {
        return mass;
    }

    public void setMass(Double mass) {
        this.mass = mass;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public Double getAtmosphereThickness() {
        return atmosphereThickness;
    }

    public void setAtmosphereThickness(Double atmosphereThickness) {
        this.atmosphereThickness = atmosphereThickness;
    }
}
