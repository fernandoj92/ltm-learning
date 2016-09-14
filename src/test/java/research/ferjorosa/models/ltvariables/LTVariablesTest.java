package research.ferjorosa.models.ltvariables;

import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.datastream.Attributes;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.variables.Variable;
import eu.amidst.core.variables.Variables;
import research.ferjorosa.core.models.ltvariables.LTVariables;
import research.ferjorosa.core.models.ltvariables.LatentVariable;
import research.ferjorosa.core.models.ltvariables.ObservedVariable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Testing the {@link LTVariables} class
 */
public class LTVariablesTest {

    private Variables variables;
    private LTVariables ltVariables;

    private ObservedVariable sprinkler;
    private ObservedVariable rain;
    private ObservedVariable wetGrass;
    private LatentVariable ltCloudy;

    @Before
    public void createLTVariables() {

        /* Creates the LTM structure (LTDAG) */

        // The ClassLoader adds a "/" at the beginning of the path that makes it throw an exception when loading
        String resourcePath = getClass().getResource("/sprinklerDataHidden.arff").getPath().substring(1);
        DataStream<DataInstance> data  = DataStreamLoader.open(resourcePath);

        variables = new Variables(data.getAttributes());
        Variable latentCloudy = variables.newMultinomialVariable("latentCloudy", Arrays.asList("TRUE", "FALSE"));

        ltVariables = new LTVariables(variables);

        sprinkler = ltVariables.newObservedVariable(variables.getVariableByName("sprinkler"));
        rain = ltVariables.newObservedVariable(variables.getVariableByName("rain"));
        wetGrass = ltVariables.newObservedVariable(variables.getVariableByName("wetGrass"));
        ltCloudy = ltVariables.newLatentVariable(latentCloudy , 0);
    }

    @Test
    public void testNewLatentVariable(){

        /* Tries to create a Latent Variable from a variable that doesn't belong to the LTVariables object */

        // TODO: Este no es problema mio, al hacer contains() tanto en la lista como en el objeto Variables, da error
        // ya que el ID de ambas vars es 0, estos es porque se usan 2 objetos Variables diferentes.

        List<Attribute> attributeList = new ArrayList<>();
        Attributes falseAttributes = new Attributes(attributeList);
        Variables otherVariablesObject = new Variables(falseAttributes);
        Variable newLVMultinomial = otherVariablesObject.newMultinomialVariable("testMultinomial", 3);

        try{
            ltVariables.newLatentVariable(newLVMultinomial, 1);
            Assert.fail("Should throw an IllegalArgumentException, this variable wasn't present in the LTVariables creation");
        }catch (IllegalArgumentException e) {
            org.junit.Assert.assertTrue(true);
        } catch (Exception e){
            org.junit.Assert.assertTrue(false);
        }

        /* Correctly creates a Latent variable from a variable that was previously present in the LTVariables object */

        ltCloudy = ltVariables.newLatentVariable(
                variables.newMultinomialVariable("latentCloudy", Arrays.asList("TRUE", "FALSE")), 0);

        Assert.assertTrue(ltVariables.getLatentVariables().contains(ltCloudy));
    }

    @Test
    public void testNewObservedVariable(){

        /* Tries to create a Observed Variable from a variable that doesn't belong to the LTVariables object */
        Variable newOVMultinomial = variables.newMultinomialVariable("testMultinomial", 3);

        try{
            ltVariables.newObservedVariable(newOVMultinomial);
            Assert.fail("Should throw an IllegalArgumentException, this variable wasn't present in the LTVariables creation");
        }catch (IllegalArgumentException e) {
            org.junit.Assert.assertTrue(true);
        } catch (Exception e){
            org.junit.Assert.assertTrue(false);
        }

        /* Correctly creates 3 Observed variable from 3 variables that were previously present in the LTVariables object */

        sprinkler = ltVariables.newObservedVariable(variables.getVariableByName("sprinkler"));
        rain = ltVariables.newObservedVariable(variables.getVariableByName("rain"));
        wetGrass = ltVariables.newObservedVariable(variables.getVariableByName("wetGrass"));

        Assert.assertTrue(ltVariables.getObservedVariables().contains(sprinkler));
        Assert.assertTrue(ltVariables.getObservedVariables().contains(rain));
        Assert.assertTrue(ltVariables.getObservedVariables().contains(wetGrass));
    }

    @Test
    public void testSize(){

        /* Checks that sizes are equal */

        int LVSize = ltVariables.getLatentVariables().size();
        int OVSize = ltVariables.getObservedVariables().size();

        Assert.assertEquals(LVSize + OVSize, ltVariables.size());
    }
}
