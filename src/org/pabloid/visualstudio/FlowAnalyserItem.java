/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pabloid.visualstudio;

import javax.microedition.lcdui.*;
import org.pabloid.ui.flow.*;

/**
 *
 * @author P@bloid
 */
public class FlowAnalyserItem extends CustomItem
{

    Flow flow = new Flow();

    public FlowAnalyserItem()
    {
        super(null);
    }

    public void setItem(int i, FlowItem fi)
    {
        flow.setItem(i, fi);
    }

    public FlowItem getItem(int i)
    {
        return flow.getItem(i);
    }

    public void append(FlowItem fi)
    {
        flow.append(fi);
    }

    public int size()
    {
        return flow.size();
    }

    public void setIndex(int index)
    {
        flow.setIndex(index);
    }

    public synchronized void set(FlowItem obj, int index)
    {
        flow.set(obj, index);
    }

    public synchronized void remove(int index)
    {
        flow.remove(index);
    }

    public int getIndex()
    {
        return flow.getIndex();
    }

    public synchronized void clear()
    {
        flow.clear();
    }

    public int getPrefContentHeight(int i)
    {
        return i;
    }

    public int getPrefContentWidth(int i)
    {
        return i;
    }

    public int getMinContentHeight()
    {
        return 120;
    }

    public int getMinContentWidth()
    {
        return 120;
    }

    public void paint(Graphics g, int x, int y)
    {
    }
}
