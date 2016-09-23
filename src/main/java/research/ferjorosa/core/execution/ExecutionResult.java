package research.ferjorosa.core.execution;

import research.ferjorosa.core.models.LTM;

import java.util.UUID;

/**
 * The execution result class is returned by the execute() methods. It wraps the resulting method with extra information
 * as the execution time
 */
public class ExecutionResult {

    /** The resulting model of the learning algorithm's execution. */
    private LTM model;

    /** The ExecutionResult's uniqueID. */
    private UUID uniqueID;

    /** Learning algorithm used to generate the result. */
    private String algorithm;

    /** Index of the Execution result. Used when there is an ongoing execution sequence, like a stream. */
    private int index;

    /** Number of nanoseconds at the start of the execution. */
    private double nanoStart;

    /** Number of nanoseconds at the end of the execution. */
    private double nanoFinish;

    /**
     * Creates a ExecutionResult by passing all its members except the uniqueID, which is randomly generated here.
     * @param model the resulting model of the learning algorithm's execution.
     * @param algorithm learning algorithm used to generate the result.
     * @param index index of the Execution result. Used when there is an ongoing execution sequence, like a stream.
     * @param nanoStart number of nanoseconds at the start of the execution.
     * @param nanoFinish number of nanoseconds at the end of the execution.
     */
    public ExecutionResult(LTM model, String algorithm, int index, double nanoStart, double nanoFinish){
        this.model = model;
        this.uniqueID = UUID.randomUUID();
        this.algorithm = algorithm;
        this.index = index;
        this.nanoStart = nanoStart;
        this.nanoFinish = nanoFinish;
    }

    /**
     * Returns the resulting model of the learning algorithm's execution.
     * @return the resulting model of the learning algorithm's execution.
     */
    public LTM getModel() {
        return model;
    }

    /**
     * Returns its uniqueID, in the form of an UUID.
     * @return its uniqueID, in the form of an UUID.
     */
    public UUID getUniqueID() {
        return uniqueID;
    }

    /**
     * Returns the name of the algorithm used to generate this execution.
     * @return the name of the algorithm used to generate this execution.
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * Returns the index of the result when generated in sequence.
     * @return the index of the result when generated in sequence.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns the number of nanoseconds at the start of the execution.
     * @return the number of nanoseconds at the start of the execution.
     */
    public double getNanoStart() {
        return nanoStart;
    }

    /**
     * Returns the number of nanoseconds at the end of the execution.
     * @return the number of nanoseconds at the end of the execution.
     */
    public double getNanoFinish() {
        return nanoFinish;
    }

    /**
     * Returns the execution's time in nanoseconds.
     * @return the execution's time in nanoseconds.
     */
    public double getNanoExecutionTime(){
        return this.nanoFinish - this.nanoStart;
    }
}
