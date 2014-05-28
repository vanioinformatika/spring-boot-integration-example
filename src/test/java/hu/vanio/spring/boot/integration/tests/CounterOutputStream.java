package hu.vanio.spring.boot.integration.tests;

import java.io.IOException;
import java.io.OutputStream;

/**
 * OutputStream implementation that counts bytes written to it
 * 
 * @author Gyula Szalai <gyula.szalai@vanio.hu>
 */
public class CounterOutputStream extends OutputStream {

    private long bytesWritten;

    @Override
    public void write(int b) throws IOException {
        this.bytesWritten++;
    }

    public long getBytesWritten() {
        return bytesWritten;
    }

}
