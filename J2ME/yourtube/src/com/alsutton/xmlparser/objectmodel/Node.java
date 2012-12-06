/*
Copyright (c)2000,2001 Al Sutton (al@alsutton.com), All rights reserved.

Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions 
are met:

1. Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation 
and/or other materials provided with the distribution.

3. Redistributions which form part of a commercial product are permitted on 
the condition that either a) An initial contribution of 1000 (One thousand) 
US Dollars is made to the paypal account of Al Sutton (al@alsutton.com), 
and a further 1 (one) percent of product profits after tax are also payed 
into the paypal account. or b) Another licence agreement is reached.

Neither the name AlSutton.com nor Al Sutton may be used to endorse or promote
products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 'AS IS' 
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR 
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/*
   Note on funding

   If you feel my work on this project is worth something, please make a donation
   to my paypal account (al@alsutton.com) at http://www.paypal.com/
 */


package com.alsutton.xmlparser.objectmodel;

import java.util.*;

/**
 * Class representing a single node in an XML tree.
 */

public class Node
{
  /**
   * The list of children of this node
   */

  public Vector children;

  /**
   * The parent of this node
   */

  private Node parent;

  /**
   * The name of this node
   */

  private String tagName;

  /**
   * The text contained in this node.
   */

  private StringBuffer text;

  /**
   * The attributes set in this nodes tag.
   */

  public Hashtable attributes;

  /**
   * Constructor. Copies details of the parents and node name
   *
   * @param _parent The parent of this node.
   * @param _name The name of this node.
   */

  public Node( Node _parent, String _name, Hashtable _attributes )
  {
    tagName = _name;
    attributes = _attributes;

    parent = _parent;
    if( parent != null )
    {
      parent.addChild( this );
    }
  }

  /**
   * Method to add some text to th text area of the tag.
   *
   * @param newText The text to add.
   */

  public void addText( String newText )
  {
    if( text == null )
      text = new StringBuffer();

    text.append( newText );
  }

  /**
   * Method to set the text area of the tag to a specific value.
   *
   * @param newText The text to use.
   */

  public void setText( String newText )
  {
    text = new StringBuffer( newText );
  }

  /**
   * Method to get the text from this node
   */

  public String getText()
  {
    String textString = "";

    if( text != null )
      textString = text.toString();

    return textString;
  }

  /**
   * Method to get the name of this node.
   */

  public String getName()
  {
    return tagName;
  }

  /**
   * Method to add a child to this node
   *
   * @param childNode The node to add.
   */

  public void addChild( Node childNode )
  {
    if(children == null )
      children = new Vector();

    children.addElement(childNode );
  }

  /**
   * Method to remove a child from this node
   *
   * @param childNode The node to remove.
   */

  public boolean removeChild( Node childNode )
  {
    boolean removed = children.removeElement( childNode );
    if( removed )
      return true;

    if( children == null )
      return false;

    synchronized( children)
    {
      Enumeration childIterator = children.elements();
      while( childIterator.hasMoreElements() )
      {
        Node thisChild = (Node) childIterator.nextElement();

        removed = thisChild.removeChild( childNode );
        if( removed )
          return true;
      }
    }

    return false;
  }

  /**
   * Method to get all the children (and childrens children ) of a specific name.
   *
   * @param name The name of the nodes to fetch.
   * @return A vector of all the nodes of that name.
   */

  public Vector getChildrenByName( String name )
  {
    Vector namedChildren = new Vector();

    getChildrenByName( name, namedChildren );

    return namedChildren;
  }

  /**
   * Method to get all the children (and childrens children ) of a specific
   * name into a given Collection object.
   *
   * @param name The name of the nodes to fetch.
   * @return A vector of all the nodes of that name.
   */

  protected void getChildrenByName( String name, Vector store )
  {
    if( children == null )
      return;

    try
    {
      synchronized( children )
      {
        Enumeration childIterator = children.elements();
        while( childIterator.hasMoreElements() )
        {
          Node thisChild = (Node) childIterator.nextElement();

          String nodeName = thisChild.getName();
          if( nodeName.equals(name) )
          {
            store.addElement( thisChild );
          }

          thisChild.getChildrenByName( name, store );
        }
      }
    }
    catch( NoSuchElementException e )
    {
    }
  }

