package sa.common.integration;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.apache.qpid.server.Broker;
import org.apache.qpid.server.BrokerOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class BaseIntegrationTest {

    protected static GreenMail greenMail;
    protected static Broker broker;

    @BeforeClass
    public static void setupRabbit() throws Exception {
        embeddedQMQPBroker();
        startMailServerMock();
    }

    @AfterClass
    public static void removeRabbit() {
        broker.shutdown();
        greenMail.stop();
    }

    private static void embeddedQMQPBroker() throws Exception {
        broker = new Broker();
        BrokerOptions brokerOptions = new BrokerOptions();
        brokerOptions.setConfigProperty("qpid.amqp_port", "15673");
        brokerOptions.setConfigProperty("qpid.broker.defaultPreferenceStoreAttributes", "{\"type\": \"Noop\"}");
        brokerOptions.setConfigurationStoreType("Memory");
        broker.startup(brokerOptions);
    }

    private static void startMailServerMock() {
        ServerSetup setup = new ServerSetup(50025, "localhost", "smtp");
        greenMail = new GreenMail(setup);
        greenMail.setUser("username", "secret");
        greenMail.start();
    }
}