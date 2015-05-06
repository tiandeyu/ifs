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
 *  File         : HTMLLayer.java
 *  Description  : This file contains the HTMLLayer class. This class has the 
 *                 responsibility of writing a HTML Layer (using the <DIV> tag).
 *                 This class extends the HTMLEntity class and hence can be added
 *                 to an HTML render tree at any level (sometimes it might be 
 *                 useful to add this as a child to overlay another component). This
 *                 class can have children and all the children will be positioned
 *                 by how the parent class is positioned. NOTE: THIS CLASS DOES NOT
 *                 HAVE EXUASTIVE LAYER FUNCTIONALITY. Just what I needed. It can
 *                 be easily extended though. For a demo of this class in action
 *                 check out the Document Structure Navigator Customizer. View
 *                 a document in the navigator and click the customize button.
 *                (look in ifs.docmaw.DocStructureNavigatorTree.java)
 *
 * DATE           USER          COMMENTS
 * 2005-11-30     SUKMLK        Created. Added all the HTML element properties
 *                              I needed.
 * 
 */

package ifs.docmaw.html;

        



/**
 *
 * @author sukmlk
 * This class abstracts the generation of an HTML Layer.
 */

/*
 * TODO: 
 * Add bottom, right co-ord generating code.
 */

public class HTMLLayer extends HTMLEntity
{
   
   /* 
    * HTML for the div tag looks like this.
    * <div id="dialogBox" class="hidden" style = "background-color: lightblue; 
    * width: 320px; height: 240px; padding: 10px; position: absolute; 
    * border: 1px black double; right:10px; bottom:10px; top:10px; right:10px; z-index:1">
    */
   
   private String    cssClass       = ""; // The css class if your using a css.
   private String    id             = ""; // The id
   private String    bgColour       = ""; // background colour of the layer
   
   private int       width          = 0;  // In pixels.
   private int       height         = 0;  // In pixels.
   private int       padding        = 0;  // This is the empty area around the contents of the layer.   
   private String    positionType   = ""; // Absolute or Relative possitioning of the layer.
   private String    borderType     = ""; // The type of the border (double etc..)
   private int       borderWidth    = 0;  // The width in pixels of the border.
   private int       right          = 0;  // Co-ordinate's of the layer. Use only
   private int       left           = 0;  // top-left only or bottom right only
   private int       top            = 0;  // for positioning. Use of all 4 results
   private int       bottom         = 0;  // in the first 2 (top right) being used.
   
   private int       zIndex         = 0;  // The depth value of the latey. Higher value layers on top.
   
         
   
   /** Creates a new instance of HTMLLayer */
   public HTMLLayer()
   {
      resetEntity(); // Reset the html entity.      
   }
        
   
   /*
    * generateHtml()
    * This method implements generateHtml() for HTML layers.
    */
   
   protected void beginGenerateHtml()
   {                     
      htmlContent = "<div "; // Open the div tag
            
      htmlContent += dUtils.genHtmlAttribute("id", id); // Add the id
      htmlContent += dUtils.genHtmlAttribute("class", cssClass); // Add the class
      
      // Style info starts here. Here we add all the style properties for the layer. (almost
      // see the todo... :))
      
       htmlContent += dUtils.genHtmlAttribute
              ("style",               
               dUtils.genStyleAttribute("background-color", bgColour)                       +               
               dUtils.genStyleAttribute("width"           , width)                          +               
               dUtils.genStyleAttribute("height"          , height)                         +               
               dUtils.genStyleAttribute("padding"         , padding)                        +               
               dUtils.genStyleAttribute("position"        , positionType)                   +               
               dUtils.genStyleAttribute("border"          , borderWidth + " " + borderType) +
               dUtils.genStyleAttribute("top"             , top)                            +               
               dUtils.genStyleAttribute("left"            , left));
      
      htmlContent += ">"; // Close the opening div tag.
      htmlContent = dUtils.addLineBreak(htmlContent);
   }
         
   
   /*
    * endGenerateHtml implements the abstract and adds the end <div> tag.
    */
   protected void endGenerateHtml()
   {
      htmlContent += "</div>"; // Close div tag
      htmlContent = dUtils.addLineBreak(htmlContent);
   }
   
   /*
    * All of the encapsulation methods that netbeans 5.0 beta 1 kindly generated for me!
    * To try it out rightclick (on the netbeans editor :) ), click refactor, click encapsulate...
    */

    public String getCssClass() 
    {
        return cssClass;
    }

    public void setCssClass(String cssClass) 
    {
        this.cssClass = cssClass;
    }

    public String getId() 
    {
        return id;
    }

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getBgColour() 
    {
        return bgColour;
    }

    public void setBgColour(String bgColour) 
    {
        this.bgColour = bgColour;
    }
    
    public int getWidth() 
    {
        return width;
    }

    public void setWidth(int width) 
    {
        this.width = width;
    }

    public int getHeight() 
    {
        return height;
    }

    public void setHeight(int height) 
    {
        this.height = height;
    }

    public int getPadding() 
    {
        return padding;
    }

    public void setPadding(int padding) 
    {
        this.padding = padding;
    }

    public String getPositionType() 
    {
        return positionType;
    }

    public void setPositionType(String positionType) 
    {
        this.positionType = positionType;
    }

    public String getBorderType() 
    {
        return borderType;
    }

    public void setBorderType(String borderType) 
    {
        this.borderType = borderType;
    }

    public int getBorderWidth() 
    {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) 
    {
        this.borderWidth = borderWidth;
    }

    public int getRight() 
    {
        return right;
    }

    public void setRight(int right) 
    {
        this.right = right;
    }

    public int getLeft() 
    {
        return left;
    }

    public void setLeft(int left) 
    {
        this.left = left;
    }

    public int getTop() 
    {
        return top;
    }

    public void setTop(int top) 
    {
        this.top = top;
    }

    public int getBottom() 
    {
        return bottom;
    }

    public void setBottom(int bottom) 
    {
        this.bottom = bottom;
    }

    public int getZIndex() 
    {
        return zIndex;
    }

    public void setZIndex(int zIndex) 
    {
        this.zIndex = zIndex;
    }
           
}
