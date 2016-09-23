package research.ferjorosa.core.execution;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;

/**
 * Created by Fer on 23/09/2016.
 */
public interface StaticExecution extends Execution{

    ExecutionResult execute(DataOnMemory<DataInstance> batch);
}
