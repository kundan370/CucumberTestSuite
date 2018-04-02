package cucumbertest.runners;

import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeOptionsFactory;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.URLOutputStream;
import cucumber.runtime.io.UTF8OutputStreamWriter;
import cucumber.runtime.junit.Assertions;
import cucumber.runtime.model.*;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Tag;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import static cucumber.runtime.Utils.toURL;

public class ScenarioRunner extends ParentRunner{

    private ExecutorService executorService;
    private ExecutorService executorServiceSequence;
    private CompletionService<String> executionPool;
    private CompletionService<String> executionPoolSequence;
    RuntimeOptions runtimeOptions;
    private int scenarioSize = 0;
    private HashMap<Integer,List<TestScenario>> scenarioHashMap = new HashMap<>();
    private HashMap<Integer,List<TestScenario>> scenarioSequenceHashMap = new HashMap<>();
    private final int maxThreads = 4;
    private final int maxThreadsSequence = 1;
    private final int maxRetryCount = 2;
    private final long MAX_SCENARIO_EXEC_TIME = 30;
    private final int FAILURE_THRESHOLD = 30;

    public ScenarioRunner(Class klass) throws InitializationError {
        super(klass);
        executorService = Executors.newFixedThreadPool(maxThreads);
        executorServiceSequence = Executors.newFixedThreadPool(maxThreadsSequence);
        executionPool = new ExecutorCompletionService<>(executorService);
        executionPoolSequence = new ExecutorCompletionService<>(executorServiceSequence);
        ClassLoader classLoader = klass.getClassLoader();
        Assertions.assertNoCucumberAnnotatedMethods(klass);
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(klass);
        runtimeOptions = runtimeOptionsFactory.create();
        ResourceLoader resourceLoader = new MultiLoader(classLoader);
        addChildren(runtimeOptions.cucumberFeatures(resourceLoader));
    }

    private void addChildren(List<CucumberFeature> cucumberFeatures) {
        for (CucumberFeature feature : cucumberFeatures){
            int i = 0;
            Feature featureGherkin = feature.getGherkinFeature();
            String uri = feature.getPath();
            for (CucumberTagStatement tagStatement : feature.getFeatureElements()){
                List<Tag> allTags=new LinkedList<>();
                for(Tag tags1:featureGherkin.getTags())
                    allTags.add(tags1);
                for(Tag tags1:tagStatement.getGherkinModel().getTags())
                    allTags.add(tags1);
                if (CucumberScenario.class.isAssignableFrom(tagStatement.getClass())){
                    boolean flag=false;
                    for(Tag tag:allTags) {
                        if (tag.getName().contains("@RunSequence")) {
                            addScenarioToHashMap(i, new TestScenario((CucumberScenario) tagStatement, featureGherkin, uri), scenarioSequenceHashMap);
                            flag = true;
                            break;
                        }
                        if (tag.getName().contains("@P1")) {
                            addScenarioToHashMap(0, new TestScenario((CucumberScenario) tagStatement, featureGherkin, uri), scenarioHashMap);
                            flag = true;
                            break;
                        }
                    }
                    if(!flag)
                        addScenarioToHashMap(i + 1, new TestScenario((CucumberScenario) tagStatement, featureGherkin, uri), scenarioHashMap);
                } else if (CucumberScenarioOutline.class.isAssignableFrom(tagStatement.getClass())){
                    CucumberScenarioOutline outline = (CucumberScenarioOutline) tagStatement;
                    for (CucumberExamples example : outline.getCucumberExamplesList()){
                        List<CucumberScenario> scenarioList = example.createExampleScenarios();
                        for(CucumberScenario exampleScenario : scenarioList){
                            boolean flag=false;
                            for(Tag tag:allTags) {
                                if (tag.getName().contains("@RunSequence")) {
                                    addScenarioToHashMap(i,new TestScenario((CucumberScenario) exampleScenario, featureGherkin, uri),scenarioSequenceHashMap);
                                    flag = true;
                                    break;
                                }
                                if (tag.getName().contains("@P1")) {
                                    addScenarioToHashMap(0, new TestScenario((CucumberScenario) exampleScenario, featureGherkin, uri), scenarioHashMap);
                                    flag = true;
                                    break;
                                }
                            }
                            if(!flag)
                                addScenarioToHashMap(i + 1, new TestScenario(exampleScenario, featureGherkin, uri), scenarioHashMap);
                        }
                    }
                } else {
                    throw new RuntimeException("should never be reachable");
                }
                i++;
            }
        }
    }

