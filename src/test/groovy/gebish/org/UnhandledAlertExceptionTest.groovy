package gebish.org

import geb.junit4.GebReportingTest
import org.junit.*
import org.junit.runners.MethodSorters
import ratpack.groovy.markup.MarkupRenderer
import ratpack.groovy.markup.internal.DefaultMarkupRenderer
import ratpack.groovy.test.embed.GroovyEmbeddedApp
import ratpack.test.embed.EmbeddedApp

import static ratpack.groovy.Groovy.htmlBuilder

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UnhandledAlertExceptionTest extends GebReportingTest {

    EmbeddedApp app = GroovyEmbeddedApp.build {
        bindings {
            bind(MarkupRenderer, DefaultMarkupRenderer)
        }
        handlers {
            get {
                render htmlBuilder {
                    html {
                        body {
                            button "Click me!", onclick: "alert('Hey Jeff!');"
                        }
                    }
                }
            }
        }
    }

    @Rule
    public UnhandledAlertExceptionInterceptingRule exceptionInterceptor = new UnhandledAlertExceptionInterceptingRule(this)

    @Before
    void setup() {
        browser.baseUrl = app.address.toString()
    }

    @After
    void cleanup() {
        app.close()
    }

    @Test
    void test1shouldFailCausingMultipleFailureException() {
        to AlertPage

        $("button").click()

        assert button.text() == "Click me!"
    }

    @Test
    void test2shouldPass() {
        to AlertPage

        assert button.text() == "Click me!"
    }

    @Test
    void test3shouldFailCausingUnhandledAlertException() {
        to AlertPage

        $("button").click()
    }

    @Test
    void test4shouldPass() {
        to AlertPage

        assert button.text() == "Click me!"
    }
}
