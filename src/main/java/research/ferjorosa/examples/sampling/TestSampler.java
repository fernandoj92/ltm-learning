package research.ferjorosa.examples.sampling;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamWriter;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.utils.BayesianNetworkGenerator;
import eu.amidst.core.utils.BayesianNetworkSampler;

/**
 * Created by Fer on 03/10/2016.
 */
public class TestSampler {

    public static void main(String[] args) throws Exception {
        BayesianNetworkGenerator.setNumberOfGaussianVars(10);
        BayesianNetworkGenerator.setNumberOfMultinomialVars(1, 2);
        BayesianNetworkGenerator.setSeed(0);
        final BayesianNetwork naiveBayes = BayesianNetworkGenerator.generateNaiveBayes(2);

        //Sampling
        BayesianNetworkSampler sampler = new BayesianNetworkSampler(naiveBayes);
        sampler.setSeed(0);

        DataStream<DataInstance> sampledStream = sampler.sampleToDataStream(100);
        DataStreamWriter.writeDataToFile(sampledStream, "datasets/ferjorosaData/sampled_nb.arff");
    }
}
