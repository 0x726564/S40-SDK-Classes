package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.style.Zone;

interface DateEditor {
   void init(DateField var1, String var2);

   void setTitle(String var1);

   int getHeight();

   int callPreferredWidth(int var1);

   void paintDateField(Graphics var1, int var2, int var3, boolean var4);

   boolean callTraverse(int var1, int var2, int var3, int[] var4);

   boolean processKey(int var1);

   Command[] getExtraCommands();

   boolean launchExtraCommand(Command var1);

   void setDisplay(Display var1);

   Zone getZone(int var1);

   void initializeDateInputFields(int var1, boolean var2);
}
