package eu.jirifrank.springler.config;

import eu.jirifrank.springler.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Test;


public class ApplicationTest extends AbstractIntegrationTest {

    @Test
    public void baseTest() {
        Assert.assertTrue("Context successfully started", true);
    }

}