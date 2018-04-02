package cucumbertest.runners;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(ScenarioRunner.class)
@CucumberOptions(
        plugin = {"pretty"},
        glue = {"classpath:cucumbertest.steps"},
        features = {"src/test/resources/cucumbertest"},
        tags = {"@cucumber-test"}
        )
public class RunCucumberTest {
}