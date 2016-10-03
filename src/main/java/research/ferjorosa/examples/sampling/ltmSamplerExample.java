package research.ferjorosa.examples.sampling;

import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.io.DataStreamWriter;
import eu.amidst.core.learning.parametric.bayesian.SVB;
import eu.amidst.core.utils.BayesianNetworkSampler;
import research.ferjorosa.core.learning.normal.LTMLearningEngine;
import research.ferjorosa.core.learning.normal.StaticLearningAlgorithm;
import research.ferjorosa.core.learning.normal.structural.ABI;
import research.ferjorosa.core.learning.normal.structural.ABIConfig;
import research.ferjorosa.core.models.LTM;
import research.ferjorosa.core.models.ltvariables.LatentVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fer on 03/10/2016.
 */
public class LtmSamplerExample {

    public static void main(String[] args) throws Exception {
        LTM ABIModel = null;
        DataStream<DataInstance> data1 = DataStreamLoader.open("datasets/ferjorosaData/Asia_zhang_sampled.arff");
        //DataStream<DataInstance> data1 = DataStreamLoader.open("datasets/ferjorosaData/Asia_train.arff");
        StaticLearningAlgorithm staticLearningAlgorithm = new ABI(new ABIConfig());

        for (DataOnMemory<DataInstance> batch : data1.iterableOverBatches(1000)){
            ABIModel = staticLearningAlgorithm.learnModel(batch);
            System.out.println(ABIModel.getLearntBayesianNetwork());
            System.out.println("ABI Score: " + ABIModel.getScore());
        }

        LTM ZhangModel = null;
        DataStream<DataInstance> data2 = DataStreamLoader.open("datasets/ferjorosaData/Asia_train.arff");

        for (DataOnMemory<DataInstance> batch : data2.iterableOverBatches(100)){
            ZhangModel = buildZhangLTM(batch);
            System.out.println(ZhangModel.getLearntBayesianNetwork());
            System.out.println("Zhang Score: " + ZhangModel.getScore());
        }
    }

    private static void sampleZhangLTM() throws  Exception {
        LTM zhangModel = null;
        DataStream<DataInstance> data2 = DataStreamLoader.open("datasets/ferjorosaData/Asia_train.arff");

        for (DataOnMemory<DataInstance> batch : data2.iterableOverBatches(100)){
            zhangModel = buildZhangLTM(batch);
            System.out.println(zhangModel.getLearntBayesianNetwork());
        }

        BayesianNetworkSampler bnSample = new BayesianNetworkSampler(zhangModel.getLearntBayesianNetwork());
        //for(LatentVariable ltVariable : zhangModel.getLtdag().getLatentVariables())
        //    bnSample.setHiddenVar(ltVariable.getVariable());

        DataStream<DataInstance> sampledStream = bnSample.sampleToDataStream(1000);
        DataStreamWriter.writeDataToFile(sampledStream, "datasets/ferjorosaData/Asia_zhang_sampled2.arff");
    }

    private static LTM buildZhangLTM(DataOnMemory<DataInstance> batch){
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
