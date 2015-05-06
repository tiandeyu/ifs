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
*  File        : Pcmw1Navigator.java 
*  Created     : 
*  Modified    : 
*  ARWILK  040907  Added folder(Node) 'Standard Job'(IID AMEC111A: Std Jobs as PM Templates)
*  NIJALK  041129  Added node "Job Program" and its component.
*  NIJALK  041223  Added window "PM for Object Structure" to "PM Action" node.
*  Chanlk  050131  merged bug 46267.
*  Chanlk  050131  merged bug 50031.
*  NIJALK  050329  Added window "Add Job Program to Object" to "Job Program" node.
*  NIJALK  051110  Modified refering page of "PM for Object Structure".
*  NIJALK  060221  Renamed "Agreement Invoicing" to "Invoicing of Fixed Price Agreements" and
*                  "Copy PM Actions" to "Copy PM Actions between Agreements".
*  ASSALK  081014  bug 77272. Added wizard PM administrator.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;

public class Pcmw1Navigator implements Navigator
{
	public ASPNavigatorNode add(ASPManager mgr)
	{
		ASPNavigatorNode nod = mgr.newASPNavigatorNode("PREMAINT: Preventive Maintenance");

		ASPNavigatorNode m = nod.addNode("STDJOB: Standard Job");
		m.addItem("STDJOBOVW: Overview - Standard Jobs","pcmw/StandardJobOvw.page","STANDARD_JOB");
		m.addItem("SEPSTDJOB: Separate Standard Job","pcmw/SeparateStandardJob2.page","SEPARATE_STANDARD_JOB");
		m.addItem("ROUSTDJOB: Route Standard Job","pcmw/RoundStandardJob.page","ROUND_STANDARD_JOB");
                m.addItem("REPLACEOBSSTDJOBS: Replace Obsolete Standard Jobs on PM Actions","pcmw/ReplaceObsoleteStdJobsDlg.page");

		m = nod.addNode("JOBPROGRAM: Job Program");
		m.addItem("JOBPROG: Job program","pcmw/JobProgram.page","JOB_PROGRAM,STANDARD_JOB_PROGRAM");
		m.addItem("JOBPROGTOOBJ: Add Job Program to Object","pcmw/JobProgToObjMain.page","SIMULATED_STD_JOB,SIMULATED_PM_ACTION");

		m = nod.addNode("PMACTS: PM Actions");
		m.addItem("PMFORSTRUCT: PM for Object Structure","pcmw/PMObjectStructure.page","PM_ACTION,MAINTENANCE_OBJECT,PM_ACTION_CALENDAR_PLAN");
		m.addItem("SEPPM: Separate PM Action", "pcmw/PmAction.page","PM_ACTION");
		m.addItem("PMROUND: PM Actions for Route List","pcmw/PMRoundDefinition.page","PM_ROUND_DEFINITION");
		m.addItem("ROUND: Route PM Action","pcmw/PmActionRound.page","PM_ACTION");
		// Bug 77272, start
		m.addItem("PMADMIN: PM Administrator","pcmw/PmAdministratorWiz.page","PM_ACTION");
		// Bug 77272, end

                m = nod.addNode("CALPLAN: Calendar Plan");
		m.addItem("CALPLANOVW: Overview - Calendar Plan","pcmw/CalendarPlanOvw.page","PM_CALENDAR_PLAN");
		m.addItem("CALGEN: Calendar Generation","pcmw/CalendarGenerationDlg.page","MODULE");
		m.addItem("EXTPLAN: Extend Plan","pcmw/ExtendPlanDlg.page");

		m = nod.addNode( "ENTPLAN: Event Plan" );
		m.addItem("ENTPLANOVW: Overview - Event Plan","pcmw/DummyWNDPmActionOvw2.page");
		m.addItem("ENTGEN: Event Generation","pcmw/EventGenerationDlg.page");  

		m = nod.addNode( "PARAM: Condition Based" );   
		m.addItem("MEASUOVW: Overview - Measurements","equipw/EquipmentObjectMeasOvw.page","EQUIPMENT_OBJECT_MEAS");
		m.addItem("NAVCONDGEN: Condition Generation","pcmw/CriteriaGeneration.page","MODULE");

		return nod; 
	}
}