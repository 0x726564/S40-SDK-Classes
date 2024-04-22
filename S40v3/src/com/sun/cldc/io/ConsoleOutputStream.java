package com.sun.cldc.io;

import java.io.IOException;
import java.io.OutputStream;

public class ConsoleOutputStream extends OutputStream {
   public synchronized native void write(int var1) throws IOException;
}
