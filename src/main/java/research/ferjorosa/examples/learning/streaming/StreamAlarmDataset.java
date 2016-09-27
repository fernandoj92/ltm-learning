package research.ferjorosa.examples.learning.streaming;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.learning.parametric.bayesian.SVB;
import research.ferjorosa.core.learning.normal.StaticLearningAlgorithm;
import research.ferjorosa.core.learning.stream.SALL;
import research.ferjorosa.core.learning.stream.conceptdrift.ConceptDriftMeasure;
import research.ferjorosa.core.learning.stream.conceptdrift.LikelihoodFading;
import research.ferjorosa.core.learning.normal.structural.ABI;
import research.ferjorosa.core.learning.normal.structural.ABIConfig;
import research.ferjorosa.core.learning.normal.structural.StructuralLearning;

/**
 * Created by Fernando on 6/25/2016.
 */
public class StreamAlarmDataset {

    public static void main(String[] args) throws Exception {

        DataStream<DataInstance> data = DataStreamLoader.open("datasets/ferjorosaData/Alarm_train.arff");

        //We create a ParallelSVB object
        SVB mainPLalgorihtm = new SVB();
        SVB driftPLalgorithm = new SVB();

        StaticLearningAlgorithm staticLearningAlgorithm = new ABI(new ABIConfig(), mainPLalgorihtm);

        ConceptDriftMeasure driftMeasure = new LikelihoodFading(driftPLalgorithm);
        SALL streamEngine = new SALL(driftMeasure,staticLearningAlgorithm);

        boolean start = true;

        long startTime = System.currentTimeMillis();
        for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(500)){
            if(start) {
                streamEngine.initLearning(batch);
                start = false;
            }else {
                streamEngine.updateModel(batch);
                System.out.println("\n MODELO NORMAL:");
                System.out.println(streamEngine.getCurrentModel().getLearntBayesianNetwork().toString());
                System.out.println("\n SCORE DEL MODELO NORMAL: " + streamEngine.getLastBatchScore());
            }
        }
        long estimatedTime = System.currentTimeMillis() - startTime;

        //System.out.println(streamEngine.getCurrentModel().getLearntBayesianNetwork().toString());

        System.out.println("elapsed time: "+estimatedTime);
        System.out.println("ABI score: "+ streamEngine.getCurrentModel().getScore());

        //BayesianNetworkWriter.saveToFile(learntModel.getLearntBayesianNetwork(),"networks/alarm/alarm_train1.bn");
        //BNWriterToHugin.saveToHuginFile(learntModel.getLearntBayesianNetwork(),"networks/alarm/alarm_train1.net");

    }
}
