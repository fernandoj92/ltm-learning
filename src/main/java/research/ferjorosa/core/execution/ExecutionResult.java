package research.ferjorosa.core.execution;

import research.ferjorosa.core.models.LTM;

import java.util.UUID;

/**
 * Created by fernando on 22/09/16.
 */
public class ExecutionResult {

    private LTM model;

    private UUID uniqueID;

    private String algorithm;

    private int index;

    private double nanoStart;

    private double nanoFinish;

    public ExecutionResult(LTM model, String algorithm, int index, double nanoStart, double nanoFinish){
        this.model = model;
        this.uniqueID = UUID.randomUUID();
        this.algorithm = algorithm;
        this.index = index;
        this.nanoStart = nanoStart;
        this.nanoFinish = nanoFinish;
    }

    public LTM getModel() {
        return model;
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public int getIndex() {
        return index;
    }

    public double getNanoStart() {
        return nanoStart;
    }

    public double getNanoFinish() {
        return nanoFinish;
    }
}
