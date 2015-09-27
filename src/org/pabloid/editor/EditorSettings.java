/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pabloid.editor;

/**
 *
 * @author root
 */
import javax.microedition.rms.*;
import javax.microedition.lcdui.*;
import java.io.*;

public class EditorSettings extends Form implements ItemStateListener,
                                                    CommandListener
{

    public static final String dbName = "J2MEditor.settings";
    public static EditorSettings singleton;
    public boolean bQwerty;
    public boolean bFullscreen;
    public boolean bLineNumbers;
    public boolean bSyntax;
    public int iDelay;
    public boolean bCheck;
    public int leftSoft, rightSoft;
    ChoiceGroup chSettings;
    TextField tfDelay = new TextField("Задержка проверки ошибок. -1 для отключения", "-1", 10, TextField.NUMERIC);
    Command CMD_SELECT = new Command("Выбор", Command.ITEM, 0);
    Command CMD_OK = new Command("OK", Command.ITEM, 0);
    Display disp;
    Editor edit;

    public EditorSettings()
    {
        super("Настройки");
        singleton = this;

        bQwerty = false;
        bLineNumbers = false;
        bFullscreen = false;

        String[] setts =
        {
            "Qwerty-клавиатура", "Номера строк", "Полный экран", "Подсветка синтаксиса"
        };
        chSettings = new ChoiceGroup("", Choice.MULTIPLE, setts, null);
        boolean[] vSel =
        {
            bQwerty, bLineNumbers, bFullscreen, bSyntax
        };
        chSettings.setSelectedFlags(vSel);

        append(chSettings);
        append(tfDelay);
        addCommand(CMD_OK);
        setItemStateListener(this);
        setCommandListener(this);
        edit = null;

        if (!hasSettings())
            getSofts();
    }

    public void getSofts()
    {
        try
        {
            Class.forName("com.siemens.mp.MIDlet");
            leftSoft = -1;
            rightSoft = -4;
        } catch (ClassNotFoundException e)
        {
            leftSoft = -6;
            rightSoft = -7;
        }
    }

    public boolean hasSettings()
    {
        try
        {
            RecordStore rs = RecordStore.openRecordStore(dbName, false);
            if (rs.getNumRecords() == 0)
            {
                rs.closeRecordStore();
                return false;
            }
            rs.closeRecordStore();
            return true;
        } catch (Exception e)
        {
            return false;
        }
    }

    public void saveSettings()
    {
        try
        {
            RecordStore rs = RecordStore.openRecordStore(dbName, true);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(bos);
            os.writeBoolean(bQwerty);
            os.writeBoolean(bLineNumbers);
            os.writeBoolean(bFullscreen);
            os.writeBoolean(bSyntax);
            os.writeInt(iDelay);
            os.writeInt(leftSoft);
            os.writeInt(rightSoft);
            byte[] data = bos.toByteArray();
            os.close();
            if (rs.getNumRecords() == 0)
                rs.addRecord(data, 0, data.length);
            else
                rs.setRecord(1, data, 0, data.length);
            rs.closeRecordStore();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void loadSettings()
    {
        try
        {
            RecordStore rs = RecordStore.openRecordStore(dbName, true);
            if (rs.getNumRecords() == 0)
            {
                rs.closeRecordStore();
                saveSettings();
            } else
            {
                byte[] data = rs.getRecord(1);
                DataInputStream is = new DataInputStream(new ByteArrayInputStream(data));
                bQwerty = is.readBoolean();
                bLineNumbers = is.readBoolean();
                bFullscreen = is.readBoolean();
                bSyntax = is.readBoolean();
                iDelay = is.readInt();
                bCheck = iDelay != -1;
                leftSoft = is.readInt();
                rightSoft = is.readInt();
                chSettings.setSelectedFlags(new boolean[]
                        {
                            bQwerty, bLineNumbers, bFullscreen, bSyntax
                        });
                is.close();
                rs.closeRecordStore();
                tfDelay.setString("" + iDelay);

            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void show(Display d, Editor next)
    {
        disp = d;
        edit = next;
        d.setCurrent(this);
    }

    public void itemStateChanged(Item item)
    {
        if (item == chSettings)
        {
            boolean[] bSettings = new boolean[4];
            chSettings.getSelectedFlags(bSettings);
            if (bSettings[0])
                bQwerty = true;
            else
                bQwerty = false;
            if (bSettings[1])
                bLineNumbers = true;
            else
                bLineNumbers = false;
            if (bSettings[2])
                bFullscreen = true;
            else
                bFullscreen = false;
            if (bSettings[3])
                bSyntax = true;
            else
                bSyntax = false;
        }
    }

    public void commandAction(Command c, Displayable d)
    {
        try
        {
            iDelay = Integer.parseInt(tfDelay.getString());
        } finally
        {
            bCheck = iDelay != -1;
            disp.setCurrent(edit);
            saveSettings();
            edit.reinit();
        }
    }
}
