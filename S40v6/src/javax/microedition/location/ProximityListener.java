package javax.microedition.location;

public interface ProximityListener {
   void proximityEvent(Coordinates var1, Location var2);

   void monitoringStateChanged(boolean var1);
}
