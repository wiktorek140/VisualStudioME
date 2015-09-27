/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pabloid.visualstudio;
import org.pabloid.visualstudio.statements.*;
import javax.microedition.lcdui.*;
import org.pabloid.text.StringUtils;

/**
 *
 * @author P@bloid
 */
public class Adder extends Form implements CommandListener, AdderConstants
  {

    private Display display;
    //#ifndef LITE
    private FlowCanvas mainForm = null;
    //#else
//#     private Form mainForm = null;
    //#endif
    private JavaCodeGenerator codegen;
    private int mode = NONE;
    private TextField tfName = new TextField("Имя", "", 32, TextField.ANY);
    private TextField tfLabel = new TextField("Заголовок", "", 1024, TextField.ANY);
    private ChoiceGroup cgRadio = new ChoiceGroup("", Choice.POPUP);
    private ChoiceGroup cgMultiple = new ChoiceGroup("Параметры", Choice.MULTIPLE);
    private ChoiceGroup cgDisplayables = new ChoiceGroup("Объекты", Choice.POPUP);
    private Command cmdOk = new Command("OK", Command.OK, 0);
    private Command cmdBack = new Command("Назад", Command.BACK, 0);

    public Adder(Display d, JavaCodeGenerator cg, Displayable disp)
      {
        super("Ввод данных");
        //#ifndef LITE
        mainForm = (FlowCanvas) disp;
        //#else
//#         mainForm = (Form) disp;
        //#endif
        addCommand(cmdOk);
        addCommand(cmdBack);
        setCommandListener(this);
        display = d;
        codegen = cg;
      }

    private void reset()
      {
        deleteAll();
        tfName.setString("");
        tfLabel.setString("");
        cgRadio.deleteAll();
        cgRadio.setLabel("");
        cgMultiple.deleteAll();
        cgDisplayables.deleteAll();
        String[] disp = codegen.getDisplayables();
        for (int i = 0; i < disp.length; i++)
            cgDisplayables.append(disp[i], null);
      }

    public void add(int mode)
      {
        display.setCurrent(this);
        this.mode = mode;
        reset();
        append(tfName);
        System.gc();
        switch (mode)
          {
            case FORM:
                append(tfLabel);
                cgMultiple.append("Показывать при старте", null);
                append(cgMultiple);
                break;
            case CANVAS:
                tfLabel.setString("<нет>");
                append(tfLabel);
                append(new TextField("Имя класса", "MyCanvas", 32, TextField.ANY));
                cgMultiple.append("Показывать при старте", null);
                append(cgMultiple);
                break;
            case LIST:
                append(tfLabel);
                cgRadio.setLabel("Тип выбора");
                cgRadio.append("IMPLICIT", null);
                cgRadio.append("EXCLUSIVE", null);
                cgRadio.append("MULTIPLE", null);
                append(cgRadio);
                append(new TextField("Пункты (по 1 на строку)", "", 4096, TextField.ANY));
                cgMultiple.append("Показывать при старте", null);
                append(cgMultiple);
                break;
            case IMAGE:
                append(new TextField("Путь загрузки", "/icon.png", 150, TextField.URL));
                break;
            case ALERT:
                append(tfLabel);
                cgRadio.setLabel("Изображение");
                cgRadio.append("null", null);
                String[] images = codegen.getImages();
                for (int i = 0; i < images.length; i++)
                    cgRadio.append(images[i], null);
                append(cgRadio);
                append(new TextField("Текст сообщения", "", 512, TextField.ANY));
                append(new TextField("Длительность", "-2", 16, TextField.NUMERIC));
                cgMultiple.append("Показывать при старте", null);
                append(cgMultiple);
                break;
            case TEXTBOX:
                append(tfLabel);
                append(new TextField("Начальное значение", "", 1024, TextField.ANY));
                append(new TextField("Кол-во символов", "512", 16, TextField.NUMERIC));
                cgRadio.setLabel("Тип");
                cgRadio.append("ANY", null);
                cgRadio.append("DECIMAL", null);
                cgRadio.append("EMAILADDR", null);
                cgRadio.append("NUMERIC", null);
                cgRadio.append("PHONENUMBER", null);
                cgRadio.append("URL", null);
                append(cgRadio);
                cgMultiple.append("Пароль", null);
                cgMultiple.append("Недоступен", null);
                cgMultiple.append("Показывать при старте", null);
                append(cgMultiple);
                break;
            case COMMAND:
                String[] disp = codegen.getDisplayables();
                for (int i = 0; i < disp.length; i++)
                    cgRadio.append(disp[i], null);
                disp = codegen.getItems();
                for (int i = 0; i < disp.length; i++)
                    cgRadio.append(disp[i], null);
                append(cgRadio);
                append(tfLabel);
                append(new ChoiceGroup("Тип", Choice.POPUP, new String[]
                          {
                            "OK", "BACK", "EXIT", "HELP", "CANCEL", "ITEM", "STOP", "SCREEN"
                          }, null));
                append(new TextField("Приоритет", "0", 16, TextField.NUMERIC));
                cgMultiple.append("Стандартная команда (только для компонентов!)", null);
                append(cgMultiple);
                break;
            case TICKER:
                disp = codegen.getDisplayables();
                for (int i = 0; i < disp.length; i++)
                    cgRadio.append(disp[i], null);
                append(cgRadio);
                append(tfLabel);
                break;
            case METHOD:
                cgRadio.append("Стадарт", null);
                cgRadio.append("Открытый", null);
                cgRadio.append("Закрытый", null);
                cgRadio.append("Защищенный", null);
                append(cgRadio);
                append(new TextField("Возвращаемый тип", "void", 32, TextField.ANY));
                append(new TextField("Параметры через запятую", "", 512, TextField.ANY));
                break;
            case FIELD:
                cgRadio.append("Стадарт", null);
                cgRadio.append("Открытая", null);
                cgRadio.append("Закрытая", null);
                cgRadio.append("Защищенная", null);
                append(cgRadio);
                append(new TextField("Тип", "Object", 32, TextField.ANY));
                append(new TextField("Начальное значение", "", 512, TextField.ANY));
                break;
            case STRINGITEM:
                disp = codegen.getDisplayables();
                for (int i = 0; i < disp.length; i++)
                    if (codegen.getField(disp[i]).getType().equals(Type.FORM))
                        cgRadio.append(disp[i], null);
                append(cgRadio);
                append(tfLabel);
                append(new TextField("Текст", "", 16 * 1024, TextField.ANY));
                append(new ChoiceGroup("Тип", Choice.POPUP, new String[]
                          {
                            "PLAIN", "HYPERLINK", "BUTTON"
                          }, null));
                break;
            case STRING:
                append(tfLabel);
                break;
            case GAUGE:
                disp = codegen.getDisplayables();
                for (int i = 0; i < disp.length; i++)
                    if (codegen.getField(disp[i]).getType().equals(Type.FORM))
                        cgRadio.append(disp[i], null);
                append(cgRadio);
                append(tfLabel);
                cgMultiple.append("Регулируется пользователем", null);
                append(cgMultiple);
                append(new TextField("Начальное значение", "", 16, TextField.NUMERIC));
                append(new TextField("Максимальное значение", "", 16, TextField.NUMERIC));
                break;
            case CHOICEGROUP:
                disp = codegen.getDisplayables();
                for (int i = 0; i < disp.length; i++)
                    if (codegen.getField(disp[i]).getType().equals(Type.FORM))
                        cgRadio.append(disp[i], null);
                append(cgRadio);
                append(tfLabel);
                append(new ChoiceGroup("Тип", Choice.POPUP, new String[]
                          {
                            "POPUP", "EXCLUSIVE", "MULTIPLE"
                          }, null));
                append(new TextField("Пункты (по 1 на строку)", "", 4096, TextField.ANY));
                break;
            case DATEFIELD:
                append(tfLabel);
                disp = codegen.getDisplayables();
                for (int i = 0; i < disp.length; i++)
                    if (codegen.getField(disp[i]).getType().equals(Type.FORM))
                        cgRadio.append(disp[i], null);
                append(cgRadio);
                append(new ChoiceGroup("Тип", Choice.POPUP, new String[]
                          {
                            "DATE", "DATE_TIME", "TIME"
                          }, null));
                append(new DateField("Начальное значение", DateField.DATE_TIME));
                break;
            case IMAGEITEM:
                append(tfLabel);
                disp = codegen.getDisplayables();
                for (int i = 0; i < disp.length; i++)
                    if (codegen.getField(disp[i]).getType().equals(Type.FORM))
                        cgRadio.append(disp[i], null);
                append(cgRadio);
                append(new ChoiceGroup("Картинка", Choice.POPUP, codegen.getImages(), null));
                append(new ChoiceGroup("Тип", Choice.POPUP, new String[]
                          {
                            "PLAIN", "HYPERLINK", "BUTTON"
                          }, null));
                append(new ChoiceGroup("Расположение", Choice.POPUP, new String[]
                          {
                            "DEFAULT", "CENTER", "LEFT", "RIGHT"
                          }, null));
                append(new ChoiceGroup("Новая строка", Choice.MULTIPLE, new String[]
                          {
                            "До", "После"
                          }, null));
                append(new TextField("Текст", "", 512, TextField.ANY));
                break;
            case SPACER:
                disp = codegen.getDisplayables();
                for (int i = 0; i < disp.length; i++)
                    if (codegen.getField(disp[i]).getType().equals(Type.FORM))
                        cgRadio.append(disp[i], null);
                append(cgRadio);
                append(new TextField("Ширина", "", 5, TextField.NUMERIC));
                append(new TextField("Высота", "", 10, TextField.NUMERIC));
                break;
            case TEXTFIELD:
                append(tfLabel);
                disp = codegen.getDisplayables();
                for (int i = 0; i < disp.length; i++)
                    if (codegen.getField(disp[i]).getType().equals(Type.FORM))
                        cgRadio.append(disp[i], null);
                append(cgRadio);
                append(new TextField("Начальное значение", "", 4096, TextField.ANY));
                append(new TextField("Кол-во символов", "", 16, TextField.NUMERIC));
                append(new ChoiceGroup("Тип", Choice.POPUP, new String[]
                          {
                            "ANY", "NUMERIC", "DECIMAL", "EMAILADDR", "URL"
                          }, null));
                cgMultiple.append("Пароль", null);
                cgMultiple.append("Недоступен", null);
                append(cgMultiple);
                break;
            case THREAD:
                append(new TextField("Имя класса", "MyThread", 32, TextField.ANY));
                cgMultiple.append("Запускать при старте", null);
                append(cgMultiple);
                break;
            //TODO Добавить Connection

          }
      }

    public void commandAction(Command command, Displayable displayable)
      {
        display.setCurrent(mainForm);
        if (command == cmdBack)
            return;
        //#ifdef DEBUG
        try
        {
        //#endif
        String name = tfName.getString(),
                label = tfLabel.getString(), init = "";
        switch (mode)
          {
            case FORM:
                init = "new Form(\"" + label + "\")";
                codegen.addDisplayable(new Field(Type.FORM, name, init), cgMultiple.isSelected(0));
                break;
            case CANVAS:
                String clazz = ((TextField) get(2)).getString();
                if ("<нет>".equals(label))
                    label = null;
                codegen.addDisplayable(new Field(new Type(clazz), name, "new " + clazz + "()"), cgMultiple.isSelected(0));
                if (label != null)
                    codegen.getMethod("startApp").addStatement(new CallStatement(name + ".setTitle","\"" + label + "\""));
                break;
            case LIST:
                init = "new List(\"" + label + "\",Choice." + cgRadio.getString(cgRadio.getSelectedIndex()) + ", ";
                init += "new String[]{ ";
                String[] s = StringUtils.split(((TextField) get(3)).getString(), "\n");
                for (int i = 0; i < s.length; i++)
                  {
                    init += "\"" + s[i] + "\"";
                    if (i != s.length - 1)
                        init += ", ";
                  }
                init += "}, null)";
                codegen.addDisplayable(new Field(Type.LIST, name, init), cgMultiple.isSelected(0));
                break;
            case IMAGE:
                codegen.addImage(name, ((TextField) get(1)).getString());
                break;
            case ALERT:
                init = "new Alert(\"" + label + "\",\"" + ((TextField) get(3)).getString() + "\"," + cgRadio.getString(cgRadio.getSelectedIndex()) + ",null)";
                codegen.addDisplayable(new Field(Type.ALERT, name, init), cgMultiple.isSelected(0));
                codegen.add("startApp", name + ".setTimeout(" + ((TextField) get(4)).getString() + ");");
                break;
            case TEXTBOX:
                String mod = "TextField." + cgRadio.getString(cgRadio.getSelectedIndex());
                if (cgMultiple.isSelected(0))
                    mod += "|TextField.PASSWORD";
                if (cgMultiple.isSelected(1))
                    mod += "|TextField.UNEDITABLE";
                init = "new TextBox(\"" + label + "\",\"" + ((TextField) get(2)).getString() + "\"," + ((TextField) get(3)).getString() + "," + mod + ")";
                codegen.addDisplayable(new Field(Type.TEXTBOX, name, init), cgMultiple.isSelected(2));
                break;
            case COMMAND:
                ChoiceGroup cgr = ((ChoiceGroup) get(3));
                codegen.addCommand(cgRadio.getSelectedIndex(), name, label, label, "Command." + cgr.getString(cgr.getSelectedIndex()), ((TextField) get(4)).getString(), cgMultiple.isSelected(0));
                break;
            case TICKER:
                codegen.addTicker(cgRadio.getString(cgRadio.getSelectedIndex()), name, label);
                break;
            case METHOD:
                Method mth = new Method(new Type(((TextField) get(2)).getString(), cgRadio.getSelectedIndex()), name, ((TextField) get(3)).getString());
                codegen.addMethod(mth);
                break;
            case FIELD:
                init = ((TextField) get(3)).getString();
                init = "".equals(init) ? null : init;
                Field f = new Field(new Type(((TextField) get(2)).getString(), cgRadio.getSelectedIndex()), name, init);
                codegen.addField(f);
                break;
            case STRINGITEM:
                init = "new StringItem(\"" + label + "\",\"" + ((TextField) get(3)).getString() + "\",Item.";
                init += ((ChoiceGroup) get(4)).getString(((ChoiceGroup) get(4)).getSelectedIndex()) + ")";
                codegen.addStringItem(name, cgRadio.getString(cgRadio.getSelectedIndex()), init);
                break;
            case STRING:
                codegen.addString(name, label);
                break;
            case GAUGE:
                codegen.addGauge(name, cgRadio.getString(cgRadio.getSelectedIndex()), label, cgMultiple.isSelected(0),
                                 ((TextField) get(4)).getString(), ((TextField) get(5)).getString());
                break;
            case CHOICEGROUP:
                init = "new ChoiceGroup(\"" + label + "\", Choice."
                       + ((ChoiceGroup) get(3)).getString(((ChoiceGroup) get(3)).getSelectedIndex()) + ", ";
                init += "new String[]{ ";
                String[] strArr = StringUtils.split(((TextField) get(4)).getString(), "\n");
                for (int i = 0; i < strArr.length; i++)
                  {
                    init += "\"" + strArr[i] + "\"";
                    if (i != strArr.length - 1)
                        init += ", ";
                  }
                init += "}, null)";
                codegen.addChoiceGroup(name, cgRadio.getString(cgRadio.getSelectedIndex()), init);
                break;
            case DATEFIELD:
                init = "new DateField(\"" + label + "\", DateField.";
                init += ((ChoiceGroup) get(2)).getString(((ChoiceGroup) get(2)).getSelectedIndex()) + ")";
                codegen.addDateField(name, cgRadio.getString(cgRadio.getSelectedIndex()), init, ((DateField) get(3)).getDate().getTime());
                break;
            case IMAGEITEM:
                init += "new ImageItem(\"" + label + "\"," + ((ChoiceGroup) get(3)).getString(((ChoiceGroup) get(3)).getSelectedIndex()) + ",ImageItem.LAYOUT_";
                init += ((ChoiceGroup) get(5)).getString(((ChoiceGroup) get(5)).getSelectedIndex());
                if (((ChoiceGroup) get(6)).isSelected(0))
                    init += "|ImageItem.LAYOUT_NEWLINE_BEFORE";
                if (((ChoiceGroup) get(6)).isSelected(1))
                    init += "|ImageItem.LAYOUT_NEWLINE_AFTER";
                init += "," + ((TextField) get(7)).getString() + "," + ((ChoiceGroup) get(4)).getString(((ChoiceGroup) get(4)).getSelectedIndex()) + ")";
                codegen.addImageItem(name, cgRadio.getString(cgRadio.getSelectedIndex()), init);
                break;
            case SPACER:
                init += "new Spacer(" + ((TextField) get(2)).getString() + "," + ((TextField) get(3)).getString() + ")";
                codegen.addSpacer(name, cgRadio.getString(cgRadio.getSelectedIndex()), init);
            case TEXTFIELD:
                init += "new TextField(\"" + label + "\",\"" + ((TextField) get(3)).getString() + "\"," + ((TextField) get(4)).getString() + ",TextField.";
                init += ((ChoiceGroup) get(5)).getString(((ChoiceGroup) get(5)).getSelectedIndex());
                if (cgMultiple.isSelected(0))
                    init += "|TextField.PASSWORD";
                if (cgMultiple.isSelected(1))
                    init += "|TextField.UNEDITABLE";
                codegen.addTextField(name, cgRadio.getString(cgRadio.getSelectedIndex()), init);
                break;
            case THREAD:
                codegen.addThread(name, ((TextField) get(1)).getString());
                if (cgMultiple.isSelected(0))
                    codegen.getMethod("startApp").addStatement(new CallStatement(name + ".start",""));
                break;

            default:
                codegen.addObject(name);

          }
        refresh();
        //#ifdef DEBUG
        } catch (Throwable t)
        {

            mainForm.setTitle(t.toString());
            t.printStackTrace();

        }
        //#endif
      }

    void refresh()
      {
        try
          {
            //#ifndef LITE
            mainForm.getItem(0).set(codegen.getFieldsHeaders());
            mainForm.getItem(1).set(codegen.getMethodsHeaders());
            mainForm.repaint();
            //#else
//#             ((StringItem) mainForm.get(0)).setText(codegen.list());
            //#endif
          }
        catch (Throwable t)
          {
          }
      }
  }
