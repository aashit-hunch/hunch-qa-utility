package org.hunch.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;

public class LoggingOutputStream extends OutputStream {
    private static final int DEFAULT_BUFFER_LENGTH = 2048;
    private boolean hasBeenClosed = false;
    private byte[] buf;
    private int count;
    private int curBufLength;
    private Logger log;
    private Level level;

    public LoggingOutputStream(Logger log, Level level) throws IllegalArgumentException {
        if (log != null && level != null) {
            this.log = log;
            this.level = level;
            this.curBufLength = 2048;
            this.buf = new byte[this.curBufLength];
            this.count = 0;
        } else {
            throw new IllegalArgumentException("Logger or log level must be not null");
        }
    }

    public void write(int b) throws IOException {
        if (this.hasBeenClosed) {
            throw new IOException("The stream has been closed.");
        } else if (b != 0) {
            if (this.count == this.curBufLength) {
                int newBufLength = this.curBufLength + 2048;
                byte[] newBuf = new byte[newBufLength];
                System.arraycopy(this.buf, 0, newBuf, 0, this.curBufLength);
                this.buf = newBuf;
                this.curBufLength = newBufLength;
            }

            this.buf[this.count] = (byte)b;
            ++this.count;
        }
    }

    public void flush() {
        if (this.count != 0) {
            byte[] bytes = new byte[this.count];
            System.arraycopy(this.buf, 0, bytes, 0, this.count);
            String str = new String(bytes);
            this.log.log(this.level, str);
            this.count = 0;
        }
    }

    public void close() {
        this.flush();
        this.hasBeenClosed = true;
    }
}
