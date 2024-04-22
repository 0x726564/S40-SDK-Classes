package org.w3c.dom;

public interface Node {
   String getNamespaceURI();

   String getLocalName();

   Node getParentNode();

   Node appendChild(Node var1) throws DOMException;

   Node removeChild(Node var1) throws DOMException;

   Node insertBefore(Node var1, Node var2) throws DOMException;
}
