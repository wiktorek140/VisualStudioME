/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pabloid.visualstudio;

import java.util.*;
import org.pabloid.text.*;
import javax.microedition.lcdui.*;
import org.pabloid.visualstudio.statements.*;

/**
 *
 * @author P@bloid
 */
public class JavaCodeGenerator
  {

    private Vector vMethods = new Vector();
    private Vector vFields = new Vector();
    private Vector vDisplayables = new Vector();
    private String type;
    private Vector vImages = new Vector();
    private Vector vItems = new Vector();
    private Hashtable htReplaces = new Hashtable();
    private Hashtable htRenames = new Hashtable();
    private Hashtable htAdds = new Hashtable();
    private String classname = "";
    private Gauge g = null;
    private Display display;

    public void setGauge(Gauge g)
      {
        this.g = g;
      }

    public String getType()
      {
        return type;
      }

    public Field getField(String name)
      {
        for (Enumeration e = vFields.elements(); e.hasMoreElements();)
          {
            Field f = (Field) e.nextElement();
            if (name.equals(f.getName()))
                return f;
          }
        return new Field(Type.OBJECT, name);
      }

    public Method getMethod(String name)
      {
        for (Enumeration e = vMethods.elements(); e.hasMoreElements();)
          {
            Method m = (Method) e.nextElement();
            if (name.equals(m.getName()))
                return m;
          }
        return new Method(Type.VOID, name, "");
      }

    public Method getMethod(String name, String params)
      {
        for (Enumeration e = vMethods.elements(); e.hasMoreElements();)
          {
            Method m = (Method) e.nextElement();
            if (name.equals(m.getName()) && params.equals(m.getParametersWithoutNames()))
                return m;
          }
        return new Method(Type.VOID, name, "");
      }

    public String[] getMethodsHeaders()
      {
        Method[] m = getMethods();
        String[] s = new String[m.length];
        for (int i = 0; i < m.length; i++)
            s[i] = m[i].getType() + " " + m[i].getName() + "(" + m[i].getParametersWithoutNames() + ")";
        return s;
      }

    public String[] getFieldsHeaders()
      {
        Field[] f = getFields();
        String[] s = new String[f.length];
        for (int i = 0; i < f.length; i++)
            s[i] = f[i].getType() + " " + f[i].getName();
        return s;
      }

    public Field getField(int i)
      {
        return (Field) vFields.elementAt(i);
      }

    public Method getMethod(int i)
      {
        return (Method) vMethods.elementAt(i);
      }

    public void remove(Field f)
      {
        vFields.removeElement(f);
      }

    public void remove(Method m)
      {
        vMethods.removeElement(m);
      }

    public String[] getDisplayables()
      {
        String[] disp = new String[vDisplayables.size()];
        vDisplayables.copyInto(disp);
        return disp;
      }

    public String[] getImages()
      {
        String[] disp = new String[vImages.size()];
        vImages.copyInto(disp);
        return disp;
      }

    public String[] getItems()
      {
        String[] disp = new String[vItems.size()];
        vItems.copyInto(disp);
        return disp;
      }

    public Method[] getMethods()
      {
        Method[] disp = new Method[vMethods.size()];
        vMethods.copyInto(disp);
        return disp;
      }

    public String list()
      {
        Method[] mth = getMethods();
        Field[] fld = getFields();
        StringBuffer sb = new StringBuffer("Имя класса: ");
        sb.append(htReplaces.get("classname"));
        sb.append("\nТип: ");
        sb.append(type);
        sb.append("\n--------------\nПоля: \n");
        for (int i = 0; i < fld.length; i++)
          {
            sb.append(fld[i].getType());
            sb.append(" ");
            sb.append(fld[i].getName());
            sb.append(";\n");
          }
        sb.append("\n--------------\nМетоды: \n");
        for (int i = 0; i < mth.length; i++)
          {
            sb.append(mth[i].getType());
            sb.append(" ");
            sb.append(mth[i].getName());
            sb.append("(");
            sb.append(mth[i].getParameters());
            sb.append(");\n");
          }
        return sb.toString();
      }

    public Field[] getFields()
      {
        Field[] disp = new Field[vFields.size()];
        vFields.copyInto(disp);
        return disp;
      }

    public JavaCodeGenerator(String classname)
      {
        this("Class", classname);
      }

    public JavaCodeGenerator(String type, String classname)
      {
        this.display = VisualStudioME.display;
        this.type = type;
        replace("classname", classname);
        this.classname = classname;
        if ("MIDlet".equals(type))
          {
            addMethod(new Method(new Type(Type.VOID, Type.PUBLIC), "commandAction", "Command _command, Displayable _displayable"));
            addMethod(new Method(new Type(Type.VOID, Type.PUBLIC), "commandAction", "Command _command, Item _item"));
            addMethod(new Method(new Type(Type.VOID, Type.PUBLIC), "startApp", ""));
            addMethod(new Method(new Type(Type.VOID, Type.PUBLIC), "destroyApp", "boolean _unconditional"));
            addMethod(new Method(new Type(Type.VOID, Type.PUBLIC), "pauseApp", ""));
            getMethod("pauseApp").addLastStatement(new CallStatement("System.gc", ""));
            getMethod("destroyApp").addLastStatement(new CallStatement("System.gc", ""));
            getMethod("destroyApp").addLastStatement(new CallStatement("notifyDestroyed", ""));
            addField(new Field(Type.DISPLAY, "_display", "Display.getDisplay(this)"));
          }
        if ("Canvas".equals(type))
          {
            addField(new Field(Type.INT, "w"));
            addField(new Field(Type.INT, "h"));
            addField(new Field(Type.INT, "kp"));
            addField(new Field(Type.INT, "kr"));
            addField(new Field(Type.GRAPHICS, "g"));
            addField(new Field(Type.IMAGE, "_imgDoubleBuffer"));
            Method m = null;
            addMethod(m = new Method(new Type(Type.CONSTRUCTOR, Type.PUBLIC), classname, ""));
            m.addStatement(new CallStatement("setFullScreenMode", "#fullscreen"));
            m.addStatement(new AssignStatement("w", "getHeight()"));
            m.addStatement(new AssignStatement("h", "getWidth()"));
            m.addStatement(new AssignStatement("_imgDoubleBuffer", "Image.createImage(w,h)"));
            m.addStatement(new AssignStatement("g", "_imgDoubleBuffer.getGraphics()"));
            m.addLastStatement(new CallStatement("new Thread(this).start", ""));
            addMethod(new Method(Type.VOID, "commandAction", "Command _command, Displayable _displayable"));
            addMethod(m = new Method(new Type(Type.VOID, Type.PUBLIC), "keyPressed", "int _keyCode"));
            m.addStatement(new AssignStatement("kp", "_keyCode"));
            m.addStatement(new AssignStatement("kr", "0"));
            addMethod(m = new Method(new Type(Type.VOID, Type.PUBLIC), "keyReleased", "int _keyCode"));
            m.addStatement(new AssignStatement("kr", "_keyCode"));
            m.addStatement(new AssignStatement("kp", "0"));
            addMethod(m = new Method(new Type(Type.VOID, Type.PUBLIC), "paint", "Graphics _graphics"));
            m.addStatement(new CallStatement("_graphics.drawImage", "_imgDoubleBuffer,0,0,Graphics.TOP|Graphics.LEFT"));
            addMethod(m = new Method(new Type(Type.VOID, Type.PUBLIC), "run", ""));
            m.addStatement(new TryStatement("#run\nrepaint();\nThread.sleep(10);", ""));
          }
        if ("Thread".equals(type))
          {
            addMethod(new Method(new Type(Type.CONSTRUCTOR, Type.PUBLIC), classname, ""));
            addMethod(new Method(new Type(Type.VOID, Type.PUBLIC), "run", ""));
          }
      }

    public String getClassname()
      {
        return classname;
      }

    public void replace(String key, String value)
      {
        htReplaces.put(key, value);
      }

    public void rename(String key, String value)
      {
        htRenames.put(key, value);
      }

    public void add(String key, String value)
      {
        String v = "";
        if (htAdds.containsKey(key))
            v += (String) htAdds.get(key);
        htAdds.put(key, v + "\n" + value);
      }

    public void addDisplayable(Field f, boolean show)
      {
        addField(f);
        vDisplayables.addElement(f.getName());
        getMethod("commandAction", "Command,Displayable").addStatement(new IfStatement("_displayable==" + f.getName(), "#" + f.getName() + "_action\n"));
        getMethod("startApp").addStatement(new CallStatement(f.getName() + ".setCommandListener", "this"));
        if (show)
            getMethod("startApp").addStatement(new CallStatement("_display.setCurrent", f.getName()));
      }

    public void addCommand(int disp, String name, String label, String longLabel, String type, String priority, boolean f)
      {
        boolean item = false;
        try
          {
            getMethod("startApp").addStatement(new CallStatement(vDisplayables.elementAt(disp).toString() + ".addCommand", name));
          }
        catch (ArrayIndexOutOfBoundsException e)
          {
            getMethod("startApp").addStatement(new CallStatement(vItems.elementAt(disp).toString() + ".addCommand", name));
            item = true;
          }
        addField(new Field(Type.COMMAND, name, "new Command(\"" + label + "\","
                                               + "\"" + ((longLabel == null) ? label : longLabel) + "\","
                                               + type + "," + priority + ");"));
        if (item)
            add(vItems.elementAt(disp).toString() + "_action",
                "if(command==" + name + ")\n_" + name + "_action();");
        else
            add(vDisplayables.elementAt(disp).toString() + "_action",
                "if(command==" + name + ")\n_" + name + "_action();");
        Method mth = new Method(Type.VOID, "_" + name + "_action", "");
        if (type.equals("Command.EXIT"))
            mth.addStatement(new CallStatement("destroyApp", "true"));
        addMethod(mth);
      }

    public void addObject(String name)
      {
        addField(new Field(Type.OBJECT, name, "new Object()"));
      }

    public void addImage(String name, String path)
      {
        addField(new Field(Type.IMAGE, name));
        getMethod("startApp").addLastStatement(new TryStatement(name + " = Image.createImage(\"" + path + "\");", ""));
        vImages.addElement(name);
      }

    public void addMethod(Method obj)
      {
        if (vMethods.contains(obj))
          {
            display.setCurrent(new Alert("Ошибка", "Метод с таким именем уже существует", null, AlertType.ERROR));
            return;
          }
        vMethods.addElement(obj);
      }

    public void addField(Field obj)
      {
        if (vFields.contains(obj))
          {
            display.setCurrent(new Alert("Ошибка", "Переменная с таким именем уже существует", null, AlertType.ERROR));
            return;
          }

        vFields.addElement(obj);
        getMethod("destroyApp").addStatement(new AssignStatement(obj.getName(), Type.getDefaultValue(obj.getType())));

      }

    public void addStatement(Method method, Statement stat)
      {
        int i = vMethods.indexOf(method);
        if (i < 0)
          {
            addMethod(method);
            i = vMethods.indexOf(method);
          }
        Method mth = (Method) vMethods.elementAt(i);
        mth.addStatement(stat);
      }

    public void addTicker(String disp, String name, String label)
      {
        addField(new Field(Type.TICKER, name, "new Ticker(\"" + label + "\")"));
        getMethod("startApp").addStatement(new CallStatement(disp + ".setTicker", name));
      }

    public void addStringItem(String name, String disp, String init)
      {
        addField(new Field(Type.STRINGITEM, name, init));
        addItem(name, disp);
      }

    public void addString(String name, String label)
      {
        addField(new Field(Type.STRING, name, "new String\"" + label + "\")"));
      }

    public void addGauge(String name, String disp, String label, boolean interactive, String init, String max)
      {
        addField(new Field(Type.GAUGE, name, "new Gauge(\"" + label + "\", " + interactive + ", " + max + ", " + init + ")"));
        addItem(name, disp);
      }

    public void addItem(String name, String disp)
      {
        getMethod("startApp").addStatement(new CallStatement(disp + ".append", name));
        vItems.addElement(name);
        getMethod("commandAction", "Command,Item").addStatement(new IfStatement("_item==" + name, "#" + name + "_action"));
        getMethod("startApp").addStatement(new CallStatement(name + ".setItemCommandListener", "this"));
      }

    public void addChoiceGroup(String name, String disp, String init)
      {
        addField(new Field(Type.CHOICEGROUP, name, init));
        addItem(name, disp);
      }

    public void addImageItem(String name, String disp, String init)
      {
        addField(new Field(Type.IMAGEITEM, name, init));
        addItem(name, disp);
      }

    public void addTextField(String name, String disp, String init)
      {
        addField(new Field(Type.TEXTFIELD, name, init));
        addItem(name, disp);
      }

    public void addThread(String name, String clazz)
      {
        addField(new Field(new Type(clazz), name, "new " + clazz + "()"));
      }

    public void addSpacer(String name, String disp, String init)
      {
        addField(new Field(Type.SPACER, name, init));
        addItem(name, disp);
      }

    public void addDateField(String name, String disp, String init, long time)
      {
        addField(new Field(Type.DATEFIELD, name, init));
        addItem(name, disp);
        getMethod("startApp").addStatement(new CallStatement(name + ".setTime", String.valueOf(time)));
      }

    public String toString()
      {
        //#ifdef DEBUG
        try
        {
        //#endif
        StringBuffer gen = new StringBuffer();
        if ("MIDlet".equals(type))
            gen.append("import javax.microedition.midlet.*;\n"
                       + "import javax.microedition.lcdui.*;\n"
                       + "\n"
                       + "public class " + classname + " extends MIDlet "
                       + "implements CommandListener,ItemCommandListener\n"
                       + "{\n");
        else if ("Canvas".equals(type))
            gen.append("import javax.microedition.midlet.*;\n"
                       + "import javax.microedition.lcdui.*;\n"
                       + "\n"
                       + "public class " + classname + " extends Canvas "
                       + "implements CommandListener,Runnable\n"
                       + "{\n");
        else if ("Iterface".equals(type))
            gen.append("public interface " + classname + "\n{");
        else if ("Thread".equals(type))
            gen.append("public class " + classname + " extends Thread\n{");
        else
            gen.append("public class " + classname + "\n{");

        String s = "";
        g.setMaxValue(htRenames.size() * vMethods.size() + vMethods.size() + vFields.size() + htReplaces.size() + htAdds.size() + 5);
        for (Enumeration e = htRenames.keys(); e.hasMoreElements();)
          {
            s = (String) e.nextElement();
            for (Enumeration en = vMethods.elements(); en.hasMoreElements();)
              {
                Method m = (Method) en.nextElement();
                String key = s;
                String value = (String) htRenames.get(key);
                m.replace(key, value, true);
                m.replace(key + "_action", value + "_action", true);
                ig();
              }
          }
        for (Enumeration e = vFields.elements(); e.hasMoreElements();)
          {
            s = e.nextElement().toString();
            gen.append(s + "\n");
            System.gc();
            ig();
          }
        for (Enumeration e = vMethods.elements(); e.hasMoreElements();)
          {
            s = e.nextElement().toString();
            gen.append(s + "\n\n");
            System.gc();
            ig();
          }
        for (Enumeration e = htReplaces.keys(); e.hasMoreElements();)
          {
            String key = (String) e.nextElement();
            String value = (String) htReplaces.get(key);
            StringUtils.replace(gen, "#" + key, value);
            ig();
          }
        for (Enumeration e = htAdds.keys(); e.hasMoreElements();)
          {
            String key = (String) e.nextElement();
            String value = (String) htAdds.get(key);
            StringUtils.replaceOnce(gen, "#" + key, value + "\n#" + key);
            ig();
          }
        StringUtils.replace(gen, "#", "//");
        ig();
        StringUtils.replace(gen, "\t", " ");
        ig();
        gen.append("}\n");
        return gen.toString();
        //#ifdef DEBUG
        } catch (Throwable t)
        {

            System.out.println(t.toString());
            t.printStackTrace();

            return "";
        }
        //#endif
      }

    private void ig()
      {
        g.setValue(g.getValue() + 1);
      }
  }
