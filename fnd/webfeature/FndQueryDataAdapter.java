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
 * File        : FndQueryDataAdapter.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * $Log: FndQueryDataAdapter.java,v $
 * 2008/08/15 buhilk - Bug 76288, Added getQueryRecord() to be overidden to create the search FNDQueryRecord.
 * 2008/06/26 mapelk - Bug 74852, Created. Programming Model for Activities.
 *  
 */

package ifs.fnd.webfeature;

import ifs.fnd.record.*;
import ifs.fnd.asp.*;

/**
 * Interface for query data.
 */
public interface FndQueryDataAdapter extends FndDataAdapter 
{
   public FndAbstractArray query(FndQueryRecord record, ASPPage page);
   public FndQueryRecord getQueryRecord(ASPBlock block);
   public int count(FndQueryRecord record, ASPPage page);
}
