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
 *  File         : HTMLFreeForm.java
 *  Description  : This file contains the class HTMLFreeForm, which is a container
 *                 for any kind of arbitary HTML that a coder might want to imeplement.
 *                 This class extends the HTMLEntity class and hense can be a part of
 *                 an HTML rendering tree AS A LEAF NODE. The reason for this is that
 *                 this class does not have the ability to add children. If required
 *                 this could be easily added, but I recomend creating a new class for
 *                 that explicit purpose (since the code for this is trivial).
 *
 * DATE           USER          COMMENTS
 * 2005-11-30     SUKMLK        Created. Added methods to add arbitary HTML.
 * 
 */

package ifs.docmaw.html;


/**
 *
 * @author sukmlk
 * This class is for freeform html code. This is leaf
 * node in the structure. It cant have any children under it.
 * You could always create a freeform html node class with children
 * by not overriding the generateChildHtml() method.
 */
public class HTMLFreeForm extends HTMLEntity
{    
   String freeFormHtml = "";
   
   /** Creates a new instance of HTMLFreeForm */
   public HTMLFreeForm()
   {
      resetEntity(); // Reset the HTML entity.
   }
   
   protected void beginGenerateHtml()
   {
      // Do nothing.
   }
   
   protected void generateChildHtml()
   {      
      htmlContent  = freeFormHtml; // Only add the freeform content.
   }
      
   protected void endGenerateHtml()
   {
      // Do nothing.
   }             
   
   public void addLine(String html)
   {
      freeFormHtml += dUtils.addLineBreak(html);
      setEntityDirty(true);
   }
   
   public void clearFreeformContent ()
   {
      freeFormHtml = "";
      setEntityDirty (true);
   }
   
   
}
