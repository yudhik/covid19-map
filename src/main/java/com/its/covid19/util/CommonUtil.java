package com.its.covid19.util;

import java.util.Locale;
import java.util.UUID;

public class CommonUtil {

  public static String generateRandomUUID(){
    return UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.ENGLISH);
  }

}
