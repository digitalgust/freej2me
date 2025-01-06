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

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import org.recompile.freej2me.FreeJ2ME;
import org.recompile.mobile.Mobile;
import org.recompile.mobile.PlatformImage;
import org.recompile.mobile.PlatformGraphics;

public abstract class Displayable {
    final static int ITEM_H = 18;
    final static int ITEM_PAD = 6;
    final static int TITLE_H = 25;

    public PlatformImage platformImage;

    public int width = 0;

    public int height = 0;

    public float scrollPos = 0.0f; // for scrolling

    public boolean fullScreen = false;

    protected String title = "";

    protected ArrayList<Command> commands = new ArrayList<Command>();

    protected java.util.List<Item> items = new CopyOnWriteArrayList<>();
    //for save container(form) commands and item commands
    protected ArrayList<Command> combinedCommands = new ArrayList<Command>();

    protected CommandListener commandlistener;

    protected boolean listCommands = false;

    protected int currentCommand = 0;

    private int currentIndex = -1;

    public Ticker ticker;

    protected Command options, optionsLeft, optionsRight;

    protected boolean oneKeyPressed = false;
    protected boolean pointerPressed = false;
    protected boolean pointerDraged = false;
    protected int dragX = 0;
    protected int dragY = 0;

    public Displayable() {
        width = FreeJ2ME.getMobile().getPlatform().lcdWidth;
        height = FreeJ2ME.getMobile().getPlatform().lcdHeight;
    }

    public void addCommand(Command cmd) {
        try {
            commands.add(cmd);
        } catch (Exception e) {
            System.out.println("Problem Adding Command: " + e.getMessage());
        }
    }

    public void removeCommand(Command cmd) {
        commands.remove(cmd);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String text) {
        title = text;
    }

    public boolean isShown() {
        return true;
    }

    public Ticker getTicker() {
        return ticker;
    }

    public void setTicker(Ticker tick) {
        ticker = tick;
    }

    public void setCommandListener(CommandListener listener) {
        commandlistener = listener;
    }

    protected void sizeChanged(int width, int height) {
    }

    public Display getDisplay() {
        return FreeJ2ME.getMobile().getDisplay();
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }


    public void keyPressed(int key) {
        oneKeyPressed = true;
    }

    public void keyReleased(int key) {
        if (oneKeyPressed) {
            //处理，一个item超过一屏的情况，一行一行移动
            Item item = getCurrentItem();
            switch (key) {
                case Mobile.NOKIA_UP:
                    if (item != null && item.getHeight() > getMainAreaHeight()) {
                        setScollPos(getScollPos() - (float) ITEM_H / (float) getScrollHeight());
                    }
                    break;
                case Mobile.NOKIA_DOWN:
                    if (item != null && item.getHeight() > getMainAreaHeight()) {
                        float dy = ITEM_H / (float) getScrollHeight();
                        setScollPos(getScollPos() + dy);
                    }
                    break;
            }

            if (listCommands) {
                keyPressedCommands(key);
                return;
            }

            boolean isProcessed = false;
            item = getCurrentItem();
            if (item != null) {
                isProcessed = item.keyReleased(key);
            }
            switch (key) {
                case Mobile.NOKIA_UP:
                    setCurrentIndex(getCurrentIndex() - 1);
                    break;
                case Mobile.NOKIA_DOWN:
                    setCurrentIndex(getCurrentIndex() + 1);
                    break;
                case Mobile.NOKIA_SOFT1:
                    doLeftCommand();
                    break;
                case Mobile.NOKIA_SOFT2:
                    doRightCommand();
                    break;
                case Mobile.NOKIA_SOFT3:
                    if (!isProcessed) {
                        doDefaultCommand();
                    }
                    break;
                //case Mobile.KEY_NUM5: doDefaultCommand(); break;

            }
        }
        oneKeyPressed = false;
        render();
    }

    public void keyRepeated(int key) {
    }

    public void pointerDragged(int x, int y) {
        pointerDraged = true;
        if (pointerPressed) {
            int oldY = dragY;
            dragX = x;
            dragY = y;

            setScollPos(getScollPos() - ((dragY - oldY) / (float) getScrollHeight()));
        }
    }

