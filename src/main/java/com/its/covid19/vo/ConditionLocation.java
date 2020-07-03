package com.its.covid19.vo;

import java.time.LocalDateTime;
import com.its.covid19.model.Location;

public class ConditionLocation {

  private String id;
  private float temperature;
  private boolean cough;
  private boolean fever;
  private boolean shortnessOfBreath;
  private LocalDateTime createdDate;
  private Location location;

  public ConditionLocation() {}

  public ConditionLocation(String id, float temperature, boolean cough, boolean fever,
      boolean shortnessOfBreath, LocalDateTime createdDate, Location location) {
    this.id = id;
    this.temperature = temperature;
    this.cough = cough;
    this.fever = fever;
    this.shortnessOfBreath = shortnessOfBreath;
    this.createdDate = createdDate;
    this.location = location;
  }

  public LocalDateTime getCreatedDate() {
    return createdDate;
  }

  public String getId() {
    return id;
  }

  public Location getLocation() {
    return location;
  }

  public float getTemperature() {
    return temperature;
  }

  public boolean isCough() {
    return cough;
  }

  public boolean isFever() {
    return fever;
  }

  public boolean isShortnessOfBreath() {
    return shortnessOfBreath;
  }

  public void setCough(boolean cough) {
    this.cough = cough;
  }

  public void setCreatedDate(LocalDateTime createdDate) {
    this.createdDate = createdDate;
  }

  public void setFever(boolean fever) {
    this.fever = fever;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public void setShortnessOfBreath(boolean shortnessOfBreath) {
    this.shortnessOfBreath = shortnessOfBreath;
  }

  public void setTemperature(float temperature) {
    this.temperature = temperature;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ConditionLocation [id=");
    builder.append(id);
    builder.append(", temperature=");
    builder.append(temperature);
    builder.append(", cough=");
    builder.append(cough);
    builder.append(", fever=");
    builder.append(fever);
    builder.append(", shortnessOfBreath=");
    builder.append(shortnessOfBreath);
    builder.append(", createdDate=");
    builder.append(createdDate);
    builder.append(", ");
    builder.append(location.toString());
    builder.append("]");
    return builder.toString();
  }

}
