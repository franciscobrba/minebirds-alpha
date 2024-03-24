package com.minebirds.utils;

import com.mongodb.lang.Nullable;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.yaml.snakeyaml.Yaml;

public class BrazilianTokens {

  @Nullable
  public static java.util.Map<String, Object> tokens = null;

  private static void loadTokens() {
    java.io.File configFile = new java.io.File(
      Bukkit.getPluginManager().getPlugin("Minebirds").getDataFolder(),
      "ptbr.yml"
    );

    Yaml yaml = new Yaml();
    try (
      java.io.FileInputStream fis = new java.io.FileInputStream(configFile)
    ) {
      java.util.Map<String, Object> data = yaml.load(fis);
      BrazilianTokens.tokens = data;
      fis.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String translate(String key) {
    if (tokens == null) {
      loadTokens();
    }
    if (BrazilianTokens.tokens.containsKey(key)) {
      return BrazilianTokens.tokens.get(key).toString();
    }
    return null;
  }
}
