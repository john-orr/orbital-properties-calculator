package com.company;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Main {

    static final double G = 6.67408 * Math.pow(10, -11);
    static final double _4pi2 = 4 * Math.pow(Math.PI, 2);
    static Map<String, BodyProperties> bodyPropertiesMap;
    static BodyProperties bodyProperties;
    static OrbitalProperties orbitalProperties;

    public static void main(String[] args) throws IOException {
        populateBodyMap();
        readKnownValues();
        if (knownValuesAreValid()) {
            calculateUnknownValues();
        }
        print();
    }

    private static void print() {
        System.out.println("Orbital Period=\t\t\t" + orbitalProperties.getOrbitalPeriod());
        System.out.println("Semi-Major Axis=\t\t" + orbitalProperties.getSemiMajorAxis());
        System.out.println("Apoapsis Height=\t\t" + orbitalProperties.getApoapsisHeight());
        System.out.println("Apoapsis Height AS=\t\t" + orbitalProperties.getApoapsisHeightAS());
        System.out.println("Periapsis Height=\t\t" + orbitalProperties.getPeriapsisHeight());
        System.out.println("Periapsis Height AS=\t" + orbitalProperties.getPeriapsisHeightAS());
        System.out.println("Eccentricity=\t\t\t" + orbitalProperties.getEccentricity());
    }

    private static void calculateUnknownValues() {
        if (orbitalProperties.getOrbitalPeriod() != null) {
            orbitalProperties.setSemiMajorAxis(calculateAFromT());
        } else if (orbitalProperties.getSemiMajorAxis() != null) {
            orbitalProperties.setOrbitalPeriod(calculateTFromA());
        }
        if (orbitalProperties.getOrbitalPeriod() != null && orbitalProperties.getSemiMajorAxis() != null) {
            if (countTier2Properties() == 0) {
                System.out.println("Assuming circular orbit");
                orbitalProperties.setEccentricity(0d);
                orbitalProperties.setPeriapsisHeight(orbitalProperties.getSemiMajorAxis());
                orbitalProperties.setApoapsisHeight(orbitalProperties.getSemiMajorAxis());
            } else {
                // One of e, rA and rP will have been provided
                if (orbitalProperties.getEccentricity() != null) {
                    // 2ae = rA - rP and
                    // 2a = rA + rP therefore
                    // a + ae = rA or a(1+e)=rA
                    Double a = orbitalProperties.getSemiMajorAxis();
                    Double e = orbitalProperties.getEccentricity();
                    orbitalProperties.setApoapsisHeight(a * (1 + e));
                    orbitalProperties.setPeriapsisHeight(a * (1 - e));
                }
                if (orbitalProperties.getApoapsisHeight() != null) {
                    Double a = orbitalProperties.getSemiMajorAxis();
                    orbitalProperties.setPeriapsisHeight(2*a - orbitalProperties.getApoapsisHeight());
                }
                if (orbitalProperties.getPeriapsisHeight() != null) {
                    Double a = orbitalProperties.getSemiMajorAxis();
                    orbitalProperties.setApoapsisHeight(2*a - orbitalProperties.getPeriapsisHeight());
                }
            }
        }
        validateHeightsAboveSurface();
    }

    private static void validateHeightsAboveSurface() {
        if (orbitalProperties.getApoapsisHeight() < bodyProperties.getRadius()) {
            throw new ImpossibleOrbitException(
                    "Apoapsis is below surface. Value=" + (orbitalProperties.getApoapsisHeight() - bodyProperties
                            .getRadius()));
        } else if (orbitalProperties.getApoapsisHeight() < bodyProperties.getRadius() + bodyProperties
                .getAtmosphereThickness()) {
            throw new ImpossibleOrbitException("Apoapsis is inside the atmosphere. Value=" + (orbitalProperties
                    .getApoapsisHeight() - (bodyProperties.getRadius() + bodyProperties.getAtmosphereThickness())));
        }
        orbitalProperties.setApoapsisHeightAS(orbitalProperties.getApoapsisHeight() - bodyProperties.getRadius());
        if (orbitalProperties.getPeriapsisHeight() < bodyProperties.getRadius()) {
            throw new ImpossibleOrbitException(
                    "Periapsis is below surface. Value=" + (orbitalProperties.getPeriapsisHeight() - bodyProperties
                            .getRadius()));
        } else if (orbitalProperties.getApoapsisHeight() < bodyProperties.getRadius() + bodyProperties
                .getAtmosphereThickness()) {
            throw new ImpossibleOrbitException("Periapsis is inside the atmosphere. Value=" + (orbitalProperties
                    .getPeriapsisHeight() - (bodyProperties.getRadius() + bodyProperties.getAtmosphereThickness())));
        }
        orbitalProperties.setPeriapsisHeightAS(orbitalProperties.getPeriapsisHeight() - bodyProperties.getRadius());
    }

    private static Double calculateTFromA() {
        double GM = G * bodyProperties.getMass();
        double A3 = Math.pow(orbitalProperties.getSemiMajorAxis(), 3);
        double squareRoot = 0.5d;
        return Math.pow((_4pi2 * A3) / GM, squareRoot);
    }

    private static Double calculateAFromT() {
        double GM = G * bodyProperties.getMass();
        double T2 = Math.pow(orbitalProperties.getOrbitalPeriod(), 2);
        double cubedRoot = (double) 1 / 3;
        return Math.pow((GM * T2) / _4pi2, cubedRoot);
    }

    private static boolean knownValuesAreValid() {
        if (bodyProperties == null) {
            System.out.println("Reference body missing");
            return false;
        }
        if (orbitalProperties.getOrbitalPeriod() != null && orbitalProperties.getSemiMajorAxis() != null) {
            System.out.println("Don't provide both orbital period (T) and semi major axis (a)");
            return false;
        } else if (orbitalProperties.getOrbitalPeriod() == null && orbitalProperties.getSemiMajorAxis() == null) {
            // both T and a missing then we need 2 out of e, rA and rP to be present
            int numPresent = countTier2Properties();
            if (numPresent < 2) {
                System.out.println("Not enough information provided");
                return false;
            } else if (numPresent > 2) {
                System.out.println("Too much information provided!");
                return false;
            }
        } else {
            // One of T and a has been provided
            int numPresent = countTier2Properties();
            if (numPresent > 1) {
                System.out.println("Too much information provided!");
                return false;
            }
            // if none provided then assume circular orbit
        }
        return true;
    }

    private static int countTier2Properties() {
        int numPresent = 0;
        numPresent += orbitalProperties.getEccentricity() != null ? 1 : 0;
        numPresent += orbitalProperties.getApoapsisHeight() != null ? 1 : 0;
        numPresent += orbitalProperties.getPeriapsisHeight() != null ? 1 : 0;
        return numPresent;
    }

    private static void readKnownValues() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(new File("resources/known_values.properties")));
        bodyProperties = bodyPropertiesMap.get(properties.getProperty("body"));
        orbitalProperties = new OrbitalProperties();
        String orbitalPeriod = properties.getProperty("T");
        if (orbitalPeriod != null && !orbitalPeriod.isEmpty()) {
            orbitalProperties.setOrbitalPeriod(orbitalPeriod);
        }
        String semiMajorAxis = properties.getProperty("a");
        if (semiMajorAxis != null && !semiMajorAxis.isEmpty()) {
            orbitalProperties.setSemiMajorAxis(semiMajorAxis);
        }
        String apoapsisHeight = properties.getProperty("rA");
        if (apoapsisHeight != null && !apoapsisHeight.isEmpty()) {
            orbitalProperties.setApoapsisHeight(apoapsisHeight);
        }
        String periapsisHeight = properties.getProperty("rP");
        if (periapsisHeight != null && !periapsisHeight.isEmpty()) {
            orbitalProperties.setPeriapsisHeight(periapsisHeight);
        }
        String eccentricity = properties.getProperty("e");
        if (eccentricity != null && !eccentricity.isEmpty()) {
            orbitalProperties.setEccentricity(eccentricity);
        }
    }

    private static void populateBodyMap() {
        bodyPropertiesMap = new HashMap<>();
        BodyProperties kerbinProperties = new BodyProperties();
        kerbinProperties.setMass(5.2915158 * Math.pow(10, 22));
        kerbinProperties.setRadius(600_000D);
        kerbinProperties.setAtmosphereThickness(70_000D);
        bodyPropertiesMap.put("Kerbin", kerbinProperties);
    }
}
