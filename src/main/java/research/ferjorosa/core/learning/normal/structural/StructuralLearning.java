package research.ferjorosa.core.learning.normal.structural;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import research.ferjorosa.core.models.LTM;

/**
 This interface defines the Algorithm for learning the Latent Tree Model structure from data.
 */
public interface StructuralLearning {

    /**
     * Learns a LTM using a given {@link DataOnMemory} object.
     * @param batch a {@link DataOnMemory} object that is going to be used to learn the model.
     * @return the learnt LTM.
     */
    LTM learnModel(DataOnMemory<DataInstance> batch);

}
