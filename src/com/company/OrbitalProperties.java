package com.company;

public class OrbitalProperties {

    private Double orbitalPeriod;
    private Double semiMajorAxis;
    private Double apoapsisHeight;
    private Double apoapsisHeightAS;
    private Double periapsisHeight;
    private Double periapsisHeightAS;
    private Double eccentricity;

    public Double getOrbitalPeriod() {
        return orbitalPeriod;
    }

    public void setOrbitalPeriod(Double orbitalPeriod) {
        this.orbitalPeriod = orbitalPeriod;
    }

    public void setOrbitalPeriod(String orbitalPeriod) {
        try {
            this.orbitalPeriod = Double.valueOf(orbitalPeriod);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid value for orbital period (T): " + orbitalPeriod);
        }
    }

    public Double getSemiMajorAxis() {
        return semiMajorAxis;
    }

    public void setSemiMajorAxis(Double semiMajorAxis) {
        this.semiMajorAxis = semiMajorAxis;
    }

    public void setSemiMajorAxis(String semiMajorAxis) {
        try {
            this.semiMajorAxis = Double.valueOf(semiMajorAxis);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid value for orbital period (T): " + semiMajorAxis);
        }
    }

    public Double getApoapsisHeight() {
        return apoapsisHeight;
    }

    public void setApoapsisHeight(Double apoapsisHeight) {
        this.apoapsisHeight = apoapsisHeight;
    }

    public void setApoapsisHeight(String apoapsisHeight) {
        try {
            this.apoapsisHeight = Double.valueOf(apoapsisHeight);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid value for orbital period (T): " + apoapsisHeight);
        }
    }

    public Double getApoapsisHeightAS() {
        return apoapsisHeightAS;
    }

    public void setApoapsisHeightAS(Double apoapsisHeightAS) {
        if (apoapsisHeightAS < 0) {
            throw new ImpossibleOrbitException("Apoapsis is below surface. Value=" + apoapsisHeightAS);
        }
        this.apoapsisHeightAS = apoapsisHeightAS;
    }

    public Double getPeriapsisHeight() {
        return periapsisHeight;
    }

    public void setPeriapsisHeight(Double periapsisHeight) {
        this.periapsisHeight = periapsisHeight;
    }

    public void setPeriapsisHeight(String periapsisHeight) {
        try {
            this.periapsisHeight = Double.valueOf(periapsisHeight);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid value for orbital period (T): " + periapsisHeight);
        }
    }

    public Double getPeriapsisHeightAS() {
        return periapsisHeightAS;
    }

    public void setPeriapsisHeightAS(Double periapsisHeightAS) {
        if (periapsisHeightAS < 0) {
            throw new ImpossibleOrbitException("Periapsis is below surface. Value=" + periapsisHeightAS);
        }
        this.periapsisHeightAS = periapsisHeightAS;
    }

    public Double getEccentricity() {
        return eccentricity;
    }

    public void setEccentricity(Double eccentricity) {
        this.eccentricity = eccentricity;
    }

    public void setEccentricity(String eccentricity) {
        try {
            this.eccentricity = Double.valueOf(eccentricity);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid value for orbital period (T): " + eccentricity);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrbitalProperties)) return false;

        OrbitalProperties that = (OrbitalProperties) o;

        if (orbitalPeriod != null ? !orbitalPeriod.equals(that.orbitalPeriod) : that.orbitalPeriod != null)
            return false;
        if (semiMajorAxis != null ? !semiMajorAxis.equals(that.semiMajorAxis) : that.semiMajorAxis != null)
            return false;
        if (apoapsisHeight != null ? !apoapsisHeight.equals(that.apoapsisHeight) : that.apoapsisHeight != null)
            return false;
        if (periapsisHeight != null ? !periapsisHeight.equals(that.periapsisHeight) : that.periapsisHeight != null)
            return false;
        return !(eccentricity != null ? !eccentricity.equals(that.eccentricity) : that.eccentricity != null);

    }

    @Override
    public int hashCode() {
        int result = orbitalPeriod != null ? orbitalPeriod.hashCode() : 0;
        result = 31 * result + (semiMajorAxis != null ? semiMajorAxis.hashCode() : 0);
        result = 31 * result + (apoapsisHeight != null ? apoapsisHeight.hashCode() : 0);
        result = 31 * result + (periapsisHeight != null ? periapsisHeight.hashCode() : 0);
        result = 31 * result + (eccentricity != null ? eccentricity.hashCode() : 0);
        return result;
    }
}
