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
*  File        : EnterwNavigator.java
*  Created by  : Nilan Mallawarachchi
*  Modified By : Himali 2001-05-16 Corrected call id 64905
*              : shdilk 2001-08-07  Corrected call id 67332
*              : dagalk 2002-02-13  add corporate form
*              : anpalk 2002-06-28  call Id 30171
*              : Jakalk 2005-09-07  Code Cleanup, Removed doReset and clone methods.
* ----------------------------- Wings Merge Start -----------------------------
*              : Haunlk 2006-11-08  Added General Info and Address Info to the navigator.
*              : Haunlk 2006-11-15  Added Invoice Info to the navigator.
*              : Haunlk 2006-11-16  Added Accounting Info to the navigator.
*              : Haunlk 2007-01-31  Merged Wings Code.
*              : Maamlk 2007-03-0-7 Merged LCS Bug 42871, Removed obsolete entry 'Replace Person Identities'.
* ------------------------------Wings Merge End -------------------------------------
*/

package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;

public class EnterwNavigator implements Navigator
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.EnterwNavigator");

   public ASPNavigatorNode add(ASPManager mgr)
   {
      if(DEBUG) Util.debug("!!EnterwNavigator!!");

      ASPNavigatorNode   nod = mgr.newASPNavigatorNode(mgr.translate( "ENTERWENTERWNAVIGATOR: Enterprise" ));

      ASPNavigatorNode n = nod.addNode(mgr.translate( "ENTERWENTERWNAVIGATORRCHCOMPANY: Company" ));
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORCOMPANYCRECOMPWIZ: Create Company Wizard")), "enterw/EnterwNewWindowOpener.page?NEWURL=CreateCompanyWizard","COMPANY");
      
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORCOMPANYGENERALINFO: General Info")),"enterw/Company.page","COMPANY");
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORORGLEVEL: Organization Level")), "genbaw/GeneralOrgLevel.page", "GENERAL_ORG_LEVEL");
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORORGANIZATION: General Organization")), "genbaw/GeneralOrganization.page", "GENERAL_ORGANIZATION");
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORORGANIZATIONNAV: General Organization Nav")), "genbaw/GeneralOrganizationNav.page", "GENERAL_ORGANIZATION");
      
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORCOMPANYADDRESSINFO: Address Info")),"enterw/CompanyAddress.page","COMPANY,COMPANY_ADDRESS,COMPANY_ADDRESS_TYPE,COMPANY_COMM_METHOD,COMPANY_ADDRESS_DELIV_INFO,DELIVERY_FEE_CODE_COMPANY,COMPANY_DELIVERY_TAX_EXEMP");
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORCOMPANYEMPLOYEEINFO: Employees Info")),"enterw/CompanyEmployees.page","COMPANY,COMPANY_EMP");
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORCOMPANYEGENERALDATAFORDIS: General Data for Distribution")),"enterw/CompanyDistributionInfo.page","COMPANY,COMPANY_DISTRIBUTION_INFO");
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORCOMPANYINVOICEINFO: Invoice Info")),"enterw/CompanyInvoice.page","COMPANY,COMPANY_INVOICE_INFO");
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORCOMPANYACCOUNTINGINFO: Accounting Info")),"enterw/CompanyAccouting.page","COMPANY,COMPANY_FINANCE,CURRENCY_TYPE_BASIC_DATA");
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORCHCOMP: Change Company")),"enterw/NavChangeCompany.page","DUAL,COMPANY_FINANCE");

      n = nod.addNode(mgr.translate( "ENTERWENTERWNAVIGATORCUTS: Customer" ));
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORGENINFOC: General Info")),"enterw/CustomerInfo.page","CUSTOMER_INFO,CUSTOMER_INFO_MSG_SETUP,CUSTOMER_INFO_OUR_ID,DUAL");
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORADDINFOC: Address Info")),"enterw/CustomerAddress.page","CUSTOMER_INFO,CUSTOMER_INFO_ADDRESS,CUSTOMER_INFO_COMM_METHOD,CUSTOMER_INFO_ADDRESS_TYPE,CUSTOMER_INFO_VAT,DELIVERY_FEE_CODE,DELIVERY_TAX_EXEMPTION");
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORINVINFOC: Invoice Info")),"invoiw/CustomerInvoice.page","CUSTOMER_INFO,IDENTITY_INVOICE_INFO");
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORPAYINFOC: Payment Info")),"paylew/CustomerPayment.page","CUSTOMER_INFO,IDENTITY_PAY_INFO,PAYMENT_WAY_PER_IDENTITY,PAYMENT_ADDRESS_GENERAL");
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORCUSTCREDITINFO: Credit Info")),"paylew/CustomerCreditInfo.page","CUSTOMER_INFO,CUSTOMER_CREDIT_INFO");
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATOROVWCUSTP: Overview Customer")),"enterw/CustomerInfoOvw.page","CUSTOMER_INFO");

      n = nod.addNode(mgr.translate( "ENTERWENTERWNAVIGATORSUPP: Supplier" ));
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORGENINFOS: General Info")),"enterw/SupplierInfoGeneral.page","SUPPLIER_INFO,SUPPLIER_INFO_MSG_SETUP,SUPPLIER_INFO_OUR_ID");
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORADDINFOS: Address Info")),"enterw/SupplierAddress.page","SUPPLIER_INFO,SUPPLIER_INFO_ADDRESS,SUPPLIER_INFO_COMM_METHOD,SUPPLIER_INFO_ADDRESS_TYPE");
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORINVINFOS: Invoice Info")),"invoiw/SupplierInvoice.page","SUPPLIER_INFO,IDENTITY_INVOICE_INFO");
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORPAYINFOS: Payment Info")),"paylew/SupplierPayment.page","SUPPLIER_INFO,IDENTITY_PAY_INFO,PAYMENT_WAY_PER_IDENTITY,PAYMENT_ADDRESS_GENERAL");

      n.addItem((mgr.translate("ENTERWENTERWNAVIGATOROVWSUP: Overview Supplier")),"enterw/SupplierInfoOvw.page","SUPPLIER_INFO");

      n = nod.addNode(mgr.translate( "ENTERWENTERWNAVIGATORMANUFT: Manufacturer" ));
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORMANUFA: Manufacturer")),"enterw/ManufacturerInfo.page","MANUFACTURER_INFO,MANUF_INFO_COMM_METHOD,MANUF_INFO_MSG_SETUP,MANUFACTURER_INFO_OUR_ID");
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATOROVWMANU: Overview Manufacturer")),"enterw/ManufacturerInfoOvw.page","MANUFACTURER_INFO");

      n = nod.addNode(mgr.translate( "ENTERWENTERWNAVIGATORPERSN: Person" ));
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORPERSINFO: Person")),"enterw/PersonInfo.page","PERSON_INFO,PERSON_INFO_ADDRESS,PERSON_INFO_ADDRESS_TYPE,PERSON_INFO_COMM_METHOD");
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORUPPARA: Overview Person")),"enterw/PersonalInfoOvw.page","PERSON_INFO");
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORACCSPROT: Access To Protected Person")),"enterw/PartyAdminUser.page","PARTY_ADMIN_USER");
      
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORPOSITION: Position")), "genbaw/GeneralPosition.page", "GENERAL_POSITION");
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORAPPGROUP: Approve Group")), "genbaw/GeneralAppGrp.page", "GENERAL_APP_GRP");

      n = nod.addNode((mgr.translate( "ENTERWENTERWNAVIGATORCMP: Employee" )),"COMPANY_EMP");
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATOROVWEMP: Overview Employee")),"enterw/CompanyEmpOvw.page","COMPANY_EMP");

      n = nod.addNode(mgr.translate( "ENTERWENTERWNAVIGATORGENENT: General Enterprise" ));

      ASPNavigatorNode m = n.addNode(mgr.translate( "ENTERWENTERWNAVIGATORADDR: Address Information" ));
      //m.addItem((mgr.translate("ENTERWENTERWNAVIGATORADDPRES: Address Presentation")),"enterw/AddressPresentation.page","ADDRESS_PRESENTATION");
      m.addItem((mgr.translate("ENTERWENTERWNAVIGATORADDRCOUNTRY: Overview Address Countries")),"enterw/AddressCountriesOvw.page","ENTERP_ADDRESS_COUNTRY2");
      m.addItem((mgr.translate("ENTERWENTERWNAVIGATORADDRSTATE: States")),"enterw/StateCode.page","STATE_CODES2");
      m.addItem((mgr.translate("ENTERWENTERWNAVIGATORADDRCOUNTY: Counties")),"enterw/CountyCode.page","COUNTY_CODE2");
      m.addItem((mgr.translate("ENTERWENTERWNAVIGATORADDRCITY: Cities")),"enterw/CityCode.page","CITY_CODE2");

      n.addItem((mgr.translate("ENTERWENTERWNAVIGATOROVWBUSINESSFORM: Forms of Business")),"enterw/Corporateform.page","CORPORATE_FORM");
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORBRANCHES: Branches")),"enterw/Branch.page","BRANCH");
      n.addItem((mgr.translate("ENTERWENTERWNAVIGATORINCOMETYPE: Income Type")),"enterw/IncomeType.page","INCOME_TYPE");

      nod.addItem((mgr.translate("ENTERWENTERWNAVIGATORGENERALBASICDATA: General Basic Data")),"genbaw/GeneralBasicData.page", "GENERAL_PROJECT,GENERAL_ZONE,GENERAL_MACH_GROUP,GENERAL_SPECIALTY,GENERAL_COMPONENT_TYPE,GENERAL_STRUCTURE,GENERAL_SYSTEM,GENERAL_COMPONENT,GENERAL_TOTALITY,GENERAL_PARTICULAR,GENERAL_ROOM,GENERAL_LOT,GENERAL_QUALITY_GRADE");
      nod.addItem((mgr.translate("ENTERWENTERWNAVIGATORGENERALLUPAGE: General Lu Page")),"genbaw/GeneralLuPage.page", "GENERAL_LU_PAGE");
      return nod;
   }
}

