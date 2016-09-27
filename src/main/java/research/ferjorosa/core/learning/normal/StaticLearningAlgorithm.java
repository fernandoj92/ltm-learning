package research.ferjorosa.core.learning.normal;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import research.ferjorosa.core.execution.StaticExecution;
import research.ferjorosa.core.models.LTM;

/**
 * Created by Fer on 23/09/2016.
 */
public abstract class StaticLearningAlgorithm implements StaticExecution {

    /**
     * Learns a LTM using a given {@link DataOnMemory} object.
     * @param batch a {@link DataOnMemory} object that is going to be used to learn the model.
     * @return the learnt LTM.
     */
    public abstract LTM learnModel(DataOnMemory<DataInstance> batch);
}