    public void pointerPressed(int x, int y) {
        pointerPressed = true;
        dragX = x;
        dragY = y;
    }

    public void pointerReleased(int x, int y) {
        if (pointerDraged) {
            pointerDraged = false;
            pointerPressed = false;
            return;
        }
        if (pointerPressed) {
            if (y > height - TITLE_H) {//左右软键
                int hit = getCommandHit(x, y);
                if (hit == 0) {
                    oneKeyPressed = true; //emulate key press
                    keyReleased(Mobile.NOKIA_SOFT1);
                } else if (hit == 1) {
                    oneKeyPressed = true;
                    keyReleased(Mobile.NOKIA_SOFT2);
                }
                render();
            } else if (y > TITLE_H) {
                //中央显示区域
                if (listCommands) {//合并command展示屏的处理
                    int commandIdx = getCombinedCommandIndex(x, y);
                    if (commandIdx >= 0 && commandIdx < combinedCommands.size()) {
                        doCommand(commandIdx);
                    }
                    int hit = getCommandHit(x, y);
                    if (hit == 0) {
                        keyReleased(Mobile.NOKIA_SOFT1);
                    } else if (hit == 1) {
                        keyReleased(Mobile.NOKIA_SOFT2);
                    }
                } else {
                    int oldIndex = getCurrentIndex();
                    int itemIdx = getItemIndex(x, y);
                    if (itemIdx >= 0 && itemIdx < items.size()) {
                        setCurrentIndex(itemIdx);
                        Item item = items.get(getCurrentIndex());
                        boolean isProcess = item.pointerReleased(x, y);
                        if (!isProcess) {
                            if (oldIndex == itemIdx) {
                                doDefaultCommand();
                            }
                        }
                    }
                }
            }
        }
        pointerPressed = false;
        render();
    }

    public void showNotify() {
    }

    public void hideNotify() {
    }

    public void notifySetCurrent() {
        render();
    }

    public void render() {
        if (listCommands) {
            renderCommands();
        } else {
            renderItems();
        }
    }

    public void renderItems() {
        PlatformGraphics gc = platformImage.getGraphics();
        gc.setClip(0, 0, width, height);
        // Draw Background:
        gc.setColor(0xFFFFFF);
        gc.fillRect(0, 0, width, height);
        gc.setColor(0x000000);

        // Draw Title:
        gc.drawString(title, width / 2, 2, Graphics.HCENTER);
        gc.drawLine(0, 20, width, 20);
        gc.drawLine(0, height - 20, width, height - 20);

        if (items.size() > 0) {
            if (getCurrentIndex() < 0) {
                setCurrentIndex(0);
            }
            // Draw list items //
            int ah = height - TITLE_H * 2; // allowed height
//            int totalH = getTotalHeight();
//            if (ah > totalH) {
//                scrollPos = 1.0f;
//            }

            gc.setClip(0, TITLE_H, width, ah);


            int y = (int) (25 - scrollPos * getScrollHeight());
            int i = 0;
            for (Item item : items) {
                int itemH = item.getPreferredHeight();
                if (getCurrentIndex() == i) {
                    gc.setColor(0x226622);
                    gc.fillRect(0, y, width, itemH);
                    gc.setColor(0xFFFFFF);
                } else {
                    gc.setColor(0x000000);
                }

                item.setBondle(0, y, getWidth(), itemH);
                item.render(gc, 0, y, getWidth(), itemH);

                y += itemH;
                i++;
            }
        }

        combinAll();

        if (combinedCommands.size() > 2) {
            if (options == null) {
                options = new Command("Options", Command.SCREEN, 0);
                options.setBondle(0, height - ITEM_H, width / 3, ITEM_H);
                optionsLeft = new Command("Options", Command.SCREEN, 0);
                optionsLeft.setBondle(0, height - ITEM_H, width / 3, ITEM_H);
                optionsRight = new Command("Options", Command.SCREEN, 0);
                optionsRight.setBondle(width * 2 / 3, height - ITEM_H, width / 3, ITEM_H);
            }
        } else {
            options = null;
        }

        // Draw Commands
        gc.setColor(0x000000);
        gc.setClip(0, height - TITLE_H, width, TITLE_H);
        switch (combinedCommands.size()) {
            case 0:
                break;
            case 1:
                Command cmd = combinedCommands.get(0);
                cmd.setBondle(0, height - ITEM_H, width / 3, ITEM_H);
                gc.drawString(combinedCommands.get(0).getLabel(), 3, height - ITEM_H, Graphics.LEFT);
                gc.drawString("" + (getCurrentIndex() + 1) + " of " + items.size(), width - 3, height - 17, Graphics.RIGHT);
                break;
            case 2:
                cmd = combinedCommands.get(0);
                cmd.setBondle(0, height - ITEM_H, width / 3, ITEM_H);
                cmd = combinedCommands.get(1);
                cmd.setBondle(width * 2 / 3, height - ITEM_H, width / 3, ITEM_H);
                gc.drawString(combinedCommands.get(0).getLabel(), 3, height - ITEM_H, Graphics.LEFT);
                gc.drawString(combinedCommands.get(1).getLabel(), width - 3, height - ITEM_H, Graphics.RIGHT);
                break;
            default:
                gc.drawString("Options", 3, height - 17, Graphics.LEFT);
        }

        if (this.getDisplay().getCurrent() == this) {
            FreeJ2ME.getMobile().getPlatform().flushGraphics(platformImage, 0, 0, width, height);
        }
    }

