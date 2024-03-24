package com.minebirds.utils;

import java.util.*;

public class Numbers {

  public static int randomBetween(int min, int max) {
    if (min >= max) {
      return min;
    }
    Random r = new Random();
    return r.nextInt((max - min) + 1) + min;
  }

  public static int gameKey() {
    return randomBetween(111, 999);
  }
}
