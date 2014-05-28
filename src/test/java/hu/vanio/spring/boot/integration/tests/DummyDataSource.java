package hu.vanio.spring.boot.integration.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

/**
 * Dummy DataSource implementation
 *
 * @author Gyula Szalai <gyula.szalai@vanio.hu>
 */
public class DummyDataSource implements DataSource {

    private final String name;
    private final int contentByte;
    private final long contentLength;
    
    public DummyDataSource(String name, int contentByte, long contentLength) {
        this.name = name;
        this.contentByte = contentByte;
        this.contentLength = contentLength;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return new ContentGeneratorInputStream(this.contentByte, this.contentLength);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getContentType() {
        return "application/octet-stream";
    }

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Dummy InputStream implementation that returns the same byte N times. Just for testing.
     */
    private static class ContentGeneratorInputStream extends InputStream {

        /** The byte that this stream returns. This stream will return as many of this byte as you specified in contentLength. */
        private final int contentByte;
        /** The size of the generated content */
        private final long contentLength;
        /** Position */
        private int pos;

        /**
         * Constructs a new instance
         *
         * @param contentByte The byte that this stream returns. This stream will return as many of this byte as you specified in contentLength.
         * @param contentLength The size of the generated content
         */
        public ContentGeneratorInputStream(int contentByte, long contentLength) {
            this.contentByte = contentByte;
            this.contentLength = contentLength;
        }

        @Override
        public int read() throws IOException {
            if (pos < contentLength) {
                pos++;
                return contentByte;
            } else {
                return -1;
            }
        }

    }

}
