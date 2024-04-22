package javax.microedition.pim;

public interface Event extends PIMItem {
   int ALARM = 100;
   int CLASS = 101;
   int CLASS_CONFIDENTIAL = 200;
   int CLASS_PRIVATE = 201;
   int CLASS_PUBLIC = 202;
   int END = 102;
   int LOCATION = 103;
   int NOTE = 104;
   int REVISION = 105;
   int START = 106;
   int SUMMARY = 107;
   int UID = 108;

   RepeatRule getRepeat();

   void setRepeat(RepeatRule var1);
}
