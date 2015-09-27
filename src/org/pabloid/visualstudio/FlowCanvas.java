package org.pabloid.visualstudio;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import org.pabloid.ui.flow.*;
import javax.microedition.lcdui.*;

/**
 * @author P@bloid
 */
public class FlowCanvas extends Canvas
  {

    /**
     * constructor
     */
    Flow flow = new Flow();
    VisualStudioME vme;

    public FlowCanvas(VisualStudioME vme)
      {
        setFullScreenMode(true);
        this.vme = vme;

      }

    /**
     * paint
     */
    public void paint(Graphics g)
      {
        try
          {
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            flow.paint(g);
            g.setColor(0);
            g.drawString(flow.getStateString(), getWidth() / 2, getHeight(), Graphics.BOTTOM | Graphics.HCENTER);
          }
        catch (Throwable t)
          {
            t.printStackTrace();
          }
      }

    /**
     * Called when a key is pressed.
     */
    protected void keyPressed(int keyCode)
      {
        if (getGameAction(keyCode) == FIRE)
            vme.edit(getIndex(), flow.getActive().getIndex());
        flow.keyPressed(keyCode, getGameAction(keyCode));
        repaint();
      }

    /**
     * Called when a key is released.
     */
    protected void keyRepeated(int keyCode)
      {
        flow.keyPressed(keyCode, getGameAction(keyCode));
        repaint();
      }

    /**
     * Called when the pointer is dragged.
     */
    protected void pointerDragged(int x, int y)
      {
      }

    /**
     * Called when the pointer is pressed.
     */
    protected void pointerPressed(int x, int y)
      {
      }

    /**
     * Called when the pointer is released.
     */
    protected void pointerReleased(int x, int y)
      {
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
  }
