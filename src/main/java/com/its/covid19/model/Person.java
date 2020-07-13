package com.its.covid19.model;

import java.time.ZoneId;
import java.util.Date;
import org.neo4j.driver.types.Node;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.neo4j.ogm.annotation.typeconversion.DateString;

public class Person {

  public static final String CREATE_STATEMENT = "CREATE (person:Person {" + "id: $id, "
      + "firstName: $firstName, " + "middleName: $middleName, " + "lastName: $lastName, "
      + "dateOfBirth: date({year:$birthYear, month:$birthMonth, day:$birthDate}), "
      + "gender: $gender, " + "accountId: $accountId" + "}) RETURN person";

  public static final String FIND_POSITIVE_COVID_PERSON_LOCATION = "MATCH (person) "
      + "<-[h:HEALTH]- (condition:Condition{fever:true,cough:true,shortnessOfBreath:true}) "
      + "-[:LOCATED]-> (location) WHERE condition.temperature >= $bodyHeat RETURN person, condition, location";

  public static Person from(Node node) {
    return new Person(node.get("id").asString(), node.get("firstName").asString(),
        node.get("middleName").asString(), node.get("lastName").asString(),
        Date.from(node.get("dateOfBirth").asLocalDate().atStartOfDay(ZoneId.of("+0")).toInstant()),
        Gender.fromString(node.get("gender").asString()), node.get("accountId").asString());
  }

  private String id;
  private String firstName;
  private String middleName;
  private String lastName;
  @DateString("MM-dd-yyyy")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
  private Date dateOfBirth;
  private Gender gender;
  private String accountId;

  public Person() {}

  public Person(String id, String firstName, String middleName, String lastName, Date dateOfBirth,
      Gender gender, String accountId) {
    this.id = id;
    this.firstName = firstName;
    this.middleName = middleName;
    this.lastName = lastName;
    this.dateOfBirth = dateOfBirth;
    this.gender = gender;
    this.accountId = accountId;
  }

  public String getAccountId() {
    return accountId;
  }

  public Date getDateOfBirth() {
    return dateOfBirth;
  }

  public String getFirstName() {
    return firstName;
  }

  public Gender getGender() {
    return gender;
  }

  public String getId() {
    return id;
  }

  public String getLastName() {
    return lastName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public void setDateOfBirth(Date dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Person [id=");
    builder.append(id);
    builder.append(", firstName=");
    builder.append(firstName);
    builder.append(", middleName=");
    builder.append(middleName);
    builder.append(", lastName=");
    builder.append(lastName);
    builder.append(", dateOfBirth=");
    builder.append(dateOfBirth);
    builder.append(", gender=");
    builder.append(gender);
    builder.append(", accountId=");
    builder.append(accountId);
    builder.append("]");
    return builder.toString();
  }
}
