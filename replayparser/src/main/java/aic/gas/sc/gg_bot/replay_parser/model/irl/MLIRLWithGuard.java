package aic.gas.sc.gg_bot.replay_parser.model.irl;

import burlap.behavior.functionapproximation.FunctionGradient;
import burlap.behavior.singleagent.learnfromdemo.CustomRewardModel;
import burlap.behavior.singleagent.learnfromdemo.mlirl.MLIRL;
import burlap.behavior.singleagent.learnfromdemo.mlirl.MLIRLRequest;
import burlap.behavior.singleagent.learnfromdemo.mlirl.support.DifferentiableRF;
import burlap.debugtools.DPrint;
import burlap.debugtools.RandomFactory;

/**
 * Extension of MLIRL to keep reward function in boundaries
 */
public class MLIRLWithGuard extends MLIRL {

  //boundaries on reward function values
  public static final double maxReward = 10000;
  public static final double minReward = -10000;

  public MLIRLWithGuard(MLIRLRequest request, double learningRate, double maxLikelihoodChange,
      int maxSteps) {
    super(request, learningRate, maxLikelihoodChange, maxSteps);
  }

  /**
   * Runs gradient ascent.
   */
  public void performIRL() {

    DifferentiableRF rf = this.request.getRf();

    //reset valueFunction
    this.request.getPlanner().resetSolver();
    this.request.getPlanner().setModel(new CustomRewardModel(request.getDomain().getModel(), rf));
    double lastLikelihood = this.logLikelihood();
    DPrint.cl(this.debugCode, "RF: " + this.request.getRf().toString());
    DPrint.cl(this.debugCode, "Log likelihood: " + lastLikelihood);

    int i;
    for (i = 0; i < maxSteps || this.maxSteps == -1; i++) {

      //move up gradient
      FunctionGradient gradient = this.logLikelihoodGradient();
      double maxChange = 0.;
      for (FunctionGradient.PartialDerivative pd : gradient.getNonZeroPartialDerivatives()) {
        double curVal = rf.getParameter(pd.parameterId);

        //make sure it keeps within boundaries
        double nexVal = Math
            .min(Math.max(curVal + this.learningRate * pd.value, minReward), maxReward);
        if (Double.isNaN(nexVal)) {
          nexVal = RandomFactory.getMapped(0).nextDouble() * 0.2 - 0.1;
        }

        rf.setParameter(pd.parameterId, nexVal);
        double delta = Math.abs(curVal - nexVal);
        maxChange = Math.max(maxChange, delta);
      }

      //reset valueFunction
      this.request.getPlanner().resetSolver();
      this.request.getPlanner().setModel(new CustomRewardModel(request.getDomain().getModel(), rf));

      double newLikelihood = this.logLikelihood();
      double likelihoodChange = newLikelihood - lastLikelihood;
      lastLikelihood = newLikelihood;

      DPrint.cl(this.debugCode, "RF: " + this.request.getRf().toString());
      DPrint.cl(this.debugCode,
          "Log likelihood: " + lastLikelihood + " (change: " + likelihoodChange + ")");

      if (Math.abs(likelihoodChange) < this.maxLikelihoodChange || Double.isNaN(likelihoodChange)) {
        i++;
        break;
      }


    }

    DPrint.cl(this.debugCode, "\nNum gradient ascent steps: " + i);
    DPrint.cl(this.debugCode, "RF: " + this.request.getRf().toString());


  }

}
