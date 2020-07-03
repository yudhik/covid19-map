package com.its.covid19.model;

import org.neo4j.driver.types.Node;

public class Location {

  public static final String CREATE_STATEMENT = "CREATE (location:Location {" + "id: $id, "
      + "address: $address, " + "longitude: $longitude, " + "latitude: $latitude, "
      + "altitude: $altitude" + "}) RETURN location";

  public static final String FIND_POSITIVE_COVID_LOCATION =
      "MATCH (condition:Condition{fever:true,cough:true,shortnessOfBreath:true}) "
          + "-[:LOCATED]-> (location) WHERE condition.temperature >= $bodyHeat RETURN location";

  public static final String RELATE_CONDITION_LOCATION =
      "MATCH (condition:Condition{id:$conditionId}), (location:Location{id:$locationId}) "
          + "CREATE (condition) "
          + "-[l:LOCATED {createdDate : localdatetime({ timezone: 'Etc/UTC' })}]-> "
          + "(location) RETURN location";

  public static Location from(Node node) {
    return new Location(node.get("id").asString(), node.get("address").asString(),
        node.get("longitude").asDouble(), node.get("latitude").asDouble(),
        node.get("altitude").asDouble());
  }

  private String id;
  private String address;
  private double longitude;
  private double latitude;
  private double altitude;

  public Location() {}

  public Location(String id, String address, double longitude, double latitude, double altitude) {
    this.id = id;
    this.address = address;
    this.longitude = longitude;
    this.latitude = latitude;
    this.altitude = altitude;
  }

  public String getAddress() {
    return address;
  }

  public double getAltitude() {
    return altitude;
  }

  public String getId() {
    return id;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public void setAltitude(double altitude) {
    this.altitude = altitude;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Location [id=");
    builder.append(id);
    builder.append(", address=");
    builder.append(address);
    builder.append(", longitude=");
    builder.append(longitude);
    builder.append(", latitude=");
    builder.append(latitude);
    builder.append(", altitude=");
    builder.append(altitude);
    builder.append("]");
    return builder.toString();
  }

}
