package hu.vanio.spring.boot.integration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.activation.DataHandler;

import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StopWatch;

import hu.vanio.spring.boot.integration.client.JaxWsClient;

/**
 * Integration test
 *
 * @author Gyula Szalai <gyula.szalai@vanio.hu>
 */
public class ExampleIntegrationApplicationTest {

    /** Spring context */
    private static ConfigurableApplicationContext context;

    /** Webservice client */
    private final static JaxWsClient client = new JaxWsClient();
    
    @BeforeClass
    public static void start() throws Exception {
        context = SpringApplication.run(ExampleIntegrationApplication.class);
    }

    @AfterClass
    public static void stop() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    public void test() throws Exception {
        StopWatch stopWatch = new StopWatch(this.getClass().getSimpleName());
        long contentlength = 100000000L;
        
        System.out.println("storeContent...");
        stopWatch.start("storeContent");
        DataHandler content = new DataHandler(new DummyDataSource("test", 65, contentlength));
        String message = client.storeContent("test", content);
        stopWatch.stop();
        System.out.println("Server message: " + message);
        assertEquals("Content successfully stored", message);
        
        System.out.println("loadContent...");
        stopWatch.start("loadContent");
        DataHandler dh = client.loadContent("test");
        stopWatch.stop();
        assertNotNull(dh);
        long size = countBytes(dh);
        System.out.printf("Downloaded file size: %,.2f kB", (double)size/1024);
        assertTrue(size == contentlength);

        System.out.println("\n" + stopWatch.prettyPrint());
    }
    
    /**
     * Count the bytes of the content
     * 
     * @param content The content
     * @throws IOException If an error occurs
     */
    static public long countBytes(DataHandler content) throws IOException {
        long size = 0;
        byte[] buffer = new byte[1024];
        try (InputStream is = content.getInputStream()) {
            CounterOutputStream outStream = new CounterOutputStream();
            try {
                for (int readBytes; (readBytes = is.read(buffer, 0, buffer.length)) > 0;) {
                    size += readBytes;
                    outStream.write(buffer, 0, readBytes);
                }
            } finally {
                size = outStream.getBytesWritten();
            }
        }
        return size;
    }
    
}
