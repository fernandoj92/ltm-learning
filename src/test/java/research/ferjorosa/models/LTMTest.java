package research.ferjorosa.models;

import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.learning.parametric.bayesian.SVB;
import research.ferjorosa.core.learning.normal.LTMLearningEngine;
import research.ferjorosa.core.learning.normal.StaticLearningAlgorithm;
import research.ferjorosa.core.learning.normal.structural.ABI;
import research.ferjorosa.core.learning.normal.structural.ABIConfig;
import research.ferjorosa.core.learning.normal.structural.StructuralLearning;
import research.ferjorosa.core.models.LTM;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Testing the {@link LTM} class.
 */
public class LTMTest {

    @Test
    public void testUpdateModel(){

        DataStream<DataInstance> data  = DataStreamLoader.open("datasets/ferjorosaData/Asia_train.arff");

        // Comparar que el score mejora al hacer update vs utilizar 2 aprendizajes independientes
        StaticLearningAlgorithm staticLearningAlgorithm = new ABI(new ABIConfig());

        LTM updatedModel = null;
        LTM zhangModel = null;
        double updatedModelScore = 0;
        double learntModelScore = 0;

        int it = 0;
        for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(250)){
            zhangModel = buildZhangLTM(batch);
            learntModelScore += zhangModel.getScore();
            if(it == 0){
                updatedModel = buildZhangLTM(batch);
                updatedModelScore += updatedModel.getScore();
                it++;
            }else
                updatedModelScore += updatedModel.updateModel(batch);

            // Assertions

            Assert.assertEquals(updatedModelScore, learntModelScore, 0);
        }
    }

    private LTM buildZhangLTM(DataOnMemory<DataInstance> batch){
        List<Attribute> leftAttributes = new ArrayList<>();
        List<Attribute> rightAttributes = new ArrayList<>();


        // Defines the default parameter learning algorithm
        SVB streamingVariationalBayes = new SVB();
        streamingVariationalBayes.setWindowsSize(100);

        LTMLearningEngine ltmLearner = new LTMLearningEngine(streamingVariationalBayes);
        List<Attribute> allAttributes = batch.getAttributes().getFullListOfAttributes();

        leftAttributes.add(batch.getAttributes().getAttributeByName("vTuberculosis"));
        leftAttributes.add(batch.getAttributes().getAttributeByName("vSmoking"));
        leftAttributes.add(batch.getAttributes().getAttributeByName("vLungCancer"));
        leftAttributes.add(batch.getAttributes().getAttributeByName("vTbOrCa"));
        leftAttributes.add(batch.getAttributes().getAttributeByName("vXRay"));

        rightAttributes.add(batch.getAttributes().getAttributeByName("vBronchitis"));
        rightAttributes.add(batch.getAttributes().getAttributeByName("vDyspnea"));
        rightAttributes.add(batch.getAttributes().getAttributeByName("vVisitToAsia"));

        return ltmLearner.learn2dimensionalLTM(leftAttributes,rightAttributes,2,2,batch);
    }
}
