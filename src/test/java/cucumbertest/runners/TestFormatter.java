package cucumbertest.runners;

import gherkin.formatter.JSONFormatter;
import gherkin.formatter.model.Match;
import gherkin.formatter.model.Result;
import gherkin.formatter.model.Scenario;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

public class TestFormatter extends JSONFormatter {

    private boolean isTestFailed = false;
    private Scenario scenario;
    private EachTestNotifier testNotifier;
    private RunNotifier runNotifier;
    private int runCount;
    private String uri;

    public TestFormatter(Appendable out, RunNotifier notifier, int runCount) {
        super(out);
        this.runNotifier = notifier;
        this.runCount = runCount;
    }

    @Override
    public void startOfScenarioLifeCycle(Scenario scenario) {
        super.startOfScenarioLifeCycle(scenario);
        isTestFailed = false;
        this.scenario = scenario;
        Description description = Description.createTestDescription(this.uri ,"Run : " + this.runCount + " Scenario "+scenario.getName());
        testNotifier = new EachTestNotifier(runNotifier, description);
        testNotifier.fireTestStarted();
    }

    @Override
    public void embedding(String mimeType, byte[] data) {
        if(mimeType.equalsIgnoreCase("text/plain")) {
//            lifecycle.fire(new MakeAttachmentEvent(data,new String(data),mimeType));
        }
        else
        {
//            lifecycle.fire(new MakeAttachmentEvent(data,"Screenshot",mimeType));
            super.embedding(mimeType,data);
        }
    }

    @Override
    public void endOfScenarioLifeCycle(Scenario scenario){
        super.endOfScenarioLifeCycle(scenario);
//        lifecycle.fire(new TestCaseFinishedEvent());
        testNotifier.fireTestFinished();
    }

    private void reportError(Result result){
        isTestFailed = true;
        Throwable error = result.getError();
        if(error != null){
            testNotifier.addFailure(error);
            if (error instanceof AssumptionViolatedException) {
//                lifecycle.fire(new TestCaseCanceledEvent().withThrowable(error));

            } else {
//                lifecycle.fire(new TestCaseFailureEvent().withThrowable(error));
            }
        }
    }

    @Override
    public void uri(String uri) {
        this.uri = uri;
        super.uri(uri);
    }

    @Override
    public void before(Match match, Result result) {
        super.before(match,result);
        if (isTestFailed(result)) {
            reportError(result);
        }
    }

    @Override
    public void result(Result result) {
        super.result(result);
        if (isTestFailed(result)) {
            reportError(result);
        }
    }

    @Override
    public void match(Match match){
        super.match(match);

    }

    public boolean isTestFailed(Result result) {
        String status = result.getStatus();
        return Result.FAILED.equals(status) || Result.UNDEFINED.getStatus().equals(status) || "pending".equals(status);
    }

    public boolean getResult(){
        return isTestFailed;
    }

    public Scenario getScenario(){
        return scenario;
    }


    private String getFeatureName(){
        String[] path = this.uri.split("/");
        return path[0];
    }

    private String getStoryName(){
        String[] path = this.uri.split("/");
        return path[path.length - 1];
    }
}
