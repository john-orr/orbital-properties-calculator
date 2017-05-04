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

    public Double getSemiMajorAxis() {
        return semiMajorAxis;
    }

    public Double getApoapsisHeight() {
        return apoapsisHeight;
    }

    public Double getApoapsisHeightAS() {
        return apoapsisHeightAS;
    }

    public Double getPeriapsisHeight() {
        return periapsisHeight;
    }

    public Double getPeriapsisHeightAS() {
        return periapsisHeightAS;
    }

    public Double getEccentricity() {
        return eccentricity;
    }

    public void setOrbitalPeriod(Double orbitalPeriod) {
        if (this.orbitalPeriod != null) {
            throw new RecalculationException("Attemped recalculation of orbital period");
        }
        this.orbitalPeriod = orbitalPeriod;
    }

    public void setSemiMajorAxis(Double semiMajorAxis) {
        if (this.semiMajorAxis != null) {
            throw new RecalculationException("Attemped recalculation of semi-major axis");
        }
        this.semiMajorAxis = semiMajorAxis;
    }

    public void setApoapsisHeight(Double apoapsisHeight) {
        if (this.apoapsisHeight != null) {
            throw new RecalculationException("Attempted recalculation of apoapsis height");
        }
        if (getPeriapsisHeight() != null && getPeriapsisHeight() > apoapsisHeight) {
            throw new ImpossibleOrbitException("Apoapsis is lower than periapsis");
        }
        this.apoapsisHeight = apoapsisHeight;
    }

    public void setApoapsisHeightAS(Double apoapsisHeightAS) {
        this.apoapsisHeightAS = apoapsisHeightAS;
    }

    public void setPeriapsisHeight(Double periapsisHeight) {
        if (this.periapsisHeight != null) {
            throw new RecalculationException("Attempted recalculation of periapsis height");
        }
        if (getApoapsisHeight() != null && getApoapsisHeight() < periapsisHeight) {
            throw new ImpossibleOrbitException("Periapsis is higher than apoapsis");
        }
        this.periapsisHeight = periapsisHeight;
    }

    public void setPeriapsisHeightAS(Double periapsisHeightAS) {
        this.periapsisHeightAS = periapsisHeightAS;
    }

    public void setEccentricity(Double eccentricity) {
        if (this.eccentricity != null) {
            throw new RecalculationException("Attempted recalculation of eccentricity");
        }
        this.eccentricity = eccentricity;
    }

    public void setOrbitalPeriod(String orbitalPeriod) {
        try {
            setOrbitalPeriod(Double.valueOf(orbitalPeriod));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid value for orbital period (T): " + orbitalPeriod);
        }
    }

    public void setSemiMajorAxis(String semiMajorAxis) {
        try {
            setSemiMajorAxis(Double.valueOf(semiMajorAxis));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid value for orbital period (T): " + semiMajorAxis);
        }
    }

    public void setApoapsisHeight(String apoapsisHeight) {
        try {
            setApoapsisHeight(Double.valueOf(apoapsisHeight));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid value for orbital period (T): " + apoapsisHeight);
        }
    }

    public void setPeriapsisHeight(String periapsisHeight) {
        try {
            setPeriapsisHeight(Double.valueOf(periapsisHeight));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid value for orbital period (T): " + periapsisHeight);
        }
    }

    public void setEccentricity(String eccentricity) {
        try {
            setEccentricity(Double.valueOf(eccentricity));
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
