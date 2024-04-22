package javax.microedition.location;

public class QualifiedCoordinates extends Coordinates {
   float horizontalAccuracy;
   float verticalAccuracy;

   public QualifiedCoordinates(double latitude, double longitude, float altitude, float horizontalAccuracy, float verticalAccuracy) {
      super(latitude, longitude, altitude);
      this.horizontalAccuracy = Float.NaN;
      this.verticalAccuracy = Float.NaN;
      this.setHorizontalAccuracy(horizontalAccuracy);
      this.setVerticalAccuracy(verticalAccuracy);
   }

   QualifiedCoordinates() {
      this(0.0D, 0.0D, Float.NaN, Float.NaN, Float.NaN);
   }

   QualifiedCoordinates(QualifiedCoordinates other) {
      this(other.latitude, other.longitude, other.altitude, other.horizontalAccuracy, other.verticalAccuracy);
   }

   public float getHorizontalAccuracy() {
      return this.horizontalAccuracy;
   }

   public float getVerticalAccuracy() {
      return this.verticalAccuracy;
   }

   public void setHorizontalAccuracy(float horizontalAccuracy) {
      if (!Float.isNaN(horizontalAccuracy) && !(horizontalAccuracy >= 0.0F)) {
         throw new IllegalArgumentException();
      } else {
         this.horizontalAccuracy = horizontalAccuracy;
      }
   }

   public void setVerticalAccuracy(float verticalAccuracy) {
      if (!Float.isNaN(verticalAccuracy) && !(verticalAccuracy >= 0.0F)) {
         throw new IllegalArgumentException();
      } else {
         this.verticalAccuracy = verticalAccuracy;
      }
   }

   public boolean equals(Object other) {
      if (other == this) {
         return true;
      } else if (!super.equals(other)) {
         return false;
      } else if (!(other instanceof QualifiedCoordinates)) {
         return false;
      } else {
         QualifiedCoordinates o = (QualifiedCoordinates)other;
         if (Float.floatToIntBits(this.getHorizontalAccuracy()) != Float.floatToIntBits(o.getHorizontalAccuracy())) {
            return false;
         } else {
            return Float.floatToIntBits(this.getVerticalAccuracy()) == Float.floatToIntBits(o.getVerticalAccuracy());
         }
      }
   }

   public int hashCode() {
      int result = 17;
      int result = 37 * result + Float.floatToIntBits(this.getHorizontalAccuracy());
      result = 37 * result + Float.floatToIntBits(this.getVerticalAccuracy());
      result = 37 * result + super.hashCode();
      return result;
   }

   QualifiedCoordinates clone() {
      QualifiedCoordinates clone = new QualifiedCoordinates(this);
      return clone;
   }
}
