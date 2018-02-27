package aic.gas.sc.gg_bot.replay_parser.model.irl_rl;

import aic.gas.sc.gg_bot.replay_parser.configuration.Configuration;
import aic.gas.sc.gg_bot.replay_parser.model.irl.KFoldBatchIterator;
import burlap.behavior.functionapproximation.DifferentiableStateActionValue;
import burlap.behavior.functionapproximation.dense.ConcatenatedObjectFeatures;
import burlap.behavior.functionapproximation.dense.NumericVariableFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TileCodingFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TilingArrangement;
import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.Episode;
import burlap.domain.singleagent.lunarlander.LunarLanderDomain;
import burlap.mdp.singleagent.oo.OOSADomain;
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
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GPMLIRL {

  public static ProgramGene<Double> learnReward(Configuration configuration,
      KFoldBatchIterator batchIterator,
      int numberOfFeatures, int depth) {
    long start = System.currentTimeMillis();

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
        .builder(doubleProgramGene -> logLikelihood(batchIterator, doubleProgramGene), CODEC)
        .maximizing()
        .alterers(
            new MultiPointCrossover<>(),
            new Mutator<>())
        .build();

    return engine.stream()
        .limit(300)
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
      ProgramGene<Double> program) {

    GPRewardFunction rf = new GPRewardFunction(program);

    LunarLanderDomain lld = new LunarLanderDomain();
    lld.setRf(rf);
    OOSADomain domain = lld.generateDomain();

    ConcatenatedObjectFeatures inputFeatures = new ConcatenatedObjectFeatures()
        .addObjectVectorizion(LunarLanderDomain.CLASS_AGENT, new NumericVariableFeatures());

    int nTilings = 5;
    double resolution = 10.;

    double xWidth = (lld.getXmax() - lld.getXmin()) / resolution;
    double yWidth = (lld.getYmax() - lld.getYmin()) / resolution;
    double velocityWidth = 2 * lld.getVmax() / resolution;
    double angleWidth = 2 * lld.getAngmax() / resolution;

    TileCodingFeatures tilecoding = new TileCodingFeatures(inputFeatures);
    tilecoding.addTilingsForAllDimensionsWithWidths(
        new double[]{xWidth, yWidth, velocityWidth, velocityWidth, angleWidth}, nTilings,
        TilingArrangement.RANDOM_JITTER);

    double defaultQ = 0.5;
    DifferentiableStateActionValue vfa = tilecoding.generateVFA(defaultQ / nTilings);
    OurGradientDescentSarsaLam planner = new OurGradientDescentSarsaLam<>(domain, 0.99, 5, vfa,
        0.02, 0.5, 500, rf);

    //learn policy
    batchIterator.trainingData()
        .forEach(episode -> planner.learnFromEpisode(episode, rf));

    return batchIterator.testingData()
        .mapToDouble(episode -> logLikelihoodOfTrajectory(episode, planner.getCurrentPolicy()))
        .sum();
  }

  private static double logLikelihoodOfTrajectory(Episode ep, Policy policy) {
    double logLike = 0.0D;
    for (int i = 0; i < ep.numTimeSteps() - 1; i++) {

      //prevent by playing action with small ppt
      double actProb = Math.max(policy.actionProb(ep.state(i), ep.action(i)), Math.exp(-100));
      logLike += Math.log(actProb);
    }
    return logLike;
  }

}
