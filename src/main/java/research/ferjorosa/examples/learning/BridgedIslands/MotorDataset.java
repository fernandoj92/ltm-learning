package research.ferjorosa.examples.learning.BridgedIslands;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.learning.parametric.bayesian.SVB;
import research.ferjorosa.core.learning.normal.StaticLearningAlgorithm;
import research.ferjorosa.core.learning.normal.structural.ABI;
import research.ferjorosa.core.learning.normal.structural.ABIConfig;
import research.ferjorosa.core.learning.normal.structural.StructuralLearning;
import research.ferjorosa.core.models.LTM;

/**
 * Created by Fernando on 7/7/2016.
 */
public class MotorDataset {

    public static void main(String[] args) throws Exception {

        DataStream<DataInstance> data = DataStreamLoader.open("datasets/ferjorosaData/dataset_motor.arff");

        //We create a ParallelSVB object
        SVB parameterLearningAlgorithm = new SVB();

        StaticLearningAlgorithm staticLearningAlgorithm = new ABI(new ABIConfig(), parameterLearningAlgorithm);

        LTM learntModel = null;

        long startTime = System.currentTimeMillis();
        int cont = 0;
        for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(1000)){
            cont++;
            if(cont == 1)
                learntModel = staticLearningAlgorithm.learnModel(batch);
        }
        long estimatedTime = System.currentTimeMillis() - startTime;

        System.out.println(learntModel.getLearntBayesianNetwork().toString());

        System.out.println("elapsed time: "+estimatedTime);
        System.out.println("ABI score: "+ learntModel.getScore());

        //BayesianNetworkWriter.saveToFile(learntModel.getLearntBayesianNetwork(),"networks/alarm/alarm_train1.bn");
        //BNWriterToHugin.saveToHuginFile(learntModel.getLearntBayesianNetwork(),"networks/alarm/alarm_train1.net");

    }
}
