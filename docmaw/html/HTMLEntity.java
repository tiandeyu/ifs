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
 *  File         : HTMLEntity.java
 *  Description  : This file contains the HTMLEntity class, which is an abstract
 *                 for all docmaw specific HTML writer classes. This class sets
 *                 the functionality for building a HTML object hearachy that can
 *                 then be used to render HTML.
 *
 * DATE           USER          COMMENTS
 * 2005-11-30     SUKMLK        Created. Added all the basic methods for rendering 
 *                              self, children, refreshing state, html object tree.
 * 
 */

package ifs.docmaw.html;

import java.util.Vector;

/**
 *
 * @author sukmlk
 * This class is the abstract class for all docmaw specific html writer classes.
 * This class represents both an HTML object and a node in a render tree. 
 */
public abstract class HTMLEntity
{
   // A boolean to see if changes have been made to any of the data structures.
   private boolean entityDirty;
   
   // A string to hold the html content for an entity.
   protected String htmlContent;
   
   // A vector to hold all of the child html entites.
   // Thread safety is not needed here (i hope...) 
   protected Vector childNodes = new Vector (5, 5);
   
   /** Creates a new instance of HTMLEntity */
   public HTMLEntity() 
   {
      resetEntity();
   }
   
   // Method to return the generated html code.
   public String getHtml()
   {
      if (entityDirty)
      {
         clearHtmlContent ();    // Clean things up for this node.
         beginGenerateHtml();    // Begin generating this entities html.
         generateChildHtml();    // Render the childrens html.
         endGenerateHtml  ();    // End generating this entities html.
         setEntityDirty(false);
      }
      return htmlContent;
   }
   
   // Method to start rendering the html
   protected abstract void beginGenerateHtml();
   
   // Method to generate the html of the children, can be overridden.
   protected void generateChildHtml()
   {
      HTMLEntity tempEntity;
      for (int i = 0; i < childNodes.size(); i++)
      {
         tempEntity = (HTMLEntity) childNodes.elementAt(i);
         htmlContent += tempEntity.getHtml();
      }
   }
   
   // Method to finish rendering the html
   protected abstract void endGenerateHtml();
   
   protected final void resetEntity()
   {
      // Set the entity as dirty. (Meaning that it needs to be re-rendered)
      setEntityDirty(true);
      // Clear the contents.
      clearHtmlContent();
   }
   
   // Clears the html content.
   protected final void clearHtmlContent()
   {
      htmlContent = "";
   }      
   
   // Set's the entities dirty flag.
   protected final void setEntityDirty (boolean value)
   {
      entityDirty = value;
   }
   
   // TODO: Should these methods be final or not? Should they be overridable?   
   
   public final void addChildNode (HTMLEntity childNode)
   {
      childNodes.add(childNode);
   }
   
   public final void removeChildNodeAt(int index)
   {
      childNodes.removeElementAt(index);
   }
   
   public final void childChildNodes ()
   {
      childNodes.clear();
   }
   
}
