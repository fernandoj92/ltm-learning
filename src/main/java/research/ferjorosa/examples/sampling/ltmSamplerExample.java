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
import research.ferjorosa.core.util.sampling.LatentBayesianNetworkSampler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fer on 03/10/2016.
 */
public class LtmSamplerExample {

    public static void main(String[] args) throws Exception {
        // DataStream<DataInstance> sampledStream = sampleZhangLTM(100);
        DataStream<DataInstance> sampledStream = improvedSampleZhangLTM(1000);

        DataStreamWriter.writeDataToFile(sampledStream, "datasets/ferjorosaData/sampled/Asia_zhang_improved_sampled.arff");

        compareModels();
    }

    private static DataStream<DataInstance> sampleZhangLTM(int nSamples) throws  Exception {

        LTM zhangModel= learnZhangLTM();

        BayesianNetworkSampler bnSample = new BayesianNetworkSampler(zhangModel.getLearntBayesianNetwork());
        for(LatentVariable ltVariable : zhangModel.getLtdag().getLatentVariables())
            bnSample.setHiddenVar(ltVariable.getVariable());

        return bnSample.sampleToDataStream(nSamples);
    }

    private static DataStream<DataInstance> improvedSampleZhangLTM(int nSamples) throws Exception{
        LTM zhangModel= learnZhangLTM();
        LatentBayesianNetworkSampler bnSample = new LatentBayesianNetworkSampler(zhangModel.getLearntBayesianNetwork());
        for(LatentVariable ltVariable : zhangModel.getLtdag().getLatentVariables())
            bnSample.setLatentVar(ltVariable.getVariable());

        return bnSample.sampleToDataStream(nSamples);
    }

    private static void compareModels(){
        LTM ABIModel = null;
        DataStream<DataInstance> data1 = DataStreamLoader.open("datasets/ferjorosaData/sampled/Asia_zhang_improved_sampled.arff");

        StaticLearningAlgorithm staticLearningAlgorithm = new ABI(new ABIConfig());

        for (DataOnMemory<DataInstance> batch : data1.iterableOverBatches(1000)){
            ABIModel = staticLearningAlgorithm.learnModel(batch);
            System.out.println(ABIModel.getLearntBayesianNetwork());
            System.out.println("ABI Score: " + ABIModel.getScore());
        }

        LTM ZhangModel = null;
        DataStream<DataInstance> data2 = DataStreamLoader.open("datasets/ferjorosaData/sampled/Asia_zhang_improved_sampled.arff");

        for (DataOnMemory<DataInstance> batch : data2.iterableOverBatches(1000)){
            ZhangModel = buildZhangLTM(batch);
            System.out.println(ZhangModel.getLearntBayesianNetwork());
            System.out.println("Zhang Score: " + ZhangModel.getScore());
        }
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

    private static LTM learnZhangLTM(){
        LTM zhangModel = null;
        DataStream<DataInstance> data2 = DataStreamLoader.open("datasets/ferjorosaData/Asia_train.arff");

        for (DataOnMemory<DataInstance> batch : data2.iterableOverBatches(100)){
            zhangModel = buildZhangLTM(batch);
            //System.out.println(zhangModel.getLearntBayesianNetwork());
        }

        return zhangModel;
    }

}
