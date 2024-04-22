package com.nokia.mid.impl.isa.m2g;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGLocatableElement;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGPath;
import org.w3c.dom.svg.SVGRGBColor;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;

public class SVGElementImpl implements SVGElement {
   protected int myElement;
   protected Document myDocument;
   protected short elementType;
   private static final String SVG_NAMESPACE = "http://www.w3.org/2000/svg";
   private static final String XLINK_NAMESPACE = "http://www.w3.org/1999/xlink";
   public static final short TEXT_NOT_SHORT = -3;
   public static final short DEFAULT_INHERIT = -2;
   public static final short SVG_ERROR = -1;
   public static final short EL_A = 0;
   public static final short EL_ANIMATE = 1;
   public static final short EL_ANIMATECOLOR = 2;
   public static final short EL_ANIMATEMOTION = 3;
   public static final short EL_ANIMATETRANSFORM = 4;
   public static final short EL_CIRCLE = 5;
   public static final short EL_DEFS = 6;
   public static final short EL_DESC = 7;
   public static final short EL_ELLIPSE = 8;
   public static final short EL_FONT = 9;
   public static final short EL_FONTFACE = 10;
   public static final short EL_FONTFACENAME = 11;
   public static final short EL_FONTFACESRC = 12;
   public static final short EL_FOREIGNOBJECT = 13;
   public static final short EL_G = 14;
   public static final short EL_GLYPH = 15;
   public static final short EL_HKERN = 16;
   public static final short EL_IMAGE = 17;
   public static final short EL_LINE = 18;
   public static final short EL_METADATA = 19;
   public static final short EL_MISSINGGLYPH = 20;
   public static final short EL_MPATH = 21;
   public static final short EL_PATH = 22;
   public static final short EL_POLYGON = 23;
   public static final short EL_POLYLINE = 24;
   public static final short EL_RECT = 25;
   public static final short EL_SET = 26;
   public static final short EL_SVG = 27;
   public static final short EL_SWITCH = 28;
   public static final short EL_TEXT = 29;
   public static final short EL_TITLE = 30;
   public static final short EL_USE = 31;
   public static final short AT_ACCENTHEIGHT = 50;
   public static final short AT_ACCUMULATE = 51;
   public static final short AT_ADDITIVE = 52;
   public static final short AT_ALPHABETIC = 53;
   public static final short AT_ARABICFORM = 54;
   public static final short AT_ASCENT = 55;
   public static final short AT_ATTRIBUTENAME = 56;
   public static final short AT_ATTRIBUTETYPE = 57;
   public static final short AT_BASEPROFILE = 58;
   public static final short AT_BBOX = 59;
   public static final short AT_BEGIN = 60;
   public static final short AT_BY = 61;
   public static final short AT_CALCMODE = 62;
   public static final short AT_CAPHEIGHT = 63;
   public static final short AT_COLOR = 64;
   public static final short AT_COLORRENDERING = 65;
   public static final short AT_CX = 67;
   public static final short AT_CY = 68;
   public static final short AT_D = 69;
   public static final short AT_DESCENT = 70;
   public static final short AT_DISPLAY = 71;
   public static final short AT_DUR = 72;
   public static final short AT_END = 73;
   public static final short AT_FILL = 74;
   public static final short AT_FILLRULE = 75;
   public static final short AT_FONTFAMILY = 76;
   public static final short AT_FONTSIZE = 77;
   public static final short AT_FONTSTRETCH = 78;
   public static final short AT_FONTSTYLE = 79;
   public static final short AT_FONTVARIANT = 80;
   public static final short AT_FONTWEIGHT = 81;
   public static final short AT_FROM = 82;
   public static final short AT_G1 = 83;
   public static final short AT_G2 = 84;
   public static final short AT_GLYPHNAME = 85;
   public static final short AT_HANGING = 86;
   public static final short AT_HEIGHT = 87;
   public static final short AT_HORIZADVX = 88;
   public static final short AT_HORIZORIGINX = 89;
   public static final short AT_ID = 90;
   public static final short AT_IDEOGRAPHIC = 91;
   public static final short AT_K = 92;
   public static final short AT_KEYPOINTS = 93;
   public static final short AT_KEYSPLINES = 94;
   public static final short AT_KEYTIMES = 95;
   public static final short AT_LANG = 96;
   public static final short AT_MATHEMATICAL = 97;
   public static final short AT_MAX = 98;
   public static final short AT_MIN = 99;
   public static final short AT_NAME = 100;
   public static final short AT_ORIGIN = 101;
   public static final short AT_OVERLINEPOSITION = 102;
   public static final short AT_OVERLINETHICKNESS = 103;
   public static final short AT_PANOSE1 = 104;
   public static final short AT_PATH = 105;
   public static final short AT_PATHLENGTH = 106;
   public static final short AT_POINTS = 107;
   public static final short AT_PRESERVEASPECTRATIO = 108;
   public static final short AT_R = 109;
   public static final short AT_REPEATCOUNT = 110;
   public static final short AT_REPEATDUR = 111;
   public static final short AT_REQUIREDEXTENSIONS = 112;
   public static final short AT_REQUIREDFEATURES = 113;
   public static final short AT_RESTART = 114;
   public static final short AT_ROTATE = 115;
   public static final short AT_RX = 116;
   public static final short AT_RY = 117;
   public static final short AT_SLOPE = 118;
   public static final short AT_STEMH = 119;
   public static final short AT_STEMV = 120;
   public static final short AT_STRIKETHROUGHPOSITION = 121;
   public static final short AT_STRIKETHROUGHTHICKNESS = 122;
   public static final short AT_STROKE = 123;
   public static final short AT_STROKEDASHARRAY = 124;
   public static final short AT_STROKEDASHOFFSET = 125;
   public static final short AT_STROKELINECAP = 126;
   public static final short AT_STROKELINEJOIN = 127;
   public static final short AT_STROKEMITERLIMIT = 128;
   public static final short AT_STROKEWIDTH = 129;
   public static final short AT_STYLE = 130;
   public static final short AT_SYSTEMLANGUAGE = 131;
   public static final short AT_TARGET = 132;
   public static final short AT_TEXTANCHOR = 133;
   public static final short AT_TO = 134;
   public static final short AT_TRANSFORM = 135;
   public static final short AT_TYPE = 136;
   public static final short AT_U1 = 137;
   public static final short AT_U2 = 138;
   public static final short AT_UNDERLINEPOSITION = 139;
   public static final short AT_UNDERLINETHICKNESS = 140;
   public static final short AT_UNICODE = 141;
   public static final short AT_UNICODERANGE = 142;
   public static final short AT_UNITSPEREM = 143;
   public static final short AT_VALUES = 144;
   public static final short AT_VERSION = 145;
   public static final short AT_VIEWBOX = 146;
   public static final short AT_VISIBILITY = 147;
   public static final short AT_WIDTH = 148;
   public static final short AT_WIDTHS = 149;
   public static final short AT_X = 150;
   public static final short AT_XHEIGHT = 151;
   public static final short AT_X1 = 152;
   public static final short AT_X2 = 153;
   public static final short AT_XLINKACTUATE = 154;
   public static final short AT_XLINKARCROLE = 155;
   public static final short AT_XLINKHREF = 156;
   public static final short AT_XLINKROLE = 157;
   public static final short AT_XLINKSHOW = 158;
   public static final short AT_XLINKTITLE = 159;
   public static final short AT_XLINKTYPE = 160;
   public static final short AT_XMLBASE = 161;
   public static final short AT_XMLLANG = 162;
   public static final short AT_XMLSPACE = 163;
   public static final short AT_Y = 164;
   public static final short AT_Y1 = 165;
   public static final short AT_Y2 = 166;
   public static final short AT_ZOOMANDPAN = 167;
   public static final short AT_MOTIONTRANSFORM = 168;
   public static final short AT_STRING = 169;
   public static final short AT_TEXTDECORATION = 170;
   public static final short AT_HORIZORIGINY = 171;
   public static final short AT_MOUSEEVENT = 172;
   public static final short AT_USERBBOX = 173;
   public static final short VAL_EVENT = 200;
   public static final short VAL_INTEGER = 201;
   public static final short VAL_PAINT = 202;
   public static final short VAL_POINT = 203;
   public static final short VAL_REAL = 204;
   public static final short VAL_STRING = 205;
   public static final short VAL_TIME = 206;
   public static final short VAL_TRANSFORM = 207;
   public static final short VAL_VECTOR = 208;
   public static final short VAL_VECTOR_POINT = 215;
   public static final short VAL_VECTOR_REAL = 216;
   public static final short VAL_VECTOR_STRING = 217;
   public static final short VAL_VECTOR_PATH = 218;
   public static final short VECTOR_TO_VAL = -12;
   public static final short PAR_NONE = 310;
   public static final short PAR_XMIDYMID = 311;
   public static final short ZPN_MAGNIFY = 320;
   public static final short ZPN_DISABLE = 321;
   public static final short PAINT_NONE = 325;
   public static final short PAINT_CURRENT = 326;
   public static final short PAINT_COLOR = 327;
   public static final short PAINT_INHERIT = 328;
   public static final short FONT_ALL = 330;
   public static final short FONT_NORMAL = 331;
   public static final short FONT_STYLE_ITALIC = 332;
   public static final short FONT_STYLE_OBLIQUE = 333;
   public static final short FONT_VARIANT_SMALLCAPS = 334;
   public static final short FONT_WEIGHT_BOLD = 335;
   public static final short FONT_WEIGHT_BOLDER = 336;
   public static final short FONT_WEIGHT_LIGHTER = 337;
   public static final short FONT_WEIGHT_100 = 338;
   public static final short FONT_WEIGHT_200 = 339;
   public static final short FONT_WEIGHT_300 = 340;
   public static final short FONT_WEIGHT_400 = 341;
   public static final short FONT_WEIGHT_500 = 342;
   public static final short FONT_WEIGHT_600 = 343;
   public static final short FONT_WEIGHT_700 = 344;
   public static final short FONT_WEIGHT_800 = 345;
   public static final short FONT_WEIGHT_900 = 346;
   public static final short FONT_STRETCH_WIDER = 347;
   public static final short FONT_STRETCH_NARROWER = 348;
   public static final short FONT_STRETCH_ULTRA_COND = 349;
   public static final short FONT_STRETCH_EXTRA_COND = 350;
   public static final short FONT_STRETCH_COND = 351;
   public static final short FONT_STRETCH_SEMI_COND = 352;
   public static final short FONT_STRETCH_SEMI_EXPD = 353;
   public static final short FONT_STRETCH_EXPD = 354;
   public static final short FONT_STRETCH_EXTRA_EXPD = 355;
   public static final short FONT_STRETCH_ULTRA_EXPD = 356;
   public static final short TEXT_ANCHOR_START = 360;
   public static final short TEXT_ANCHOR_MIDDLE = 361;
   public static final short TEXT_ANCHOR_END = 362;
   public static final short TEXT_UNDER_LINE = 363;
   public static final short TEXT_OVER_LINE = 364;
   public static final short TEXT_LINE_THROUGH = 365;
   public static final int FONT_SIZE_XXSMALL = 131072;
   public static final int FONT_SIZE_XSMALL = 262144;
   public static final int FONT_SIZE_SMALL = 393216;
   public static final int FONT_SIZE_MEDIUM = 655360;
   public static final int FONT_SIZE_LARGE = 1048576;
   public static final int FONT_SIZE_XLARGE = 1310720;
   public static final int FONT_SIZE_XXLARGE = 1572864;
   public static final short FILL_RULE_EVENODD = 375;
   public static final short FILL_RULE_NONZERO = 376;
   public static final short DISPLAY_NONE = 380;
   public static final short DISPLAY_OTHER = 381;
   public static final short VISIBILITY_VISIBLE = 385;
   public static final short VISIBILITY_OTHER = 386;
   public static final short COLOR_RENDERING_AUTO = 390;
   public static final short COLOR_RENDERING_SPEED = 391;
   public static final short COLOR_RENDERING_QUALITY = 392;
   public static final short STROKE_LINECAP_BUTT = 395;
   public static final short STROKE_LINECAP_ROUND = 396;
   public static final short STROKE_LINECAP_SQUARE = 397;
   public static final short STROKE_LINEJOIN_MITER = 400;
   public static final short STROKE_LINEJOIN_ROUND = 401;
   public static final short STROKE_LINEJOIN_BEVEL = 402;
   public static final short ANIM_INDEFINITE = 445;
   public static final short ACCUMULATE_NONE = 450;
   public static final short ACCUMULATE_SUM = 451;
   public static final short ADDITIVE_REPLACE = 455;
   public static final short ADDITIVE_SUM = 456;
   public static final short CALC_MODE_DISCRETE = 460;
   public static final short CALC_MODE_LINEAR = 461;
   public static final short CALC_MODE_PACED = 462;
   public static final short CALC_MODE_SPLINE = 463;
   public static final short FILL_REMOVE = 465;
   public static final short FILL_FREEZE = 466;
   public static final short RESTART_ALWAYS = 470;
   public static final short RESTART_NEVER = 471;
   public static final short RESTART_WHENNOTACTIVE = 472;
   public static final short TYPE_TRANSLATE = 475;
   public static final short TYPE_SCALE = 476;
   public static final short TYPE_ROTATE = 477;
   public static final short TYPE_SKEWX = 478;
   public static final short TYPE_SKEWY = 479;
   public static final short ATTR_TYPE_CSS = 485;
   public static final short ATTR_TYPE_XML = 486;
   public static final short ATTR_TYPE_AUTO = 487;
   public static final short ROTATE_AUTO = 490;
   public static final short ROTATE_AUTOREVERSE = 491;
   public static final short ANIM_FROM_TO = 500;
   public static final short ANIM_FROM_BY = 501;
   public static final short ANIM_BY = 502;
   public static final short ANIM_TO = 503;
   public static final short ANIM_VALUES = 504;
   public static final short ANIM_PATH = 505;
   public static final short PATH_COMMAND_M = 600;
   public static final short PATH_COMMAND_m = 601;
   public static final short PATH_COMMAND_Z = 602;
   public static final short PATH_COMMAND_L = 603;
   public static final short PATH_COMMAND_l = 604;
   public static final short PATH_COMMAND_H = 605;
   public static final short PATH_COMMAND_h = 606;
   public static final short PATH_COMMAND_V = 607;
   public static final short PATH_COMMAND_v = 608;
   public static final short PATH_COMMAND_C = 609;
   public static final short PATH_COMMAND_c = 610;
   public static final short PATH_COMMAND_S = 611;
   public static final short PATH_COMMAND_s = 612;
   public static final short PATH_COMMAND_Q = 613;
   public static final short PATH_COMMAND_q = 614;
   public static final short PATH_COMMAND_T = 615;
   public static final short PATH_COMMAND_t = 616;
   public static final short EVENT_BEGIN = 650;
   public static final short EVENT_END = 651;
   public static final short EVENT_REPEAT = 652;
   public static final short EVENT_BEGIN_EL = 653;
   public static final short EVENT_END_EL = 654;

