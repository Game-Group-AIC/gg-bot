package aic.gas.sc.gg_bot.bot.main;

import aic.gas.sc.gg_bot.bot.service.implementation.BotFacade;
import java.io.IOException;

public class Bot extends BotFacade {

  private Bot() {
    super(60, false, true, false);
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    new Bot().run();
  }

}
