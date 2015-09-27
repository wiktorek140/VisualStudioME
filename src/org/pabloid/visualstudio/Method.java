/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pabloid.visualstudio;
import org.pabloid.visualstudio.statements.*;

import org.pabloid.text.*;
import java.util.*;

/**
 *
 * @author P@bloid
 */
public class Method
{

    private Type returnType;
    private String name;
    private String sParams;
    private Vector vStatements = new Vector();
    private Vector vLastStatements = new Vector();

    public Method(Type returnType, String name, String vParams)
    {
        this.returnType = returnType;
        this.name = name;
        if (vParams != null)
            this.sParams = vParams;
        else
            this.sParams = "";
    }

    public String toString()
    {
        StringBuffer ret = new StringBuffer(returnType.toString() + " " + name + " (");
        ret.append(sParams);
        ret.append(")\n{\n");
        for (Enumeration e = vStatements.elements(); e.hasMoreElements();)
            ret.append( e.nextElement() + "\n");
        for (Enumeration e = vLastStatements.elements(); e.hasMoreElements();)
            ret.append( e.nextElement() + "\n");
        ret.append("}");
        return ret.toString();
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Method other = (Method) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
            return false;
        if (this.sParams != other.sParams && (this.sParams == null || !this.sParams.equals(other.sParams)))
            return false;
        return true;
    }

    public int hashCode()
    {
        int hash = 3;
        hash = 31 * hash ^ (this.returnType != null ? this.returnType.hashCode() : 0);
        hash = 31 * hash ^ (this.name != null ? this.name.hashCode() : 0);
        hash = 31 * hash ^ (this.sParams != null ? this.sParams.hashCode() : 0);
        return hash;
    }

    public void addStatement(Statement obj)
    {
        vStatements.addElement(obj);
    }

    public void addLastStatement(Statement obj)
    {
        vLastStatements.addElement(obj);
    }

    public void clear()
    {
        vStatements.removeAllElements();
    }

    public String getName()
    {
        return name;
    }

    public Type getType()
    {
        return returnType;
    }

    public String getParameters()
    {
        return sParams;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setType(Type returnType)
    {
        this.returnType = returnType;
    }

    public void setParameters(String sParams)
    {
        this.sParams = sParams;
    }

    public String getParametersWithoutNames()
    {
        if ("".equals(sParams) || sParams == null)
            return "";
        String[] s = StringUtils.split(sParams, ",");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length; i++)
        {
            String str = s[i].trim();
            str = str.substring(0, str.indexOf(" "));
            sb.append(str);
            if (i != s.length - 1)
                sb.append(",");
        }
        return sb.toString();
    }

    public void replace(String what, String to, boolean word)
    {
        //#ifdef DEBUG
        System.out.println(name + ".replace(\"" + what + "\",\"" + to + "\"," + word + ");");
        //#endif
        for (int i = 0; i < vStatements.size(); i++)
        {
            String obj = !word ? StringUtils.replace(vStatements.elementAt(i).toString(), what, to) : StringUtils.replaceWholeWord(vStatements.elementAt(i).toString(), what, to);
            vStatements.setElementAt(obj, i);
        }
        for (int i = 0; i < vLastStatements.size(); i++)
        {
            String obj = !word ? StringUtils.replace(vLastStatements.elementAt(i).toString(), what, to) : StringUtils.replaceWholeWord(vLastStatements.elementAt(i).toString(), what, to);
            vLastStatements.setElementAt(obj, i);
        }
    }

    public Statement getStatement(int i)
    {
        if (i < vStatements.size())
            return (Statement)vStatements.elementAt(i);
        else
            return (Statement)vLastStatements.elementAt(i - vStatements.size());

    }

    public void setStatement(int i, Statement s)
    {
        if (i < vStatements.size())
            vStatements.setElementAt(s, i);
        else
            vLastStatements.setElementAt(s, i = vStatements.size());
    }

    public Statement[] getStatements()
    {
        Statement[] a = new Statement[vStatements.size() + vLastStatements.size()];
        int i;
        for (i = 0; i < vStatements.size(); i++)
            a[i] = (Statement)vStatements.elementAt(i);
        for (i = 0; i < vLastStatements.size(); i++)
            a[i + vStatements.size()] = (Statement)vLastStatements.elementAt(i);
        return a;
    }
}