   public SVGElementImpl(Document doc, int handle) {
      this.myElement = handle;
      this.myDocument = doc;
      this.elementType = _getType(this.myElement);
   }

   public int getHandle() {
      return this.myElement;
   }

   public int getDocumentHandle() {
      return ((DocumentImpl)this.myDocument).getDocumentHandle();
   }

   public void addEventListener(String type, EventListener listener, boolean useCapture) {
      if (type != null && !type.equals("") && listener != null) {
         if (useCapture) {
            throw new DOMException((short)9, "capture phase not supported in SVG Tiny");
         } else {
            ((DocumentImpl)this.myDocument).register(this, type, listener, true);
         }
      } else {
         throw new NullPointerException();
      }
   }

   public void removeEventListener(String type, EventListener listener, boolean useCapture) {
      if (type != null && !type.equals("") && listener != null) {
         if (useCapture) {
            throw new DOMException((short)9, "capture phase not supported in SVG Tiny");
         } else {
            ((DocumentImpl)this.myDocument).register(this, type, listener, false);
         }
      } else {
         throw new NullPointerException();
      }
   }

   public float getFloatTrait(String name) {
      if (name != null && !name.equals("")) {
         short traitType = stringToEnumTrait(name);
         if (traitType != -1 && checkTraitOnElement(this.elementType, traitType)) {
            if (!isAllowedGetFloatTrait(traitType)) {
               throw new DOMException((short)17, "cannot get attribute " + name + " with floatTrait");
            } else {
               return _getFloatTrait(this.myElement, traitType);
            }
         } else {
            throw new DOMException((short)9, "trait not supported on this element");
         }
      } else {
         throw new DOMException((short)9, "called trait with null traitname");
      }
   }

   public String getId() {
      return _getStringTrait(this.myElement, (short)90);
   }

