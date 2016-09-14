package mt.ferjorosa.examples.sprinkler;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.inference.InferenceAlgorithm;
import eu.amidst.core.inference.messagepassing.VMP;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.learning.parametric.ParallelMaximumLikelihood;
import eu.amidst.core.learning.parametric.ParameterLearningAlgorithm;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.models.DAG;
import eu.amidst.core.variables.Assignment;
import eu.amidst.core.variables.HashMapAssignment;
import eu.amidst.core.variables.Variable;
import eu.amidst.core.variables.Variables;
import eu.amidst.huginlink.inference.HuginInference;

/**
 * Created by Fer on 02/03/2016.
 */
public class SprinklerInference {

    private static DataStream<DataInstance> data;

    public static void main(String[] args) throws Exception {

        //We can open the data stream using the static class DataStreamLoader
        data = DataStreamLoader.open("datasets/ferjorosaData/sprinklerData300.arff");

        BayesianNetwork bnModel = learnBayesianNetwork(getSprinklerStructure(data));

        //We print the model
        System.out.println(bnModel.toString());

        // Now we can do some inference in the model
        // 0 = False / 1 = True

        //First we create an instance of a inference algorithm. In this case we use the Hugin exact inference engine
        InferenceAlgorithm inferenceAlgorithm = new HuginInference();

        //Then, we set the BN model
        inferenceAlgorithm.setModel(bnModel);

        // We recover the variables that interest us (in this case, all of them)
        Variable vCloudy = bnModel.getVariables().getVariableByName("cloudy");
        Variable vSprinkler = bnModel.getVariables().getVariableByName("sprinkler");
        Variable vRain = bnModel.getVariables().getVariableByName("rain");
        Variable vWetGrass = bnModel.getVariables().getVariableByName("wetGrass");

        // We assign the known evidence for our query
        Assignment assignment = new HashMapAssignment(1);
        assignment.setValue(vWetGrass,1);
        inferenceAlgorithm.setEvidence(assignment);

        //Then we run inference
        inferenceAlgorithm.runInference();

        //Then we query the posterior of
        System.out.println("P(Sprinkler|W=1) = " + inferenceAlgorithm.getPosterior(vSprinkler));

        //Then we query the posterior of
        System.out.println("P(Rain|W=1) = " + inferenceAlgorithm.getPosterior(vRain));

        // loglikelihood of the evidence
        System.out.println("P(W=1) = " + Math.exp(inferenceAlgorithm.getLogProbabilityOfEvidence()));
    }

    private static BayesianNetwork learnBayesianNetwork(DAG dag){

        //We create a ParameterLearningAlgorithm object with the MaximumLikelihood builder
        ParameterLearningAlgorithm parameterLearningAlgorithm = new ParallelMaximumLikelihood();

        //We fix the DAG structure
        parameterLearningAlgorithm.setDAG(dag);

        //We should invoke this method before processing any data
        parameterLearningAlgorithm.initLearning();

        //Then we show how we can perform parameter learning by a sequential updating of data batches.
        for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(100)){
            parameterLearningAlgorithm.updateModel(batch);
        }

        return parameterLearningAlgorithm.getLearntBayesianNetwork();
    }

    private static DAG getSprinklerStructure(DataStream<DataInstance> dataStream){

        Variables variables = new Variables(dataStream.getAttributes());

        //Pre-defined structure
        Variable cloudy = variables.getVariableByName("cloudy");
        Variable sprinkler = variables.getVariableByName("sprinkler");
        Variable rain = variables.getVariableByName("rain");
        Variable wetGrass = variables.getVariableByName("wetGrass");

        /**
         * 1. Once you have defined your {@link Variables} object, the next step is to create
         * a DAG structure over this set of variables.
         *
         * 2. To add parents to each variable, we first recover the ParentSet object by the method
         * getParentSet(Variable var) and then call the method addParent().
         */

        DAG dag = new DAG(variables);

        dag.getParentSet(sprinkler).addParent(cloudy);
        dag.getParentSet(rain).addParent(cloudy);
        dag.getParentSet(wetGrass).addParent(sprinkler);
        dag.getParentSet(wetGrass).addParent(rain);

        /**
         * 1. We first check if the graph contains cycles.
         *
         * 2. We print out the created DAG. We can check that everything is as expected.
         */
        if (dag.containCycles()) {
            try {
            } catch (Exception ex) {
                throw new IllegalArgumentException(ex);
            }
        }

        System.out.println(dag.toString());

        return dag;
    }
}
