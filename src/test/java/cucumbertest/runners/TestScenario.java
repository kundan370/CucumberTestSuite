package cucumbertest.runners;

import cucumber.runtime.model.CucumberScenario;
import gherkin.formatter.model.Feature;

public class TestScenario {

    private CucumberScenario scenario;
    private Feature feature;
    private TestFormatter formatter;
    private int runCount=0;
    private final int maxRetryCount = 2;
    private String reportPath ;
    private String uri;

    public TestScenario(CucumberScenario scenario, Feature feature,String uri){
        this.scenario = scenario;
        this.feature = feature;
        this.uri = uri;
    }

    public void setFormatter(TestFormatter formatter){
        this.formatter = formatter;
    }

    public void setReportPath(String path){
        this.reportPath = path;
    }

    public Feature getFeature(){
        return this.feature;
    }

    public CucumberScenario getScenario(){
        return this.scenario;
    }

    public TestFormatter getFormatter(){
        return this.formatter;
    }

    public String getName(){
        return this.scenario.getGherkinModel().getName();
    }

    public String getReportPath(){
        return this.reportPath;
    }

    public String getFeatureUri(){
        return this.uri;
    }

    public boolean isFail(){
        return this.formatter.getResult();
    }

    public void addRunCount(){
        this.runCount++;
    }

    public int getRunCount(){ return this.runCount;}

    public boolean canRun(){
        if( runCount == 0 ) return true;
        return isFail();
    }
}
