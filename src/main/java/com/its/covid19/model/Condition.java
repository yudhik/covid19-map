package com.its.covid19.model;

import org.neo4j.driver.types.Node;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Condition {

  public static final String CREATE_CONDITION_LOCATION_STATEMENT =
      "CREATE (condition:Condition {"
      + "id: $conditionId, "
      + "temperature: $temperature, "
      + "cough: $cough, "
      + "fever: $fever, "
      + "shortnessOfBreath: $shortnessOfBreath"
      + "}), (location:Location {"
      + "id: $locationId, "
      + "address: $address, "
      + "longitude: $longitude, "
      + "latitude: $latitude, "
      + "altitude: $altitude}) "
      + "RETURN condition, location";  

  public static final String RELATE_PERSON_HEALTH =
      "MATCH (person:Person{id:$personId}), (condition:Condition{id:$conditionId}) "
          + "CREATE (person) "
          + "<-[h:HEALTH {createdDate : localdatetime({ timezone: 'Etc/UTC' })}]- "
          + "(condition) RETURN condition";

  public static Condition from(Node node) {
    return new Condition(node.get("id").asString(), node.get("temperature").asFloat(),
        node.get("cough").asBoolean(), node.get("fever").asBoolean(),
        node.get("shortnessOfBreath").asBoolean());
  }

  @Id
  private String id;
  private float temperature;
  private boolean cough;
  private boolean fever;
  private boolean shortnessOfBreath;
//  private LocalDateTime createdDate;

  public Condition() {}

  public Condition(String id, float temperature, boolean cough, boolean fever,
      boolean shortnessOfBreath) {
    this.id = id;
    this.temperature = temperature;
    this.cough = cough;
    this.fever = fever;
    this.shortnessOfBreath = shortnessOfBreath;
  }

  public String getId() {
    return id;
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

  public void setFever(boolean fever) {
    this.fever = fever;
  }

  public void setId(String id) {
    this.id = id;
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
    builder.append("Condition [id=");
    builder.append(id);
    builder.append(", temperature=");
    builder.append(temperature);
    builder.append(", cough=");
    builder.append(cough);
    builder.append(", fever=");
    builder.append(fever);
    builder.append(", shortnessOfBreath=");
    builder.append(shortnessOfBreath);
    builder.append("]");
    return builder.toString();
  }

}
