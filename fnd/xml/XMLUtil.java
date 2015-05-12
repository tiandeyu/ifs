/*
 *                 IFS Research & Development
 *
 *  This program is protected by copyright law and by international
 *  conventions. All licensing, renting, lending or copying (including
 *  for private use), and all other use of the program, which is not
 *  expressively permitted by IFS Research & Development (IFS), is a
 *  violation of the rights of IFS. Such violations will be reported to the
 *  appropriate authorities.
 *
 *  VIOLATIONS OF ANY COPYRIGHT IS PUNISHABLE BY LAW AND CAN LEAD
 *  TO UP TO TWO YEARS OF IMPRISONMENT AND LIABILITY TO PAY DAMAGES.
 * ----------------------------------------------------------------------------
 * File        : XMLUtil.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    JOH   2000-12-28 - Created.
 *    SABA  2001-02-22 - Added methods saveToString(Document) and
 *                       saveToFile(Document, String).
 * MDAHSE   2001-03-22 - Added overloaded method loadFromInputStream which now
 *                       does not need the encoding parameter
 * Jacek P  2001-04-06 - Included in Web Kit 3.5
 * Ramila H 2003-07-26 - Log Id 1080, Added code to parse an XML doc to a buffer.
 * ----------------------------------------------------------------------------
 * New Comments:
 * Revision 1.1 2006/07/21             buhilk
 * Changed depthSearchXML() to work with multiple child node attributes
 * 
 * Revision 1.0 2006/07/20             buhilk
 * Bug 59518, Changed depthSearchXML() to map boolean values from xml to buffer
*/

package ifs.fnd.xml;

import org.w3c.dom.*;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xml.serialize.*;
import org.apache.xerces.parsers.*;
import org.xml.sax.*;

import java.io.*;
import ifs.fnd.buffer.*;


/**
 * Utility help routines for Xerces xml-parser.
 */
public class XMLUtil
{
   /**
    * Saves a document to a String.
    * @param doc the xml document.
    * @return    the document formatted to a xml-string.
    */
   public static String saveToString(Document doc) throws Exception
   {
      OutputFormat format = new OutputFormat(doc);
      StringWriter stringOut = new StringWriter();
      XMLSerializer serial = new XMLSerializer(stringOut, format);
      serial.asDOMSerializer();
      serial.serialize(doc);
      String str = stringOut.toString();
      stringOut.close();
      return str;
   }

   /**
    * Saves a document to a String.
    * @param doc      the xml document.
    * @param encoding the encoding of the document (example "UTF-8").
    * @param pretty   flag that set format style of the xml-string to pretty.
    * @return         the document formatted to a xml-string.
    */
   public static String saveToString(Document doc, String encoding, boolean pretty) throws Exception
   {
      OutputFormat format = new OutputFormat(doc, encoding, pretty);
      StringWriter stringOut = new StringWriter();
      XMLSerializer serial = new XMLSerializer(stringOut, format);
      serial.asDOMSerializer();
      serial.serialize(doc);
      String str = stringOut.toString();
      stringOut.close();
      return(str);
   }

   /**
    * Saves a document to a file.
    * @param doc      the xml document.
    * @param fileName the output file-name.
    */
   public static void saveToFile(Document doc, String fileName) throws Exception
   {
      OutputFormat format = new OutputFormat(doc);
      FileWriter fw = new FileWriter(fileName);
      XMLSerializer serial = new XMLSerializer(fw, format);
      serial.asDOMSerializer();
      serial.serialize(doc);
      fw.close();
   }

   /**
    * Saves a document to a file.
    * @param doc      the xml document.
    * @param fileName the output file-name.
    * @param encoding the encoding of the document (example "UTF-8").
    * @param pretty   flag that set format style of the xml-string to pretty.
    */
   public static void saveToFile(Document doc, String fileName, String encoding, boolean pretty) throws Exception
   {
      OutputFormat format = new OutputFormat(doc, encoding, pretty);
      FileWriter fw = new FileWriter(fileName);
      XMLSerializer serial = new XMLSerializer(fw, format);
      serial.asDOMSerializer();
      serial.serialize(doc);
      fw.close();
   }