    /**
     * 返回所有item的总高度
     *
     * @return
     */
    protected int getTotalHeight() {
        int h = 0;
        for (Item item : items) {
            h += item.getPreferredHeight();
        }
        return h;
    }

    /**
     * 用来计算scrollPos的高度
     *
     * @return
     */
    protected int getScrollHeight() {
        int h = getTotalHeight();
        h = h - getMainAreaHeight();
        if (h < 0) {
            h = getMainAreaHeight();
        }
        return h;
    }

    /**
     * 返回第i个item在画的时候的Y值
     *
     * @param index
     * @return
     */
    protected int getItemTop(int index) {
        int h = 0;
        int i = 0;
        for (Item item : items) {
            if (i == index) {
                break;
            }
            h += item.getPreferredHeight();
            i++;
        }
        return h;
    }

    protected void setScollPos(float pos) {
        if (Float.isNaN(pos)) return;
        scrollPos = pos;

        if (scrollPos < 0.0f) {
            scrollPos = 0.0f;
        }

        if (scrollPos > 1.0f) {
            scrollPos = 1.0f;
        }

        render();
    }

    protected float getScollPos() {
        return scrollPos;
    }

    /**
     * 返回主区域的X坐标
     *
     * @return
     */
    protected int getMainAreaX() {
        return 0;
    }

    protected int getMainAreaY() {
        return 0;
    }

    protected int getMainAreaWidth() {
        return width;
    }

    protected int getMainAreaHeight() {
        return height - TITLE_H * 2;
    }

    protected void renderCommands() {
        PlatformGraphics gc = platformImage.getGraphics();
        gc.setClip(0, 0, width, height);

        // Draw Background:
        gc.setColor(0xFFFFFF);
        gc.fillRect(0, 0, width, height);
        gc.setColor(0x000000);

        // Draw Title:
        gc.drawString("Options", width / 2, 2, Graphics.HCENTER);
        gc.drawLine(0, 20, width, 20);
        gc.drawLine(0, height - 20, width, height - 20);

        combinAll();
        if (combinedCommands.size() > 0) {
            if (currentCommand < 0) {
                currentCommand = 0;
            }
            // Draw commands //
            int ah = height - 50; // allowed height
            int max = (int) Math.floor(ah / ITEM_H); // max items per page
            if (combinedCommands.size() < max) {
                max = combinedCommands.size();
            }

            int page = 0;
            page = (int) Math.floor(currentCommand / max); // current page
            int first = page * max; // first item to show
            int last = first + max - 1;

            if (last >= combinedCommands.size()) {
                last = combinedCommands.size() - 1;
            }

            int y = 25;
            for (int i = first; i <= last; i++) {
                if (currentCommand == i) {
                    gc.fillRect(0, y, width, ITEM_H);
                    gc.setColor(0xFFFFFF);
                }

                Command cmd = combinedCommands.get(i);
                cmd.setBondle(0, y, width, ITEM_H);
                gc.drawString(cmd.getLabel(), width / 2, y, Graphics.HCENTER);

                gc.setColor(0x000000);
                y += ITEM_H;
            }
        }
        gc.drawString("Okay", 3, height - 17, Graphics.LEFT);
        gc.drawString("Back", width - 3, height - 17, Graphics.RIGHT);

        if (this.getDisplay().getCurrent() == this) {
            FreeJ2ME.getMobile().getPlatform().flushGraphics(platformImage, 0, 0, width, height);
        }
    }

