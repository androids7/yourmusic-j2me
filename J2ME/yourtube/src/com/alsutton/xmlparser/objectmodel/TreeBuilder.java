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

import java.io.*;
import java.util.*;

import com.alsutton.xmlparser.*;

/**
 * Class for building an the object tree.
 */

public class TreeBuilder
  implements XMLEventListener
{
  /**
   * The current node being parsed.
   */

  private Node currentNode;

  /**
   * The root node of the tree being parsed.
   */

  private Node rootNode;

  /**
   * Default constructor.
   */

  public TreeBuilder()
  {
    currentNode = null;
  }

  /**
   * Method to construct a tree from a reader.
   *
   * @return The root node of the tree.
   */

  public Node createTree( Reader is )
    throws IOException
  {
    XMLParser parser = new XMLParser( this );
    
    /* Added by Oleg Derevenetz for YourTube */
    parser.convertAllTagNamesToLowerCase(true);
    parser.setInputUTF8Encoded(false);

    parser.parse( is );

    return rootNode;
  }

  /**
   * Method called when an tag start is encountered.
   *
   * @param name Tag name.
   * @param attributes The tags attributes.
   */

  public void tagStarted( String name, Hashtable attributes )
  {
    Node newNode = new Node( currentNode, name, attributes );
    if( currentNode == null )
      rootNode = newNode;

    currentNode = newNode;
  }

  /**
   * Method called when some plain text between two tags is encountered.
   *
   * @param text The plain text in question.
   */

  public void plaintextEncountered( String text )
  {
    if( currentNode != null )
    {
      currentNode.addText( text );
    }
  }

  /**
   * The method called when a tag end is encountered.
   *
   * @param name The name of the tag that has just ended.
   */

  public void tagEnded( String name )
  {
    String currentNodeName = currentNode.getName();
    if( currentNodeName.equals( name ) )
      currentNode = currentNode.getParent();
  }
}
