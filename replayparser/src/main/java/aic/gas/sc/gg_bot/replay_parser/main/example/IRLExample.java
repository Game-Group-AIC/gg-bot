package aic.gas.sc.gg_bot.replay_parser.main.example;

import burlap.behavior.functionapproximation.dense.DenseStateFeatures;
import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.ArrowActionGlyph;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.LandmarkColorBlendInterpolation;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.PolicyGlyphPainter2D;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.StateValuePainter2D;
import burlap.behavior.singleagent.learnfromdemo.RewardValueProjection;
import burlap.behavior.singleagent.learnfromdemo.mlirl.MLIRL;
import burlap.behavior.singleagent.learnfromdemo.mlirl.MLIRLRequest;
import burlap.behavior.singleagent.learnfromdemo.mlirl.commonrfs.LinearStateDifferentiableRF;
import burlap.behavior.singleagent.learnfromdemo.mlirl.differentiableplanners.DifferentiableSparseSampling;
import burlap.behavior.valuefunction.QProvider;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.debugtools.RandomFactory;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.vardomain.VariableDomain;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.shell.visual.VisualExplorer;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;
import java.awt.Color;
import java.util.List;

/**
 * Example code for performing IRL. Choose the steps to perform in the main method by one of the
 * three primary methods. Create your demonstrations using the {@link #launchExplorer()} method. See
 * its documentation which describes how to use the standard BURLAP shell to record and save your
 * demonstrations to disk. If you wish to verify which demonstrations were saved to disk, use the
 * {@link #launchSavedEpisodeSequenceVis(String)} method, giving it the path to where you saved the
 * data. Then, use the {@link #runIRL(String)} method giving it the same path to the saved
 * demonstrations to actually run IRL. It will then visualize the learned reward function
 *
 * @author James MacGlashan.
 */
public class IRLExample {

  ExampleGridWorld gwd;
  SADomain domain;
  Visualizer v;
  State initialState = new EXGridState(0, 0);
  HashableStateFactory hashingFactory = new SimpleHashableStateFactory();

  public IRLExample() {

    this.gwd = new ExampleGridWorld();
    this.domain = gwd.generateDomain();
    this.v = gwd.getVisualizer();

  }

  public static void main(String[] args) {

    IRLExample ex = new IRLExample();

    //only have one of the below uncommented

//        ex.launchExplorer(); //choose this to record demonstrations
//        ex.launchSavedEpisodeSequenceVis("irl_demos"); //choose this review the demonstrations that you've recorded
    ex.runIRL(
        "irl_demos"); //choose this to run MLIRL on the demonstrations and visualize the learned reward function and policy


  }

  /**
   * Creates a visual explorer that you can use to to record trajectories. Use the "`" key to reset
   * to a random initial state Use the wasd keys to move north south, east, and west, respectively.
   * To enable recording, first open up the shell and type: "rec -b" (you only need to type this
   * one). Then you can move in the explorer as normal. Each demonstration begins after an
   * environment reset. After each demonstration that you want to keep, go back to the shell and
   * type "rec -r" If you reset the environment before you type that, the episode will be discarded.
   * To temporarily view the episodes you've created, in the shell type "episode -v". To actually
   * record your episodes to file, type "rec -w path/to/save/directory base_file_name" For example
   * "rec -w irl_demos demo" A recommendation for examples is to record two demonstrations that both
   * go to the pink cell while avoiding blue ones and do so from two different start locations on
   * the left (if you keep resetting the environment, it will change where the agent starts).
   */
  public void launchExplorer() {
    SimulatedEnvironment env = new SimulatedEnvironment(this.domain, this.initialState);
    VisualExplorer exp = new VisualExplorer(this.domain, env, this.v, 800, 800);
    exp.addKeyAction("w", GridWorldDomain.ACTION_NORTH, "");
    exp.addKeyAction("s", GridWorldDomain.ACTION_SOUTH, "");
    exp.addKeyAction("d", GridWorldDomain.ACTION_EAST, "");
    exp.addKeyAction("a", GridWorldDomain.ACTION_WEST, "");

    //exp.enableEpisodeRecording("r", "f", "irlDemo");

    exp.initGUI();
  }

  /**
   * Launch a episode sequence visualizer to display the saved trajectories in the folder "irlDemo"
   */
  public void launchSavedEpisodeSequenceVis(String pathToEpisodes) {

    new EpisodeSequenceVisualizer(this.v, this.domain, pathToEpisodes);

  }