   public SVGMatrix getMatrixTrait(String name) {
      if (name != null && !name.equals("")) {
         short traitType = stringToEnumTrait(name);
         if (traitType != -1 && checkTraitOnElement(this.elementType, traitType)) {
            if (!isAllowedGetMatrixTrait(traitType)) {
               throw new DOMException((short)17, "cannot get attribute " + name + " with matrixTrait");
            } else {
               SVGMatrixImpl myMatrix = new SVGMatrixImpl();
               return _getMatrixTrait(this.myElement, traitType, myMatrix.getArray()) == -1 ? new SVGMatrixImpl(1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F) : myMatrix;
            }
         } else {
            throw new DOMException((short)9, "trait not supported on this element");
         }
      } else {
         throw new DOMException((short)9, "called trait with null traitname");
      }
   }

   public SVGPath getPathTrait(String name) {
      if (name != null && !name.equals("")) {
         short traitType = stringToEnumTrait(name);
         if (traitType != -1 && checkTraitOnElement(this.elementType, traitType)) {
            if (!isAllowedGetPathTrait(traitType)) {
               throw new DOMException((short)17, "cannot get attribute " + name + " with pathTrait");
            } else {
               int retVal = _getPathTrait(this.myElement, traitType);
               return retVal != 0 ? new SVGPathImpl(retVal) : null;
            }
         } else {
            throw new DOMException((short)9, "trait not supported on this element");
         }
      } else {
         throw new DOMException((short)9, "called trait with null traitname");
      }
   }

   public SVGRect getRectTrait(String name) {
      if (name != null && !name.equals("")) {
         short traitType = stringToEnumTrait(name);
         if (traitType != -1 && checkTraitOnElement(this.elementType, traitType)) {
            if (!isAllowedGetRectTrait(traitType)) {
               throw new DOMException((short)17, "cannot get attribute " + name + " with rectTrait");
            } else {
               SVGRectImpl myRect = new SVGRectImpl(0.0F, 0.0F, 0.0F, 0.0F);
               return _getRectTrait(this.myElement, traitType, myRect.getArray()) == -1 ? null : myRect;
            }
         } else {
            throw new DOMException((short)9, "trait not supported on this element");
         }
      } else {
         throw new DOMException((short)9, "called trait with null traitname");
      }
   }

   public SVGRGBColor getRGBColorTrait(String name) {
      if (name != null && !name.equals("")) {
         short traitType = stringToEnumTrait(name);
         if (traitType != -1 && checkTraitOnElement(this.elementType, traitType)) {
            if (!isAllowedGetRgbColorTrait(traitType)) {
               throw new DOMException((short)17, "cannot get attribute " + name + " with rgbColorTrait");
            } else {
               SVGRGBColorImpl myColor = new SVGRGBColorImpl(0, 0, 0);
               int success = _getColorTrait(this.myElement, traitType, myColor.getArray());
               if (success == -1) {
                  return traitType != 74 && traitType != 64 ? null : new SVGRGBColorImpl(0, 0, 0);
               } else {
                  return success == 0 ? null : myColor;
               }
            }
         } else {
            throw new DOMException((short)9, "trait not supported on this element");
         }
      } else {
         throw new DOMException((short)9, "called trait with null traitname");
      }
   }

   public String getTrait(String name) {
      return this.getTraitNS((String)null, name);
   }

   public String getTraitNS(String nsuri, String name) {
      if (name != null && !name.equals("")) {
         if (nsuri != null && !nsuri.equals("http://www.w3.org/1999/xlink")) {
            throw new DOMException((short)9, "This namespace is not supported");
         } else {
            short traitType = stringToEnumTrait(name);
            if (traitType != -1 && checkTraitOnElement(this.elementType, traitType)) {
               String ssret;
               if (nsuri != null && !nsuri.equals("http://www.w3.org/2000/svg")) {
                  if (!isAllowedGetTraitNs(traitType)) {
                     throw new DOMException((short)17, "cannot get attribute " + name + " with getTraitNS");
                  } else {
                     ssret = _getStringTrait(this.myElement, traitType);
                     return ssret == null ? "" : ssret;
                  }
               } else if (!isAllowedGetTrait(traitType)) {
                  throw new DOMException((short)17, "cannot get attribute " + name + " with getTrait");
               } else if (traitType == 156) {
                  throw new DOMException((short)9, "trait not supported in this namespace");
               } else if (traitType != 169 && traitType != 145 && traitType != 58 && traitType != 132 && traitType != 76) {
                  short retVal = _getTrait(this.myElement, traitType);
                  if (retVal == 0) {
                     return "";
                  } else if (traitType == 81 && retVal == 337) {
                     return "300";
                  } else if (traitType == 81 && retVal == 331) {
                     return "400";
                  } else if (traitType == 81 && retVal == 335) {
                     return "700";
                  } else {
                     return traitType == 81 && retVal == 336 ? "800" : enumToStringValue(retVal);
                  }
               } else {
                  ssret = _getStringTrait(this.myElement, traitType);
                  return ssret == null ? "" : ssret;
               }
            } else {
               throw new DOMException((short)9, "trait not supported on this element");
            }
         }
      } else {
         throw new DOMException((short)9, "called trait with null traitname");
      }
   }

   public void setFloatTrait(String name, float v) {
      if (name != null && !name.equals("")) {
         short traitType = stringToEnumTrait(name);
         if (traitType != -1 && checkTraitOnElement(this.elementType, traitType)) {
            if (!isAllowedSetFloatTrait(traitType)) {
               throw new DOMException((short)17, "cannot get attribute " + name + " with floatTrait");
            } else if (Float.isNaN(v)) {
               throw new DOMException((short)15, "cannot set value to NaN");
            } else if (v < 1.0F && traitType == 128) {
               throw new DOMException((short)15, "value must be >= 1");
            } else if (!(v < 0.0F) || traitType != 77 && traitType != 129 && traitType != 87 && traitType != 148 && traitType != 116 && traitType != 117 && traitType != 109) {
               _setFloatTrait(this.getDocumentHandle(), this.myElement, traitType, v);
            } else {
               throw new DOMException((short)15, "value must be >= 0");
            }
         } else {
            throw new DOMException((short)9, "trait not supported on this element");
         }
      } else {
         throw new DOMException((short)9, "called trait with null traitname");
      }
   }

   public void setId(String value) throws DOMException {
      if (value == null) {
         throw new NullPointerException("setID value was null");
      } else if (this.getId() != null) {
         throw new DOMException((short)7, "Element id cannot be changed");
      } else if (!_isUniqueId(this.getDocumentHandle(), value)) {
         throw new DOMException((short)15, "Element id already used");
      } else {
         _setStringTrait(this.getDocumentHandle(), this.myElement, (short)90, value);
      }
   }

   public void setMatrixTrait(String name, SVGMatrix matrix) {
      if (name != null && !name.equals("")) {
         short traitType = stringToEnumTrait(name);
         if (traitType != -1 && checkTraitOnElement(this.elementType, traitType)) {
            if (!isAllowedSetMatrixTrait(traitType)) {
               throw new DOMException((short)17, "cannot set attribute " + name + " with matrixTrait");
            } else if (matrix == null) {
               throw new DOMException((short)15, "value cannot be set to null");
            } else {
               _setMatrixTrait(this.getDocumentHandle(), this.myElement, traitType, matrix.getComponent(0), matrix.getComponent(1), matrix.getComponent(2), matrix.getComponent(3), matrix.getComponent(4), matrix.getComponent(5));
            }
         } else {
            throw new DOMException((short)9, "trait not supported on this element");
         }
      } else {
         throw new DOMException((short)9, "called trait with null traitname");
      }
   }

   public void setPathTrait(String name, SVGPath path) {
      if (name != null && !name.equals("")) {
         short traitType = stringToEnumTrait(name);
         if (traitType != -1 && checkTraitOnElement(this.elementType, traitType)) {
            if (!isAllowedSetPathTrait(traitType)) {
               throw new DOMException((short)17, "cannot set attribute " + name + " with pathTrait");
            } else if (path != null && ((SVGPathImpl)path).getHandle() != 0) {
               if (path.getNumberOfSegments() != 0 && path.getSegment(0) != 77) {
                  throw new DOMException((short)15, "first command in path must be MOVE_TO");
               } else {
                  _setPathTrait(this.myElement, ((SVGPathImpl)path).getHandle());
               }
            } else {
               throw new DOMException((short)15, "value cannot be set to null");
            }
         } else {
            throw new DOMException((short)9, "trait not supported on this element");
         }
      } else {
         throw new DOMException((short)9, "called trait with null traitname");
      }
   }

