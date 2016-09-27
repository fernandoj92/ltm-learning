package research.ferjorosa.examples.learning.execution;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import research.ferjorosa.core.execution.ExecutionResult;
import research.ferjorosa.core.learning.normal.StaticLearningAlgorithm;
import research.ferjorosa.core.learning.normal.structural.ABI;
import research.ferjorosa.core.learning.normal.structural.ABIConfig;

/**
 * Created by Fer on 27/09/2016.
 */
public class AsiaExecutionResult {

    public static void main(String[] args) throws Exception {

        DataStream<DataInstance> data = DataStreamLoader.open("datasets/ferjorosaData/Asia_train.arff");

        StaticLearningAlgorithm staticLearningAlgorithm = new ABI(new ABIConfig());

        for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(100)){
            ExecutionResult result = staticLearningAlgorithm.execute(batch);
            System.out.println(result);
        }

    }
}
