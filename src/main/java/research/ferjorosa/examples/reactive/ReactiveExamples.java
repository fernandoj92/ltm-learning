package research.ferjorosa.examples.reactive;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.models.DAG;
import eu.amidst.core.variables.Variable;
import eu.amidst.core.variables.Variables;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

/**
 * Created by Fer on 23/09/2016.
 */
public class ReactiveExamples {

    public static void main(String[] args) throws Exception {

        // singleValueExample();

    }

    private static void singleValueExample(){
        Observable<Integer> obs = Observable.just(1337);

        Subscription subscription = obs.subscribe(new Observer<Integer>() {
            @Override
            public void onCompleted() {
                System.out.println("Observer: Called when sequence completes");
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Observer: Called when the sequence completes with n error");
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("Observer: Called on every element of the sequence");
            }
        });

        System.out.println(subscription.isUnsubscribed());
    }

    private static void unfinishedComplexExample(){
        //We can open the data stream using the static class DataStreamLoader
        DataStream<DataInstance> data = DataStreamLoader.open("datasets/syntheticData.arff");


        /**
         * 1. Once the data is loaded, we create a random variable for each of the attributes (i.e. data columns)
         * in our data.
         *
         * 2. {@link Variables} is the class for doing that. It takes a list of Attributes and internally creates
         * all the variables. We create the variables using Variables class to guarantee that each variable
         * has a different ID number and make it transparent for the user.
         *
         * 3. We can extract the Variable objects by using the method getVariableByName();
         */
        Variables variables = new Variables(data.getAttributes());

        Variable a = variables.getVariableByName("A");
        Variable b = variables.getVariableByName("B");
        Variable c = variables.getVariableByName("C");
        Variable d = variables.getVariableByName("D");
        Variable e = variables.getVariableByName("E");
        Variable g = variables.getVariableByName("G");
        Variable h = variables.getVariableByName("H");
        Variable i = variables.getVariableByName("I");

        /**
         * 1. Once you have defined your {@link Variables} object, the next step is to create
         * a DAG structure over this set of variables.
         *
         * 2. To add parents to each variable, we first recover the ParentSet object by the method
         * getParentSet(Variable var) and then call the method addParent().
         */
        DAG dag = new DAG(variables);

        dag.getParentSet(e).addParent(a);
        dag.getParentSet(e).addParent(b);

        dag.getParentSet(h).addParent(a);
        dag.getParentSet(h).addParent(b);

        dag.getParentSet(i).addParent(a);
        dag.getParentSet(i).addParent(b);
        dag.getParentSet(i).addParent(c);
        dag.getParentSet(i).addParent(d);

        dag.getParentSet(g).addParent(c);
        dag.getParentSet(g).addParent(d);

        /**
         * 1. We first check if the graph contains cycles.
         *
         * 2. We print out the created DAG. We can check that everything is as expected.
         */
        if (dag.containCycles()) {
            try {
            } catch (Exception ex) {
                throw new IllegalArgumentException(ex);
            }
        }





        //System.out.println(dag.toString());

        //BayesianNetwork bn = new BayesianNetwork(dag);
        //System.out.println(bn.toString());
    }

}
