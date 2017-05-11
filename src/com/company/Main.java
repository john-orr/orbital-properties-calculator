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
    static final long SECONDS_IN_MINUTE = 60;
    static final long SECONDS_IN_HOUR = SECONDS_IN_MINUTE*60;
    static Map<String, BodyProperties> bodyPropertiesMap;
    static BodyProperties bodyProperties;
    static OrbitalProperties orbitalProperties;
    static String resultPrecision;

    public static void main(String[] args) throws IOException {
        populateBodyMap();
        readInputParameters();
        if (knownValuesAreValid()) {
            calculateUnknownValues();
            print();
        }
    }

    private static void print() {
        String numberFormat = "." + resultPrecision + "f";
        System.out.println(formatOrbitalPeriod());
        System.out.println(
                String.format("Semi-Major Axis\t\t%," + numberFormat + " m", orbitalProperties.getSemiMajorAxis()));
        System.out.println(String.format("Apoapsis\t\t\t%," + numberFormat + " m (%," + numberFormat + " m)",
                orbitalProperties.getApoapsisHeight(), orbitalProperties.getApoapsisHeightAS()));
        System.out.println(String.format("Periapsis\t\t\t%," + numberFormat + " m (%," + numberFormat + " m)",
                orbitalProperties.getPeriapsisHeight(), orbitalProperties.getPeriapsisHeightAS()));
        System.out.println(String.format("Eccentricity\t\t%.4f", orbitalProperties.getEccentricity()));
    }

    private static String formatOrbitalPeriod() {
        StringBuilder output = new StringBuilder("Orbital Period\t\t");
        long orbitalPeriod = Math.round(orbitalProperties.getOrbitalPeriod());
        if (orbitalPeriod > SECONDS_IN_HOUR) {
            long numHours = orbitalPeriod / SECONDS_IN_HOUR;
            output.append(numHours).append("h ");
            orbitalPeriod -= numHours * SECONDS_IN_HOUR;
        }
        if (orbitalPeriod >= SECONDS_IN_MINUTE) {
            long numMinutes = orbitalPeriod / SECONDS_IN_MINUTE;
            output.append(numMinutes).append("m ");
            orbitalPeriod -= numMinutes * SECONDS_IN_MINUTE;
        }
        if (orbitalPeriod > 0) {
            output.append(orbitalPeriod).append("s");
        }
        return output.toString();
    }

    private static void calculateUnknownValues() {
        if (orbitalProperties.getOrbitalPeriod() != null || orbitalProperties.getSemiMajorAxis() != null) {
            if (orbitalProperties.getOrbitalPeriod() != null) {
                calculateAFromT();
            } else {
                calculateTFromA();
            }
            if (countTier2Properties() == 0) {
                System.out.println("Assuming circular orbit");
                orbitalProperties.setEccentricity(0d);
                orbitalProperties.setPeriapsisHeight(orbitalProperties.getSemiMajorAxis());
                orbitalProperties.setApoapsisHeight(orbitalProperties.getSemiMajorAxis());
            } else {
                // One of e, rA and rP will have been provided
                Double a = orbitalProperties.getSemiMajorAxis();
                if (orbitalProperties.getEccentricity() != null) {
                    // 2ae = rA - rP and
                    // 2a = rA + rP therefore
                    // a + ae = rA or a(1+e)=rA
                    Double e = orbitalProperties.getEccentricity();
                    orbitalProperties.setApoapsisHeight(a * (1 + e));
                    orbitalProperties.setPeriapsisHeight(a * (1 - e));
                } else {
                    if (orbitalProperties.getApoapsisHeight() != null) {
                        orbitalProperties.setPeriapsisHeight(2 * a - orbitalProperties.getApoapsisHeight());
                    } else {
                        orbitalProperties.setApoapsisHeight(2 * a - orbitalProperties.getPeriapsisHeight());
                    }
                    calculateEFromRaAndRp();
                }
            }
        } else {
            // T and a both missing
            if (orbitalProperties.getEccentricity() != null) {
                // one of rA and rP will be present
                double e = orbitalProperties.getEccentricity();
                double ratioRaToRp = (1 + e) / (1 - e);
                if (orbitalProperties.getApoapsisHeight() != null) {
                    orbitalProperties.setPeriapsisHeight(orbitalProperties.getApoapsisHeight() / ratioRaToRp);
                } else {
                    orbitalProperties.setApoapsisHeight(orbitalProperties.getPeriapsisHeight() * ratioRaToRp);
                }
            } else {
                calculateEFromRaAndRp();
            }
            orbitalProperties.setSemiMajorAxis(
                    (orbitalProperties.getApoapsisHeight() + orbitalProperties.getPeriapsisHeight()) / 2);
            calculateTFromA();
        }
        validateHeightsAboveSurface();
        validateInsideSphereOfInfluence();
    }

    private static void validateInsideSphereOfInfluence() {
        if (bodyProperties.getSphereOfInfluence() != null &&
                orbitalProperties.getApoapsisHeight() > bodyProperties.getSphereOfInfluence()) {
            throw new ImpossibleOrbitException("This orbit is outside this body's sphere of influence");
        }
    }

    private static void calculateEFromRaAndRp() {
        Double rA = orbitalProperties.getApoapsisHeight();
        Double rP = orbitalProperties.getPeriapsisHeight();
        orbitalProperties.setEccentricity((rA - rP) / (rA + rP));
    }

    private static void validateHeightsAboveSurface() {
        if (orbitalProperties.getPeriapsisHeight() < bodyProperties.getRadius()) {
            throw new ImpossibleOrbitException("This orbit is below surface");
        } else if (orbitalProperties.getPeriapsisHeight() <
                bodyProperties.getRadius() + bodyProperties.getAtmosphereThickness()) {
            throw new ImpossibleOrbitException("This orbit is inside the atmosphere");
        }
        orbitalProperties.setApoapsisHeightAS(orbitalProperties.getApoapsisHeight() - bodyProperties.getRadius());
        orbitalProperties.setPeriapsisHeightAS(orbitalProperties.getPeriapsisHeight() - bodyProperties.getRadius());
    }

    private static void calculateTFromA() {
        double GM = G * bodyProperties.getMass();
        double A3 = Math.pow(orbitalProperties.getSemiMajorAxis(), 3);
        double squareRoot = 0.5d;
        orbitalProperties.setOrbitalPeriod(Math.pow((_4pi2 * A3) / GM, squareRoot));
    }

    private static void calculateAFromT() {
        double GM = G * bodyProperties.getMass();
        double T2 = Math.pow(orbitalProperties.getOrbitalPeriod(), 2);
        double cubedRoot = (double) 1 / 3;
        orbitalProperties.setSemiMajorAxis(Math.pow((GM * T2) / _4pi2, cubedRoot));
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

    private static void readInputParameters() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(new File("resources/known_values.properties")));
        bodyProperties = bodyPropertiesMap.get(properties.getProperty("body"));
        orbitalProperties = new OrbitalProperties();
        String orbitalPeriod = properties.getProperty("T");
        if (orbitalPeriod != null && !orbitalPeriod.isEmpty()) {
            orbitalProperties.setOrbitalPeriod(parseOrbitalPeriod(orbitalPeriod));
        }
        String semiMajorAxis = properties.getProperty("a");
        if (semiMajorAxis != null && !semiMajorAxis.isEmpty()) {
            semiMajorAxis = semiMajorAxis.replaceAll(",", "");
            orbitalProperties.setSemiMajorAxis(semiMajorAxis);
        }
        String apoapsisHeight = properties.getProperty("rA");
        if (apoapsisHeight != null && !apoapsisHeight.isEmpty()) {
            apoapsisHeight = apoapsisHeight.replaceAll(",", "");
            orbitalProperties.setApoapsisHeight(apoapsisHeight);
        }
        String periapsisHeight = properties.getProperty("rP");
        if (periapsisHeight != null && !periapsisHeight.isEmpty()) {
            periapsisHeight = periapsisHeight.replaceAll(",", "");
            orbitalProperties.setPeriapsisHeight(periapsisHeight);
        }
        String eccentricity = properties.getProperty("e");
        if (eccentricity != null && !eccentricity.isEmpty()) {
            orbitalProperties.setEccentricity(eccentricity);
        }
        resultPrecision = defaultIfNull(properties.getProperty("precision"), "0");
    }

    private static Double parseOrbitalPeriod(String orbitalPeriod) {
        orbitalPeriod = orbitalPeriod.replaceAll(",", "");
        String[] split = orbitalPeriod.split(" ");
        Double numSeconds = 0D;
        for (String numberAndUnit : split) {
            if (numberAndUnit.length() > 1) {
                String unit = numberAndUnit.substring(numberAndUnit.length() - 1);
                Double number = Double.valueOf(numberAndUnit.substring(0, numberAndUnit.length() - 1));
                switch (unit) {
                    case "s":
                        numSeconds += number;
                        break;
                    case "m":
                        numSeconds += number * SECONDS_IN_MINUTE;
                        break;
                    case "h":
                        numSeconds += number * SECONDS_IN_HOUR;
                        break;
                }
            }
        }
        return numSeconds;
    }

    private static String defaultIfNull(String precision, String defaultValue) {
        return precision != null && !precision.isEmpty() ? precision : defaultValue;
    }

    private static void populateBodyMap() {
        bodyPropertiesMap = new HashMap<>();
        bodyPropertiesMap
                .put("Kerbin", new BodyProperties(5.2915158 * Math.pow(10, 22), 600_000D, 70_000D, 84_159_286D));
        bodyPropertiesMap.put("Mun", new BodyProperties(9.7599066 * Math.pow(10, 20), 200_000D, 0D, 2_429_559.1D));
    }
}
