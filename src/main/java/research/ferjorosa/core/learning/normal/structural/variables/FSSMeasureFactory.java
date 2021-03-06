package research.ferjorosa.core.learning.normal.structural.variables;

import static research.ferjorosa.core.learning.normal.structural.variables.FSSMeasures.MUTUAL_INFORMATION;

/**
 * Created by fernando on 5/09/16.
 */
public class FSSMeasureFactory {

    public static FSSMeasure retrieveInstance(String fssMeasure){
        if(fssMeasure.equals(MUTUAL_INFORMATION.toString()))
            return new MutualInformation();

        // Unexpected error
        throw new IllegalArgumentException();
    }
}
