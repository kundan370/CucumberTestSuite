package cucumbertest.runners;

import cucumber.runtime.ClassFinder;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeOptionsFactory;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.model.CucumberScenario;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import cucumber.runtime.Runtime;

import java.util.concurrent.Callable;

public class RunScenario implements Callable<String> {

    private TestScenario scenario;
    private Formatter formatter;
    private Reporter reporter;
    private RuntimeOptions runtimeOptions;

    RunScenario(TestScenario scenario, RuntimeOptions runtimeOptions) {
        this.scenario = scenario;
        this.formatter = scenario.getFormatter();
        this.reporter = scenario.getFormatter();
        this.runtimeOptions = runtimeOptions;
    }

    @Override
    public String call() {
        ClassLoader classLoader = super.getClass().getClassLoader();
        ResourceLoader resourceLoader = new MultiLoader(classLoader);
        Runtime runtime = createRuntime(resourceLoader, classLoader, runtimeOptions);
        CucumberScenario cucumberScenario = this.scenario.getScenario();
        System.out.println("******************************************************************************************************************");
        System.out.println("Started Scenario :  " + cucumberScenario.getGherkinModel().getName());
        cucumberScenario.run(formatter, reporter , runtime);
        this.scenario.addRunCount();
        formatter.done();
        formatter.close();
        return "COMPLETED " + cucumberScenario.getGherkinModel().getName();
    }

    private cucumber.runtime.Runtime createRuntime(ResourceLoader resourceLoader, ClassLoader classLoader,
                                                   RuntimeOptions runtimeOptions)  {
        ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
        Runtime runtime = null;
        try {
            runtime =  new cucumber.runtime.Runtime(resourceLoader, classFinder, classLoader, runtimeOptions);
        }
        catch(Exception e){
            System.out.println("Failed to create runtime due to exception: "  + e);
        }
        return runtime;
    }
}
