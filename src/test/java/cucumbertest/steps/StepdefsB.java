package cucumbertest.steps;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class StepdefsB {

    private int time = 1000;

    @When("^feature Two scenario One step One$")
    public void step1() {
        try {
            Thread.sleep(time);
            System.out.println("-----------------------------------feature Two scenario One step One");
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @And("^feature Two scenario One step Two$")
    public void step2 () {
        try {
            Thread.sleep(time);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("^feature Two scenario One step Three$")
    public void step3() {
        try {
            Thread.sleep(time);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @When("^feature Two scenario Two step One$")
    public void step4() {
        try {
            Thread.sleep(time);
            System.out.println("-----------------------------------feature Two scenario Two step One");
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @And("^feature Two scenario Two step Two$")
    public void step5 () {
        try {
            Thread.sleep(time);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("^feature Two scenario Two step Three$")
    public void step6() {
        try {
            Thread.sleep(time);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @When("^feature Two scenario Three step One$")
    public void step7() {
        try {
            Thread.sleep(time);
            System.out.println("-----------------------------------feature Two scenario Three step One");
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @And("^feature Two scenario Three step Two$")
    public void step8 () {
        try {
            Thread.sleep(time);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("^feature Two scenario Three step Three$")
    public void step9() {
        try {
            Thread.sleep(time);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @When("^feature Two scenario Four step One$")
    public void step10() {
        try {
            Thread.sleep(time);
            System.out.println("-----------------------------------feature Two scenario Four step One");
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @And("^feature Two scenario Four step Two$")
    public void step11 () {
        try {
            Thread.sleep(time);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("^feature Two scenario Four step Three$")
    public void step12() {
        try {
            Thread.sleep(time);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}