package gebish.org

import geb.junit4.GebReportingTest
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.MultipleFailureException
import org.junit.runners.model.Statement
import org.openqa.selenium.UnhandledAlertException

class UnhandledAlertExceptionInterceptingRule implements TestRule {

    GebReportingTest test

    UnhandledAlertExceptionInterceptingRule(GebReportingTest test) {
        this.test = test;
    }

    void handleAlert() {
        def alert = test.browser.driver.switchTo().alert()
        def alertText = alert.text
        alert.accept()
        test.report("Alert: $alertText")
    }

    Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            void evaluate() throws Throwable {
                try {
                    base.evaluate()
                } catch (MultipleFailureException e) {
                    if (e.failures.any { it in UnhandledAlertException }) {
                        handleAlert()
                    }
                    throw e
                } catch (UnhandledAlertException e) {
                    handleAlert()
                    throw e
                }
            }
        }
    }
}
