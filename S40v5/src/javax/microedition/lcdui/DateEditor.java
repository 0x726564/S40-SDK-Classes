package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.style.Zone;

interface DateEditor {
   void a(DateField var1);

   void setTitle(String var1);

   int getHeight();

   int T();

   void a(Graphics var1, boolean var2);

   boolean a(int var1, int[] var2);

   boolean o(int var1);

   Command[] getExtraCommands();

   boolean a(Command var1);

   void setDisplay(Display var1);

   Zone getZone(int var1);

   void a(int var1, boolean var2);
}
