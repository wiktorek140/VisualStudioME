/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pabloid.editor;

/**
 *
 * @author P@bloid
 */
public class EditorItem
{

    public EditorItem(String text)
    {
        this(text, false);
    }

    public EditorItem(String text, boolean uneditable)
    {
        sText = text;
        bUneditable = uneditable;
    }
    private String sText = "";
    private boolean bUneditable = false;

    public boolean isUneditable()
    {
        return bUneditable;
    }

    public void setUneditable(boolean bUneitable)
    {
        this.bUneditable = bUneitable;
    }

    public String toString()
    {
        return sText;
    }

    public void setText(String sText)
    {
        this.sText = sText;
    }
}
