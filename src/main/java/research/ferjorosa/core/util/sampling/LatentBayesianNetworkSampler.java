package research.ferjorosa.core.util.sampling;



import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.datastream.Attributes;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.utils.AmidstOptionsHandler;
import eu.amidst.core.utils.LocalRandomGenerator;
import eu.amidst.core.utils.Utils;
import eu.amidst.core.variables.Assignment;
import eu.amidst.core.variables.HashMapAssignment;
import eu.amidst.core.variables.Variable;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This class re-implements the class {@link eu.amidst.core.utils.BayesianNetworkSampler} and extends its functionality
 * by allowing latent variables, which will be ignored in the sampling process.
 */
public class LatentBayesianNetworkSampler implements AmidstOptionsHandler, Serializable {

    /**
     * Represents the serial version ID for serializing the object.
     */
    private static final long serialVersionUID = 4107783324901370839L;

    /**
     * Represents the {@link BayesianNetwork} object from which the data will be sampled.
     */
    private BayesianNetwork network;

    /**
     * Represents the list of variables given in a causal order.
     */
    private List<Variable> causalOrder;

    /**
     * Represents the initial seed for random sampling.
     */
    private int seed = 0;

    /**
     * Represents a {@link java.util.Random} object.
     */
    private Random random = new Random(seed);

    /**
     * Represents a {@code Map} containing the hidden variables.
     */
    private Map<Variable, Boolean> hiddenVars = new HashMap();

    /**
     * Represents a {@code Map} containing the noisy variables.
     */
    private Map<Variable, Double> marNoise = new HashMap();

    /**
     * Respresents a {@code List} containing the latent variables.
     */
    private Map<Variable, Boolean> latentVars = new HashMap();

    /**
     * Creates a new LatentBayesianNetworkSampler given an input {@link BayesianNetwork} object.
     *
     * @param network1 an input {@link BayesianNetwork} object.
     */
    public LatentBayesianNetworkSampler(BayesianNetwork network1) {
        network = network1;
        this.causalOrder = Utils.getTopologicalOrder(network.getDAG());
    }

    /**
     * Returns a {@code Stream} of randomly sampled {@link Assignment}s for an input number of samples.
     *
     * @param nSamples an input number of samples.
     * @return a {@code Stream} of randomly sampled {@link Assignment}s.
     */
    private Stream<Assignment> getSampleStream(int nSamples) {
        LocalRandomGenerator randomGenerator = new LocalRandomGenerator(seed);
        return IntStream.range(0, nSamples)
                .mapToObj(i -> sample(network, causalOrder, randomGenerator.current()))
                .map(this::filter);
    }


    /**
     * Sets a given {@link Variable} object as hidden.
     *
     * @param var a given {@link Variable} object.
     */
    public void setHiddenVar(Variable var) {
        this.hiddenVars.put(var, true);
    }

    /**
     * Sets a given {@link Variable} object as noisy.
     *
     * @param var       a given {@link Variable} object.
     * @param noiseProb a double that represents the noise probability.
     */
    public void setMARVar(Variable var, double noiseProb) {
        this.marNoise.put(var, noiseProb);
    }

    /**
     * Sets a given {@link Variable} object as latent. A latent variable doesn't generate a sample value assignment.
     *
     * @param var a given {@link Variable} object.
     */
    public void setLatentVar(Variable var){
        this.latentVars.put(var, true);
    }

    /**
     * Filters a given {@link Assignment} object, i.e., sets the values assigned to either missing or noisy variables to Double.NaN.
     *
     * @param assignment a given {@link Assignment} object.
     * @return a filtered {@link Assignment}.
     */
    private Assignment filter(Assignment assignment) {
        hiddenVars.keySet().stream().forEach(var -> assignment.setValue(var, Utils.missingValue()));
        marNoise.entrySet().forEach(e -> {
            if (random.nextDouble() < e.getValue())
                assignment.setValue(e.getKey(), Utils.missingValue());
        });

        return assignment;
    }

    /**
     * Returns a {@code List} of randomly sampled {@link Assignment}s of size nSamples.
     *
     * @param nSamples an {@code int} that represents the number of samples.
     * @return a {@code List} of randomly sampled {@link Assignment}s.
     */
    private List<Assignment> getSampleList(int nSamples) {
        return this.getSampleStream(nSamples).collect(Collectors.toList());
    }

    /**
     * Returns an iterator over randomly sampled {@link Assignment}s of size nSamples.
     *
     * @param nSamples an {@code int} that represents the number of samples.
     * @return an iterator over randomly sampled {@link Assignment}s.
     */
    private Iterable<Assignment> getSampleIterator(int nSamples) {
        class I implements Iterable<Assignment> {
            public Iterator<Assignment> iterator() {
                return getSampleStream(nSamples).iterator();
            }
        }
        return new I();
    }

