package research.ferjorosa.models;


import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.variables.Variable;
import eu.amidst.core.variables.Variables;
import research.ferjorosa.core.models.LTDAG;
import research.ferjorosa.core.models.ltvariables.LTVariables;
import research.ferjorosa.core.models.ltvariables.LatentVariable;
import research.ferjorosa.core.models.ltvariables.ObservedVariable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

/**
 * Testing the {@link LTDAG} class
 */
public class LTDAGTest {

    private LTDAG ltdag;
    private ObservedVariable sprinkler;
    private ObservedVariable rain;
    private ObservedVariable wetGrass;
    private LatentVariable ltCloudy;

    @Before
    public void createLTDAG(){

        /* Creates the LTM structure (LTDAG) */

        DataStream<DataInstance> data  = DataStreamLoader.open("datasets/ferjorosaData/sprinklerDataHidden.arff");

        Variables variables = new Variables(data.getAttributes());
        Variable latentCloudy = variables.newMultinomialVariable("latentCloudy", Arrays.asList("TRUE", "FALSE"));

        LTVariables ltVariables = new LTVariables(variables);

        sprinkler = ltVariables.newObservedVariable(variables.getVariableByName("sprinkler"));
        rain = ltVariables.newObservedVariable(variables.getVariableByName("rain"));
        wetGrass = ltVariables.newObservedVariable(variables.getVariableByName("wetGrass"));
        ltCloudy = ltVariables.newLatentVariable(latentCloudy, 0);

        ltdag = new LTDAG(ltVariables);
    }

    @Test
    public void testGetVariables(){

        /* Checks that the returned observed variables corresponds with the previously created ones */

        Assert.assertEquals(ltdag.getObservedVariables().size(), 3);
        Assert.assertTrue(ltdag.getObservedVariables().contains(sprinkler));
        Assert.assertTrue(ltdag.getObservedVariables().contains(rain));
        Assert.assertTrue(ltdag.getObservedVariables().contains(wetGrass));

        /* Checks that the returned latent variables corresponds with the previously created ones */

        Assert.assertEquals(ltdag.getLatentVariables().size(), 1);
        Assert.assertTrue(ltdag.getLatentVariables().contains(ltCloudy));
    }

    @Test
    public void  testAddParent(){

        /* Tries to make an observed variable parent of a latent variable */

        try {
            ltdag.addParent(ltCloudy, sprinkler);
            Assert.fail("Should throw an IllegalArgumentException, a Latent Variable cannot have an Observed Variable as parent");
        }catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        } catch (Exception e){
            Assert.assertTrue(false);
        }

        /* Creates a series of correct parent links */

        ltdag.addParent(sprinkler, ltCloudy);
        ltdag.addParent(rain, ltCloudy);
        ltdag.addParent(wetGrass, sprinkler);
        ltdag.addParent(wetGrass, rain);

        /* Tests that these parents have been added correctly */

        Assert.assertEquals(ltdag.getDAG().getParentSets().size(), 4);
        Assert.assertTrue(ltdag.getDAG().getParentSet(sprinkler.getVariable()).contains(ltCloudy.getVariable()));
        Assert.assertTrue(ltdag.getDAG().getParentSet(rain.getVariable()).contains(ltCloudy.getVariable()));
        Assert.assertTrue(ltdag.getDAG().getParentSet(wetGrass.getVariable()).contains(sprinkler.getVariable()));
        Assert.assertTrue(ltdag.getDAG().getParentSet(wetGrass.getVariable()).contains(rain.getVariable()));

    }
}
