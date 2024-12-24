/*
	This file is part of FreeJ2ME.

	FreeJ2ME is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	FreeJ2ME is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with FreeJ2ME.  If not, see http://www.gnu.org/licenses/
*/
package javax.microedition.lcdui;

import org.recompile.mobile.PlatformGraphics;

import java.util.ArrayList;


public abstract class Item {

    public static final int BUTTON = 2;
    public static final int HYPERLINK = 1;

    public static final int LAYOUT_DEFAULT = 0;

    public static final int LAYOUT_LEFT = 1;
    public static final int LAYOUT_RIGHT = 2;
    public static final int LAYOUT_CENTER = 3;

    public static final int LAYOUT_TOP = 0x10;
    public static final int LAYOUT_BOTTOM = 0x20;
    public static final int LAYOUT_VCENTER = 0x30;

    public static final int LAYOUT_NEWLINE_BEFORE = 0x100;
    public static final int LAYOUT_NEWLINE_AFTER = 0x200;

    public static final int LAYOUT_SHRINK = 0x400;
    public static final int LAYOUT_VSHRINK = 0x1000;
    public static final int LAYOUT_EXPAND = 0x800;
    public static final int LAYOUT_VEXPAND = 0x2000;

    public static final int LAYOUT_2 = 0x4000;

    public static final int PLAIN = 0;


    private String label;

    private ArrayList<Command> commands = new ArrayList();

    private int layout;

    private Command defaultCommand;

    private ItemCommandListener commandListener;

    private int prefWidth = 64;

    private int prefHeight = 16;

    int left, top, width, height;


    public void addCommand(Command cmd) {
        commands.add(cmd);
    }

    public String getLabel() {
        return label;
    }

    public int getLayout() {
        return layout;
    }

    public int getMinimumHeight() {
        return 16;
    }

    public int getMinimumWidth() {
        return 64;
    }

    public int getPreferredHeight() {
        return prefHeight;
    }

    public int getPreferredWidth() {
        return prefWidth;
    }

    public void notifyStateChanged() {
    }

    public void removeCommand(Command cmd) {
        commands.remove(cmd);
    }

    public void setDefaultCommand(Command cmd) {
        defaultCommand = cmd;
        commands.add(cmd);
    }

    Command getDefaultCommand() {
        return defaultCommand;
    }

    public void setItemCommandListener(ItemCommandListener listener) {
        commandListener = listener;
    }

    public void setLabel(String text) {
        label = text;
    }

    public void setLayout(int value) {
        layout = value;
    }

    public void setPreferredSize(int width, int height) {
        prefWidth = width;
        prefHeight = height;
    }

    protected ArrayList<Command> getCommands() {
        return commands;
    }

    protected void doActive() {
        if (commands.size() > 0) {
            if (commandListener != null) {
                commandListener.commandAction(commands.get(0), this);
            }
        }
    }

    protected ItemCommandListener getCommandListener() {
        return commandListener;
    }

    protected String getString() {
        return "";
    }

    protected void setBondle(int left, int top, int width, int height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }

    protected boolean isInRange(int x, int y) {
        if (x >= left && x <= left + width && y >= top && y <= top + height) {
            return true;
        }
        return false;
    }

    protected void render(PlatformGraphics gc, int x, int y, int w, int h) {
        String s = (label == null ? "" : label) + getString();
        if (defaultCommand != null) {
            s += " >>";
        }
        gc.drawString(s, x, y, Graphics.TOP | Graphics.LEFT);
    }

    int getHeight() {
        return height;
    }


    protected void keyPressed(int key) {
    }

    protected void keyReleased(int key) {
    }

    protected void keyRepeated(int key) {
    }

    protected void pointerDragged(int x, int y) {
    }

    protected void pointerPressed(int x, int y) {
    }

    protected void pointerReleased(int x, int y) {
    }

    /**
     * Need double click to active item firekey
     * @return
     */
    boolean needDoubleClick() {
        return false;
    }
}
