/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pabloid.visualstudio;

/**
 *
 * @author P@bloid
 */
public class Field
{

    private Type type;
    private String name;
    private String init;

    public String getInit()
    {
        return init;
    }

    public Field(Type type, String name)
    {
        this(type, name, null);
    }

    public Field(Type type, String name, String init)
    {
        this.type = type;
        this.name = name;
        this.init = init;
    }

    public String toString()
    {
        String s = type.toString() + " " + name + (init == null || init.length() == 0 ? "" : " = " + init);
        if (!s.endsWith(";"))
            s += ";";
        return s;
    }

    public Type getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Field other = (Field) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
            return false;
        return true;
    }

    public int hashCode()
    {
        int hash = 3;
        hash = 97 * hash ^ (this.type != null ? this.type.hashCode() : 0);
        hash = 97 * hash ^ (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash ^ (this.init != null ? this.init.hashCode() : 0);
        return hash;
    }

    public void setInit(String init)
    {
        this.init = init;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setType(Type type)
    {
        this.type = type;
    }
}
