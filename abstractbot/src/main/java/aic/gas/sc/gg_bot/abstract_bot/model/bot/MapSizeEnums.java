package aic.gas.sc.gg_bot.abstract_bot.model.bot;

public enum MapSizeEnums {
  MAP_FOR_2, MAP_FOR_3_AND_MORE;

  public static MapSizeEnums getByStartBases(int startBasesCount) {
    if (startBasesCount == 2) {
      return MAP_FOR_2;
    } else {
      return MAP_FOR_3_AND_MORE;
    }
  }

}
