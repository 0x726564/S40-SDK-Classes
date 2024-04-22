package javax.microedition.location;

public interface LocationListener {
   void locationUpdated(LocationProvider var1, Location var2);

   void providerStateChanged(LocationProvider var1, int var2);
}
