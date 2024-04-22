package javax.microedition.m2g;

import com.nokia.mid.impl.isa.m2g.SVGAnimatorImpl;

public abstract class SVGAnimator {
   public static SVGAnimator createAnimator(SVGImage svgImage) {
      return SVGAnimatorImpl.createAnimatorImpl(svgImage);
   }

   public static SVGAnimator createAnimator(SVGImage svgImage, String componentBaseClass) {
      return SVGAnimatorImpl.createAnimatorImpl(svgImage, componentBaseClass);
   }

   public abstract void setSVGEventListener(SVGEventListener var1);

   public abstract void setTimeIncrement(float var1);

   public abstract float getTimeIncrement();

   public abstract void play();

   public abstract void pause();

   public abstract void stop();

   public abstract Object getTargetComponent();

   public abstract void invokeAndWait(Runnable var1) throws InterruptedException;

   public abstract void invokeLater(Runnable var1);
}
