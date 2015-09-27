/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pabloid.visualstudio;

import org.pabloid.editor.*;
import org.pabloid.javaparser.*;

/**
 *
 * @author P@bloid
 */
public class Checker extends Thread
{

    Editor tbEditor = null;
    JavaParser jp;

    public Checker(Editor tbEditor)
    {
        this.tbEditor = tbEditor;

        jp = new JavaParser(tbEditor.getString());
        start();
    }

    public void run()
    {
        while (true)
            try
            {
                int delay = (tbEditor.getSettings() == null ? -1 : (tbEditor.getSettings().iDelay));
                delay = Math.abs(delay);
                Thread.sleep(delay);
                boolean bCheck = (tbEditor.getSettings() == null ? false : (tbEditor.getSettings().bCheck));
                if (bCheck)
                {
                    jp.reInit(tbEditor.getString());
                    jp.checkSyntax();
                }
                tbEditor.setError(-1, -1, -1);

            } catch (JavaParserException e)
            {
                int line = JavaParserException.iLine, start = JavaParserException.iColumn, end = JavaParserException.iColumn + JavaParserException.iLength;
                tbEditor.setError(line, start, end);
            } catch (TokenManagerError e)
            {
                int line = TokenManagerError.iLine, start = TokenManagerError.iColumn;
                tbEditor.setError(line, start, start);
            } catch (Throwable t)
            {
            }
    }
}
