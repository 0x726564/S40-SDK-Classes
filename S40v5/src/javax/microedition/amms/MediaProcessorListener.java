package javax.microedition.amms;

public interface MediaProcessorListener {
   String PROCESSING_ABORTED = "processingAborted";
   String PROCESSING_COMPLETED = "processingCompleted";
   String PROCESSING_ERROR = "processingError";
   String PROCESSING_STARTED = "processingStarted";
   String PROCESSING_STOPPED = "processingStopped";
   String PROCESSOR_REALIZED = "processRealized";

   void mediaProcessorUpdate(MediaProcessor var1, String var2, Object var3);
}
