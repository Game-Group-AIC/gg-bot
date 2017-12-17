package aic.gas.sc.gg_bot.bot.main;

import aic.gas.sc.gg_bot.bot.service.implementation.AgentUnitHandler;
import aic.gas.sc.gg_bot.bot.service.implementation.BotFacade;
import aic.gas.sc.gg_bot.bot.service.implementation.LocationInitializer;
import aic.gas.sc.gg_bot.bot.service.implementation.PlayerInitializer;
import java.beans.IntrospectionException;
import java.io.IOException;

public class SimpleBot extends BotFacade {

  private SimpleBot() {
    super(AgentUnitHandler::new, PlayerInitializer::new, LocationInitializer::new);
  }

  public static void main(String[] args)
      throws IOException, InterruptedException, IntrospectionException {
    new SimpleBot().run();
  }

}