    private void combinAll() {
        Item curItem = null;
        if (getCurrentIndex() >= 0 && getCurrentIndex() < items.size()) {
            curItem = items.get(getCurrentIndex());
        }

        combinedCommands.clear();
        if (curItem != null) {
//            if (curItem.getDefaultCommand() != null) {
//                combinedCommands.add(curItem.getDefaultCommand());
//            }
            combinedCommands.addAll(curItem.getCommands());
        }
        combinedCommands.addAll(commands);
    }

    Item getCurrentItem() {
        Item curItem = null;
        if (getCurrentIndex() >= 0 && getCurrentIndex() < items.size()) {
            curItem = items.get(getCurrentIndex());
        }
        return curItem;
    }

    protected void keyPressedCommands(int key) {
        combinAll();
        switch (key) {
            case Mobile.KEY_NUM2:
                currentCommand--;
                break;
            case Mobile.KEY_NUM8:
                currentCommand++;
                break;
            case Mobile.NOKIA_UP:
                currentCommand--;
                break;
            case Mobile.NOKIA_DOWN:
                currentCommand++;
                break;
            case Mobile.NOKIA_SOFT1:
                doLeftCommand();
                break;
            case Mobile.NOKIA_SOFT2:
                doRightCommand();
                break;
            case Mobile.KEY_NUM5:
                doDefaultCommand();
                break;
        }
        if (currentCommand >= combinedCommands.size()) {
            currentCommand = 0;
        }
        if (currentCommand < 0) {
            currentCommand = combinedCommands.size() - 1;
        }
        if (listCommands) {
            renderCommands();
        }
    }

