package research.ferjorosa.util;

import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import research.ferjorosa.core.util.pair.SymmetricPair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Testing the {@link SymmetricPair} class
 */
public class SymmetricPairTest {

    private Attribute left;
    private Attribute right;

    @Before
    public void createPairContents(){

        DataStream<DataInstance> data  = DataStreamLoader.open("datasets/ferjorosaData/sprinklerDataHidden.arff");

        List<Attribute> attributes = data.getAttributes().getFullListOfAttributes();

        left = attributes.get(0);
        right = attributes.get(1);
    }

    @Test
    public void testSymmetry(){

        SymmetricPair<Attribute, Attribute> newLeftPair = new SymmetricPair<>(left,right);
        SymmetricPair<Attribute, Attribute> newRightPair = new SymmetricPair<>(right,left);

        /* Checks that the equals() method returns the same on both cases */

        Assert.assertEquals(newLeftPair, newRightPair);

        /* Checks that the hashcode() method returns the same on both cases */

        Assert.assertEquals(newLeftPair.hashCode(), newRightPair.hashCode());
    }
}
