package cucumbertest.steps;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class StepdefsA {

    private int time = 1000;

    @When("^feature One scenario One step One$")
    public void step1() {
        try {
            Thread.sleep(time);
            System.out.println("-----------------------------------feature One scenario One step One");
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @And("^feature One scenario One step Two$")
    public void step2 () {
        try {
            Thread.sleep(time);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("^feature One scenario One step Three$")
    public void step3() {
        try {
            Thread.sleep(time);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @When("^feature One scenario Two step One$")
    public void step4() {
        try {
            Thread.sleep(time);
            System.out.println("-----------------------------------feature One scenario Two step One");
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @And("^feature One scenario Two step Two$")
    public void step5 () {
        try {
            Thread.sleep(time);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("^feature One scenario Two step Three$")
    public void step6() {
        try {
            Thread.sleep(time);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @When("^feature One scenario Three step One$")
    public void step7() {
        try {
            Thread.sleep(time);
            System.out.println("-----------------------------------feature One scenario Three step One");
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @And("^feature One scenario Three step Two$")
    public void step8 () {
        try {
            Thread.sleep(time);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("^feature One scenario Three step Three$")
    public void step9() {
        try {
            Thread.sleep(time);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @When("^feature One scenario Four step One$")
    public void step10() {
        try {
            Thread.sleep(time);
            System.out.println("-----------------------------------feature One scenario Four step One");
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @And("^feature One scenario Four step Two$")
    public void step11 () {
        try {
            Thread.sleep(time);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("^feature One scenario Four step Three$")
    public void step12() {
        try {
            Thread.sleep(time);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}