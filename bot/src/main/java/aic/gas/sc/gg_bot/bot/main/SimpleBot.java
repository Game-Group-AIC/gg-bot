package aic.gas.sc.gg_bot.bot.main;

import aic.gas.sc.gg_bot.bot.service.implementation.BotFacade;
import java.io.IOException;

public class SimpleBot extends BotFacade {

  private SimpleBot() {
    super(35, false, true);
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    new SimpleBot().run();
  }

}
