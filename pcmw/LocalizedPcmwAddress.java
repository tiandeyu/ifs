
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
*  File        : LocalizedEnterwAddress.java
* Description :
* Notes       :
* ----------------------------------------------------------------------------
* Modified    :  jewilk 2002-02-20 : Used the LocalizedEnterwAddress.java file 
*                                    (which was created by Ramila) as a template. 
*                ARWILK 031223       Edge Developments - (Removed clone and doReset Methods)
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import java.util.*;

public class LocalizedPcmwAddress extends LocalizedAddressField
{
	private String ent_display_address_layout = "";
	private String ent_edit_address_layout = "";
	private ASPField[] field_list;
	private String country_code ="";
	private ASPCommand cmd;

	public LocalizedPcmwAddress()
	{
	}

	public void setLayouts(ASPManager mgr ,String country_code)
	{
		field_list = getFieldList();   // 2 ASPfields need for customFunction

		mgr.getASPLog().debug("country_code ****************************************************"+country_code);
		//mgr.showAlert("country_code = *"+country_code+"*");
		// mgr.showAlert("field_list[1].getName() = *"+field_list[1].getName()+"*");

		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		cmd = trans.addCustomFunction("DISPLAY_LAYOUT","ADDRESS_PRESENTATION_API.Get_Display_Layout",field_list[0].getName());
		cmd.addInParameter(field_list[1].getName(),country_code);

		cmd = trans.addCustomFunction("EDIT_LAYOUT","ADDRESS_PRESENTATION_API.Get_Edit_Layout",field_list[0].getName());
		cmd.addInParameter(field_list[1].getName(),country_code);

		//trans = mgr.performConfig(trans);
		trans = mgr.perform(trans);

		ent_display_address_layout = trans.getValue("DISPLAY_LAYOUT/DATA/"+field_list[0].getName());
		ent_edit_address_layout = trans.getValue("EDIT_LAYOUT/DATA/"+field_list[0].getName());

		if ( ! mgr.isEmpty(ent_display_address_layout) )
			setAddressDisplayLayout(ent_display_address_layout);

		if ( ! mgr.isEmpty(ent_edit_address_layout) )
			setAddressEditLayout(ent_edit_address_layout);
	}
}
