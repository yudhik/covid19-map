package com.its.covid19.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {

  MALE("male"), FEMALE("female"), OTHER("other");

  public static Gender fromString(String genderString) {
    for (Gender gender : Gender.values()) {
      if (gender.inString.equalsIgnoreCase(genderString)) {
        return gender;
      }
    }
    return OTHER;
  }

  private String inString;

  private Gender(String genderString) {
    this.inString = genderString;
  }

  @JsonValue
  @Override
  public String toString() {
    return this.inString;
  }

}
