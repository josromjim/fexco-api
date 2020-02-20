package jrj.fexco.test.api;

import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * API Test configuration
 * 
 * @author José Romero
 *
 */
@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty"})
public class RunApiCucumberTest {
}
