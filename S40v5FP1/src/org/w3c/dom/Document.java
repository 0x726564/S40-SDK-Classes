package org.w3c.dom;

public interface Document extends Node {
   Element createElementNS(String var1, String var2) throws DOMException;

   Element getDocumentElement();

   Element getElementById(String var1);
}