  /**
   * Method to get the parent of this node
   */

  public Node getParent()
  {
    return parent;
  }

  /**
   * Method to return the data as a byte stream ready to send over
   * the wire
   *
   * @return The data to send as a byte array
   */

  public byte[] getBytes()
  {
    String data = toString();
    return data.getBytes();
  }

  /**
   * Method to get the XML representation of this node.
   *
   * @return A string holding the XML representation.
   */

  public String toString()
  {
    String nodeName = getName();

    StringBuffer xmlRepresentation = new StringBuffer( "<" );
    xmlRepresentation.append( nodeName );

    if( attributes != null )
    {
      synchronized( attributes )
      {
        Enumeration attrIter = attributes.keys();
        while( attrIter.hasMoreElements() )
        {
          String key = (String) attrIter.nextElement();
          String value = (String) attributes.get( key );

          xmlRepresentation.append(' ');
          xmlRepresentation.append( key );
          xmlRepresentation.append("=\"");
          xmlRepresentation.append(value);
          xmlRepresentation.append('\"');
        }
      }
    }

    if( (children == null || children.size() == 0)
    &&  (text == null     || text.length() == 0 ) )
    {
      xmlRepresentation.append( "/>" );
      return xmlRepresentation.toString();
    }

    xmlRepresentation.append( ">" );
    if( text != null )
    {
      xmlRepresentation.append( text );
    }

    if( children != null )
    {
      synchronized( children )
      {
        Enumeration iter = children.elements();
        while( iter.hasMoreElements() )
        {
          Object nextObject = iter.nextElement();
          String nodeRepresentation = nextObject.toString();
          xmlRepresentation.append( nodeRepresentation );
        }
      }
    }

    xmlRepresentation.append( "</" );
    xmlRepresentation.append( nodeName );
    xmlRepresentation.append( ">" );

    return xmlRepresentation.toString();
  }

  /**
   * Method to get an attribute
   *
   * @param attributeName The name of the attribute to get
   * @return The value of the attribute
   */

  public String getAttribute( String attributeName )
  {
    if( attributes == null )
      return null;

    return (String) attributes.get( attributeName );
  }

  /**
   * Method to set an attribute value
   *
   * @param attributeName The name of the attribute to set
   * @param value The value of the attribute
   */

  public void setAttribute( String attributeName, String value )
  {
    if( attributeName == null || value == null )
      return;

    if( attributes == null )
      attributes = new Hashtable();

    attributes.put( attributeName, value );
  }

  /**
   * Method to remove an attribute.
   *
   * @param attributeName The attribute name to remove.
   */

  public void removeAttribute( String attributeName )
  {
    if( attributes == null )
      return;

    attributes.remove(attributeName);
  }
  /**
   * Method to replace a child block with another. The tag name and
   * namespace (xmlns attribute) must be the same.<p>
   * If there is no current child which matches the name and
   *
   *
   * @param replacementNode The replacement node.
   */

  public void replaceNode( Node replacementNode )
  {
    String replacementNodeName = replacementNode.getName();

    String nameSpace = null;
    if( replacementNode.attributes != null );
      nameSpace = (String)replacementNode.attributes.get( "xmlns" );

    if( children == null )
    {
      addChild( replacementNode );
      return;
    }

    synchronized( children )
    {
      Enumeration childIter = children.elements();
      while( childIter.hasMoreElements() )
      {
        Node thisNode = (Node) childIter.nextElement();

        String thisNodeName = thisNode.getName();
        if( thisNodeName.equals( replacementNodeName ) == false )
          continue;

        if( nameSpace == null )
        {
          children.removeElement( thisNode );
          addChild( replacementNode );
          return;
        }

        if( thisNode.attributes == null )
          continue;

        String thisNodeNamespace = (String) thisNode.attributes.get("xmlns");
        if( nameSpace.equals( thisNodeNamespace ) )
        {
          children.removeElement( thisNode );
          addChild( replacementNode );
          return;
        }
      }
    }

    addChild( replacementNode );
  }

  /**
   * Method to get all the children of the current node
   *
   * @return A List of all the children of this node
   */

  public Vector getChildren()
  {
    if( children == null )
      return null;

    return children;
  }
}
