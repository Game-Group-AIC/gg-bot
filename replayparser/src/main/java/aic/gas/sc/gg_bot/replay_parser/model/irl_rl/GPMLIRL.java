package aic.gas.sc.gg_bot.replay_parser.model.irl_rl;

import aic.gas.sc.gg_bot.abstract_bot.model.decision.OurProbabilisticPolicy;
import aic.gas.sc.gg_bot.replay_parser.configuration.Configuration;
import aic.gas.sc.gg_bot.replay_parser.model.irl.KFoldBatchIterator;
import burlap.behavior.singleagent.Episode;
import io.jenetics.Genotype;
import io.jenetics.MultiPointCrossover;
import io.jenetics.Mutator;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.prog.ProgramChromosome;
import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.op.MathOp;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.op.Var;
import io.jenetics.util.ISeq;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GPMLIRL {

  //TODO refactor configuration
  public static ProgramGene<Double> learnReward(Configuration configuration,
      KFoldBatchIterator batchIterator, int numberOfFeatures, int depth,
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
        Codec.of(
            Genotype.of(ProgramChromosome.of(
                depth,
                ch -> ch.getRoot().size() <= 40,
                OPERATIONS,
                TERMINALS
            )),
            Genotype::getGene
        );

    final Engine<ProgramGene<Double>, Double> engine = Engine
        .builder(doubleProgramGene -> logLikelihood(batchIterator, doubleProgramGene,
            planerInitializerStrategy), CODEC)
        .maximizing()
        .alterers(
            new MultiPointCrossover<>(),
            new Mutator<>())
        .build();

    return engine.stream()
        .limit(50)
        .peek(programGeneDoubleEvolutionResult -> log.info("Best solution in generation: "
            + programGeneDoubleEvolutionResult.getBestFitness()))
        .peek(programGeneDoubleEvolutionResult -> batchIterator.next())
        .collect(EvolutionResult.toBestGenotype())
        .getGene();
  }

  /**
   * Computes and returns the log-likelihood of all expert trajectories under the current reward function parameters.
   *
   * @return the log-likelihood of all expert trajectories under the current reward function parameters.
   */
  private static double logLikelihood(KFoldBatchIterator batchIterator,
      ProgramGene<Double> program, IPlanerInitializerStrategy planerInitializerStrategy) {

    GPRewardFunction rf = new GPRewardFunction(program);
    OurGradientDescentSarsaLam planner = planerInitializerStrategy.initPlanner();

    //learn policy
    batchIterator.trainingData()
        .forEach(episode -> planner.learnFromEpisode(episode, rf));

    return batchIterator.testingData()
        .mapToDouble(episode -> logLikelihoodOfTrajectory(episode, planner.getCurrentPolicy()))
        .sum();
  }

  private static double logLikelihoodOfTrajectory(Episode ep, OurProbabilisticPolicy policy) {
    double logLike = 0.0D;
    for (int i = 0; i < ep.numTimeSteps() - 1; i++) {

      //prevent by playing action with small ppt
      double actProb = Math.max(policy.getProbabilityOfActionInState(ep.state(i), ep.action(i))
          + getNoise(), Math.exp(-100));
      logLike += Math.log(actProb);
    }
    return logLike;
  }

  private static double getNoise() {
    return ThreadLocalRandom.current().nextDouble(-0.05, 0.05);
  }

}
