/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pabloid.visualstudio;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import org.pabloid.io.*;
import java.io.*;
import org.pabloid.editor.Editor;
import org.pabloid.ui.flow.*;
import org.pabloid.visualstudio.statements.*;

/**
 * @author P@bloid
 */
public class VisualStudioME extends MIDlet implements CommandListener, ItemStateListener
  {

    private boolean midletPaused = false;
    private JavaCodeGenerator cg = null;
    private Adder adder = null;
    private TextField tfMethodName = new TextField("Method Name", "", 32, TextField.ANY),
            tfMethodType = new TextField("Method type", "", 32, TextField.ANY),
            tfMethodParams = new TextField("Params", "", 512, TextField.ANY);
    public static Display display;
    private java.util.Hashtable __previousDisplayables = new java.util.Hashtable();
    //#ifdef LITE
    //#private Form fMainForm;
    //#else
    private FlowCanvas fMainForm;
    //#endif
    private StringItem siCodeGenerator;
    private Form newProjectForm;
    private ChoiceGroup cgClassType;
    private TextField tfName;
    private ChoiceGroup cgOptions;
    private List lAdd;
    private List lAddDisplayable;
    private List lAddItem;
    private List lAddOther;
    private Editor tbEditor;
    private List lObjects;
    private List lOptions;
    private Form fEdit;
    private TextField tType;
    private TextField tName;
    private TextField tInit;
    private List lMethods;
    private TextBox tbEdit;
    private Form fMethodEdit;
    private StatementForm tbAddExpression;
    private List lStatements;
    private TextBox tbSave;
    private Form fAbout;
    private StringItem siAbout;
    private ImageItem iiAbout;
    private StatementForm tbStatement;
    private List lAddExpression;
    private Form fWait;
    private Gauge gProcess;
    private Command cmdNewProject;
    private Command cmdConfirm;
    private Command cmdExit;
    private Command cmdBack;
    private Command cmdAdd;
    private Command cmdViewCode;
    private Command cmdObjects;
    private Command cmdSettings;
    private Command cmdEdit;
    private Command cmdOptions;
    private Command cmdMethods;
    private Command cmdSave;
    private Command cmdAbout;
    private Image iSplashScreen;
    private Font fntAbout;

    /**
     * The VisualStudioME constructor.
     */
    public VisualStudioME()
      {
        display = getDisplay();
      }

    void refresh()
      {
        try
          {
            //#ifndef LITE
            fMainForm.getItem(0).set(cg.getFieldsHeaders());
            fMainForm.getItem(1).set(cg.getMethodsHeaders());
            fMainForm.repaint();
            //#else
//# 			siCodeGenerator.setText(cg.list());
            //#endif
          }
        catch (Throwable t)
          {
          }
      }

    void edit(int a, int b)
      {
        switch (a)
          {
            case 0:
                lObjects.deleteAll();
                Field[] f = cg.getFields();
                for (int i = 0; i < f.length; i++)
                    lObjects.append(f[i].getType().toString() + " " + f[i].getName() + ";", null);
                lObjects.setSelectedIndex(b, true);
                commandAction(cmdEdit, lObjects);
                break;
            case 1:
                lMethods.deleteAll();
                Method[] m = cg.getMethods();
                for (int i = 0; i < m.length; i++)
                    lMethods.append(m[i].getType().toString() + " " + m[i].getName() + "(" + m[i].getParameters() + ");", null);
                lMethods.setSelectedIndex(b, true);
                commandAction(cmdOptions, lMethods);
                break;
          }
      }

    /**
     * Switches a display to previous displayable of the current displayable.
     * The <code>display</code> instance is obtain from the <code>getDisplay</code> method.
     */
    private void switchToPreviousDisplayable()
      {
        Displayable __currentDisplayable = getDisplay().getCurrent();
        if (__currentDisplayable != null)
          {
            Displayable __nextDisplayable = (Displayable) __previousDisplayables.get(__currentDisplayable);
            if (__nextDisplayable != null)
                switchDisplayable(null, __nextDisplayable);
          }
      }

    /**
     * Initilizes the application.
     * It is called only once when the MIDlet is started. The method is called before the <code>startMIDlet</code> method.
     */
    private void initialize()
      {

        siCodeGenerator = new StringItem("", null, Item.PLAIN);
        //#ifndef LITE
        fMainForm = new FlowCanvas(this);
        //#else
//# 		fMainForm = new Form("Lite");
//# 		fMainForm.append(siCodeGenerator);
        //#endif
        fMainForm.addCommand(getCmdAdd());
        fMainForm.addCommand(getCmdObjects());
        fMainForm.addCommand(getCmdMethods());
        fMainForm.addCommand(getCmdViewCode());
        fMainForm.addCommand(getCmdNewProject());
        fMainForm.addCommand(getCmdSettings());
        fMainForm.addCommand(getCmdAbout());
        fMainForm.addCommand(getCmdExit());
        fMainForm.setCommandListener(this);
        cgClassType = new ChoiceGroup("Class type", Choice.POPUP);
        cgClassType.append("MIDlet", null);
        cgClassType.append("Canvas", null);
        cgClassType.append("Thread", null);
        cgClassType.append("Class", null);
        cgClassType.append("Interface", null);
        cgClassType.append("Plugin", null);
        cgClassType.setFitPolicy(Choice.TEXT_WRAP_DEFAULT);
        cgClassType.setSelectedFlags(new boolean[]
                  {
                    true, false, false, false, false, false
                  });
        tfName = new TextField("Project name", "Main", 32, TextField.ANY);
        cgOptions = new ChoiceGroup("Options", Choice.MULTIPLE);
        cgOptions.append("Full screen", null);
        cgOptions.setFitPolicy(Choice.TEXT_WRAP_DEFAULT);
        cgOptions.setSelectedFlags(new boolean[]
                  {
                    false
                  });
        newProjectForm = new Form("New project", new Item[]
                  {
                    tfName, cgClassType, cgOptions
                  });
        newProjectForm.addCommand(getCmdConfirm());
        newProjectForm.addCommand(getCmdBack());
        newProjectForm.setCommandListener(this);
        lAdd = new List("Add", Choice.IMPLICIT);
        lAdd.append("display object", null);
        lAdd.append("component", null);
        lAdd.append("other", null);
        lAdd.addCommand(getCmdBack());
        lAdd.setCommandListener(this);
        lAdd.setSelectedFlags(new boolean[]
                  {
                    false, false, false
                  });
        lAddDisplayable = new List("Displayable", Choice.IMPLICIT);
        lAddDisplayable.append("Shape", null);
        lAddDisplayable.append("Canvas", null);
        lAddDisplayable.append("Letter", null);
        lAddDisplayable.append("Message", null);
        lAddDisplayable.append("Entry field", null);
        lAddDisplayable.addCommand(getCmdBack());
        lAddDisplayable.setCommandListener(this);
        lAddDisplayable.setSelectedFlags(new boolean[]
                  {
                    false, false, false, false, false
                  });
        lAddItem = new List("Items", Choice.IMPLICIT);
        lAddItem.append("Group selection", null);
        lAddItem.append("Date field", null);
        lAddItem.append("Scale", null);
        lAddItem.append("Image", null);
        lAddItem.append("Separator", null);
        lAddItem.append("String", null);
        lAddItem.append("Entry field", null);
        lAddItem.addCommand(getCmdBack());
        lAddItem.setCommandListener(this);
        lAddItem.setSelectedFlags(new boolean[]
                  {
                    false, false, false, false, false, false, false
                  });
        lAddOther = new List("Other", Choice.IMPLICIT);
        lAddOther.append("Command", null);
        lAddOther.append("Ticker", null);
        lAddOther.append("Thread (multithread)", null);
        lAddOther.append("Method", null);
        lAddOther.append("Image", null);
        lAddOther.append("String", null);
        lAddOther.append("Variable", null);
        lAddOther.addCommand(getCmdBack());
        lAddOther.setCommandListener(this);
        lAddOther.setSelectedFlags(new boolean[]
                  {
                    false, false, false, false, false, false, false, false
                  });
        tbEditor = new Editor(Display.getDisplay(this), "VisualStudioME");
        tbEditor.setTitle("editor");
        tbEditor.addCommand(getCmdEdit());
        tbEditor.addCommand(getCmdSave());
        tbEditor.addCommand(getCmdBack());
        tbEditor.addCommand(getCmdSettings());
        tbEditor.setCommandListener(this);
        tbEditor.setTitle("VisualStudioME");
        tbEditor.setCommandListener(this);
        lObjects = new List("Objects", Choice.IMPLICIT);
        lObjects.addCommand(getCmdBack());
        lObjects.addCommand(getCmdEdit());
        lObjects.setCommandListener(this);
        lObjects.setSelectCommand(getCmdEdit());
        lOptions = new List("Options", Choice.IMPLICIT);
        lOptions.append("Editing", null);
        lOptions.append("Add expression", null);
        lOptions.append("Experssions", null);
        lOptions.addCommand(getCmdBack());
        lOptions.setCommandListener(this);
        lOptions.setFitPolicy(Choice.TEXT_WRAP_DEFAULT);
        lOptions.setSelectedFlags(new boolean[]
                  {
                    false, false, false
                  });
        tType = new TextField("Type", null, 32, TextField.ANY);
        tName = new TextField("Name", null, 32, TextField.ANY);
        tInit = new TextField("String initailization", null, 4096, TextField.ANY);
        fEdit = new Form("Edit", new Item[]
                  {
                    tType, tName, tInit
                  });
        fEdit.addCommand(getCmdBack());
        fEdit.addCommand(getCmdConfirm());
        fEdit.setCommandListener(this);
        lMethods = new List("Methods", Choice.IMPLICIT);
        lMethods.addCommand(getCmdBack());
        lMethods.addCommand(getCmdOptions());
        lMethods.setCommandListener(this);
        lMethods.setSelectCommand(getCmdOptions());
        tbEdit = new TextBox("Edit", null, 1024, TextField.ANY);
        tbEdit.addCommand(getCmdBack());
        tbEdit.addCommand(getCmdConfirm());
        tbEdit.setCommandListener(this);
        fMethodEdit = new Form("Edit method", new Item[]
                  {
                  });
        fMethodEdit.addCommand(getCmdBack());
        fMethodEdit.addCommand(getCmdConfirm());
        fMethodEdit.setCommandListener(this);
        tbAddExpression = new StatementForm();
        tbAddExpression.addCommand(getCmdBack());
        tbAddExpression.addCommand(getCmdConfirm());
        tbAddExpression.setCommandListener(this);
        lStatements = new List("Statements", Choice.IMPLICIT);
        lStatements.addCommand(getCmdBack());
        lStatements.addCommand(getCmdConfirm());
        lStatements.setCommandListener(this);
        lStatements.setSelectCommand(getCmdConfirm());
        tbSave = new TextBox("Folder to save", "/e:/other/java/s/", 100, TextField.ANY);
        tbSave.addCommand(getCmdSave());
        tbSave.addCommand(getCmdBack());
        tbSave.setCommandListener(this);
        fAbout = new Form("About", new Item[]
                  {
                    getIiAbout(), getSiAbout()
                  });
        fAbout.addCommand(getCmdBack());
        fAbout.setCommandListener(this);
        tbStatement = new StatementForm();
        tbStatement.addCommand(getCmdConfirm());
        tbStatement.addCommand(getCmdBack());
        tbStatement.setCommandListener(this);
        lAddExpression = new List("Add expresion", Choice.IMPLICIT);
        lAddExpression.append("if", null);
        lAddExpression.append("for(;;){}", null);
        lAddExpression.append("while(){}", null);
        lAddExpression.append("do{}while();", null);
        lAddExpression.append("try {} catch(Throwable _throwable){}", null);
        lAddExpression.append("method();", null);
        lAddExpression.append("variable = value;", null);
        lAddExpression.append("break;", null);
        lAddExpression.append("continue;", null);
        lAddExpression.append("switch(){}", null);
        lAddExpression.addCommand(getCmdConfirm());
        lAddExpression.addCommand(getCmdBack());
        lAddExpression.setCommandListener(this);
        lAddExpression.setSelectCommand(getCmdConfirm());
        gProcess = new Gauge("Process", false, 100, 0);
        fWait = new Form("Generate source code", new Item[]
                  {
                    gProcess
                  });
        fWait.addCommand(getCmdBack());
        fWait.setCommandListener(this);
        fntAbout = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE);
        fMainForm.removeCommand(cmdAdd);
        fMainForm.removeCommand(cmdViewCode);
        fMainForm.removeCommand(cmdMethods);
        fMainForm.removeCommand(cmdObjects);
        fMainForm.removeCommand(cmdAdd);
        newProjectForm.setItemStateListener(this);
        newProjectForm.delete(2);
        //#ifndef LITE
        fMainForm.repaint();
        //#endif
        fMethodEdit.append(tfMethodType);
        fMethodEdit.append(tfMethodName);
        fMethodEdit.append(tfMethodParams);

      }

    public void startMIDlet()
      {
        switchDisplayable(null, fMainForm);

      }

    /**
     * Performs an action assigned to the Mobile Device - MIDlet Resumed point.
     */
    public void resumeMIDlet()
      {
      }

    /**
     * Switches a current displayable in a display. The <code>display</code> instance is taken from <code>getDisplay</code> method. This method is used by all actions in the design for switching displayable.
     * @param alert the Alert which is temporarily set to the display; if <code>null</code>, then <code>nextDisplayable</code> is set immediately
     * @param nextDisplayable the Displayable to be set
     */
    public void switchDisplayable(Alert alert, Displayable nextDisplayable)
      {

        Display display = getDisplay();
        Displayable __currentDisplayable = display.getCurrent();
        if (__currentDisplayable != null && nextDisplayable != null)
            __previousDisplayables.put(nextDisplayable, __currentDisplayable);
        if (alert == null)
            display.setCurrent(nextDisplayable);
        else
            display.setCurrent(alert, nextDisplayable);

      }

    /**
     * Called by a system to indicated that a command has been invoked on a particular displayable.
     * @param command the Command that was invoked
     * @param displayable the Displayable where the command was invoked
     */
    public void commandAction(Command command, Displayable displayable)
      {
        refresh();
        if (displayable == lAdd)
          {
            if (command == List.SELECT_COMMAND)
                addListAction();
            else if (command == cmdBack)
                switchDisplayable(null, fMainForm);
          }
        else if (displayable == fAbout)
          {
            if (command == cmdBack)
                switchToPreviousDisplayable();
          }
        else if (displayable == fEdit)
          {
            if (command == cmdBack)
                switchToPreviousDisplayable();
            else if (command == cmdConfirm)
              {
                Field f = cg.getField(lObjects.getSelectedIndex());
                String name = f.getName();
                f.setInit(tInit.getString());
                f.setName(tName.getString());
                f.setType(new Type(tType.getString()));
                refresh();
                cg.rename(name, tName.getString());

                switchDisplayable(null, fMainForm);

              }
          }
        else if (displayable == fMainForm)
          {
            if (command == cmdAbout)
                switchDisplayable(null, fAbout);
            else if (command == cmdAdd)
              {
                if ("MIDlet".equals(cg.getType()))
                    switchDisplayable(null, lAdd);
              }
            else if (command == cmdExit)
                exitMIDlet();
            else if (command == cmdMethods)
              {
                lMethods.deleteAll();
                Method[] f = cg.getMethods();
                for (int i = 0; i < f.length; i++)
                    lMethods.append(f[i].getType().toString() + " " + f[i].getName() + "(" + f[i].getParameters() + ");", null);
                switchDisplayable(null, lMethods);

              }
            else if (command == cmdNewProject)
                getDisplay().setCurrent(newProjectForm);
            else if (false)
                switchDisplayable(null, newProjectForm);
            else if (command == cmdObjects)
              {
                lObjects.deleteAll();
                Field[] f = cg.getFields();
                for (int i = 0; i < f.length; i++)
                    lObjects.append(f[i].getType().toString() + " " + f[i].getName() + ";", null);

                switchDisplayable(null, lObjects);

              }
            else if (command == cmdSettings)
                tbEditor.showSettings();
            else if (command == cmdViewCode)
              {
                tbEditor.setString(cg.toString());
                switchDisplayable(null, tbEditor);

              }
          }
        else if (displayable == fMethodEdit)
          {
            if (command == cmdBack)
                switchToPreviousDisplayable();
            else if (command == cmdConfirm)
              {
                Method m = cg.getMethod(lMethods.getSelectedIndex());
                cg.rename(m.getName(), tName.getString());
                m.setName(tfMethodName.getString());
                m.setType(new Type(tfMethodType.getString()));
                m.setParameters(tfMethodParams.getString());
                refresh();
                lMethods.deleteAll();
                Method[] f = cg.getMethods();
                for (int i = 0; i < f.length; i++)
                    lMethods.append(f[i].getType().toString() + " " + f[i].getName() + "(" + f[i].getParametersWithoutNames() + ");", null);
                switchDisplayable(null, lOptions);

              }
          }
        else if (displayable == fWait)
          {
            if (command == cmdBack)
                switchToPreviousDisplayable();
          }
        else if (displayable == lAddDisplayable)
          {
            if (command == List.SELECT_COMMAND)
                lAddDisplayableAction();
            else if (command == cmdBack)
                switchDisplayable(null, lAdd);
          }
        else if (displayable == lAddExpression)
          {
            if (command == List.SELECT_COMMAND)
                lAddExpressionAction();
            else if (command == cmdBack)
                switchDisplayable(null, lOptions);
            else if (command == cmdConfirm)
              {
                tbAddExpression.setStatement(lAddExpression.getSelectedIndex());
                switchDisplayable(null, tbAddExpression);

              }
          }
        else if (displayable == lAddItem)
          {
            if (command == List.SELECT_COMMAND)
                lAddItemAction();
            else if (command == cmdBack)
                switchDisplayable(null, lAdd);
          }
        else if (displayable == lAddOther)
          {
            if (command == List.SELECT_COMMAND)
                lAddOtherAction();
            else if (command == cmdBack)
                switchDisplayable(null, lAdd);
          }
        else if (displayable == lMethods)
          {
            if (command == List.SELECT_COMMAND)
                lMethodsAction();
            else if (command == cmdBack)
                switchDisplayable(null, fMainForm);
            else if (command == cmdOptions)
                switchDisplayable(null, lOptions);
          }
        else if (displayable == lObjects)
          {
            if (command == List.SELECT_COMMAND)
                lObjectsAction();
            else if (command == cmdBack)
                switchDisplayable(null, fMainForm);
            else if (command == cmdEdit)
              {
                Field f = cg.getField(lObjects.getSelectedIndex());
                tName.setString(f.getName());
                tType.setString(f.getType().toString());
                tInit.setString(f.getInit());
                switchDisplayable(null, fEdit);

              }
          }
        else if (displayable == lOptions)
          {
            if (command == List.SELECT_COMMAND)
                lOptionsAction();
            else if (command == cmdBack)
                switchDisplayable(null, lMethods);
          }
        else if (displayable == lStatements)
          {
            if (command == List.SELECT_COMMAND)
                lStatementsAction();
            else if (command == cmdBack)
                switchDisplayable(null, lOptions);
            else if (command == cmdConfirm)
              {
                Method f = cg.getMethod(lMethods.getSelectedIndex());
                Statement s = f.getStatement(lStatements.getSelectedIndex());
                tbStatement.setStatement(s);
                switchDisplayable(null, tbStatement);

              }
          }
        else if (displayable == newProjectForm)
          {
            if (command == cmdBack)
                switchToPreviousDisplayable();
            else if (command == cmdConfirm)
              {

                fMainForm.addCommand(cmdAdd);
                fMainForm.addCommand(cmdViewCode);
                fMainForm.addCommand(cmdObjects);
                fMainForm.addCommand(cmdMethods);
                switchDisplayable(null, fMainForm);
                String clazz;
                cg = new JavaCodeGenerator(clazz = cgClassType.getString(cgClassType.getSelectedIndex()), tfName.getString());
                cg.replace("fullscreen", cgOptions.isSelected(0) ? "true" : "false");
                adder = new Adder(Display.getDisplay(this), cg, fMainForm);
                cg.setGauge(gProcess);
                //#ifndef LITE
                fMainForm.clear();
                fMainForm.append(new FlowItem(fMainForm.flow, "Variables", cg.getFieldsHeaders(), 5, 5));
                fMainForm.append(new FlowItem(fMainForm.flow, "Methods", cg.getMethodsHeaders(), 5, 100));
                fMainForm.repaint();
                //#endif

              }
          }
        else if (displayable == tbAddExpression)
          {
            if (command == cmdBack)
                switchToPreviousDisplayable();
            else if (command == cmdConfirm)
              {
                Method m = cg.getMethod(lMethods.getSelectedIndex());
                m.addStatement(tbAddExpression.getStatement());
                switchDisplayable(null, lOptions);

              }
          }
        else if (displayable == tbEdit)
          {
            if (command == cmdBack)
                switchDisplayable(null, tbEditor);
            else if (command == cmdConfirm)
              {
                tbEditor.setCurrentString(tbEdit.getString());
                switchDisplayable(null, tbEditor);

              }
          }
        else if (displayable == tbEditor)
          {
            if (command == cmdBack)
                switchDisplayable(null, fMainForm);
            else if (command == cmdEdit)
              {
                String s = tbEditor.getCurrentString();
                if (s != null)
                  {
                    tbEdit.setString(s);
                    switchDisplayable(null, tbEdit);
                  }


              }
            else if (command == cmdSave)
                switchDisplayable(null, tbSave);
            else if (command == cmdSettings)
                tbEditor.showSettings();
          }
        else if (displayable == tbSave)
          {
            if (command == cmdBack)
                switchDisplayable(null, tbEditor);
            else if (command == cmdSave)
              {
                final String path = tbSave.getString() + cg.getClassname() + ".java";
                saveFile(path);
                switchDisplayable(null, tbEditor);

              }
          }
        else if (displayable == tbStatement)
            if (command == cmdBack)
                switchToPreviousDisplayable();
            else if (command == cmdConfirm)
              {
                Method f = cg.getMethod(lMethods.getSelectedIndex());
                f.setStatement(lStatements.getSelectedIndex(), tbStatement.getStatement());
                switchDisplayable(null, lOptions);

              }

      }

    /**
     * Performs an action assigned to the selected list element in the addList component.
     */
    public void addListAction()
      {
        // enter pre-action user code here
        String __selectedString = lAdd.getString(lAdd.getSelectedIndex());
        if (__selectedString != null)
            if (__selectedString.equals("Displayable"))
                switchDisplayable(null, lAddDisplayable);
            else if (__selectedString.equals("Component"))
                switchDisplayable(null, lAddItem);
            else if (__selectedString.equals("Other"))
                switchDisplayable(null, lAddOther);
        // enter post-action user code here
      }

    /**
     * Performs an action assigned to the selected list element in the lAddDisplayable component.
     */
    public void lAddDisplayableAction()
      {
        // enter pre-action user code here
        String __selectedString = lAddDisplayable.getString(lAddDisplayable.getSelectedIndex());
        if (__selectedString != null)
            if (__selectedString.equals("Shape"))
                adder.add(Adder.FORM);
            else if (__selectedString.equals("Canvas"))
                adder.add(Adder.CANVAS);
            else if (__selectedString.equals("Lettet"))
                adder.add(Adder.LIST);
            else if (__selectedString.equals("Message"))
                adder.add(Adder.ALERT);
            else if (__selectedString.equals("Entry field"))
                adder.add(Adder.TEXTBOX);
        // enter post-action user code here
      }

    /**
     * Performs an action assigned to the selected list element in the lAddItem component.
     */
    public void lAddItemAction()
      {

          
        // enter pre-action user code here
        String __selectedString = lAddItem.getString(lAddItem.getSelectedIndex());
        if (__selectedString != null)
            if (__selectedString.equals("Group selection"))
                adder.add(Adder.CHOICEGROUP);
            else if (__selectedString.equals("Date field"))
                adder.add(Adder.DATEFIELD);
            else if (__selectedString.equals("Scale"))
                adder.add(Adder.GAUGE);
            else if (__selectedString.equals("Image"))
                adder.add(Adder.IMAGEITEM);
            else if (__selectedString.equals("Separator"))
                adder.add(Adder.SPACER);
            else if (__selectedString.equals("String"))
                adder.add(Adder.STRINGITEM);
            else if (__selectedString.equals("Entry field"))
                adder.add(Adder.TEXTFIELD);
        // enter post-action user code here
      }

    /**
     * Performs an action assigned to the selected list element in the lAddOther component.
     */
    public void lAddOtherAction()
      {
        // enter pre-action user code here
        String __selectedString = lAddOther.getString(lAddOther.getSelectedIndex());
        if (__selectedString != null)
            if (__selectedString.equals("Command"))
                adder.add(Adder.COMMAND);
            else if (__selectedString.equals("Ticker"))
                adder.add(Adder.TICKER);
            else if (__selectedString.equals("Thread (multithread)"))
                adder.add(Adder.THREAD);
            else if (__selectedString.equals("Method"))
                adder.add(Adder.METHOD);
            else if (__selectedString.equals("Image"))
                adder.add(Adder.IMAGE);
            else if (__selectedString.equals("String"))
                adder.add(Adder.STRING);
            else if (__selectedString.equals("Variable"))
                adder.add(Adder.FIELD);
        // enter post-action user code here
      }

    /**
     * Performs an action assigned to the selected list element in the lObjects component.
     */
    public void lObjectsAction()
      {
        switchDisplayable(null, lOptions);// enter pre-action user code here

        // enter post-action user code here
      }

    /**
     * Performs an action assigned to the selected list element in the lOptions component.
     */
    public void lOptionsAction()
      {
        // enter pre-action user code here
        String __selectedString = lOptions.getString(lOptions.getSelectedIndex());
        if (__selectedString != null)
            if (__selectedString.equals("Editing"))
              {
                Method f = cg.getMethod(lMethods.getSelectedIndex());
                tfMethodType.setString(f.getType().toString());
                tfMethodName.setString(f.getName());
                tfMethodParams.setString(f.getParameters());
                switchDisplayable(null, fMethodEdit);

              }
            else if (__selectedString.equals("Add Expression"))
                switchDisplayable(null, lAddExpression);
            else if (__selectedString.equals("Expresssion"))
              {
                Method f = cg.getMethod(lMethods.getSelectedIndex());
                Statement[] s = f.getStatements();
                lStatements.deleteAll();
                for (int i = 0; i < s.length; i++)
                    lStatements.append(s[i].toShortString(), null);
                switchDisplayable(null, lStatements);

              }
        // enter post-action user code here
      }

    /**
     * Performs an action assigned to the selected list element in the lMethods component.
     */
    public void lMethodsAction()
      {
        // enter pre-action user code here
        String __selectedString = lMethods.getString(lMethods.getSelectedIndex());
        // enter post-action user code here
      }

    /**
     * Performs an action assigned to the selected list element in the lStatements component.
     */
    public void lStatementsAction()
      {
        // enter pre-action user code here
        String __selectedString = lStatements.getString(lStatements.getSelectedIndex());
        // enter post-action user code here
      }

    /**
     * Performs an action assigned to the selected list element in the lAddExpression component.
     */
    public void lAddExpressionAction()
      {
        // enter pre-action user code here
        switch (lAddExpression.getSelectedIndex())
          {
            case 0:



                break;
            case 1:



                break;
            case 2:



                break;
            case 3:



                break;
            case 4:



                break;
            case 5:



                break;
            case 6:



                break;
            case 7:



                break;
            case 8:



                break;
          }
        // enter post-action user code here
      }

    /**
     * Returns an initiliazed instance of siAbout component.
     * @return the initialized component instance
     */
    public StringItem getSiAbout()
      {
        if (siAbout == null)
          {

            siAbout = new StringItem("About ", "Author - P@bloid. \nTranslate - wiktorek140.\nVisualStudio Micro Edition - application on J2ME platform for visual editing of Java-source.", Item.PLAIN);
            siAbout.setLayout(ImageItem.LAYOUT_CENTER | Item.LAYOUT_TOP | Item.LAYOUT_BOTTOM | Item.LAYOUT_VCENTER | Item.LAYOUT_2);
            siAbout.setFont(fntAbout);

          }
        return siAbout;
      }

    /**
     * Returns an initiliazed instance of iiAbout component.
     * @return the initialized component instance
     */
    public ImageItem getIiAbout()
      {
        if (iiAbout == null)
            iiAbout = new ImageItem(null, iSplashScreen, ImageItem.LAYOUT_CENTER | Item.LAYOUT_TOP | Item.LAYOUT_VCENTER | ImageItem.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_2, "", Item.PLAIN);
        return iiAbout;
      }

    /**
     * Returns an initiliazed instance of cmdNewProject component.
     * @return the initialized component instance
     */
    public Command getCmdNewProject()
      {
        if (cmdNewProject == null)
            cmdNewProject = new Command("New project", Command.OK, 3);
        return cmdNewProject;
      }

    /**
     * Returns an initiliazed instance of cmdExit component.
     * @return the initialized component instance
     */
    public Command getCmdExit()
      {
        if (cmdExit == null)
            cmdExit = new Command("Exit", Command.EXIT, 10);
        return cmdExit;
      }

    /**
     * Returns an initiliazed instance of cmdAbout component.
     * @return the initialized component instance
     */
    public Command getCmdAbout()
      {
        if (cmdAbout == null)
            cmdAbout = new Command("About", Command.HELP, 5);
        return cmdAbout;
      }

    /**
     * Returns an initiliazed instance of cmdConfirm component.
     * @return the initialized component instance
     */
    public Command getCmdConfirm()
      {
        if (cmdConfirm == null)
            cmdConfirm = new Command("Ok", Command.OK, 0);
        return cmdConfirm;
      }

    /**
     * Returns an initiliazed instance of cmdBack component.
     * @return the initialized component instance
     */
    public Command getCmdBack()
      {
        if (cmdBack == null)
            cmdBack = new Command("Back", Command.BACK, 0);
        return cmdBack;
      }

    /**
     * Returns an initiliazed instance of cmdSettings component.
     * @return the initialized component instance
     */
    public Command getCmdSettings()
      {
        if (cmdSettings == null)
            cmdSettings = new Command("Settings", Command.SCREEN, 4);
        return cmdSettings;
      }

    /**
     * Returns an initiliazed instance of cmdEdit component.
     * @return the initialized component instance
     */
    public Command getCmdEdit()
      {
        if (cmdEdit == null)
            cmdEdit = new Command("Edit", Command.SCREEN, 0);
        return cmdEdit;
      }

    /**
     * Returns an initiliazed instance of cmdSave component.
     * @return the initialized component instance
     */
    public Command getCmdSave()
      {
        if (cmdSave == null)
            cmdSave = new Command("Save", Command.SCREEN, 0);
        return cmdSave;
      }

    /**
     * Returns an initiliazed instance of cmdOptions component.
     * @return the initialized component instance
     */
    public Command getCmdOptions()
      {
        if (cmdOptions == null)
            cmdOptions = new Command("Options", Command.SCREEN, 0);
        return cmdOptions;
      }

    /**
     * Returns an initiliazed instance of cmdAdd component.
     * @return the initialized component instance
     */
    public Command getCmdAdd()
      {
        if (cmdAdd == null)
            cmdAdd = new Command("Add", Command.OK, 0);
        return cmdAdd;
      }

    /**
     * Returns an initiliazed instance of cmdViewCode component.
     * @return the initialized component instance
     */
    public Command getCmdViewCode()
      {
        if (cmdViewCode == null)
            cmdViewCode = new Command("Code", Command.SCREEN, 2);
        return cmdViewCode;
      }

    /**
     * Returns an initiliazed instance of cmdObjects component.
     * @return the initialized component instance
     */
    public Command getCmdObjects()
      {
        if (cmdObjects == null)
            cmdObjects = new Command("Objects", Command.OK, 1);
        return cmdObjects;
      }

    /**
     * Returns an initiliazed instance of cmdMethods component.
     * @return the initialized component instance
     */
    public Command getCmdMethods()
      {
        if (cmdMethods == null)
            cmdMethods = new Command("Methods", Command.SCREEN, 1);
        return cmdMethods;
      }

    /**
     * Returns a display instance.
     * @return the display instance.
     */
    public Display getDisplay()
      {
        return Display.getDisplay(this);
      }

    /**
     * Exits MIDlet.
     */
    public void exitMIDlet()
      {
        switchDisplayable(null, null);
        destroyApp(true);
        notifyDestroyed();
      }

    public void itemStateChanged(Item i)
      {
        if (cgClassType.getSelectedIndex() == 1)
            newProjectForm.append(cgOptions);
        else if (newProjectForm.size() > 2)
            newProjectForm.delete(2);
      }

    /**
     * Called when MIDlet is started.
     * Checks whether the MIDlet have been already started and initialize/starts or resumes the MIDlet.
     */
    public void startApp()
      {
        if (midletPaused)
            resumeMIDlet();
        else
          {
            initialize();
            startMIDlet();
            //#ifdef DEBUG
            try
            {
                //commandAction(cmdConfirm, newProjectForm);
            } catch (Throwable t)
            {
                t.printStackTrace();
            }
            //#endif
          }
        midletPaused = false;
      }

    /**
     * Called when MIDlet is paused.
     */
    public void pauseApp()
      {
        midletPaused = true;
      }

    private void saveFile(String s)
      {
        //#ifdef DEBUG
        s = "/root1/photos/Main.java";
        //#endif
        final String path = new String(s);
        new Thread()
          {

            public void run()
              {
                try
                  {
                    OutputStream out = new FileOutputStream(path);
                    out.write(tbEditor.getString().getBytes());
                    out.close();
                  }
                catch (Throwable t)
                  {
                    t.printStackTrace();
                  }
              }
          }.start();
      }

    /**
     * Called to signal the MIDlet to terminate.
     * @param unconditional if true, then the MIDlet has to be unconditionally terminated and all resources has to be released.
     */
    public void destroyApp(boolean unconditional)
      {
      }
  }