    public void runScenarios(RunNotifier notifier){
        int folderIncrement = 0;
        int folderIncrementSequence = 0;
        int retryCount = 0;
        do{
            List<String> scenarioNames = new ArrayList<>();
            int executionCount = 0;
            int executionCountSequence=0;
            Iterator parallelScenarioIterator = scenarioHashMap.entrySet().iterator();
            while(parallelScenarioIterator.hasNext()){
                Map.Entry pair = (Map.Entry) parallelScenarioIterator.next();
                List<TestScenario> scenarioList = (List<TestScenario>) pair.getValue();
                for(TestScenario scenario : scenarioList){
                    if(retryCount == 0) {
                        scenario.setReportPath("target/cucumber/p" + folderIncrement + "/cucumber.json");
                        folderIncrement++;
                    }
                    if(scenario.canRun()){
                        try {
                            scenarioNames.add(scenario.getName());
                            TestFormatter testFormatter = new TestFormatter(new UTF8OutputStreamWriter(new URLOutputStream(toURL(scenario.getReportPath()))) , notifier, (retryCount+1));
                            testFormatter.uri(scenario.getFeatureUri());
                            testFormatter.feature(scenario.getFeature());
                            scenario.setFormatter(testFormatter);
                            executionPool.submit(new RunScenario(scenario, runtimeOptions)); //Submit scenario to executorservice
                            executionCount++;
                        }
                        catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
            Iterator sequenceScenarioIterator = scenarioSequenceHashMap.entrySet().iterator();
            while(sequenceScenarioIterator.hasNext()){
                Map.Entry pair = (Map.Entry) sequenceScenarioIterator.next();
                List<TestScenario> scenarioList = (List<TestScenario>) pair.getValue();
                for(TestScenario scenario : scenarioList){
                    if(retryCount == 0) {
                        scenario.setReportPath("target/cucumber/s" + folderIncrementSequence + "/cucumber.json");
                        folderIncrementSequence++;
                    }
                    if(scenario.canRun()){
                        try {
                            scenarioNames.add(scenario.getName());
                            TestFormatter testFormatter = new TestFormatter(new UTF8OutputStreamWriter(new URLOutputStream(toURL(scenario.getReportPath()))) , notifier, (retryCount+1));
                            testFormatter.uri(scenario.getFeatureUri());
                            testFormatter.feature(scenario.getFeature());
                            scenario.setFormatter(testFormatter);
                            executionPoolSequence.submit(new RunScenario(scenario, runtimeOptions));
                            executionCountSequence++;
                        }
                        catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
            for(int i = 0; i < executionCount ; i++){
                try {
                    System.out.println("Inside Try Catch Block");
                    System.out.println("Ready to run Scenario "+scenarioNames.get(i));
                    String result = executionPool.take().get(MAX_SCENARIO_EXEC_TIME, TimeUnit.MINUTES);
                    System.out.println(result);
                    System.out.println( i+1 + "/" + executionCount + " scenarios completed "  );
                    System.out.println("Exiting out try");

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                catch (Exception e){
                    System.out.println("SCENARIO : " +  scenarioNames.get(i) +" Timed out as it ran for more than " + MAX_SCENARIO_EXEC_TIME + " minutes or any other exception");
                    e.printStackTrace();
                }
            }
            for(int i = 0; i < executionCountSequence ; i++){
                try {
                    String result = executionPoolSequence.take().get(MAX_SCENARIO_EXEC_TIME,TimeUnit.MINUTES);
                    System.out.println(result);
                    System.out.println( i+1 + "/" + executionCountSequence + " Sequence scenarios completed "  );
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                catch (Exception e){
                    System.out.println("SCENARIO : " +  scenarioNames.get(i) +" Timed out as it ran for more than " + MAX_SCENARIO_EXEC_TIME + " minutes or any other exception");
                    e.printStackTrace();
                }
            }
            int failureCount = 0;
            Boolean reRun = true;
            Iterator parallelHashMapIterator = scenarioHashMap.entrySet().iterator();
            while(parallelHashMapIterator.hasNext()) {
                Map.Entry pair = (Map.Entry) parallelHashMapIterator.next();
                List<TestScenario> scenarioList = (List<TestScenario>) pair.getValue();
                for (TestScenario scenario : scenarioList) {
                    if(!scenario.isFail() && (scenario.getRunCount() > 1)) {
                        System.out.println(" Scenario name : " + scenario.getName());
                        System.out.println(" Passed on run " + scenario.getRunCount());
                    }
                    if (scenario.isFail()) failureCount++;
                }
            }
            Iterator sequenceHashMapIterator = scenarioSequenceHashMap.entrySet().iterator();
            while(sequenceHashMapIterator.hasNext()) {
                Map.Entry pair = (Map.Entry) sequenceHashMapIterator.next();
                List<TestScenario> scenarioList = (List<TestScenario>) pair.getValue();
                for (TestScenario scenario : scenarioList) {
                    if(!scenario.isFail() && (scenario.getRunCount() > 1)) {
                        System.out.println(" Scenario name : " + scenario.getName());
                        System.out.println(" Passed on run " + scenario.getRunCount());
                    }
                    if (scenario.isFail()) failureCount++;
                }
            }
            if(failureCount > 0) {
                int failurePercent = (failureCount * 100 ) / scenarioSize ;
                System.out.println("Total Tests: " + scenarioSize + " FAILED: " + failureCount);
                if(failurePercent > FAILURE_THRESHOLD ) {
                    System.out.println("Failures are more than " + FAILURE_THRESHOLD + "%. Not retrying");
                    reRun = false;
                }
            }
            else{
                System.out.println("HURRAY .. All tests passed");
                reRun = false;
            }
            if(!reRun){
                break;
            }
            retryCount++;
        }while(retryCount <= maxRetryCount);

        //Cleanup
        try {
            executorService.shutdown();
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                // pool didn't terminate after the first try
                executorService.shutdownNow();
            }


            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                // pool didn't terminate after the second try
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void addScenarioToHashMap(Object key,TestScenario scenario,Map map){
        List<TestScenario> hashValue = (List<TestScenario>)map.get(key);
        if (hashValue == null) {
            hashValue = new ArrayList<>();
        }
        hashValue.add(scenario);
        map.put(key, hashValue);
        scenarioSize++;
    }

    @Override
    protected List getChildren() {
        return new ArrayList();
    }

    @Override
    protected Description describeChild(Object o) {
        return null;
    }

    @Override
    protected void runChild(Object o, RunNotifier runNotifier) {
        if(o != null)
            System.out.println("THATS IMPOSSIBLE");
        else
            System.out.println("Nothing to run here");
    }

    @Override
    public void run(RunNotifier notifier) {
        runScenarios(notifier);
    }
}
