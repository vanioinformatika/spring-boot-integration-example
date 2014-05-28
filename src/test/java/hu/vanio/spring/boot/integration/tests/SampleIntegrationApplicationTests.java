package hu.vanio.spring.boot.integration.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;

import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StopWatch;

import hu.vanio.spring.boot.integration.ExampleIntegrationApplication;
import hu.vanio.spring.boot.integration.client.JaxWsClient;

/**
 * Integration test
 *
 * @author Gyula Szalai <gyula.szalai@vanio.hu>
 */
public class SampleIntegrationApplicationTests {

    /** Spring context */
    private static ConfigurableApplicationContext context;

    /** Webservice client */
    private final static JaxWsClient client = new JaxWsClient();
    
    /** Test content URL */
    static public final URL TEST_CONTENT_URL = Thread.currentThread().getContextClassLoader().getResource("spring-ws-logo.png");
    
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
        //String message = client.storeContent("test", new DataHandler(new URLDataSource(TEST_CONTENT_URL)));
        System.out.println("storeContent...");
        stopWatch.start("storeContent");
        String message = client.storeContent("test", new DataHandler(new FileDataSource("/home/gyszalai/apps/jboss-fuse-full-6.0.0.redhat-024.zip")));
        stopWatch.stop();
        System.out.println("Server message: " + message);
        assertEquals("Content successfully stored", message);
        
        System.out.println("loadContent...");
        stopWatch.start("loadContent");
        DataHandler dh = client.loadContent("test");
        stopWatch.stop();
        assertNotNull(dh);
        long size = countBytes(dh);
        System.out.println("Downloaded file size: " + size + " bytes");
        assertTrue(size > 0);

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
            CounterStream outStream = new CounterStream();
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
    
    /**
     * OutputStream implementation that counts bytes written to it
     */
    static private class CounterStream extends OutputStream {

        private long bytesWritten;
        
        @Override
        public void write(int b) throws IOException {
            this.bytesWritten++;
        }

        public long getBytesWritten() {
            return bytesWritten;
        }
        
    }
    
    
}