   public void setRectTrait(String name, SVGRect rect) {
      if (name != null && !name.equals("")) {
         short traitType = stringToEnumTrait(name);
         if (traitType != -1 && checkTraitOnElement(this.elementType, traitType)) {
            if (!isAllowedSetRectTrait(traitType)) {
               throw new DOMException((short)17, "cannot set attribute " + name + " with rectTrait");
            } else if (rect == null) {
               throw new DOMException((short)15, "value cannot be set to null");
            } else if (!(rect.getWidth() < 0.0F) && !(rect.getHeight() < 0.0F)) {
               _setRectAttribute(this.getDocumentHandle(), this.myElement, traitType, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
            } else {
               throw new DOMException((short)15, "height and width can not be negative");
            }
         } else {
            throw new DOMException((short)9, "trait not supported on this element");
         }
      } else {
         throw new DOMException((short)9, "called trait with null traitname");
      }
   }

   public void setRGBColorTrait(String name, SVGRGBColor color) {
      if (name != null && !name.equals("")) {
         if (color == null) {
            throw new DOMException((short)15, "'" + name + "' attribute can not be set to null");
         } else {
            short traitType = stringToEnumTrait(name);
            if (traitType != -1 && checkTraitOnElement(this.elementType, traitType)) {
               if (!isAllowedSetRgbColorTrait(traitType)) {
                  throw new DOMException((short)17, "cannot set attribute " + name + " with rgbColorTrait");
               } else {
                  _setColorTrait(this.getDocumentHandle(), this.myElement, traitType, ((SVGRGBColorImpl)color).getArray());
               }
            } else {
               throw new DOMException((short)9, "trait not supported on this element");
            }
         }
      } else {
         throw new DOMException((short)9, "called trait with null traitname");
      }
   }

   public void setTrait(String name, String v) {
      this.setTraitNS((String)null, name, v);
   }

   public void setTraitNS(String nsuri, String name, String v) {
      if (v == null) {
         throw new DOMException((short)15, "value cannot be set to null");
      } else if (name != null && !name.equals("")) {
         if (nsuri != null && !nsuri.equals("http://www.w3.org/1999/xlink")) {
            throw new DOMException((short)9, "This namespace is not supported");
         } else {
            short traitType = stringToEnumTrait(name);
            if (traitType != -1 && checkTraitOnElement(this.elementType, traitType)) {
               if (nsuri != null && !nsuri.equals("http://www.w3.org/2000/svg")) {
                  if (!isAllowedSetTraitNs(traitType)) {
                     throw new DOMException((short)17, "cannot set " + name + " with setTraitNS");
                  }

                  if (this.elementType == 31 && _elementInDOM(this.getDocumentHandle(), this.myElement) && (v.length() <= 1 || !v.startsWith("#") || this.myDocument.getElementById(v.substring(1)) == null)) {
                     throw new DOMException((short)15, "invalid id set for use element");
                  }

                  _setStringTrait(this.getDocumentHandle(), this.myElement, traitType, v);
                  if (this.elementType == 17 && traitType == 156 && _elementInDOM(this.getDocumentHandle(), this.myElement)) {
                     ((DocumentImpl)this.myDocument).invokeResourceHandler(v);
                  }
               } else {
                  if (traitType == 145 || traitType == 58) {
                     throw new DOMException((short)7, "'" + name + "' is a read only attribute");
                  }

                  if (traitType == 156) {
                     throw new DOMException((short)9, "trait not supported in this namespace");
                  }

                  if (!isAllowedSetTrait(traitType)) {
                     throw new DOMException((short)17, "cannot set " + name + " with setTrait");
                  }

                  short enumVal = stringToEnumValue(traitType, v);
                  if (enumVal == -1) {
                     throw new DOMException((short)15, "invalid value");
                  }

                  if (enumVal == -3) {
                     _setStringTrait(this.getDocumentHandle(), this.myElement, traitType, v);
                  } else {
                     _setTrait(this.getDocumentHandle(), this.myElement, traitType, enumVal);
                  }
               }

            } else {
               throw new DOMException((short)9, "trait not supported on this element");
            }
         }
      } else {
         throw new DOMException((short)9, "called trait with null traitname");
      }
   }

   public Element getFirstElementChild() {
      int retVal = _getFirstElementChild(this.myElement);
      return retVal == 0 ? null : DocumentImpl.makeJavaElementType(this.myDocument, retVal);
   }

   public Element getNextElementSibling() {
      int retVal = _getNextElementSibling(this.myElement);
      return retVal == 0 ? null : DocumentImpl.makeJavaElementType(this.myDocument, retVal);
   }

   public String getLocalName() {
      return enumToStringElement(this.elementType);
   }

   public String getNamespaceURI() {
      return "http://www.w3.org/2000/svg";
   }

   public Node getParentNode() {
      int parentHndl = _getParent(this.myElement);
      return parentHndl == 0 ? null : DocumentImpl.makeJavaElementType(this.myDocument, parentHndl);
   }

   public Node appendChild(Node newChild) {
      return this.insertBefore(newChild, (Node)null);
   }

   public Node removeChild(Node oldChild) throws DOMException {
      if (oldChild == null) {
         throw new NullPointerException();
      } else if (oldChild instanceof DocumentImpl) {
         throw new DOMException((short)9, "child is wrong type (Document)");
      } else if (!isAllowedRemoveType(((SVGElementImpl)oldChild).elementType)) {
         throw new DOMException((short)9, "Cannot remove node of that type");
      } else {
         SVGElementImpl parent = (SVGElementImpl)oldChild.getParentNode();
         if (parent != null && parent.getHandle() == this.myElement) {
            if (_checkRemoveable(((SVGElementImpl)oldChild).getHandle()) == 0) {
               throw new DOMException((short)15, "This node and/or a child does not have a null id");
            } else {
               _removeChild(this.getDocumentHandle(), this.myElement, ((SVGElementImpl)oldChild).getHandle());
               return oldChild;
            }
         } else {
            throw new DOMException((short)8, "Cannot remove: Not a child of this node");
         }
      }
   }

   public Node insertBefore(Node newChild, Node refChild) {
      if (newChild == null) {
         throw new NullPointerException();
      } else if (newChild instanceof DocumentImpl) {
         throw new DOMException((short)3, "child is wrong type (Document)");
      } else if (!isAllowedInsertType(((SVGElementImpl)newChild).elementType)) {
         throw new DOMException((short)9, "Cannot insert node of that type");
      } else if (!checkElementAsChild(this.elementType, ((SVGElementImpl)newChild).elementType)) {
         throw new DOMException((short)3, "Cannot insert node of that type");
      } else if (IsAncestor(newChild, this)) {
         throw new DOMException((short)3, "Hierarchy request error in Document");
      } else if (((SVGElementImpl)newChild).getDocumentHandle() != this.getDocumentHandle()) {
         throw new DOMException((short)4, "Child belongs to different Document" + ((SVGElementImpl)newChild).getDocumentHandle() + " this.document = " + this.getDocumentHandle());
      } else if (refChild != null && ((SVGElementImpl)refChild.getParentNode()).getHandle() != this.myElement) {
         throw new DOMException((short)8, "The child to insert before doesn't exist in this current node");
      } else {
         String thisHref;
         if (((SVGElementImpl)newChild).elementType == 31) {
            thisHref = ((SVGElementImpl)newChild).getTraitNS("http://www.w3.org/1999/xlink", "href");
            if (thisHref.length() != 0 && (!thisHref.startsWith("#") || ((SVGElementImpl)newChild).myDocument.getElementById(thisHref.substring(1)) == null)) {
               throw new DOMException((short)11, "invalid id value for use element");
            }
         }

         if (refChild == null) {
            _appendChild(this.getDocumentHandle(), this.myElement, ((SVGElementImpl)newChild).getHandle());
         } else {
            _insertBefore(this.getDocumentHandle(), this.myElement, ((SVGElementImpl)newChild).getHandle(), ((SVGElementImpl)refChild).getHandle());
         }

         thisHref = _getStringTrait(((SVGElementImpl)newChild).getHandle(), (short)156);
         if (_elementInDOM(this.getDocumentHandle(), ((SVGElementImpl)newChild).getHandle()) && ((SVGElementImpl)newChild).elementType == 17 && thisHref != null) {
            ((DocumentImpl)this.myDocument).invokeResourceHandler(thisHref);
         }

         return newChild;
      }
   }

   private static final boolean IsAncestor(Node ancestor, Node child) throws DOMException {
      if (child != null) {
         if (child instanceof DocumentImpl) {
            throw new DOMException((short)3, "Cannot append Document elements");
         }

         if (child instanceof SVGElementImpl || child instanceof SVGLocatableElementImpl || child instanceof SVGSVGElementImpl) {
            if (((SVGElementImpl)ancestor).getHandle() == ((SVGElementImpl)child).getHandle()) {
               return true;
            }

            return IsAncestor(ancestor, child.getParentNode());
         }
      }

      return false;
   }

   private static final boolean IsReadOnly(Node child) {
      String qualsReadOnly = " animateColor animateMotion animateTransform defs desc font font-face font-face-name font-face-src foreignObject glyph hkern metadata missing-glyph mpath polygon polyline set switch ";
      if (child != null && (child instanceof SVGElementImpl || child instanceof SVGLocatableElement || child instanceof SVGSVGElement)) {
         String checkName = " " + ((SVGElementImpl)child).getLocalName() + " ";
         return " animateColor animateMotion animateTransform defs desc font font-face font-face-name font-face-src foreignObject glyph hkern metadata missing-glyph mpath polygon polyline set switch ".indexOf(checkName) != -1 ? true : IsReadOnly(child.getParentNode());
      } else {
         return false;
      }
   }

   public boolean isUsed() {
      return _isUsed(this.myElement);
   }

   public SVGElement getUsedFromElement() {
      int elHandle = _getUsedFromElement(this.myElement);
      return elHandle == 0 ? null : DocumentImpl.makeJavaElementType(this.myDocument, elHandle);
   }

   private static native int _getUsedFromElement(int var0);

   protected static native boolean _isUsed(int var0);

   protected static native boolean _elementInDOM(int var0, int var1);

   protected static native void _setRectAttribute(int var0, int var1, short var2, float var3, float var4, float var5, float var6);

   protected static native int _getRectTrait(int var0, short var1, float[] var2);

   private static native void _setColorTrait(int var0, int var1, short var2, int[] var3);

   private static native int _getColorTrait(int var0, short var1, int[] var2);

   private static native int _getFirstElementChild(int var0);

   protected static native int _getNextElementSibling(int var0);

   protected static native void _appendChild(int var0, int var1, int var2);

   protected static native int _removeChild(int var0, int var1, int var2);

   private static native void _insertBefore(int var0, int var1, int var2, int var3);

   private static native int _getParent(int var0);

   protected static native void _setFloatTrait(int var0, int var1, short var2, float var3);

   private static native float _getFloatTrait(int var0, short var1);

   private static native void _setTrait(int var0, int var1, short var2, short var3);

   protected static native short _getTrait(int var0, short var1);

   private static native void _setPathTrait(int var0, int var1);

   private static native int _getPathTrait(int var0, short var1);

   protected static native void _setMatrixTrait(int var0, int var1, short var2, float var3, float var4, float var5, float var6, float var7, float var8);

   protected static native int _getMatrixTrait(int var0, short var1, float[] var2);

   protected static native short _getType(int var0);

   protected static native int _checkRemoveable(int var0);

   protected static native String _getStringTrait(int var0, short var1);

   private static native void _setStringTrait(int var0, int var1, short var2, String var3);

   private static native boolean _isUniqueId(int var0, String var1);

   protected static native void _getScreenBBox(int var0, float[] var1);

   public static final short stringToEnumElement(String elementName) {
      if (elementName.equals("a")) {
         return 0;
      } else if (elementName.equals("animate")) {
         return 1;
      } else if (elementName.equals("animateColor")) {
         return 2;
      } else if (elementName.equals("animateMotion")) {
         return 3;
      } else if (elementName.equals("animateTransform")) {
         return 4;
      } else if (elementName.equals("circle")) {
         return 5;
      } else if (elementName.equals("defs")) {
         return 6;
      } else if (elementName.equals("desc")) {
         return 7;
      } else if (elementName.equals("ellipse")) {
         return 8;
      } else if (elementName.equals("font")) {
         return 9;
      } else if (elementName.equals("font-face")) {
         return 10;
      } else if (elementName.equals("font-face-name")) {
         return 11;
      } else if (elementName.equals("font-face-src")) {
         return 12;
      } else if (elementName.equals("foreignObject")) {
         return 13;
      } else if (elementName.equals("g")) {
         return 14;
      } else if (elementName.equals("glyph")) {
         return 15;
      } else if (elementName.equals("hkern")) {
         return 16;
      } else if (elementName.equals("image")) {
         return 17;
      } else if (elementName.equals("line")) {
         return 18;
      } else if (elementName.equals("metadata")) {
         return 19;
      } else if (elementName.equals("missing-glyph")) {
         return 20;
      } else if (elementName.equals("mpath")) {
         return 21;
      } else if (elementName.equals("path")) {
         return 22;
      } else if (elementName.equals("polygon")) {
         return 23;
      } else if (elementName.equals("polyline")) {
         return 24;
      } else if (elementName.equals("rect")) {
         return 25;
      } else if (elementName.equals("set")) {
         return 26;
      } else if (elementName.equals("svg")) {
         return 27;
      } else if (elementName.equals("switch")) {
         return 28;
      } else if (elementName.equals("text")) {
         return 29;
      } else if (elementName.equals("title")) {
         return 30;
      } else {
         return (short)(elementName.equals("use") ? 31 : -1);
      }
   }

   public static final short stringToEnumTrait(String traitName) {
      if (traitName.equals("#text")) {
         return 169;
      } else if (traitName.equals("accent-height")) {
         return 50;
      } else if (traitName.equals("accumulate")) {
         return 51;
      } else if (traitName.equals("additive")) {
         return 52;
      } else if (traitName.equals("alphabetic")) {
         return 53;
      } else if (traitName.equals("arabic-form")) {
         return 54;
      } else if (traitName.equals("ascent")) {
         return 55;
      } else if (traitName.equals("attributeName")) {
         return 56;
      } else if (traitName.equals("attributeType")) {
         return 57;
      } else if (traitName.equals("baseProfile")) {
         return 58;
      } else if (traitName.equals("bbox")) {
         return 59;
      } else if (traitName.equals("begin")) {
         return 60;
      } else if (traitName.equals("by")) {
         return 61;
      } else if (traitName.equals("calcMode")) {
         return 62;
      } else if (traitName.equals("cap-height")) {
         return 63;
      } else if (traitName.equals("color")) {
         return 64;
      } else if (traitName.equals("color-rendering")) {
         return 65;
      } else if (traitName.equals("cx")) {
         return 67;
      } else if (traitName.equals("cy")) {
         return 68;
      } else if (traitName.equals("d")) {
         return 69;
      } else if (traitName.equals("descent")) {
         return 70;
      } else if (traitName.equals("display")) {
         return 71;
      } else if (traitName.equals("dur")) {
         return 72;
      } else if (traitName.equals("end")) {
         return 73;
      } else if (traitName.equals("fill")) {
         return 74;
      } else if (traitName.equals("fill-rule")) {
         return 75;
      } else if (traitName.equals("font-family")) {
         return 76;
      } else if (traitName.equals("font-size")) {
         return 77;
      } else if (traitName.equals("font-stretch")) {
         return 78;
      } else if (traitName.equals("font-style")) {
         return 79;
      } else if (traitName.equals("font-variant")) {
         return 80;
      } else if (traitName.equals("font-weight")) {
         return 81;
      } else if (traitName.equals("from")) {
         return 82;
      } else if (traitName.equals("g1")) {
         return 83;
      } else if (traitName.equals("g2")) {
         return 84;
      } else if (traitName.equals("glyph-name")) {
         return 85;
      } else if (traitName.equals("hanging")) {
         return 86;
      } else if (traitName.equals("height")) {
         return 87;
      } else if (traitName.equals("horiz-adv-x")) {
         return 88;
      } else if (traitName.equals("horiz-origin-x")) {
         return 89;
      } else if (traitName.equals("id")) {
         return 90;
      } else if (traitName.equals("ideographic")) {
         return 91;
      } else if (traitName.equals("k")) {
         return 92;
      } else if (traitName.equals("keyPoints")) {
         return 93;
      } else if (traitName.equals("keySplines")) {
         return 94;
      } else if (traitName.equals("keyTimes")) {
         return 95;
      } else if (traitName.equals("lang")) {
         return 96;
      } else if (traitName.equals("mathematical")) {
         return 97;
      } else if (traitName.equals("max")) {
         return 98;
      } else if (traitName.equals("min")) {
         return 99;
      } else if (traitName.equals("name")) {
         return 100;
      } else if (traitName.equals("origin")) {
         return 101;
      } else if (traitName.equals("overline-position")) {
         return 102;
      } else if (traitName.equals("overline-thickness")) {
         return 103;
      } else if (traitName.equals("panose-1")) {
         return 104;
      } else if (traitName.equals("path")) {
         return 105;
      } else if (traitName.equals("pathLength")) {
         return 106;
      } else if (traitName.equals("points")) {
         return 107;
      } else if (traitName.equals("preserveAspectRatio")) {
         return 108;
      } else if (traitName.equals("r")) {
         return 109;
      } else if (traitName.equals("repeatCount")) {
         return 110;
      } else if (traitName.equals("repeatDur")) {
         return 111;
      } else if (traitName.equals("requiredExtensions")) {
         return 112;
      } else if (traitName.equals("requiredFeatures")) {
         return 113;
      } else if (traitName.equals("restart")) {
         return 114;
      } else if (traitName.equals("rotate")) {
         return 115;
      } else if (traitName.equals("rx")) {
         return 116;
      } else if (traitName.equals("ry")) {
         return 117;
      } else if (traitName.equals("slope")) {
         return 118;
      } else if (traitName.equals("stemh")) {
         return 119;
      } else if (traitName.equals("stemv")) {
         return 120;
      } else if (traitName.equals("strikethrough-position")) {
         return 121;
      } else if (traitName.equals("strikethrough-thickness")) {
         return 122;
      } else if (traitName.equals("stroke")) {
         return 123;
      } else if (traitName.equals("stroke-dasharray")) {
         return 124;
      } else if (traitName.equals("stroke-dashoffset")) {
         return 125;
      } else if (traitName.equals("stroke-linecap")) {
         return 126;
      } else if (traitName.equals("stroke-linejoin")) {
         return 127;
      } else if (traitName.equals("stroke-miterlimit")) {
         return 128;
      } else if (traitName.equals("stroke-width")) {
         return 129;
      } else if (traitName.equals("style")) {
         return 130;
      } else if (traitName.equals("systemLanguage")) {
         return 131;
      } else if (traitName.equals("target")) {
         return 132;
      } else if (traitName.equals("text-anchor")) {
         return 133;
      } else if (traitName.equals("to")) {
         return 134;
      } else if (traitName.equals("transform")) {
         return 135;
      } else if (traitName.equals("type")) {
         return 136;
      } else if (traitName.equals("u1")) {
         return 137;
      } else if (traitName.equals("u2")) {
         return 138;
      } else if (traitName.equals("underline-position")) {
         return 139;
      } else if (traitName.equals("underline-thickness")) {
         return 140;
      } else if (traitName.equals("unicode")) {
         return 141;
      } else if (traitName.equals("unicode-range")) {
         return 142;
      } else if (traitName.equals("units-per-em")) {
         return 143;
      } else if (traitName.equals("values")) {
         return 144;
      } else if (traitName.equals("version")) {
         return 145;
      } else if (traitName.equals("viewBox")) {
         return 146;
      } else if (traitName.equals("visibility")) {
         return 147;
      } else if (traitName.equals("width")) {
         return 148;
      } else if (traitName.equals("widths")) {
         return 149;
      } else if (traitName.equals("x")) {
         return 150;
      } else if (traitName.equals("xheight")) {
         return 151;
      } else if (traitName.equals("x1")) {
         return 152;
      } else if (traitName.equals("x2")) {
         return 153;
      } else if (traitName.equals("xlink:actuate")) {
         return 154;
      } else if (traitName.equals("actuate")) {
         return 154;
      } else if (traitName.equals("xlink:arcrole")) {
         return 155;
      } else if (traitName.equals("arcrole")) {
         return 155;
      } else if (traitName.equals("xlink:href")) {
         return 156;
      } else if (traitName.equals("href")) {
         return 156;
      } else if (traitName.equals("xlink:role")) {
         return 157;
      } else if (traitName.equals("role")) {
         return 157;
      } else if (traitName.equals("xlink:show")) {
         return 158;
      } else if (traitName.equals("show")) {
         return 158;
      } else if (traitName.equals("xlink:title")) {
         return 159;
      } else if (traitName.equals("title")) {
         return 159;
      } else if (traitName.equals("xlink:type")) {
         return 160;
      } else if (traitName.equals("xml:base")) {
         return 161;
      } else if (traitName.equals("base")) {
         return 161;
      } else if (traitName.equals("xml:lang")) {
         return 162;
      } else if (traitName.equals("xml:space")) {
         return 163;
      } else if (traitName.equals("space")) {
         return 163;
      } else if (traitName.equals("y")) {
         return 164;
      } else if (traitName.equals("y1")) {
         return 165;
      } else if (traitName.equals("y2")) {
         return 166;
      } else {
         return (short)(traitName.equals("zoomAndPan") ? 167 : -1);
      }
   }

   public static final short stringToEnumValue(short trait, String value) {
      switch(trait) {
      case 64:
         if (value.equals("inherit")) {
            return 328;
         }

         return -1;
      case 71:
         if (value.equals("none")) {
            return 380;
         } else if (value.equals("inline")) {
            return 381;
         } else {
            if (value.equals("inherit")) {
               return -2;
            }

            return -1;
         }
      case 74:
         if (value.equals("none")) {
            return 325;
         } else if (value.equals("currentColor")) {
            return 326;
         } else {
            if (value.equals("inherit")) {
               return 328;
            }

            return -1;
         }
      case 75:
         if (value.equals("evenodd")) {
            return 375;
         } else if (value.equals("nonzero")) {
            return 376;
         } else {
            if (value.equals("inherit")) {
               return -2;
            }

            return -1;
         }
      case 76:
         if (value.equals("inherit")) {
            return -2;
         }

         return -3;
      case 77:
         if (value.equals("inherit")) {
            return -2;
         }

         return -1;
      case 79:
         if (value.equals("normal")) {
            return 331;
         } else if (value.equals("italic")) {
            return 332;
         } else if (value.equals("oblique")) {
            return 333;
         } else {
            if (value.equals("inherit")) {
               return -2;
            }

            return -1;
         }
      case 81:
         if (value.equals("normal")) {
            return 331;
         } else if (value.equals("bold")) {
            return 335;
         } else if (value.equals("bolder")) {
            return 336;
         } else if (value.equals("lighter")) {
            return 337;
         } else if (value.equals("100")) {
            return 338;
         } else if (value.equals("200")) {
            return 339;
         } else if (value.equals("300")) {
            return 340;
         } else if (value.equals("400")) {
            return 341;
         } else if (value.equals("500")) {
            return 342;
         } else if (value.equals("600")) {
            return 343;
         } else if (value.equals("700")) {
            return 344;
         } else if (value.equals("800")) {
            return 345;
         } else if (value.equals("900")) {
            return 346;
         } else {
            if (value.equals("inherit")) {
               return -2;
            }

            return -1;
         }
      case 123:
         if (value.equals("none")) {
            return 325;
         } else if (value.equals("currentColor")) {
            return 326;
         } else {
            if (value.equals("inherit")) {
               return 328;
            }

            return -1;
         }
      case 125:
         if (value.equals("inherit")) {
            return -2;
         }

         return -1;
      case 126:
         if (value.equals("butt")) {
            return 395;
         } else if (value.equals("round")) {
            return 396;
         } else if (value.equals("square")) {
            return 397;
         } else {
            if (value.equals("inherit")) {
               return -2;
            }

            return -1;
         }
      case 127:
         if (value.equals("miter")) {
            return 400;
         } else if (value.equals("round")) {
            return 401;
         } else if (value.equals("bevel")) {
            return 402;
         } else {
            if (value.equals("inherit")) {
               return -2;
            }

            return -1;
         }
      case 128:
         if (value.equals("inherit")) {
            return -2;
         }

         return -1;
      case 129:
         if (value.equals("inherit")) {
            return -2;
         }

         return -1;
      case 132:
         return -3;
      case 133:
         if (value.equals("start")) {
            return 360;
         } else if (value.equals("middle")) {
            return 361;
         } else if (value.equals("end")) {
            return 362;
         } else {
            if (value.equals("inherit")) {
               return -2;
            }

            return -1;
         }
      case 147:
         if (value.equals("visible")) {
            return 385;
         } else if (value.equals("hidden")) {
            return 386;
         } else {
            if (value.equals("inherit")) {
               return -2;
            }

            return -1;
         }
      case 156:
         return -3;
      case 167:
         if (value.equals("magnify")) {
            return 320;
         } else {
            if (value.equals("disable")) {
               return 321;
            }

            return -1;
         }
      case 169:
         return -3;
      default:
         return -1;
      }
   }

   public static final String enumToStringElement(short elementEnum) {
      switch(elementEnum) {
      case 0:
         return "a";
      case 1:
         return "animate";
      case 2:
         return "animateColor";
      case 3:
         return "animateMotion";
      case 4:
         return "animateTransform";
      case 5:
         return "circle";
      case 6:
         return "defs";
      case 7:
         return "desc";
      case 8:
         return "ellipse";
      case 9:
         return "font";
      case 10:
         return "font-face";
      case 11:
         return "font-face-name";
      case 12:
         return "font-face-src";
      case 13:
         return "foreignObject";
      case 14:
         return "g";
      case 15:
         return "glyph";
      case 16:
         return "hkern";
      case 17:
         return "image";
      case 18:
         return "line";
      case 19:
         return "metadata";
      case 20:
         return "missing-glyph";
      case 21:
         return "mpath";
      case 22:
         return "path";
      case 23:
         return "polygon";
      case 24:
         return "polyline";
      case 25:
         return "rect";
      case 26:
         return "set";
      case 27:
         return "svg";
      case 28:
         return "switch";
      case 29:
         return "text";
      case 30:
         return "title";
      case 31:
         return "use";
      default:
         return "";
      }
   }

   public static final String enumToStringTrait(short traitEnum) {
      switch(traitEnum) {
      case 50:
         return "accent-height";
      case 51:
         return "accumulate";
      case 52:
         return "additive";
      case 53:
         return "alphabetic";
      case 54:
         return "arabic-form";
      case 55:
         return "ascent";
      case 56:
         return "attributeName";
      case 57:
         return "attributeType";
      case 58:
         return "baseProfile";
      case 59:
         return "bbox";
      case 60:
         return "begin";
      case 61:
         return "by";
      case 62:
         return "calcMode";
      case 63:
         return "cap-height";
      case 64:
         return "color";
      case 65:
         return "color-rendering";
      case 66:
      case 168:
      default:
         return "";
      case 67:
         return "cx";
      case 68:
         return "cy";
      case 69:
         return "d";
      case 70:
         return "descent";
      case 71:
         return "display";
      case 72:
         return "dur";
      case 73:
         return "end";
      case 74:
         return "fill";
      case 75:
         return "fill-rule";
      case 76:
         return "font-family";
      case 77:
         return "font-size";
      case 78:
         return "font-stretch";
      case 79:
         return "font-style";
      case 80:
         return "font-variant";
      case 81:
         return "font-weight";
      case 82:
         return "from";
      case 83:
         return "g1";
      case 84:
         return "g2";
      case 85:
         return "glyph-name";
      case 86:
         return "hanging";
      case 87:
         return "height";
      case 88:
         return "horiz-adv-x";
      case 89:
         return "horiz-origin-x";
      case 90:
         return "id";
      case 91:
         return "ideographic";
      case 92:
         return "k";
      case 93:
         return "keyPoints";
      case 94:
         return "keySplines";
      case 95:
         return "keyTimes";
      case 96:
         return "lang";
      case 97:
         return "mathematical";
      case 98:
         return "max";
      case 99:
         return "min";
      case 100:
         return "name";
      case 101:
         return "origin";
      case 102:
         return "overline-position";
      case 103:
         return "overline-thickness";
      case 104:
         return "panose-1";
      case 105:
         return "path";
      case 106:
         return "pathLength";
      case 107:
         return "points";
      case 108:
         return "preserveAspectRatio";
      case 109:
         return "r";
      case 110:
         return "repeatCount";
      case 111:
         return "repeatDur";
      case 112:
         return "requiredExtensions";
      case 113:
         return "requiredFeatures";
      case 114:
         return "restart";
      case 115:
         return "rotate";
      case 116:
         return "rx";
      case 117:
         return "ry";
      case 118:
         return "slope";
      case 119:
         return "stemh";
      case 120:
         return "stemv";
      case 121:
         return "strikethrough-position";
      case 122:
         return "strikethrough-thickness";
      case 123:
         return "stroke";
      case 124:
         return "stroke-dasharray";
      case 125:
         return "stroke-dashoffset";
      case 126:
         return "stroke-linecap";
      case 127:
         return "stroke-linejoin";
      case 128:
         return "stroke-miterlimit";
      case 129:
         return "stroke-width";
      case 130:
         return "style";
      case 131:
         return "systemLanguage";
      case 132:
         return "target";
      case 133:
         return "text-anchor";
      case 134:
         return "to";
      case 135:
         return "transform";
      case 136:
         return "type";
      case 137:
         return "u1";
      case 138:
         return "u2";
      case 139:
         return "underline-position";
      case 140:
         return "underline-thickness";
      case 141:
         return "unicode";
      case 142:
         return "unicode-range";
      case 143:
         return "units-per-em";
      case 144:
         return "values";
      case 145:
         return "version";
      case 146:
         return "viewBox";
      case 147:
         return "visibility";
      case 148:
         return "width";
      case 149:
         return "widths";
      case 150:
         return "x";
      case 151:
         return "xheight";
      case 152:
         return "x1";
      case 153:
         return "x2";
      case 154:
         return "xlink:actuate";
      case 155:
         return "xlink:arcrole";
      case 156:
         return "xlink:href";
      case 157:
         return "xlink:role";
      case 158:
         return "xlink:show";
      case 159:
         return "xlink:title";
      case 160:
         return "xlink:type";
      case 161:
         return "xml:base";
      case 162:
         return "xml:lang";
      case 163:
         return "xml:space";
      case 164:
         return "y";
      case 165:
         return "y1";
      case 166:
         return "y2";
      case 167:
         return "zoomAndPan";
      case 169:
         return "#text";
      }
   }

   public static final String enumToStringValue(short valueEnum) {
      switch(valueEnum) {
      case -3:
         return "#text";
      case -2:
      case 328:
         return "inherit";
      case 320:
         return "magnify";
      case 321:
         return "disable";
      case 325:
      case 380:
         return "none";
      case 326:
         return "currentColor";
      case 331:
         return "normal";
      case 332:
         return "italic";
      case 333:
         return "oblique";
      case 335:
         return "bold";
      case 336:
         return "bolder";
      case 337:
         return "lighter";
      case 338:
         return "100";
      case 339:
         return "200";
      case 340:
         return "300";
      case 341:
         return "400";
      case 342:
         return "500";
      case 343:
         return "600";
      case 344:
         return "700";
      case 345:
         return "800";
      case 346:
         return "900";
      case 360:
         return "start";
      case 361:
         return "middle";
      case 362:
         return "end";
      case 375:
         return "evenodd";
      case 376:
         return "nonzero";
      case 381:
         return "inline";
      case 385:
         return "visible";
      case 386:
         return "hidden";
      case 395:
         return "butt";
      case 396:
      case 401:
         return "round";
      case 397:
         return "square";
      case 400:
         return "miter";
      case 402:
         return "bevel";
      default:
         return "";
      }
   }

   public static final boolean checkTraitOnElement(short element, short trait) {
      switch(element) {
      case 0:
         if (trait != 64 && trait != 71 && trait != 74 && trait != 75 && trait != 123 && trait != 125 && trait != 126 && trait != 127 && trait != 128 && trait != 129 && trait != 147 && trait != 76 && trait != 77 && trait != 79 && trait != 81 && trait != 133 && trait != 135 && trait != 132 && trait != 156) {
            return false;
         }

         return true;
      case 1:
      case 2:
      case 3:
      case 4:
      case 6:
      case 7:
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 15:
      case 16:
      case 19:
      case 20:
      case 21:
      case 23:
      case 24:
      case 26:
      case 28:
      case 30:
      default:
         return false;
      case 5:
         if (trait != 64 && trait != 71 && trait != 74 && trait != 75 && trait != 123 && trait != 125 && trait != 126 && trait != 127 && trait != 128 && trait != 129 && trait != 147 && trait != 135 && trait != 67 && trait != 68 && trait != 109) {
            return false;
         }

         return true;
      case 8:
         if (trait != 64 && trait != 71 && trait != 74 && trait != 75 && trait != 123 && trait != 125 && trait != 126 && trait != 127 && trait != 128 && trait != 129 && trait != 147 && trait != 135 && trait != 67 && trait != 68 && trait != 116 && trait != 117) {
            return false;
         }

         return true;
      case 14:
         if (trait != 64 && trait != 71 && trait != 74 && trait != 75 && trait != 123 && trait != 125 && trait != 126 && trait != 127 && trait != 128 && trait != 129 && trait != 147 && trait != 76 && trait != 77 && trait != 79 && trait != 81 && trait != 133 && trait != 135) {
            return false;
         }

         return true;
      case 17:
         if (trait != 64 && trait != 71 && trait != 74 && trait != 75 && trait != 123 && trait != 125 && trait != 126 && trait != 127 && trait != 128 && trait != 129 && trait != 147 && trait != 135 && trait != 150 && trait != 164 && trait != 148 && trait != 87 && trait != 156) {
            return false;
         }

         return true;
      case 18:
         if (trait != 64 && trait != 71 && trait != 74 && trait != 75 && trait != 123 && trait != 125 && trait != 126 && trait != 127 && trait != 128 && trait != 129 && trait != 147 && trait != 135 && trait != 152 && trait != 153 && trait != 165 && trait != 166) {
            return false;
         }

         return true;
      case 22:
         if (trait != 64 && trait != 71 && trait != 74 && trait != 75 && trait != 123 && trait != 125 && trait != 126 && trait != 127 && trait != 128 && trait != 129 && trait != 147 && trait != 135 && trait != 69) {
            return false;
         }

         return true;
      case 25:
         if (trait != 64 && trait != 71 && trait != 74 && trait != 75 && trait != 123 && trait != 125 && trait != 126 && trait != 127 && trait != 128 && trait != 129 && trait != 147 && trait != 135 && trait != 87 && trait != 148 && trait != 150 && trait != 164 && trait != 116 && trait != 117) {
            return false;
         }

         return true;
      case 27:
         if (trait != 64 && trait != 71 && trait != 74 && trait != 75 && trait != 123 && trait != 125 && trait != 126 && trait != 127 && trait != 128 && trait != 129 && trait != 147 && trait != 76 && trait != 77 && trait != 79 && trait != 81 && trait != 133 && trait != 145 && trait != 58 && trait != 146 && trait != 167) {
            return false;
         }

         return true;
      case 29:
         if (trait != 64 && trait != 71 && trait != 74 && trait != 75 && trait != 123 && trait != 125 && trait != 126 && trait != 127 && trait != 128 && trait != 129 && trait != 147 && trait != 76 && trait != 77 && trait != 79 && trait != 81 && trait != 133 && trait != 135 && trait != 150 && trait != 164 && trait != 169) {
            return false;
         }

         return true;
      case 31:
         return trait == 64 || trait == 71 || trait == 74 || trait == 75 || trait == 123 || trait == 125 || trait == 126 || trait == 127 || trait == 128 || trait == 129 || trait == 147 || trait == 76 || trait == 77 || trait == 79 || trait == 81 || trait == 133 || trait == 135 || trait == 150 || trait == 164 || trait == 156;
      }
   }

   public static final boolean checkElementAsChild(short parentElement, short childElement) {
      switch(parentElement) {
      case 0:
         switch(childElement) {
         case 0:
         case 5:
         case 8:
         case 14:
         case 17:
         case 18:
         case 22:
         case 25:
         case 29:
         case 31:
            return true;
         case 1:
         case 2:
         case 3:
         case 4:
         case 6:
         case 7:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 15:
         case 16:
         case 19:
         case 20:
         case 21:
         case 23:
         case 24:
         case 26:
         case 27:
         case 28:
         case 30:
         default:
            return false;
         }
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
         return false;
      case 6:
         switch(childElement) {
         case 0:
         case 5:
         case 8:
         case 14:
         case 17:
         case 18:
         case 22:
         case 25:
         case 29:
         case 31:
            return true;
         case 1:
         case 2:
         case 3:
         case 4:
         case 6:
         case 7:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 15:
         case 16:
         case 19:
         case 20:
         case 21:
         case 23:
         case 24:
         case 26:
         case 27:
         case 28:
         case 30:
         default:
            return false;
         }
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
         return false;
      case 14:
         switch(childElement) {
         case 0:
         case 5:
         case 8:
         case 14:
         case 17:
         case 18:
         case 22:
         case 25:
         case 29:
         case 31:
            return true;
         case 1:
         case 2:
         case 3:
         case 4:
         case 6:
         case 7:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 15:
         case 16:
         case 19:
         case 20:
         case 21:
         case 23:
         case 24:
         case 26:
         case 27:
         case 28:
         case 30:
         default:
            return false;
         }
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      case 20:
      case 21:
      case 22:
      case 23:
      case 24:
      case 25:
      case 26:
         return false;
      case 27:
      case 28:
         switch(childElement) {
         case 0:
         case 5:
         case 8:
         case 14:
         case 17:
         case 18:
         case 22:
         case 25:
         case 29:
         case 31:
            return true;
         case 1:
         case 2:
         case 3:
         case 4:
         case 6:
         case 7:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 15:
         case 16:
         case 19:
         case 20:
         case 21:
         case 23:
         case 24:
         case 26:
         case 27:
         case 28:
         case 30:
         default:
            return false;
         }
      case 29:
         switch(childElement) {
         case 0:
            return true;
         default:
            return false;
         }
      case 30:
      case 31:
      default:
         return false;
      }
   }

   private static final boolean isAllowedRemoveType(short type) {
      switch(type) {
      case 0:
      case 5:
      case 8:
      case 14:
      case 17:
      case 18:
      case 22:
      case 25:
      case 29:
      case 31:
         return true;
      case 1:
      case 2:
      case 3:
      case 4:
      case 6:
      case 7:
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 15:
      case 16:
      case 19:
      case 20:
      case 21:
      case 23:
      case 24:
      case 26:
      case 27:
      case 28:
      case 30:
      default:
         return false;
      }
   }

   private static final boolean isAllowedInsertType(short type) {
      switch(type) {
      case 0:
      case 5:
      case 8:
      case 14:
      case 17:
      case 18:
      case 22:
      case 25:
      case 29:
      case 31:
         return true;
      case 1:
      case 2:
      case 3:
      case 4:
      case 6:
      case 7:
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 15:
      case 16:
      case 19:
      case 20:
      case 21:
      case 23:
      case 24:
      case 26:
      case 27:
      case 28:
      case 30:
      default:
         return false;
      }
   }

   private static final boolean isAllowedGetFloatTrait(short trait) {
      switch(trait) {
      case 67:
      case 68:
      case 77:
      case 87:
      case 109:
      case 116:
      case 117:
      case 125:
      case 128:
      case 129:
      case 148:
      case 150:
      case 152:
      case 153:
      case 164:
      case 165:
      case 166:
         return true;
      default:
         return false;
      }
   }

   private static final boolean isAllowedSetFloatTrait(short trait) {
      switch(trait) {
      case 67:
      case 68:
      case 77:
      case 87:
      case 109:
      case 116:
      case 117:
      case 125:
      case 128:
      case 129:
      case 148:
      case 150:
      case 152:
      case 153:
      case 164:
      case 165:
      case 166:
         return true;
      default:
         return false;
      }
   }

   private static final boolean isAllowedGetTrait(short trait) {
      switch(trait) {
      case 58:
      case 71:
      case 75:
      case 76:
      case 79:
      case 81:
      case 126:
      case 127:
      case 132:
      case 133:
      case 145:
      case 147:
      case 156:
      case 167:
      case 169:
         return true;
      default:
         return false;
      }
   }

   private static final boolean isAllowedSetTrait(short trait) {
      switch(trait) {
      case 64:
      case 71:
      case 74:
      case 75:
      case 76:
      case 77:
      case 79:
      case 81:
      case 123:
      case 125:
      case 126:
      case 127:
      case 128:
      case 129:
      case 132:
      case 133:
      case 147:
      case 156:
      case 167:
      case 169:
         return true;
      default:
         return false;
      }
   }

   private static final boolean isAllowedGetRgbColorTrait(short trait) {
      switch(trait) {
      case 64:
      case 74:
      case 123:
         return true;
      default:
         return false;
      }
   }

   private static final boolean isAllowedSetRgbColorTrait(short trait) {
      switch(trait) {
      case 64:
      case 74:
      case 123:
         return true;
      default:
         return false;
      }
   }

   private static final boolean isAllowedGetMatrixTrait(short trait) {
      switch(trait) {
      case 135:
         return true;
      default:
         return false;
      }
   }

   private static final boolean isAllowedSetMatrixTrait(short trait) {
      switch(trait) {
      case 135:
         return true;
      default:
         return false;
      }
   }

   private static final boolean isAllowedGetPathTrait(short trait) {
      switch(trait) {
      case 69:
         return true;
      default:
         return false;
      }
   }

   private static final boolean isAllowedSetPathTrait(short trait) {
      switch(trait) {
      case 69:
         return true;
      default:
         return false;
      }
   }

   private static final boolean isAllowedGetRectTrait(short trait) {
      switch(trait) {
      case 146:
         return true;
      default:
         return false;
      }
   }

   private static final boolean isAllowedSetRectTrait(short trait) {
      switch(trait) {
      case 146:
         return true;
      default:
         return false;
      }
   }

   private static final boolean isAllowedGetTraitNs(short trait) {
      switch(trait) {
      case 156:
         return true;
      default:
         return false;
      }
   }

   private static final boolean isAllowedSetTraitNs(short trait) {
      switch(trait) {
      case 156:
         return true;
      default:
         return false;
      }
   }
}
