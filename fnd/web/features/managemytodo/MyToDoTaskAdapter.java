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
 *  File        : MyToDoTaskAdapter.java
 *  Modified    :
 * ----------------------------------------------------------------------------
 *  $Log: MyToDoTaskAdapter.java,v $
 *  BUHILK      2010-SEP-21       Bug id: 93074, Modified adapter behavior to omit unnessary query results.
 *  BUHILK      2010-May-31       Bug id: 90640, Modified getPersons() to replace personID attribute with userId and add extra search commands.
 *  BUHILK      2010-May-07       Bug id: 90503, Modified getPersons(), excluded all attributes except personid and name from query.
 *  BUHILK      2009-Feb-27       Bug id: 80960, Added Missing functionality for reassigning & sharing tasks.
 *  BUHILK      2008-Oct-08       Bug id: 77648, Modified setFollowUpFlag() to clear flag when null.
 *  BUHILK      2008-Aug-15       Bug id: 76288, Added getQueryRecord(), countRelatedMyTodo() and setMaxRowCount() to
 *                                implement context task pane for Todo items.
 *  BUHILK      2008-Jul-09       Created.
 */
package ifs.fnd.web.features.managemytodo;

import ifs.fnd.asp.*;
import ifs.fnd.base.*;
import ifs.fnd.record.*;
import ifs.fnd.service.FndException;
import ifs.fnd.util.Str;
import java.util.StringTokenizer;
import ifs.application.mytodoitem.*;
import ifs.application.managemytodo.*;
import ifs.application.todoitem.TodoItem;
import ifs.application.todofolder.TodoFolder;
import ifs.application.personinfo.PersonInfo;
import ifs.fnd.webfeature.FndSaveDataAdapter;
import ifs.fnd.webfeature.FndQueryDataAdapter;
import ifs.fnd.internal.FndAttributeInternals;
import ifs.application.personinfo.PersonInfoArray;
import ifs.application.todofolder.TodoFolderArray;
import ifs.application.enumeration.TodoFlagEnumeration;
import ifs.application.enumeration.TodoPriorityEnumeration;
import ifs.application.enumeration.TodoFolderTypeEnumeration;

/**
 * Adaptor for the manage MyTodo webfeature.
 */
public class MyToDoTaskAdapter implements FndQueryDataAdapter, FndSaveDataAdapter {

    private int max_rows = -1;

    /**
     * Creates a new instance of MyToDoTaskAdapter
     */
    public MyToDoTaskAdapter() {
    }

    //==============================================================
    // Adaptor implementation
    //==============================================================
    /**
     * Returns the MyTodo item used as the template.
     * @return MyToDo item template
     */
    public FndAbstractRecord getTemplate() {
        MyTodoItem mytodo_item = new MyTodoItem();

        mytodo_item.excludeQueryResults();
        mytodo_item.objVersion.include();
        mytodo_item.itemId.include();
        mytodo_item.identity.include();
        mytodo_item.folderId.include();
        mytodo_item.read.include();
        mytodo_item.sentBy.include();
        mytodo_item.dateReceived.include();
        mytodo_item.dueDate.include();
        mytodo_item.flag.include();

        mytodo_item.item.include();
        mytodo_item.item.getRecord().excludeQueryResults();
        mytodo_item.item.getRecord().priority.include();
        mytodo_item.item.getRecord().shared.include();
        mytodo_item.item.getRecord().url.include();
        mytodo_item.item.getRecord().title.include();
        mytodo_item.item.getRecord().itemMessage.include();
        mytodo_item.item.getRecord().businessObject.include();

        mytodo_item.sender.include();
        mytodo_item.sender.getRecord().excludeQueryResults();
        mytodo_item.sender.getRecord().name.include();

        return mytodo_item;
    }

    /**
     * Used by the framework to count the number of records retrieved.
     * @param record Query condition record
     * @param page Current ASPPage
     * @return number of records retrieved.
     */
    public int count(FndQueryRecord record, ASPPage page) {
        FndAbstractArray arr = query(record, page);
        if (arr != null) {
            return arr.getLength();
        }
        return 0;
    }

