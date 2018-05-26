package aic.gas.sc.gg_bot.replay_parser.model.irl;

import aic.gas.sc.gg_bot.abstract_bot.model.decision.OurProbabilisticPolicy;
import aic.gas.sc.gg_bot.replay_parser.configuration.Configuration;
import burlap.behavior.singleagent.Episode;
import io.jenetics.Genotype;
import io.jenetics.Mutator;
import io.jenetics.SwapMutator;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.ext.SingleNodeCrossover;
import io.jenetics.prog.MathTreePruneAlterer;
import io.jenetics.prog.ProgramChromosome;
import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.op.MathOp;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.op.Var;
import io.jenetics.util.ISeq;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GPMLIRL {

  public static ProgramGene<Double> learnReward(Configuration configuration,
      KFoldBatchIterator batchIterator, int numberOfFeatures,
      IPlanerInitializerStrategy planerInitializerStrategy) {

    // Definition of the terminals.
    final ISeq<Op<Double>> TERMINALS = ISeq.of(
        IntStream.range(0, numberOfFeatures).boxed()
            .map(integer -> Var.of("v" + integer, integer))
            .toArray(Var[]::new));

    // Definition of the allowed operations.
    final ISeq<Op<Double>> OPERATIONS = ISeq.of(
        MathOp.ADD,
        MathOp.SUB,
        MathOp.MUL
    );

    // Codec
    final Codec<ProgramGene<Double>, ProgramGene<Double>> CODEC =
        Codec.of(Genotype.of(ProgramChromosome.of(configuration.getHeightOfTree(),
            ch -> ch.getRoot().size() <= configuration.getTreeSize(), OPERATIONS, TERMINALS)),
            Genotype::getGene);

    final Engine<ProgramGene<Double>, Double> engine = Engine
        .builder(doubleProgramGene -> logLikelihoodCrossValidated(batchIterator.copy(),
            doubleProgramGene, planerInitializerStrategy, configuration), CODEC)
        .maximizing()
        .alterers(
            new SingleNodeCrossover<>(),
            new SwapMutator<>(),
            new MathTreePruneAlterer<>(),
            new Mutator<>())
        .build();

    return engine.stream()
        .limit(configuration.getNumberOfIterations())
        .peek(programGeneDoubleEvolutionResult -> log.info("Best solution in generation: "
            + programGeneDoubleEvolutionResult.getBestFitness()))
        .collect(EvolutionResult.toBestGenotype())
        .getGene();
  }

  /**
   * Computes and returns the log-likelihood of all expert trajectories under the current reward
   * function parameters.
   *
   * @return the log-likelihood of all expert trajectories under the current reward function
   * parameters.
   */
  private static double logLikelihoodCrossValidated(KFoldBatchIterator batchIterator,
      ProgramGene<Double> program, IPlanerInitializerStrategy planerInitializerStrategy,
      Configuration configuration) {

    double sumTrainingError = 0;
    for (int i = 0; i < configuration.getFolds(); i++) {
      sumTrainingError = sumTrainingError + logLikelihood(batchIterator, program,
          planerInitializerStrategy);
      batchIterator.next();
    }

    //avg testing error for cross-validation
    return sumTrainingError / configuration.getFolds();
  }

  private static double logLikelihood(KFoldBatchIterator batchIterator, ProgramGene<Double> program,
      IPlanerInitializerStrategy planerInitializerStrategy) {
    GPRewardFunction rf = new GPRewardFunction(program);
    OurGradientDescentSARSA planner = planerInitializerStrategy.initPlanner();

    //train
    batchIterator.trainingData().forEach(episode -> planner.learnFromEpisode(episode, rf));
    for (int i = 0; i < 10; i++) {
      if (planner.getMaxRelativeQValueChange() <= 0.1) {
        break;
      }
      planner.resetMaxRelativeQValueChange();
      batchIterator.trainingData().forEach(episode -> planner.learnFromEpisode(episode, rf));
    }

    //test
    return batchIterator.testingData()
        .mapToDouble(episode -> logLikelihoodOfTrajectory(episode, planner.getCurrentPolicy()))
        .sum();
  }

  private static double logLikelihoodOfTrajectory(Episode ep, OurProbabilisticPolicy policy) {
    double logLike = 0.0D;
    for (int i = 0; i < ep.numTimeSteps() - 1; i++) {

      //prevent by playing action with small ppt
      double actProb = policy.getProbabilityOfActionInState(ep.state(i), ep.action(i));
      logLike += Math.log(actProb);
    }
    return logLike;
  }

}
