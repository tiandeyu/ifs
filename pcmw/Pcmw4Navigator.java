package ifs.pcmw;

import ifs.fnd.asp.*;

public class Pcmw4Navigator implements Navigator
{
   public ASPNavigatorNode add(ASPManager mgr)
   {
      ASPNavigatorNode nod = mgr.newASPNavigatorNode("MROMAINTENANCE: MRO Maintenance");

      nod.addItem("MROSHOPVISIT: MRO Shop Visit","vimmrw/CustomerVisitDefaults.page","CUSTOMER_VISIT_DEFAULTS");
      nod.addItem("MROWOROKORDER: MRO Work Order","srvagw/ActiveSeparate.page","ACTIVE_SEPARATE");
      nod.addItem("MROTASKSUMMERY: Task Summary","vimmrw/TaskSummary.page","VIM_TASK_SUMMARY");
      nod.addItem("CHANGEWORKSCOPETASK: Change Work Scope Tasks","vimmrw/ChangeTaskSummary.page","CHANGE_TASK_SUMMARY");
      nod.addItem("OVWPOSITIONPART: Overview - Position Parts", "vimmrw/PositionPartsOvw.page", "POSITION_PART_REFERENCE");
      nod.addItem("SERIALSTRUCTURESTEMPLPREFORMANU: Serial Structure Templates Prepared For Manufacturing","vimmrw/SerialStructTempTransToManu.page","SERIAL_STRUCTURE_TOP_PART4");
      nod.addItem("STRUCTURESNAPSHOTFROMVISITS: Structure Snapshot from Visits","vimmrw/StructureSnapshotFromVisit.page","STRUCTURE_SNAPSHOTS_FROM_VISIT");
      
      ASPNavigatorNode a = nod.addNode("CONFIGCONTROLMRO: Configuration Controlled MRO");
      a.addItem("OVWPOSITIONPART: Overview - Position Parts", "vimmrw/PositionPartsOvw.page", "POSITION_PART_REFERENCE");
      a.addItem("STRUCTTMPLPREFORMANU: Serial Structure Templates Prepared For Manufacturing","vimmrw/SerialStructTempTransToManu.page","SERIAL_STRUCTURE_TOP_PART4");
      a.addItem("PRELIMCONFLOG: Overview - Preliminary Conformance Log","vimmrw/PrelimConfLog.page","CONFORMANCY_PRELIMINARY_LOG");      


      return nod;
   }

}