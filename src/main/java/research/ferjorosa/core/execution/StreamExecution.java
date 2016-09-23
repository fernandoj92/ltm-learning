package research.ferjorosa.core.execution;

import rx.Observable;

/**
 * Created by Fer on 23/09/2016.
 */
public interface StreamExecution extends Execution{

    Observable<ExecutionResult> execute();
}