    /**
     * Sets the seed.
     *
     * @param seed an {@code int} that represents the seed value.
     */
    public void setSeed(int seed) {
        this.seed = seed;
        random = new Random(seed);
    }

    /**
     * Samples randomly a data stream of size nSamples from this LatentBayesianNetworkSampler.
     *
     * @param nSamples an {@code int} that represents the number of samples in the data stream.
     * @return a {@link DataStream} of {@link DataInstance}s.
     */
    public DataStream<DataInstance> sampleToDataStream(int nSamples) {
        class TemporalDataStream implements DataStream<DataInstance>, Serializable {
            /** Represents the serial version ID for serializing the object. */
            private static final long serialVersionUID = -3436599636425587512L;

            Attributes atts;
            LatentBayesianNetworkSampler sampler;
            int nSamples;

            TemporalDataStream(LatentBayesianNetworkSampler sampler1, int nSamples1) {
                this.sampler = sampler1;
                this.nSamples = nSamples1;

                List<Attribute> list = this.sampler.network.getVariables().getListOfVariables().stream()
                        .filter(var -> !LatentBayesianNetworkSampler.this.latentVars.containsKey(var))
                        .map(var -> new Attribute(var.getVarID(), var.getName(), var.getStateSpaceType())).collect(Collectors.toList());
                this.atts = new Attributes(list);
            }

            @Override
            public Attributes getAttributes() {
                return atts;
            }

            @Override
            public Stream<DataInstance> stream() {
                class TemporalDataInstance implements DataInstance, Serializable {

                    /** Represents the serial version ID for serializing the object. */
                    private static final long serialVersionUID = -3436599636425587512L;

                    Assignment assignment;
                    Attributes attributes;
                    List<Variable> variables;

                    TemporalDataInstance(Assignment assignment1, Attributes atts) {
                        this.assignment = assignment1;
                        this.attributes = atts;
                        this.variables = sampler.network.getVariables().getListOfVariables();
                    }

                    @Override
                    public double getValue(Variable var) {
                        return this.assignment.getValue(var);
                    }

                    @Override
                    public void setValue(Variable var, double value) {
                        this.assignment.setValue(var, value);
                    }

                    @Override
                    public Attributes getAttributes() {
                        return attributes;
                    }

                    @Override
                    public Set<Variable> getVariables() {
                        return assignment.getVariables();
                    }

                    @Override
                    public double getValue(Attribute att) {
                        return this.assignment.getValue(variables.get(att.getIndex()));
                    }

                    @Override
                    public void setValue(Attribute att, double value) {
                        if (!att.isSpecialAttribute())
                            this.assignment.setValue(variables.get(att.getIndex()), value);
                    }

                    @Override
                    public double[] toArray() {
                        int numAtts = attributes.getNumberOfAttributes();
                        double[] values = new double[numAtts];
                        for (int att = 0; att < numAtts; att++) {
                            values[att] = getValue(attributes.getFullListOfAttributes().get(att));
                        }

                        return values;
                    }

                    @Override
                    public String toString() {
                        return this.outputString();
                    }
                }

                return this.sampler.getSampleStream(this.nSamples).map(a -> new TemporalDataInstance(a, this.atts));
            }

            @Override
            public void close() {

            }

            @Override
            public boolean isRestartable() {
                return false;
            }

            @Override
            public void restart() {

            }
        }

        random = new Random(seed);

        return new TemporalDataStream(this, nSamples);
    }

    /**
     * Samples an {@link Assignment} randomly from a {@link BayesianNetwork}.
     *
     * @param network     a {@link BayesianNetwork} object.
     * @param causalOrder a list of variables given in a causal order.
     * @param random      a {@link Random} object.
     * @return the sampled {@link Assignment}.
     */
    private Assignment sample(BayesianNetwork network, List<Variable> causalOrder, Random random) {
        HashMapAssignment assignment = new HashMapAssignment(network.getNumberOfVars() - this.latentVars.size());
        for (Variable var : causalOrder) {
            if(!this.latentVars.containsKey(var)) {
                double sampledValue = network.getConditionalDistribution(var).getUnivariateDistribution(assignment).sample(random);
                assignment.setValue(var, sampledValue);
            }
        }
        return assignment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String listOptions() {
        return classNameID() + ",\\" +
                "-seed, 0, seed for random number generator\\";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String listOptionsRecursively() {
        return this.listOptions()
                + "\n" + network.listOptionsRecursively();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadOptions() {
        seed = getIntOption("-seed");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String classNameID() {
        return "LatentBayesianNetworkSampler";
    }
}