    int getItemIndex(int x, int y) {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item != null) {
                if (item.isInRange(x, y)) return i;
            }
        }
        return -1;
    }

    int getCombinedCommandIndex(int x, int y) {
        if (FreeJ2ME.getMobile().getDisplay().getCurrent() != this) {
            return -1;
        }
        for (int i = 0; i < combinedCommands.size(); i++) {
            Command cmd = combinedCommands.get(i);
            if (cmd != null) {
                if (cmd.isInRange(x, y)) return i;
            }
        }
        return -1;
    }

    protected void doCommand(int index) {
        if (index >= 0 && combinedCommands.size() > index) {
            if (commandlistener != null) {
                Command cmd = combinedCommands.get(index);
                if (isCurrentItemOwn(cmd)) {
                    Item cur = getCurrentItem();
                    cur.getCommandListener().commandAction(cmd, cur);
                } else {
                    commandlistener.commandAction(cmd, this);
                }
            }
        }
    }

    protected void doDefaultCommand() {
        //doCommand(0);
        Item curItem = getCurrentItem();
        if (curItem != null) {
            curItem.doActive();
        }
    }

    protected void doLeftCommand() {
        if (combinedCommands.size() > 2) {
            if (listCommands == true) {
                doCommand(currentCommand);
            } else {
                listCommands = true;
                currentCommand = 0;
                render();
            }
            return;
        } else {
            if (combinedCommands.size() > 0 && combinedCommands.size() <= 2) {
                doCommand(0);
            }
        }
    }

    protected void doRightCommand() {
        if (listCommands == true) {
            listCommands = false;
            currentCommand = 0;
            render();
        } else {
            if (combinedCommands.size() > 0 && combinedCommands.size() <= 2) {
                doCommand(1);
            }
        }
    }

    protected boolean isCurrentItemOwn(Command cmd) {
        Item cur = getCurrentItem();
        if (cur != null) {
            return cur.getCommands().contains(cmd);
        }
        return false;
    }

    protected ArrayList<Command> getCombinedCommands() {
        return combinedCommands;
    }

    /**
     * if x,y is left area return 0
     * if x,y is right area return 1
     * otherwise return -1
     *
     * @param x
     * @param y
     * @return
     */
    int getCommandHit(int x, int y) {
        if (y < height - ITEM_H) {
            return -1;
        } else {
            return (x < width / 2) ? 0 : 1;
        }
    }

    //中英文都适用
    public static Vector<String> getSubSection(String strSource, Font font, int width, String splitor) {
        Vector<String> vector = new Vector<>();
        String temp = strSource;
        int i, j;
        int lastLength = 1;
        int step = 0;
        try {
            while (!temp.equals("")) {
                i = temp.indexOf("\n");
                if (i > 0) {
                    if (font.stringWidth(temp.substring(0, i - 1)) >= width) {
                        i = -1;
                    }
                }
                if (i == -1) {
                    if (lastLength > temp.length()) {
                        i = temp.length();
                    } else {
                        i = lastLength;
                        step = font.stringWidth(temp.substring(0, i)) > width ? -1 : 1;
                        if (i < temp.length()) {
                            while (!(font.stringWidth(temp.substring(0, i)) <= width &&
                                    font.stringWidth(temp.substring(0, i + 1)) > width)) {
                                i = i + step;
                                if (i == temp.length()) {
                                    break;
                                }
                            }
                        }
                    }
                    if (splitor != null && splitor.length() > 0) {
                        j = i;
                        if (i < temp.length()) {
                            while (!splitor.contains(temp.substring(i - 1, i))) {
                                i--;
                                if (i == 0) {
                                    i = j;
                                    break;
                                }
                            }
                        }

                    }
                }
                lastLength = i;
                vector.addElement(temp.substring(0, i));
                if (i == temp.length()) {
                    temp = "";
                } else {
                    temp = temp.substring(i);
                    if (temp.charAt(0) == '\n') {
                        temp = temp.substring(1);
                    }
                }
            }
        } catch (Exception e) {
            System.out.print("getSubSectionException");
        }
        return vector;
    }

    protected int getCurrentIndex() {
        return currentIndex;
    }

    protected void setCurrentIndex(int currentIndex) {

        if (currentIndex < 0) {
            currentIndex = (items.size() - 1);
        }
        if (currentIndex >= items.size()) {
            currentIndex = (0);
        }
        int old = this.currentIndex;
        this.currentIndex = currentIndex;

        int scrollHeight = getScrollHeight();
        int curScreenTop = (int) (scrollHeight * scrollPos);
        int mainH = getMainAreaHeight();
        int curScreenBottom = curScreenTop + mainH;
        int newItemTop = getItemTop(currentIndex);
        int newItemBottom = newItemTop + items.get(currentIndex).getHeight();
        //如果整个item都显示在mainarea则不变化
        if (newItemTop >= curScreenTop && newItemBottom <= curScreenBottom) {
            setScollPos((float) (curScreenTop) / scrollHeight);//不变化
        } else {
            if (old < this.currentIndex) {//向下滚动
                if (newItemBottom > curScreenBottom) {
                    setScollPos((float) (newItemBottom - mainH) / scrollHeight);
                } else {
                    setScollPos((float) (newItemTop) / scrollHeight);
                }
            } else if (old > this.currentIndex) {//向上滚动
                if (newItemTop < 0) {
                    setScollPos((float) (newItemTop) / scrollHeight);
                } else {
                    setScollPos((float) (newItemBottom - mainH) / scrollHeight);
                }
            }
        }

    }
}
