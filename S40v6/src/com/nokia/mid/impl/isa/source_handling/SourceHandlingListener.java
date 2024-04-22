package com.nokia.mid.impl.isa.source_handling;

public interface SourceHandlingListener {
   int CONSUMER_EOF = 0;
   int EXCEPTION = 1;
   int PRODUCER_EOF = 0;

   void sourceHandlingEvent(int var1, SourceHandlingException var2);
}