    /**
     * Used by the framework to query data from the view.
     * @param record Query condition record
     * @param page Current ASPPage
     * @return An array containing the query results.
     */
    public FndAbstractArray query(FndQueryRecord record, ASPPage page) {
        try {
            String folderType = page.getASPContext().readValue("MyTodoFolderType", page.getASPManager().readValue("MyTodoFolderType"));
            if (Str.isEmpty(folderType)) {
                folderType = TodoFolderTypeEnumeration.TODO.toString();
            }

            ManageMyTodo manage_mytodo = ManageMyTodoFactory.getHandler();

            TodoFolder folder = new TodoFolder();
            FndAbstractRecord folderRec = folder.newInstance();
            TodoFolderArray folderArr = manage_mytodo.initializeFolders(new FndQueryRecord(folderRec));
            String folderid = "";
            for (int i = 0; i < folderArr.getLength(); i++) {
                folderRec = FndAttributeInternals.internalGet(folderArr, i);
                if (folderRec.getAttribute(folder.folderType.getName()).toString().equals(folderType)) {
                    folderid = folderRec.getAttribute(folder.folderId.getName()).toString();
                    break;
                }
            }
            record.addCondition(new MyTodoItem().folderId.createEqualCondition(folderid));
            if (max_rows > -1) {
                record.maxRows.setValue(max_rows);
            } else {
                record.maxRows.clear();
            }
            max_rows = -1;
            FndAbstractArray resultSet = manage_mytodo.queryMyTodoItem(record);

            //Sort by date received
            String sortStr = FndSort.formatOrderByClause(getTemplate(), new FndSortField[]{new MyTodoItem().dateReceived.descending()});
            resultSet.sort(sortStr);

            return resultSet;
        } catch (SystemException ex) {
            ex.printStackTrace();
        } catch (IfsException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Used by the framework to save data to the view.
     * @param record data record
     * @param page Current ASPPage
     * @return the saved record results.
     */
    public FndAbstractRecord save(FndAbstractRecord record, ASPPage page) {
        try {
            ManageMyTodo manage_mytodo = ManageMyTodoFactory.getHandler();
            manage_mytodo.saveMyTodoItem((MyTodoItem) record);
            return record;
        } catch (SystemException ex) {
            ex.printStackTrace();
        } catch (IfsException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Used by the framework to remove data from the view.
     * @param record data record
     * @param page Current ASPPage
     * @return the removed record results.
     */
    public FndAbstractRecord remove(FndAbstractRecord record, ASPPage page) {
        try {
            ManageMyTodo manage_mytodo = ManageMyTodoFactory.getHandler();
            manage_mytodo.removeMyTodoItem((MyTodoItem) record);
            return record;
        } catch (SystemException ex) {
            ex.printStackTrace();
        } catch (IfsException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public FndQueryRecord getQueryRecord(ASPBlock block) {
        MyTodoItem mytodo_item = (MyTodoItem) getTemplate();

        TodoItem todo_item = new TodoItem();
        todo_item.excludeQueryResults();
        todo_item.priority.include();
        todo_item.shared.include();
        todo_item.url.include();
        todo_item.title.include();
        todo_item.itemMessage.include();
        todo_item.businessObject.include();

        PersonInfo sender = new PersonInfo();
        sender.excludeQueryResults();
        sender.name.include();

        block.getASPManager().getConditions(todo_item, "ITEM", block);
        block.getASPManager().getConditions(sender, "SENDER", block);
        block.getASPManager().getConditions(mytodo_item, null, block);

        FndDetailCondition _itemCondition = mytodo_item.item.createDetailCondition(todo_item, FndQueryReferenceCategory.EXISTS_IN);
        if (_itemCondition != null) {
            mytodo_item.addCondition(_itemCondition);
        }

        FndDetailCondition _senderCondition = mytodo_item.sender.createDetailCondition(sender, FndQueryReferenceCategory.EXISTS_IN);
        if (_senderCondition != null) {
            mytodo_item.addCondition(_senderCondition);
        }

        return new FndQueryRecord(mytodo_item);
    }

    // =====================================================================================================
    //                                  Non-abstract implementations
    // =====================================================================================================
    /* Set the max row count for a query.
     * @param max_rows Max row count. (-1: unlimited)
     */
    void setMaxRowCount(int max_rows) {
        this.max_rows = max_rows;
    }

    /**
     * Retrieves a single MyTodoItem
     * @param itemId id of the todo item
     * @param identity userid.directoryid of the owner
     * @return MyTodoItem matching the criteria
     */
    MyTodoItem getMyTodoItem(String itemId, String identity) {
        MyTodoItem todo_item = (MyTodoItem) getTemplate();
        try {
            todo_item.itemId.setValue(itemId);
            todo_item.identity.setValue(identity);
            ManageMyTodo manage_mytodo = ManageMyTodoFactory.getHandler();
            todo_item = manage_mytodo.getMyTodoItem(todo_item);
        } catch (SystemException ex) {
            ex.printStackTrace();
        } catch (IfsException ex) {
            ex.printStackTrace();
        }
        return todo_item;
    }

    /**
     * Saves a new MyTodoItem
     * @param postItem New item to be saved
     * @return true if saved sucessfully, false otherwise.
     */
    boolean postNewItem(PostNewItem postItem) {
        try {
            ManageMyTodo manage_mytodo = ManageMyTodoFactory.getHandler();
            manage_mytodo.postNewItem(postItem);
            return true;
        } catch (SystemException ex) {
            ex.printStackTrace();
        } catch (IfsException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * ReAssigns a MyTodoItem
     * @param _item item to be reassigned.
     * @param _receivers New item receivers.
     * @return true if saved sucessfully, false otherwise.
     */
    boolean reassignItem(MyTodoItem _item, PersonInfoArray _receivers) throws FndException {
        try {
            ManageMyTodo manage_mytodo = ManageMyTodoFactory.getHandler();
            manage_mytodo.reassign(_item, _receivers);
            return true;
        } catch (SystemException ex) {
            ex.printStackTrace();
        } catch (IfsException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Share a MyTodoItem
     * @param _item item to be shared.
     * @param _receivers New item receivers.
     * @return true if saved sucessfully, false otherwise.
     */
    boolean shareItem(MyTodoItem _item, PersonInfoArray _receivers) throws FndException {
        try {
            ManageMyTodo manage_mytodo = ManageMyTodoFactory.getHandler();
            manage_mytodo.share(_item, _receivers);
            return true;
        } catch (SystemException ex) {
            ex.printStackTrace();
        } catch (IfsException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Set the Followup flag for the given MyTodoItem
     * @param item MyTodoItem to set the flag
     * @param flag Followup flag to be set.
     * @return true if saved sucessfully, false otherwise.
     */
    boolean setFollowUpFlag(MyTodoItem item, TodoFlagEnumeration.Enum flag) {
        try {
            FndAbstractRecord record = getTemplate().newInstance();
            record.excludeQueryResults();
            record.getAttribute(item.itemId).include();
            record.getAttribute(item.identity).include();
            record.getAttribute(item.flag).include();
            record.getAttribute(item.item).include();
            record.addCondition(item.itemId.createEqualCondition(item.itemId.getValue()));
            record.addCondition(item.identity.createEqualCondition(item.identity.getValue()));
            ManageMyTodo manage_mytodo = ManageMyTodoFactory.getHandler();
            MyTodoItemArray array = manage_mytodo.queryMyTodoItem(new FndQueryRecord(record));
            for (int i = 0; i < array.getLength(); i++) {
                MyTodoItem _item = array.get(i);
                _item.flag.setValue(flag);
                manage_mytodo.saveMyTodoItem(_item);
            }
            return true;
        } catch (SystemException ex) {
            ex.printStackTrace();
        } catch (IfsException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Set the Priority for the given MyTodoItem
     * @param item MyTodoItem to set the priority
     * @param priority to be set.
     * @return true if saved sucessfully, false otherwise.
     */
    boolean setPriority(MyTodoItem item, TodoPriorityEnumeration.Enum priority) {
        try {
            FndAbstractRecord record = getTemplate().newInstance();
            record.excludeQueryResults();
            record.getAttribute(item.itemId).include();
            record.getAttribute(item.identity).include();
            record.getAttribute(item.item).include();
            record.addCondition(item.itemId.createEqualCondition(item.itemId.getValue()));
            record.addCondition(item.identity.createEqualCondition(item.identity.getValue()));
            ManageMyTodo manage_mytodo = ManageMyTodoFactory.getHandler();
            MyTodoItemArray array = manage_mytodo.queryMyTodoItem(new FndQueryRecord(record));
            for (int i = 0; i < array.getLength(); i++) {
                MyTodoItem _item = array.get(i);
                _item.item().priority.setValue(priority);
                manage_mytodo.saveMyTodoItem(_item);
            }
            return true;
        } catch (SystemException ex) {
            ex.printStackTrace();
        } catch (IfsException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Retrive a list of shared users
     * @param itemid Id of the shared MyTodoItem
     * @return String array of user names.
     */
    String[] getSharedUsers(String itemid) {
        String users[] = null;

        try {
            ManageMyTodo manage_mytodo = ManageMyTodoFactory.getHandler();
            PersonInfoArray array = manage_mytodo.querySharedUsers(itemid);
            users = new String[array.getLength()];
            for (int i = 0; i < array.getLength(); i++) {
                PersonInfo _user = array.get(i);
                users[i] = _user.name.getValue();
            }
        } catch (SystemException ex) {
            ex.printStackTrace();
        } catch (IfsException ex) {
            ex.printStackTrace();
        }
        return (users == null) ? new String[0] : users;
    }

    /**
     * Set the read value for the given MyTodoItem
     * @param item MyTodoItem to set the read value
     * @return true if saved sucessfully, false otherwise.
     */
    boolean setRead(MyTodoItem item) {
        try {
            MyTodoItem my_todo_item = new MyTodoItem();
            my_todo_item.excludeQueryResults();
            my_todo_item.folderId.include();
            my_todo_item.itemId.include();
            my_todo_item.identity.include();
            my_todo_item.item.getRecord().excludeQueryResults();
            my_todo_item.item.getRecord().shared.include();
            my_todo_item.read.include();

            my_todo_item.addCondition(my_todo_item.itemId.createEqualCondition(item.itemId.getValue()));
            my_todo_item.addCondition(my_todo_item.identity.createEqualCondition(item.identity.getValue()));
            ManageMyTodo manage_mytodo = ManageMyTodoFactory.getHandler();
            MyTodoItemArray array = manage_mytodo.queryMyTodoItem(new FndQueryRecord(my_todo_item));
            for (int i = 0; i < array.getLength(); i++) {
                MyTodoItem _item = array.get(i);
                _item.read.setValue(item.read.booleanValue(false));
                manage_mytodo.saveMyTodoItem(_item);
            }
            return true;
        } catch (SystemException ex) {
            ex.printStackTrace();
        } catch (IfsException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieve a list of PersonInfo objects that contain a set of identities.
     * @param identityList comma/semicolan separated list of identites
     * @return a PersonInfoArray
     */
    PersonInfoArray getPersons(String identityList) {
        PersonInfo person = new PersonInfo();
        FndAbstractRecord record = person.newInstance();
        record.excludeQueryResults();
        record.getAttribute(person.personId).include();
        record.getAttribute(person.userId).include();
        record.getAttribute(person.name).include();
        record.getAttribute(person.firstName).include();
        record.getAttribute(person.lastName).include();
        PersonInfoArray ppl = null;
        FndQueryRecord query = new FndQueryRecord(record);

        try {
            ManageMyTodo manage_mytodo = ManageMyTodoFactory.getHandler();
            if (Str.isEmpty(identityList)) {
                return null;
            }
            StringTokenizer st = new StringTokenizer(identityList, ",;");
            boolean or = true;
            FndCondition con = null;
            while (st.hasMoreTokens()) {
                String tmp = st.nextToken().toString().trim();
                or = !or;
                if (or) {
                    con = con.or(person.userId.createEqualIgnoreCaseCondition(tmp));
                } else {
                    con = person.userId.createEqualIgnoreCaseCondition(tmp);
                }

                con = con.or(person.name.createLikeIgnoreCaseCondition(tmp));
                con = con.or(person.firstName.createLikeIgnoreCaseCondition(tmp));
                con = con.or(person.lastName.createLikeIgnoreCaseCondition(tmp));

                or = false;
            }
            query.addCondition(con);
            ppl = manage_mytodo.queryUserPersons(query);
        } catch (SystemException ex) {
            ex.printStackTrace();
        } catch (IfsException ex) {
            ex.printStackTrace();
        }
        return ppl;
    }

    public int countRelatedMyTodo(String lu_name, ASPPage page) {
        if (lu_name == null) {
            return 0;
        }

        MyTodoItem mytodo_item = new MyTodoItem();
        mytodo_item.excludeQueryResults();
        mytodo_item.folderId.include();
        mytodo_item.itemId.include();
        mytodo_item.identity.include();
        mytodo_item.item.include();
        mytodo_item.item.getRecord().excludeQueryResults();
        mytodo_item.item.getRecord().businessObject.include();

        TodoItem todo_item = new TodoItem();
        todo_item.excludeQueryResults();
        todo_item.businessObject.include();
        todo_item.addCondition(todo_item.businessObject.createEqualIgnoreCaseCondition(lu_name));

        FndDetailCondition busness_obj_cond = mytodo_item.item.createDetailCondition(todo_item, FndQueryReferenceCategory.EXISTS_IN);
        if (busness_obj_cond != null) {
            mytodo_item.addCondition(busness_obj_cond);
        } else {
            return -1;
        }

        return count(new FndQueryRecord(mytodo_item), page);
    }
}
