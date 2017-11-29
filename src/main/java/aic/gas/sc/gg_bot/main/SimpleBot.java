package aic.gas.sc.gg_bot.main;

import aic.gas.sc.gg_bot.service.implementation.AgentUnitHandlerImpl;
import aic.gas.sc.gg_bot.service.implementation.BotFacade;
import aic.gas.sc.gg_bot.service.implementation.LocationInitializerImpl;
import aic.gas.sc.gg_bot.service.implementation.PlayerInitializerImpl;
import java.beans.IntrospectionException;
import java.io.IOException;

public class SimpleBot extends BotFacade {

  private SimpleBot() {
    super(AgentUnitHandlerImpl::new, PlayerInitializerImpl::new, LocationInitializerImpl::new);
  }

  public static void main(String[] args)
      throws IOException, InterruptedException, IntrospectionException {
    new SimpleBot().run();
  }

}
