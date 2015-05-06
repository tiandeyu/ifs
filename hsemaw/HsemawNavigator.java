package ifs.hsemaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPNavigatorNode;
import ifs.fnd.asp.Navigator;
import ifs.fnd.service.Util;
  
public class HsemawNavigator implements Navigator
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.hsemaw.hsemawNavigator");
   
   public ASPNavigatorNode add(ASPManager mgr)
   {
      if(DEBUG) Util.debug("!!ifs.hsemaw.hsemawNavigator!!");
      
      ASPNavigatorNode hsemaw_nav = mgr.newASPNavigatorNode("HSEMAWNAVIGATOR: Health Safe Environment");
      hsemaw_nav.addItem("HSEMAWNAVIGATORHSEBASEDATALEC: Hse Base Data Lec", "hsemaw/HseBaseDataLec.page");
      hsemaw_nav.addItem("HSEMAWNAVIGATORHEALTHSAFEENVIRONMENT: Hse Organization", "hsemaw/HseOrganization.page");
      hsemaw_nav.addItem("HSEMAWNAVIGATORHSEDOCUMENT: Hse Document", "hsemaw/HseDocument.page"); 
      ASPNavigatorNode n = hsemaw_nav.addNode(mgr.translate( "HSEMAWNAVIGATORDANGERFACTOR: Danger Factor" ));
      n.addItem("HSEMAWNAVIGATORDANGERSOURE: Danger Soure", "hsemaw/HseDangerSource.page");
      n.addItem("HSEMAWNAVIGATORENVIRONMENTFACTOR: Environment Factor", "hsemaw/HseEnvFactor.page");
      n.addItem("HSEMAWNAVIGATORMANAGEMENTPLAN: Management Plan", "hsemaw/HseManPlan.page");
      hsemaw_nav.addItem("HSEMAWNAVIGATORHSEEMERGENCYPLAN: Hse Emergency Plan", "hsemaw/HseEmergencyPlan.page");
      hsemaw_nav.addItem("HSEMAWNAVIGATORHSESAFETYACTIVITY: Hse Safety Activity", "hsemaw/HseSafetyActivity.page");  
      hsemaw_nav.addItem("HSEMAWNAVIGATORHSEACCIDENTMEASURE: Hse Accident Measure", "hsemaw/HseAccidentMeasure.page");
      hsemaw_nav.addItem("HSEMAWNAVIGATORHSESAFETYTRAINING: Hse Safety Training", "hsemaw/HseSafetyTraining.page");
      hsemaw_nav.addItem("HSEMAWNAVIGATORHSEMEASUREPLAN: Hse Measure Plan", "hsemaw/HseMeasurePlan.page");
      hsemaw_nav.addItem("HSEMAWNAVIGATORHSESPECIALSAFEMAN: Hse Special Safe Man", "hsemaw/HseSpecialSafeMan.page");
      hsemaw_nav.addItem("HSEMAWNAVIGATORHSECHECK: Hse Check", "hsemaw/HseCheck.page");
      hsemaw_nav.addItem("HSEMAWNAVIGATORHSEAGREEMENT: Hse Agreement", "hsemaw/HseAgreement.page");
      ASPNavigatorNode acc = hsemaw_nav.addNode("HSEMAWNAVGATORHSEACCIDENT: Hse Accident");
      acc.addItem("HSEMAWNAVGATORHSEACCIDENTREPORT: Hse Accident Report","hsemaw/HseAccident.page");
      acc.addItem("HSEMAWNAVGATORHSEACCIDENTHANDLEPLAN: Hse Accident Handle Plan","hsemaw/HseAccidentHandlePlan.page");
      acc.addItem("HSEMAWNAVGATORHSEACCIDENTHANDLERESULT: Hse Accident Handle Result","hsemaw/HseAccidentHandleResult.page");
      acc.addItem("HSEMAWNAVGATORHSECASUALTYACCIDENT: Hse Casualty Accident","hsemaw/HseCasualtyAccident.page");
      acc.addItem("HSEMAWNAVGATORHSEMONTHLYREPORT: Hse Monthly Report","hsemaw/HseMonthlyReport.page");
      hsemaw_nav.addItem("HSEPERSONNELMAN: Hse Personnel Man", "hsemaw/HsePersonnelMan.page");
      hsemaw_nav.addItem("CONMAWNAVIGATORCONSPECIALPERSONCHECK: Con Special Person Check", "conmaw/ConSpecialPersonCheck.page");
      hsemaw_nav.addItem("HSEPROFESSIONHEALTH: Hse Profession Health", "hsemaw/HseProfessionHealth.page");
      hsemaw_nav.addItem("HSECHECKNOTICE: Hse Check Notice", "hsemaw/HseCheckNotice.page");
      hsemaw_nav.addItem("HSESAFETYTESTREPORT: Hse Safety Test Report", "hsemaw/HseSafetyTestReport.page");
      hsemaw_nav.addItem("HSELARGEEQUMAN: Hse Large Equ Man", "hsemaw/HseLargeEquMan.page");
      hsemaw_nav.addItem("HSELARGEEQUREVAPPL: Hse Large Equ Rev Appl", "hsemaw/HseLargeEquRevAppl.page");
      hsemaw_nav.addItem("HSELARGEMACADMEXAM: Hse Large Mac Adm Exam", "hsemaw/HseLargeMacAdmExam.page");
      hsemaw_nav.addItem("HSELARGEMACREVEXAM: Hse Large Mac Rev Exam", "hsemaw/HseLargeMacRevExam.page");
      hsemaw_nav.addItem("HSESAFETYEQULDGER: Hse Safety Equ Ldger", "hsemaw/HseSafetyEquLdger.page");
      hsemaw_nav.addItem("HSEFIREFACMANREG: Hse Fire Fac Man Reg", "hsemaw/HseFireFacManReg.page");
      hsemaw_nav.addItem("HSEFLOODMAN: Hse Flood Man", "hsemaw/HseFloodMan.page");
      hsemaw_nav.addItem("HSESAFETYDISCLOSURE: Hse Safety Disclosure", "hsemaw/HseSafetyDisclosure.page");
      hsemaw_nav.addItem("HSESAFETYAWARD: Hse Safety Award", "hsemaw/HseSafetyAward.page");
      hsemaw_nav.addItem("HSESAFETYDEPOSIT: Hse Safety Deposit", "hsemaw/HseSafetyDeposit.page");
      hsemaw_nav.addItem("HSESPEDEVREG: Hse Spe Dev Reg", "hsemaw/HseSpeDevReg.page");
      hsemaw_nav.addItem("HSESPEOPEPERREG: Hse Spe Ope Per Reg", "hsemaw/HseSpeOpePerReg.page");
      hsemaw_nav.addItem("HSESAFEADJUSTNOTICE: Hse Safe Adjust Notice", "conmaw/HseSafeConAdjustNotice.page");
      hsemaw_nav.addItem("HSESAFEADJUSTREPLY: Hse Safe Adjust Reply", "conmaw/HseSafeConAdjustReply.page");
      hsemaw_nav.addItem("HSEFULLSECPEREXAM: Hse Full Sec Per Exam", "hsemaw/HseFullSecPerExam.page");
      hsemaw_nav.addItem("HSESECQUAEXAM: Hse Sec Qua Exam", "hsemaw/HseSecQuaExam.page");
      hsemaw_nav.addItem("HSEMEASUREEXAM: Hse Measure Exam", "hsemaw/HseMeasureExam.page");
      return hsemaw_nav;                                       
   }  
}