  /**
   * Runs MLIRL on the trajectories stored in the "irlDemo" directory and then visualizes the
   * learned reward function.
   */
  public void runIRL(String pathToEpisodes) {

    //create reward function features to use
    LocationFeatures features = new LocationFeatures(11 * 11);

    //create a reward function that is linear with respect to those features and has small random
    //parameter values to start
    LinearStateDifferentiableRF rf = new LinearStateDifferentiableRF(features, 11 * 11);
    for (int i = 0; i < rf.numParameters(); i++) {
      rf.setParameter(i, RandomFactory.getMapped(0).nextDouble() * 0.2 - 0.1);
    }

    //load our saved demonstrations from disk
    List<Episode> episodes = Episode.readEpisodes(pathToEpisodes);

    //use either DifferentiableVI or DifferentiableSparseSampling for planning. The latter enables receding horizon IRL,
    //but you will probably want to use a fairly large horizon for this kind of reward function.
    double beta = 10;
    //DifferentiableVI dplanner = new DifferentiableVI(this.domain, rf, 0.99, beta, new SimpleHashableStateFactory(), 0.01, 100);
    DifferentiableSparseSampling dplanner = new DifferentiableSparseSampling(this.domain, rf, 0.99,
        hashingFactory, 10, -1, beta);

    dplanner.toggleDebugPrinting(false);

    //define the IRL problem
    MLIRLRequest request = new MLIRLRequest(domain, dplanner, episodes, rf);
    request.setBoltzmannBeta(beta);

    //run MLIRL on it
    MLIRL irl = new MLIRL(request, 0.1, 0.1, 10);
    irl.performIRL();

    manualValueFunctionVis(new RewardValueProjection(rf),
        new GreedyQPolicy((QProvider) request.getPlanner()));

  }

  public void manualValueFunctionVis(ValueFunction valueFunction, Policy p) {

    List<State> allStates = StateReachability.getReachableStates(
        initialState, domain, hashingFactory);

    //define color function
    LandmarkColorBlendInterpolation rb = new LandmarkColorBlendInterpolation();
    rb.addNextLandMark(0., Color.RED);
    rb.addNextLandMark(1., Color.BLUE);

    //define a 2D painter of state values,
    //specifying which attributes correspond to the x and y coordinates of the canvas
    StateValuePainter2D svp = new StateValuePainter2D(rb);
    svp.setXYKeys("x", "y",
        new VariableDomain(0, 11), new VariableDomain(0, 11),
        1, 1);

    //create our ValueFunctionVisualizer that paints for all states
    //using the ValueFunction source and the state value painter we defined
    ValueFunctionVisualizerGUI gui = new ValueFunctionVisualizerGUI(
        allStates, svp, valueFunction);

    //define a policy painter that uses arrow glyphs for each of the grid world actions
    PolicyGlyphPainter2D spp = new PolicyGlyphPainter2D();
    spp.setXYKeys("x", "y", new VariableDomain(0, 11),
        new VariableDomain(0, 11),
        1, 1);

    spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_NORTH, new ArrowActionGlyph(0));
    spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_SOUTH, new ArrowActionGlyph(1));
    spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_EAST, new ArrowActionGlyph(2));
    spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_WEST, new ArrowActionGlyph(3));
    spp.setRenderStyle(PolicyGlyphPainter2D.PolicyGlyphRenderStyle.DISTSCALED);

    //add our policy renderer to it
    gui.setSpp(spp);
    gui.setPolicy(p);

    //set the background color for places where states are not rendered to grey
    gui.setBgColor(Color.GRAY);

    //start it
    gui.initGUI();
  }

  /**
   * A state feature vector generator that create a binary feature vector where each element
   * indicates whether the agent is in a cell of of a different type. All zeros indicates that the
   * agent is in an empty cell.
   */
  public static class LocationFeatures implements DenseStateFeatures {

    protected int numLocations;

    public LocationFeatures(int numLocations) {
      this.numLocations = numLocations;
    }

    @Override
    public double[] features(State s) {

      double[] fv = new double[this.numLocations];

      int aL = ((EXGridState) s).getIndex();
      if (aL != -1) {
        fv[aL] = 1.;
      }

      return fv;
    }

    @Override
    public DenseStateFeatures copy() {
      return new LocationFeatures(numLocations);
    }
  }

}
