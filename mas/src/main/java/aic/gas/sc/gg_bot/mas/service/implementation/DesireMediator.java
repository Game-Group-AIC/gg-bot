package aic.gas.sc.gg_bot.mas.service.implementation;

import aic.gas.sc.gg_bot.mas.model.QueuedItemInterfaceWithResponse;
import aic.gas.sc.gg_bot.mas.model.ResponseReceiverInterface;
import aic.gas.sc.gg_bot.mas.model.agents.Agent;
import aic.gas.sc.gg_bot.mas.model.planing.SharedDesire;
import aic.gas.sc.gg_bot.mas.model.planing.SharedDesireForAgents;
import aic.gas.sc.gg_bot.mas.model.planing.SharedDesireInRegister;
import aic.gas.sc.gg_bot.mas.model.servicies.desires.DesireRegister;
import aic.gas.sc.gg_bot.mas.model.servicies.desires.IReadOnlyDesireRegister;
import aic.gas.sc.gg_bot.mas.model.servicies.desires.IWorkingDesireRegister;
import aic.gas.sc.gg_bot.mas.service.AMediatorTemplate;
import java.util.Optional;
import java.util.Set;

/**
 * DesireMediator instance enables agents to propose desires for other agents to commit to. It keeps
 * status what is available and which agent is committed to what. This information are available
 * then to other agents. Class defines method to access queue. <p>
 */
public class DesireMediator extends
    AMediatorTemplate<IReadOnlyDesireRegister, IWorkingDesireRegister> {

  public DesireMediator() {
    super(new DesireRegister());
  }

  /**
   * Method to add item to queue with code to register desire
   */
  public boolean registerDesire(SharedDesireInRegister sharedDesire,
      ResponseReceiverInterface<Boolean> responseReceiver) {
    return addToQueue(new QueuedItemInterfaceWithResponse<Boolean>() {
      @Override
      public Boolean executeCode() {
        return workingRegister.addedDesire(sharedDesire);
      }

      @Override
      public ResponseReceiverInterface<Boolean> getReceiverOfResponse() {
        return responseReceiver;
      }
    });
  }

  /**
   * Method to remove agent from register of desires
   */
  public void removeAgentFromRegister(Agent agent,
      ResponseReceiverInterface<Boolean> responseReceiver) {
    addToQueue(new QueuedItemInterfaceWithResponse<Boolean>() {
      @Override
      public Boolean executeCode() {
        return workingRegister.removeAgent(agent);
      }

      @Override
      public ResponseReceiverInterface<Boolean> getReceiverOfResponse() {
        return responseReceiver;
      }
    });
  }

  /**
   * Method to add item to queue with code to unregister desire
   */
  public boolean unregisterDesire(SharedDesire sharedDesire,
      ResponseReceiverInterface<Boolean> responseReceiver) {
    return addToQueue(new QueuedItemInterfaceWithResponse<Boolean>() {
      @Override
      public Boolean executeCode() {
        return workingRegister.removedDesire(sharedDesire);
      }

      @Override
      public ResponseReceiverInterface<Boolean> getReceiverOfResponse() {
        return responseReceiver;
      }
    });
  }

  /**
   * Method to add item to queue with code to unregister desires
   */
  public boolean unregisterDesires(Set<SharedDesire> sharedDesires,
      ResponseReceiverInterface<Boolean> responseReceiver) {
    return addToQueue(new QueuedItemInterfaceWithResponse<Boolean>() {
      @Override
      public Boolean executeCode() {
        sharedDesires.forEach(workingRegister::removedDesire);
        return true;
      }

      @Override
      public ResponseReceiverInterface<Boolean> getReceiverOfResponse() {
        return responseReceiver;
      }
    });
  }

  /**
   * Method to add item to queue with code to make commitment to desire
   */
  public boolean addCommitmentToDesire(Agent agentWhoWantsToCommitTo,
      SharedDesireForAgents desireForOthersHeWantsToCommitTo,
      ResponseReceiverInterface<Optional<SharedDesireForAgents>> responseReceiver) {
    return addToQueue(new QueuedItemInterfaceWithResponse<Optional<SharedDesireForAgents>>() {
      @Override
      public Optional<SharedDesireForAgents> executeCode() {
        return workingRegister
            .commitToDesire(agentWhoWantsToCommitTo, desireForOthersHeWantsToCommitTo);
      }

      @Override
      public ResponseReceiverInterface<Optional<SharedDesireForAgents>> getReceiverOfResponse() {
        return responseReceiver;
      }
    });
  }

  /**
   * Method to add item to queue with code to remove commitment to desire
   */
  public boolean removeCommitmentToDesire(Agent agentWhoWantsToRemoveCommitment,
      SharedDesireForAgents desireHeWantsToRemoveCommitmentTo,
      ResponseReceiverInterface<Boolean> responseReceiver) {
    return addToQueue(new QueuedItemInterfaceWithResponse<Boolean>() {
      @Override
      public Boolean executeCode() {
        return workingRegister.removeCommitmentToDesire(agentWhoWantsToRemoveCommitment,
            desireHeWantsToRemoveCommitmentTo);
      }

      @Override
      public ResponseReceiverInterface<Boolean> getReceiverOfResponse() {
        return responseReceiver;
      }
    });
  }

  /**
   * Method to add item to queue with code to remove commitment to desire
   */
  public void removeCommitmentToDesires(Agent agentWhoWantsToRemoveCommitment,
      Set<SharedDesireForAgents> desiresHeWantsToRemoveCommitmentTo,
      ResponseReceiverInterface<Boolean> responseReceiver) {
    addToQueue(new QueuedItemInterfaceWithResponse<Boolean>() {
      @Override
      public Boolean executeCode() {
        desiresHeWantsToRemoveCommitmentTo.forEach(
            desireHeWantsToRemoveCommitmentTo -> workingRegister
                .removeCommitmentToDesire(agentWhoWantsToRemoveCommitment,
                    desireHeWantsToRemoveCommitmentTo));
        return true;
      }

      @Override
      public ResponseReceiverInterface<Boolean> getReceiverOfResponse() {
        return responseReceiver;
      }
    });
  }

}
