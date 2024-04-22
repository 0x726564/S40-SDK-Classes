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

   public SVGElementImpl(Document var1, int var2) {
      this.myElement = var2;
      this.myDocument = var1;
      this.elementType = _getType(this.myElement);
   }

   public int getHandle() {
      return this.myElement;
   }

   public int getDocumentHandle() {
      return ((DocumentImpl)this.myDocument).getDocumentHandle();
   }

   public void addEventListener(String var1, EventListener var2, boolean var3) {
      if (var1 != null && !var1.equals("") && var2 != null) {
         if (var3) {
            throw new DOMException((short)9, "capture phase not supported in SVG Tiny");
         } else {
            ((DocumentImpl)this.myDocument).register(this, var1, var2, true);
         }
      } else {
         throw new NullPointerException();
      }
   }

   public void removeEventListener(String var1, EventListener var2, boolean var3) {
      if (var1 != null && !var1.equals("") && var2 != null) {
         if (var3) {
            throw new DOMException((short)9, "capture phase not supported in SVG Tiny");
         } else {
            ((DocumentImpl)this.myDocument).register(this, var1, var2, false);
         }
      } else {
         throw new NullPointerException();
      }
   }

   public float getFloatTrait(String var1) {
      if (var1 != null && !var1.equals("")) {
         short var2 = stringToEnumTrait(var1);
         if (var2 != -1 && checkTraitOnElement(this.elementType, var2)) {
            if (!isAllowedGetFloatTrait(var2)) {
               throw new DOMException((short)17, "cannot get attribute " + var1 + " with floatTrait");
            } else {
               return _getFloatTrait(this.myElement, var2);
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

   public SVGMatrix getMatrixTrait(String var1) {
      if (var1 != null && !var1.equals("")) {
         short var2 = stringToEnumTrait(var1);
         if (var2 != -1 && checkTraitOnElement(this.elementType, var2)) {
            if (!isAllowedGetMatrixTrait(var2)) {
               throw new DOMException((short)17, "cannot get attribute " + var1 + " with matrixTrait");
            } else {
               SVGMatrixImpl var3 = new SVGMatrixImpl();
               return _getMatrixTrait(this.myElement, var2, var3.getArray()) == -1 ? new SVGMatrixImpl(1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F) : var3;
            }
         } else {
            throw new DOMException((short)9, "trait not supported on this element");
         }
      } else {
         throw new DOMException((short)9, "called trait with null traitname");
      }
   }

   public SVGPath getPathTrait(String var1) {
      if (var1 != null && !var1.equals("")) {
         short var2 = stringToEnumTrait(var1);
         if (var2 != -1 && checkTraitOnElement(this.elementType, var2)) {
            if (!isAllowedGetPathTrait(var2)) {
               throw new DOMException((short)17, "cannot get attribute " + var1 + " with pathTrait");
            } else {
               int var3 = _getPathTrait(this.myElement, var2);
               return var3 != 0 ? new SVGPathImpl(var3) : null;
            }
         } else {
            throw new DOMException((short)9, "trait not supported on this element");
         }
      } else {
         throw new DOMException((short)9, "called trait with null traitname");
      }
   }

   public SVGRect getRectTrait(String var1) {
      if (var1 != null && !var1.equals("")) {
         short var2 = stringToEnumTrait(var1);
         if (var2 != -1 && checkTraitOnElement(this.elementType, var2)) {
            if (!isAllowedGetRectTrait(var2)) {
               throw new DOMException((short)17, "cannot get attribute " + var1 + " with rectTrait");
            } else {
               SVGRectImpl var3 = new SVGRectImpl(0.0F, 0.0F, 0.0F, 0.0F);
               return _getRectTrait(this.myElement, var2, var3.getArray()) == -1 ? null : var3;
            }
         } else {
            throw new DOMException((short)9, "trait not supported on this element");
         }
      } else {
         throw new DOMException((short)9, "called trait with null traitname");
      }
   }

   public SVGRGBColor getRGBColorTrait(String var1) {
      if (var1 != null && !var1.equals("")) {
         short var2 = stringToEnumTrait(var1);
         if (var2 != -1 && checkTraitOnElement(this.elementType, var2)) {
            if (!isAllowedGetRgbColorTrait(var2)) {
               throw new DOMException((short)17, "cannot get attribute " + var1 + " with rgbColorTrait");
            } else {
               SVGRGBColorImpl var3 = new SVGRGBColorImpl(0, 0, 0);
               int var4 = _getColorTrait(this.myElement, var2, var3.getArray());
               if (var4 == -1) {
                  return var2 != 74 && var2 != 64 ? null : new SVGRGBColorImpl(0, 0, 0);
               } else {
                  return var4 == 0 ? null : var3;
               }
            }
         } else {
            throw new DOMException((short)9, "trait not supported on this element");
         }
      } else {
         throw new DOMException((short)9, "called trait with null traitname");
      }
   }

   public String getTrait(String var1) {
      return this.getTraitNS((String)null, var1);
   }

   public String getTraitNS(String var1, String var2) {
      if (var2 != null && !var2.equals("")) {
         if (var1 != null && !var1.equals("http://www.w3.org/1999/xlink")) {
            throw new DOMException((short)9, "This namespace is not supported");
         } else {
            short var3 = stringToEnumTrait(var2);
            if (var3 != -1 && checkTraitOnElement(this.elementType, var3)) {
               String var4;
               if (var1 != null && !var1.equals("http://www.w3.org/2000/svg")) {
                  if (!isAllowedGetTraitNs(var3)) {
                     throw new DOMException((short)17, "cannot get attribute " + var2 + " with getTraitNS");
                  } else {
                     var4 = _getStringTrait(this.myElement, var3);
                     return var4 == null ? "" : var4;
                  }
               } else if (!isAllowedGetTrait(var3)) {
                  throw new DOMException((short)17, "cannot get attribute " + var2 + " with getTrait");
               } else if (var3 == 156) {
                  throw new DOMException((short)9, "trait not supported in this namespace");
               } else if (var3 != 169 && var3 != 145 && var3 != 58 && var3 != 132 && var3 != 76) {
                  short var5 = _getTrait(this.myElement, var3);
                  if (var5 == 0) {
                     return "";
                  } else if (var3 == 81 && var5 == 337) {
                     return "300";
                  } else if (var3 == 81 && var5 == 331) {
                     return "400";
                  } else if (var3 == 81 && var5 == 335) {
                     return "700";
                  } else {
                     return var3 == 81 && var5 == 336 ? "800" : enumToStringValue(var5);
                  }
               } else {
                  var4 = _getStringTrait(this.myElement, var3);
                  return var4 == null ? "" : var4;
               }
            } else {
               throw new DOMException((short)9, "trait not supported on this element");
            }
         }
      } else {
         throw new DOMException((short)9, "called trait with null traitname");
      }
   }

   public void setFloatTrait(String var1, float var2) {
      if (var1 != null && !var1.equals("")) {
         short var3 = stringToEnumTrait(var1);
         if (var3 != -1 && checkTraitOnElement(this.elementType, var3)) {
            if (!isAllowedSetFloatTrait(var3)) {
               throw new DOMException((short)17, "cannot get attribute " + var1 + " with floatTrait");
            } else if (Float.isNaN(var2)) {
               throw new DOMException((short)15, "cannot set value to NaN");
            } else if (var2 < 1.0F && var3 == 128) {
               throw new DOMException((short)15, "value must be >= 1");
            } else if (!(var2 < 0.0F) || var3 != 77 && var3 != 129 && var3 != 87 && var3 != 148 && var3 != 116 && var3 != 117 && var3 != 109) {
               _setFloatTrait(this.getDocumentHandle(), this.myElement, var3, var2);
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

   public void setId(String var1) throws DOMException {
      if (var1 == null) {
         throw new NullPointerException("setID value was null");
      } else if (this.getId() != null) {
         throw new DOMException((short)7, "Element id cannot be changed");
      } else if (!_isUniqueId(this.getDocumentHandle(), var1)) {
         throw new DOMException((short)15, "Element id already used");
      } else {
         _setStringTrait(this.getDocumentHandle(), this.myElement, (short)90, var1);
      }
   }

   public void setMatrixTrait(String var1, SVGMatrix var2) {
      if (var1 != null && !var1.equals("")) {
         short var3 = stringToEnumTrait(var1);
         if (var3 != -1 && checkTraitOnElement(this.elementType, var3)) {
            if (!isAllowedSetMatrixTrait(var3)) {
               throw new DOMException((short)17, "cannot set attribute " + var1 + " with matrixTrait");
            } else if (var2 == null) {
               throw new DOMException((short)15, "value cannot be set to null");
            } else {
               _setMatrixTrait(this.getDocumentHandle(), this.myElement, var3, var2.getComponent(0), var2.getComponent(1), var2.getComponent(2), var2.getComponent(3), var2.getComponent(4), var2.getComponent(5));
            }
         } else {
            throw new DOMException((short)9, "trait not supported on this element");
         }
      } else {
         throw new DOMException((short)9, "called trait with null traitname");
      }
   }

   public void setPathTrait(String var1, SVGPath var2) {
      if (var1 != null && !var1.equals("")) {
         short var3 = stringToEnumTrait(var1);
         if (var3 != -1 && checkTraitOnElement(this.elementType, var3)) {
            if (!isAllowedSetPathTrait(var3)) {
               throw new DOMException((short)17, "cannot set attribute " + var1 + " with pathTrait");
            } else if (var2 != null && ((SVGPathImpl)var2).getHandle() != 0) {
               if (var2.getNumberOfSegments() != 0 && var2.getSegment(0) != 77) {
                  throw new DOMException((short)15, "first command in path must be MOVE_TO");
               } else {
                  _setPathTrait(this.myElement, ((SVGPathImpl)var2).getHandle());
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

   public void setRectTrait(String var1, SVGRect var2) {
      if (var1 != null && !var1.equals("")) {
         short var3 = stringToEnumTrait(var1);
         if (var3 != -1 && checkTraitOnElement(this.elementType, var3)) {
            if (!isAllowedSetRectTrait(var3)) {
               throw new DOMException((short)17, "cannot set attribute " + var1 + " with rectTrait");
            } else if (var2 == null) {
               throw new DOMException((short)15, "value cannot be set to null");
            } else if (!(var2.getWidth() < 0.0F) && !(var2.getHeight() < 0.0F)) {
               _setRectAttribute(this.getDocumentHandle(), this.myElement, var3, var2.getX(), var2.getY(), var2.getWidth(), var2.getHeight());
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

   public void setRGBColorTrait(String var1, SVGRGBColor var2) {
      if (var1 != null && !var1.equals("")) {
         if (var2 == null) {
            throw new DOMException((short)15, "'" + var1 + "' attribute can not be set to null");
         } else {
            short var3 = stringToEnumTrait(var1);
            if (var3 != -1 && checkTraitOnElement(this.elementType, var3)) {
               if (!isAllowedSetRgbColorTrait(var3)) {
                  throw new DOMException((short)17, "cannot set attribute " + var1 + " with rgbColorTrait");
               } else {
                  _setColorTrait(this.getDocumentHandle(), this.myElement, var3, ((SVGRGBColorImpl)var2).getArray());
               }
            } else {
               throw new DOMException((short)9, "trait not supported on this element");
            }
         }
      } else {
         throw new DOMException((short)9, "called trait with null traitname");
      }
   }

   public void setTrait(String var1, String var2) {
      this.setTraitNS((String)null, var1, var2);
   }

   public void setTraitNS(String var1, String var2, String var3) {
      if (var3 == null) {
         throw new DOMException((short)15, "value cannot be set to null");
      } else if (var2 != null && !var2.equals("")) {
         if (var1 != null && !var1.equals("http://www.w3.org/1999/xlink")) {
            throw new DOMException((short)9, "This namespace is not supported");
         } else {
            short var4 = stringToEnumTrait(var2);
            if (var4 != -1 && checkTraitOnElement(this.elementType, var4)) {
               if (var1 != null && !var1.equals("http://www.w3.org/2000/svg")) {
                  if (!isAllowedSetTraitNs(var4)) {
                     throw new DOMException((short)17, "cannot set " + var2 + " with setTraitNS");
                  }

                  if (this.elementType == 31 && _elementInDOM(this.getDocumentHandle(), this.myElement) && (var3.length() <= 1 || !var3.startsWith("#") || this.myDocument.getElementById(var3.substring(1)) == null)) {
                     throw new DOMException((short)15, "invalid id set for use element");
                  }

                  _setStringTrait(this.getDocumentHandle(), this.myElement, var4, var3);
                  if (this.elementType == 17 && var4 == 156 && _elementInDOM(this.getDocumentHandle(), this.myElement)) {
                     ((DocumentImpl)this.myDocument).invokeResourceHandler(var3);
                  }
               } else {
                  if (var4 == 145 || var4 == 58) {
                     throw new DOMException((short)7, "'" + var2 + "' is a read only attribute");
                  }

                  if (var4 == 156) {
                     throw new DOMException((short)9, "trait not supported in this namespace");
                  }

                  if (!isAllowedSetTrait(var4)) {
                     throw new DOMException((short)17, "cannot set " + var2 + " with setTrait");
                  }

                  short var5 = stringToEnumValue(var4, var3);
                  if (var5 == -1) {
                     throw new DOMException((short)15, "invalid value");
                  }

                  if (var5 == -3) {
                     _setStringTrait(this.getDocumentHandle(), this.myElement, var4, var3);
                  } else {
                     _setTrait(this.getDocumentHandle(), this.myElement, var4, var5);
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
      int var1 = _getFirstElementChild(this.myElement);
      return var1 == 0 ? null : DocumentImpl.makeJavaElementType(this.myDocument, var1);
   }

   public Element getNextElementSibling() {
      int var1 = _getNextElementSibling(this.myElement);
      return var1 == 0 ? null : DocumentImpl.makeJavaElementType(this.myDocument, var1);
   }

   public String getLocalName() {
      return enumToStringElement(this.elementType);
   }

   public String getNamespaceURI() {
      return "http://www.w3.org/2000/svg";
   }

   public Node getParentNode() {
      int var1 = _getParent(this.myElement);
      return var1 == 0 ? null : DocumentImpl.makeJavaElementType(this.myDocument, var1);
   }

   public Node appendChild(Node var1) {
      return this.insertBefore(var1, (Node)null);
   }

   public Node removeChild(Node var1) throws DOMException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var1 instanceof DocumentImpl) {
         throw new DOMException((short)9, "child is wrong type (Document)");
      } else if (!isAllowedRemoveType(((SVGElementImpl)var1).elementType)) {
         throw new DOMException((short)9, "Cannot remove node of that type");
      } else {
         SVGElementImpl var3 = (SVGElementImpl)var1.getParentNode();
         if (var3 != null && var3.getHandle() == this.myElement) {
            if (_checkRemoveable(((SVGElementImpl)var1).getHandle()) == 0) {
               throw new DOMException((short)15, "This node and/or a child does not have a null id");
            } else {
               int var2 = _removeChild(this.getDocumentHandle(), this.myElement, ((SVGElementImpl)var1).getHandle());
               return var1;
            }
         } else {
            throw new DOMException((short)8, "Cannot remove: Not a child of this node");
         }
      }
   }

   public Node insertBefore(Node var1, Node var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var1 instanceof DocumentImpl) {
         throw new DOMException((short)3, "child is wrong type (Document)");
      } else if (!isAllowedInsertType(((SVGElementImpl)var1).elementType)) {
         throw new DOMException((short)9, "Cannot insert node of that type");
      } else if (!checkElementAsChild(this.elementType, ((SVGElementImpl)var1).elementType)) {
         throw new DOMException((short)3, "Cannot insert node of that type");
      } else if (IsAncestor(var1, this)) {
         throw new DOMException((short)3, "Hierarchy request error in Document");
      } else if (((SVGElementImpl)var1).getDocumentHandle() != this.getDocumentHandle()) {
         throw new DOMException((short)4, "Child belongs to different Document" + ((SVGElementImpl)var1).getDocumentHandle() + " this.document = " + this.getDocumentHandle());
      } else if (var2 != null && ((SVGElementImpl)var2.getParentNode()).getHandle() != this.myElement) {
         throw new DOMException((short)8, "The child to insert before doesn't exist in this current node");
      } else {
         String var3;
         if (((SVGElementImpl)var1).elementType == 31) {
            var3 = ((SVGElementImpl)var1).getTraitNS("http://www.w3.org/1999/xlink", "href");
            if (var3.length() != 0 && (!var3.startsWith("#") || ((SVGElementImpl)var1).myDocument.getElementById(var3.substring(1)) == null)) {
               throw new DOMException((short)11, "invalid id value for use element");
            }
         }

         if (var2 == null) {
            _appendChild(this.getDocumentHandle(), this.myElement, ((SVGElementImpl)var1).getHandle());
         } else {
            _insertBefore(this.getDocumentHandle(), this.myElement, ((SVGElementImpl)var1).getHandle(), ((SVGElementImpl)var2).getHandle());
         }

         var3 = _getStringTrait(((SVGElementImpl)var1).getHandle(), (short)156);
         if (_elementInDOM(this.getDocumentHandle(), ((SVGElementImpl)var1).getHandle()) && ((SVGElementImpl)var1).elementType == 17 && var3 != null) {
            ((DocumentImpl)this.myDocument).invokeResourceHandler(var3);
         }

         return var1;
      }
   }

   private static final boolean IsAncestor(Node var0, Node var1) throws DOMException {
      if (var1 != null) {
         if (var1 instanceof DocumentImpl) {
            throw new DOMException((short)3, "Cannot append Document elements");
         }

         if (var1 instanceof SVGElementImpl || var1 instanceof SVGLocatableElementImpl || var1 instanceof SVGSVGElementImpl) {
            if (((SVGElementImpl)var0).getHandle() == ((SVGElementImpl)var1).getHandle()) {
               return true;
            }

            return IsAncestor(var0, var1.getParentNode());
         }
      }

      return false;
   }

   private static final boolean IsReadOnly(Node var0) {
      if (var0 != null && (var0 instanceof SVGElementImpl || var0 instanceof SVGLocatableElement || var0 instanceof SVGSVGElement)) {
         String var2 = " " + ((SVGElementImpl)var0).getLocalName() + " ";
         return " animateColor animateMotion animateTransform defs desc font font-face font-face-name font-face-src foreignObject glyph hkern metadata missing-glyph mpath polygon polyline set switch ".indexOf(var2) != -1 ? true : IsReadOnly(var0.getParentNode());
      } else {
         return false;
      }
   }

   public boolean isUsed() {
      return _isUsed(this.myElement);
   }

   public SVGElement getUsedFromElement() {
      int var1 = _getUsedFromElement(this.myElement);
      return var1 == 0 ? null : DocumentImpl.makeJavaElementType(this.myDocument, var1);
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

   public static final short stringToEnumElement(String var0) {
      if (var0.equals("a")) {
         return 0;
      } else if (var0.equals("animate")) {
         return 1;
      } else if (var0.equals("animateColor")) {
         return 2;
      } else if (var0.equals("animateMotion")) {
         return 3;
      } else if (var0.equals("animateTransform")) {
         return 4;
      } else if (var0.equals("circle")) {
         return 5;
      } else if (var0.equals("defs")) {
         return 6;
      } else if (var0.equals("desc")) {
         return 7;
      } else if (var0.equals("ellipse")) {
         return 8;
      } else if (var0.equals("font")) {
         return 9;
      } else if (var0.equals("font-face")) {
         return 10;
      } else if (var0.equals("font-face-name")) {
         return 11;
      } else if (var0.equals("font-face-src")) {
         return 12;
      } else if (var0.equals("foreignObject")) {
         return 13;
      } else if (var0.equals("g")) {
         return 14;
      } else if (var0.equals("glyph")) {
         return 15;
      } else if (var0.equals("hkern")) {
         return 16;
      } else if (var0.equals("image")) {
         return 17;
      } else if (var0.equals("line")) {
         return 18;
      } else if (var0.equals("metadata")) {
         return 19;
      } else if (var0.equals("missing-glyph")) {
         return 20;
      } else if (var0.equals("mpath")) {
         return 21;
      } else if (var0.equals("path")) {
         return 22;
      } else if (var0.equals("polygon")) {
         return 23;
      } else if (var0.equals("polyline")) {
         return 24;
      } else if (var0.equals("rect")) {
         return 25;
      } else if (var0.equals("set")) {
         return 26;
      } else if (var0.equals("svg")) {
         return 27;
      } else if (var0.equals("switch")) {
         return 28;
      } else if (var0.equals("text")) {
         return 29;
      } else if (var0.equals("title")) {
         return 30;
      } else {
         return (short)(var0.equals("use") ? 31 : -1);
      }
   }

   public static final short stringToEnumTrait(String var0) {
      if (var0.equals("#text")) {
         return 169;
      } else if (var0.equals("accent-height")) {
         return 50;
      } else if (var0.equals("accumulate")) {
         return 51;
      } else if (var0.equals("additive")) {
         return 52;
      } else if (var0.equals("alphabetic")) {
         return 53;
      } else if (var0.equals("arabic-form")) {
         return 54;
      } else if (var0.equals("ascent")) {
         return 55;
      } else if (var0.equals("attributeName")) {
         return 56;
      } else if (var0.equals("attributeType")) {
         return 57;
      } else if (var0.equals("baseProfile")) {
         return 58;
      } else if (var0.equals("bbox")) {
         return 59;
      } else if (var0.equals("begin")) {
         return 60;
      } else if (var0.equals("by")) {
         return 61;
      } else if (var0.equals("calcMode")) {
         return 62;
      } else if (var0.equals("cap-height")) {
         return 63;
      } else if (var0.equals("color")) {
         return 64;
      } else if (var0.equals("color-rendering")) {
         return 65;
      } else if (var0.equals("cx")) {
         return 67;
      } else if (var0.equals("cy")) {
         return 68;
      } else if (var0.equals("d")) {
         return 69;
      } else if (var0.equals("descent")) {
         return 70;
      } else if (var0.equals("display")) {
         return 71;
      } else if (var0.equals("dur")) {
         return 72;
      } else if (var0.equals("end")) {
         return 73;
      } else if (var0.equals("fill")) {
         return 74;
      } else if (var0.equals("fill-rule")) {
         return 75;
      } else if (var0.equals("font-family")) {
         return 76;
      } else if (var0.equals("font-size")) {
         return 77;
      } else if (var0.equals("font-stretch")) {
         return 78;
      } else if (var0.equals("font-style")) {
         return 79;
      } else if (var0.equals("font-variant")) {
         return 80;
      } else if (var0.equals("font-weight")) {
         return 81;
      } else if (var0.equals("from")) {
         return 82;
      } else if (var0.equals("g1")) {
         return 83;
      } else if (var0.equals("g2")) {
         return 84;
      } else if (var0.equals("glyph-name")) {
         return 85;
      } else if (var0.equals("hanging")) {
         return 86;
      } else if (var0.equals("height")) {
         return 87;
      } else if (var0.equals("horiz-adv-x")) {
         return 88;
      } else if (var0.equals("horiz-origin-x")) {
         return 89;
      } else if (var0.equals("id")) {
         return 90;
      } else if (var0.equals("ideographic")) {
         return 91;
      } else if (var0.equals("k")) {
         return 92;
      } else if (var0.equals("keyPoints")) {
         return 93;
      } else if (var0.equals("keySplines")) {
         return 94;
      } else if (var0.equals("keyTimes")) {
         return 95;
      } else if (var0.equals("lang")) {
         return 96;
      } else if (var0.equals("mathematical")) {
         return 97;
      } else if (var0.equals("max")) {
         return 98;
      } else if (var0.equals("min")) {
         return 99;
      } else if (var0.equals("name")) {
         return 100;
      } else if (var0.equals("origin")) {
         return 101;
      } else if (var0.equals("overline-position")) {
         return 102;
      } else if (var0.equals("overline-thickness")) {
         return 103;
      } else if (var0.equals("panose-1")) {
         return 104;
      } else if (var0.equals("path")) {
         return 105;
      } else if (var0.equals("pathLength")) {
         return 106;
      } else if (var0.equals("points")) {
         return 107;
      } else if (var0.equals("preserveAspectRatio")) {
         return 108;
      } else if (var0.equals("r")) {
         return 109;
      } else if (var0.equals("repeatCount")) {
         return 110;
      } else if (var0.equals("repeatDur")) {
         return 111;
      } else if (var0.equals("requiredExtensions")) {
         return 112;
      } else if (var0.equals("requiredFeatures")) {
         return 113;
      } else if (var0.equals("restart")) {
         return 114;
      } else if (var0.equals("rotate")) {
         return 115;
      } else if (var0.equals("rx")) {
         return 116;
      } else if (var0.equals("ry")) {
         return 117;
      } else if (var0.equals("slope")) {
         return 118;
      } else if (var0.equals("stemh")) {
         return 119;
      } else if (var0.equals("stemv")) {
         return 120;
      } else if (var0.equals("strikethrough-position")) {
         return 121;
      } else if (var0.equals("strikethrough-thickness")) {
         return 122;
      } else if (var0.equals("stroke")) {
         return 123;
      } else if (var0.equals("stroke-dasharray")) {
         return 124;
      } else if (var0.equals("stroke-dashoffset")) {
         return 125;
      } else if (var0.equals("stroke-linecap")) {
         return 126;
      } else if (var0.equals("stroke-linejoin")) {
         return 127;
      } else if (var0.equals("stroke-miterlimit")) {
         return 128;
      } else if (var0.equals("stroke-width")) {
         return 129;
      } else if (var0.equals("style")) {
         return 130;
      } else if (var0.equals("systemLanguage")) {
         return 131;
      } else if (var0.equals("target")) {
         return 132;
      } else if (var0.equals("text-anchor")) {
         return 133;
      } else if (var0.equals("to")) {
         return 134;
      } else if (var0.equals("transform")) {
         return 135;
      } else if (var0.equals("type")) {
         return 136;
      } else if (var0.equals("u1")) {
         return 137;
      } else if (var0.equals("u2")) {
         return 138;
      } else if (var0.equals("underline-position")) {
         return 139;
      } else if (var0.equals("underline-thickness")) {
         return 140;
      } else if (var0.equals("unicode")) {
         return 141;
      } else if (var0.equals("unicode-range")) {
         return 142;
      } else if (var0.equals("units-per-em")) {
         return 143;
      } else if (var0.equals("values")) {
         return 144;
      } else if (var0.equals("version")) {
         return 145;
      } else if (var0.equals("viewBox")) {
         return 146;
      } else if (var0.equals("visibility")) {
         return 147;
      } else if (var0.equals("width")) {
         return 148;
      } else if (var0.equals("widths")) {
         return 149;
      } else if (var0.equals("x")) {
         return 150;
      } else if (var0.equals("xheight")) {
         return 151;
      } else if (var0.equals("x1")) {
         return 152;
      } else if (var0.equals("x2")) {
         return 153;
      } else if (var0.equals("xlink:actuate")) {
         return 154;
      } else if (var0.equals("actuate")) {
         return 154;
      } else if (var0.equals("xlink:arcrole")) {
         return 155;
      } else if (var0.equals("arcrole")) {
         return 155;
      } else if (var0.equals("xlink:href")) {
         return 156;
      } else if (var0.equals("href")) {
         return 156;
      } else if (var0.equals("xlink:role")) {
         return 157;
      } else if (var0.equals("role")) {
         return 157;
      } else if (var0.equals("xlink:show")) {
         return 158;
      } else if (var0.equals("show")) {
         return 158;
      } else if (var0.equals("xlink:title")) {
         return 159;
      } else if (var0.equals("title")) {
         return 159;
      } else if (var0.equals("xlink:type")) {
         return 160;
      } else if (var0.equals("xml:base")) {
         return 161;
      } else if (var0.equals("base")) {
         return 161;
      } else if (var0.equals("xml:lang")) {
         return 162;
      } else if (var0.equals("xml:space")) {
         return 163;
      } else if (var0.equals("space")) {
         return 163;
      } else if (var0.equals("y")) {
         return 164;
      } else if (var0.equals("y1")) {
         return 165;
      } else if (var0.equals("y2")) {
         return 166;
      } else {
         return (short)(var0.equals("zoomAndPan") ? 167 : -1);
      }
   }

   public static final short stringToEnumValue(short var0, String var1) {
      switch(var0) {
      case 64:
         if (var1.equals("inherit")) {
            return 328;
         }

         return -1;
      case 71:
         if (var1.equals("none")) {
            return 380;
         } else if (var1.equals("inline")) {
            return 381;
         } else {
            if (var1.equals("inherit")) {
               return -2;
            }

            return -1;
         }
      case 74:
         if (var1.equals("none")) {
            return 325;
         } else if (var1.equals("currentColor")) {
            return 326;
         } else {
            if (var1.equals("inherit")) {
               return 328;
            }

            return -1;
         }
      case 75:
         if (var1.equals("evenodd")) {
            return 375;
         } else if (var1.equals("nonzero")) {
            return 376;
         } else {
            if (var1.equals("inherit")) {
               return -2;
            }

            return -1;
         }
      case 76:
         if (var1.equals("inherit")) {
            return -2;
         }

         return -3;
      case 77:
         if (var1.equals("inherit")) {
            return -2;
         }

         return -1;
      case 79:
         if (var1.equals("normal")) {
            return 331;
         } else if (var1.equals("italic")) {
            return 332;
         } else if (var1.equals("oblique")) {
            return 333;
         } else {
            if (var1.equals("inherit")) {
               return -2;
            }

            return -1;
         }
      case 81:
         if (var1.equals("normal")) {
            return 331;
         } else if (var1.equals("bold")) {
            return 335;
         } else if (var1.equals("bolder")) {
            return 336;
         } else if (var1.equals("lighter")) {
            return 337;
         } else if (var1.equals("100")) {
            return 338;
         } else if (var1.equals("200")) {
            return 339;
         } else if (var1.equals("300")) {
            return 340;
         } else if (var1.equals("400")) {
            return 341;
         } else if (var1.equals("500")) {
            return 342;
         } else if (var1.equals("600")) {
            return 343;
         } else if (var1.equals("700")) {
            return 344;
         } else if (var1.equals("800")) {
            return 345;
         } else if (var1.equals("900")) {
            return 346;
         } else {
            if (var1.equals("inherit")) {
               return -2;
            }

            return -1;
         }
      case 123:
         if (var1.equals("none")) {
            return 325;
         } else if (var1.equals("currentColor")) {
            return 326;
         } else {
            if (var1.equals("inherit")) {
               return 328;
            }

            return -1;
         }
      case 125:
         if (var1.equals("inherit")) {
            return -2;
         }

         return -1;
      case 126:
         if (var1.equals("butt")) {
            return 395;
         } else if (var1.equals("round")) {
            return 396;
         } else if (var1.equals("square")) {
            return 397;
         } else {
            if (var1.equals("inherit")) {
               return -2;
            }

            return -1;
         }
      case 127:
         if (var1.equals("miter")) {
            return 400;
         } else if (var1.equals("round")) {
            return 401;
         } else if (var1.equals("bevel")) {
            return 402;
         } else {
            if (var1.equals("inherit")) {
               return -2;
            }

            return -1;
         }
      case 128:
         if (var1.equals("inherit")) {
            return -2;
         }

         return -1;
      case 129:
         if (var1.equals("inherit")) {
            return -2;
         }

         return -1;
      case 132:
         return -3;
      case 133:
         if (var1.equals("start")) {
            return 360;
         } else if (var1.equals("middle")) {
            return 361;
         } else if (var1.equals("end")) {
            return 362;
         } else {
            if (var1.equals("inherit")) {
               return -2;
            }

            return -1;
         }
      case 147:
         if (var1.equals("visible")) {
            return 385;
         } else if (var1.equals("hidden")) {
            return 386;
         } else {
            if (var1.equals("inherit")) {
               return -2;
            }

            return -1;
         }
      case 156:
         return -3;
      case 167:
         if (var1.equals("magnify")) {
            return 320;
         } else {
            if (var1.equals("disable")) {
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

   public static final String enumToStringElement(short var0) {
      switch(var0) {
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

   public static final String enumToStringTrait(short var0) {
      switch(var0) {
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

   public static final String enumToStringValue(short var0) {
      switch(var0) {
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

   public static final boolean checkTraitOnElement(short var0, short var1) {
      switch(var0) {
      case 0:
         if (var1 != 64 && var1 != 71 && var1 != 74 && var1 != 75 && var1 != 123 && var1 != 125 && var1 != 126 && var1 != 127 && var1 != 128 && var1 != 129 && var1 != 147 && var1 != 76 && var1 != 77 && var1 != 79 && var1 != 81 && var1 != 133 && var1 != 135 && var1 != 132 && var1 != 156) {
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
         if (var1 != 64 && var1 != 71 && var1 != 74 && var1 != 75 && var1 != 123 && var1 != 125 && var1 != 126 && var1 != 127 && var1 != 128 && var1 != 129 && var1 != 147 && var1 != 135 && var1 != 67 && var1 != 68 && var1 != 109) {
            return false;
         }

         return true;
      case 8:
         if (var1 != 64 && var1 != 71 && var1 != 74 && var1 != 75 && var1 != 123 && var1 != 125 && var1 != 126 && var1 != 127 && var1 != 128 && var1 != 129 && var1 != 147 && var1 != 135 && var1 != 67 && var1 != 68 && var1 != 116 && var1 != 117) {
            return false;
         }

         return true;
      case 14:
         if (var1 != 64 && var1 != 71 && var1 != 74 && var1 != 75 && var1 != 123 && var1 != 125 && var1 != 126 && var1 != 127 && var1 != 128 && var1 != 129 && var1 != 147 && var1 != 76 && var1 != 77 && var1 != 79 && var1 != 81 && var1 != 133 && var1 != 135) {
            return false;
         }

         return true;
      case 17:
         if (var1 != 64 && var1 != 71 && var1 != 74 && var1 != 75 && var1 != 123 && var1 != 125 && var1 != 126 && var1 != 127 && var1 != 128 && var1 != 129 && var1 != 147 && var1 != 135 && var1 != 150 && var1 != 164 && var1 != 148 && var1 != 87 && var1 != 156) {
            return false;
         }

         return true;
      case 18:
         if (var1 != 64 && var1 != 71 && var1 != 74 && var1 != 75 && var1 != 123 && var1 != 125 && var1 != 126 && var1 != 127 && var1 != 128 && var1 != 129 && var1 != 147 && var1 != 135 && var1 != 152 && var1 != 153 && var1 != 165 && var1 != 166) {
            return false;
         }

         return true;
      case 22:
         if (var1 != 64 && var1 != 71 && var1 != 74 && var1 != 75 && var1 != 123 && var1 != 125 && var1 != 126 && var1 != 127 && var1 != 128 && var1 != 129 && var1 != 147 && var1 != 135 && var1 != 69) {
            return false;
         }

         return true;
      case 25:
         if (var1 != 64 && var1 != 71 && var1 != 74 && var1 != 75 && var1 != 123 && var1 != 125 && var1 != 126 && var1 != 127 && var1 != 128 && var1 != 129 && var1 != 147 && var1 != 135 && var1 != 87 && var1 != 148 && var1 != 150 && var1 != 164 && var1 != 116 && var1 != 117) {
            return false;
         }

         return true;
      case 27:
         if (var1 != 64 && var1 != 71 && var1 != 74 && var1 != 75 && var1 != 123 && var1 != 125 && var1 != 126 && var1 != 127 && var1 != 128 && var1 != 129 && var1 != 147 && var1 != 76 && var1 != 77 && var1 != 79 && var1 != 81 && var1 != 133 && var1 != 145 && var1 != 58 && var1 != 146 && var1 != 167) {
            return false;
         }

         return true;
      case 29:
         if (var1 != 64 && var1 != 71 && var1 != 74 && var1 != 75 && var1 != 123 && var1 != 125 && var1 != 126 && var1 != 127 && var1 != 128 && var1 != 129 && var1 != 147 && var1 != 76 && var1 != 77 && var1 != 79 && var1 != 81 && var1 != 133 && var1 != 135 && var1 != 150 && var1 != 164 && var1 != 169) {
            return false;
         }

         return true;
      case 31:
         return var1 == 64 || var1 == 71 || var1 == 74 || var1 == 75 || var1 == 123 || var1 == 125 || var1 == 126 || var1 == 127 || var1 == 128 || var1 == 129 || var1 == 147 || var1 == 76 || var1 == 77 || var1 == 79 || var1 == 81 || var1 == 133 || var1 == 135 || var1 == 150 || var1 == 164 || var1 == 156;
      }
   }

   public static final boolean checkElementAsChild(short var0, short var1) {
      switch(var0) {
      case 0:
         switch(var1) {
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
         switch(var1) {
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
         switch(var1) {
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
         switch(var1) {
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
         switch(var1) {
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

   private static final boolean isAllowedRemoveType(short var0) {
      switch(var0) {
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

   private static final boolean isAllowedInsertType(short var0) {
      switch(var0) {
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

   private static final boolean isAllowedGetFloatTrait(short var0) {
      switch(var0) {
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

   private static final boolean isAllowedSetFloatTrait(short var0) {
      switch(var0) {
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

   private static final boolean isAllowedGetTrait(short var0) {
      switch(var0) {
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

   private static final boolean isAllowedSetTrait(short var0) {
      switch(var0) {
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

   private static final boolean isAllowedGetRgbColorTrait(short var0) {
      switch(var0) {
      case 64:
      case 74:
      case 123:
         return true;
      default:
         return false;
      }
   }

   private static final boolean isAllowedSetRgbColorTrait(short var0) {
      switch(var0) {
      case 64:
      case 74:
      case 123:
         return true;
      default:
         return false;
      }
   }

   private static final boolean isAllowedGetMatrixTrait(short var0) {
      switch(var0) {
      case 135:
         return true;
      default:
         return false;
      }
   }

   private static final boolean isAllowedSetMatrixTrait(short var0) {
      switch(var0) {
      case 135:
         return true;
      default:
         return false;
      }
   }

   private static final boolean isAllowedGetPathTrait(short var0) {
      switch(var0) {
      case 69:
         return true;
      default:
         return false;
      }
   }

   private static final boolean isAllowedSetPathTrait(short var0) {
      switch(var0) {
      case 69:
         return true;
      default:
         return false;
      }
   }

   private static final boolean isAllowedGetRectTrait(short var0) {
      switch(var0) {
      case 146:
         return true;
      default:
         return false;
      }
   }

   private static final boolean isAllowedSetRectTrait(short var0) {
      switch(var0) {
      case 146:
         return true;
      default:
         return false;
      }
   }

   private static final boolean isAllowedGetTraitNs(short var0) {
      switch(var0) {
      case 156:
         return true;
      default:
         return false;
      }
   }

   private static final boolean isAllowedSetTraitNs(short var0) {
      switch(var0) {
      case 156:
         return true;
      default:
         return false;
      }
   }
}
