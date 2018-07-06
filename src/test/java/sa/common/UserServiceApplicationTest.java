package sa.common;

import org.apache.qpid.server.Broker;
import org.apache.qpid.server.BrokerOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceApplicationTest {

    private static Broker broker;

    @BeforeClass
    public static void setup() throws Exception {
        embeddedQMQPBroker();
    }

    @Test
    public void contextLoads() {
    }

    @AfterClass
    public static void close() {
        broker.shutdown();
    }

    private static void embeddedQMQPBroker() throws Exception {
        broker = new Broker();
        BrokerOptions brokerOptions = new BrokerOptions();
        brokerOptions.setConfigProperty("qpid.amqp_port", "15673");
        brokerOptions.setConfigProperty("qpid.broker.defaultPreferenceStoreAttributes", "{\"type\": \"Noop\"}");
        brokerOptions.setConfigurationStoreType("Memory");
        broker.startup(brokerOptions);
    }

}
