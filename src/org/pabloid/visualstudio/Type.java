/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pabloid.visualstudio;

/**
 *
 * @author P@bloid
 */
public class Type implements TypeConstants
{

    private String name;
    private int dimension = 0;
    private int type;

    public Type(Type a, int tp)
    {
        this(a.name, tp);
    }

    public Type(Type a, int tp, int d)
    {
        this(a.name, tp, d);
    }

    public Type(String name, int type, int dimension)
    {
        this.name = name;
        this.type = type;
        this.dimension = dimension;
    }

    public Type(String name, int tp)
    {
        this(name, tp, 0);
    }

    public Type(String name)
    {
        this(name, 0);
    }

    public String toString()
    {
        String ret = new String(name);
        if (type == PUBLIC)
            ret = "public " + ret;
        if (type == PRIVATE)
            ret = "private " + ret;
        if (type == PROTECTED)
            ret = "protected " + ret;
        for (int i = 0; i < dimension; i++)
            ret += "[]";
        return ret;
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Type other = (Type) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
            return false;
        if (this.dimension != other.dimension)
            return false;
        return true;
    }

    public int hashCode()
    {
        int hash = 3;
        hash = 53 * hash ^ (this.name != null ? this.name.hashCode() : 0);
        hash = 53 * hash ^ this.dimension;
        return hash;
    }

    public static String getDefaultValue(Type t)
    {
        if (t.equals(INT) || t.equals(LONG) || t.equals(SHORT) || t.equals(BYTE) || t.equals(CHAR) || t.equals(FLOAT) || t.equals(DOUBLE))
            return "((" + t + ")0)";
        if (t.equals(BOOLEAN))
            return "false";
        return "null";
    }
}