   /**
    * Loads a document from a String
    * @param xml the xml string to parse
    * @return    the DOM-document.
    */
   public static Document loadFromString(String xml) throws Exception
   {
      DOMParser p = new DOMParser();
      InputSource inp = new InputSource(new StringReader(xml));
      p.parse(inp);
      return(p.getDocument());
   }

   /**
    * Loads a document from a file.
    * @param fileName the xml file to parse.
    * @return         the DOM-document.
    */
   public static Document loadFromFile(String fileName) throws Exception
   {
      DOMParser p = new DOMParser();
      InputSource inp = new InputSource(new FileReader(fileName));
      p.parse(inp);
      return(p.getDocument());
   }

   /**
    * Loads a document from an InputStream.
    * @param inStream the InputStream to parse.
    * @return         the DOM-document.
    */
   public static DocumentImpl loadFromInputStreamImpl(InputStream inStream) throws Exception
   {
      DOMParser p = new DOMParser();
      InputSource inp = new InputSource( inStream );
      p.parse(inp);
      return((DocumentImpl)p.getDocument());
   }

   /**
    * Loads a document from an InputStream.
    * @param inStream the InputStream to parse.
    * @return         the DOM-document.
    */
   public static Document loadFromInputStream(InputStream inStream) throws Exception
   {
      DOMParser p = new DOMParser();
      InputSource inp = new InputSource( inStream );
      p.parse(inp);
      return(p.getDocument());
   }

   /**
    * Loads a document from an InputStream.
    * @param inStream the InputStream to parse.
    * @param encoding the encoding type.
    * @return         the DOM-document.
    */
   public static Document loadFromInputStream(InputStream inStream, String encoding) throws Exception
   {
      DOMParser p = new DOMParser();
      InputSource inp = new InputSource( inStream );
      inp.setEncoding( encoding );
      p.parse(inp);
      return(p.getDocument());
   }

   /**
    * Get an element by the name from a parent element.
    * @param parent   the parent element.
    * @param tagName  the tagName to search for.
    * @return         the founded element.
    */
   public static Element getElementByTagName(Element parent, String tagName)
   {
      NodeList list = parent.getChildNodes();
      for (int i=0; i<list.getLength();i++)
      {
         if (list.item(i).getNodeName().equals(tagName))              return((Element)list.item(i));
      }
      return null;
   }

   
   
   public static void saveToBuffer(String fileName, Buffer dest, String root_tag) throws Exception
   {
      Document doc = loadFromFile(fileName);
      saveToBuffer(doc, dest, root_tag);
   }

   public static void saveToBuffer(Document doc, Buffer dest, String root_tag)
   {
      NodeList nodes = doc.getElementsByTagName(root_tag);
      Node n = nodes.item(0); //there should only be one!
      
      depthSearchXML(n,dest);
   }

   private static void depthSearchXML(Node node, Buffer dest)
   {
      NodeList nodes = node.getChildNodes();
      int len = nodes.getLength();

      for(int i=0; i<len; i++)
      {
         Node next_node = nodes.item(i);
         String node_name = next_node.getNodeName();

         if(next_node.getNodeType()==Node.COMMENT_NODE)
         {
            continue;
         }

         if(next_node.hasChildNodes())
         {
            NodeList child_nodes = next_node.getChildNodes();
            Node     first_child = child_nodes.item(0);
            
            // ASSUMPTION:
            // A node is a leaf only if it has only one child and the type of the child is '#text'
            // Then the value of the child node is the value of the leaf
            if(child_nodes.getLength()==1 && "#text".equals(first_child.getNodeName()) )
            {
               String value = first_child.getNodeValue();
               try{ // Bug 59518 Fix.
                  Node att = first_child.getParentNode().getAttributes().getNamedItem("ifsrecord:datatype");
                  if(att!=null && "Boolean".equals(att.getNodeValue()))
                     value = "1".equals(value)? "TRUE":"FALSE";
               }catch(Exception e){}
               dest.addItem(new Item(node_name.toUpperCase(),value));
            }
            else
            {
               Buffer buf = dest.newInstance();
               dest.addItem(new Item(node_name.toUpperCase(), buf));
               depthSearchXML(next_node, buf);
            }
         }
         else
         {
            continue;
         }
      }
   }
   
   
}
