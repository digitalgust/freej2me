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

import org.recompile.freej2me.FreeJ2ME;
import org.recompile.mobile.Mobile;
import org.recompile.mobile.PlatformGraphics;

import java.util.ArrayList;


public class ChoiceGroup extends Item implements Choice {

    private String label;

    private int type;

    private ArrayList<String> strings = new ArrayList<String>();

    private ArrayList<Image> images = new ArrayList<Image>();
    java.util.List<int[]> bounds = new ArrayList<int[]>();

    private int fitPolicy;

    int selectedIndex = 0;

    boolean[] selectedFlags;

    public ChoiceGroup(String choiceLabel, int choiceType) {
        label = choiceLabel == null ? "" : choiceLabel;
        setType(choiceType);
    }

    public ChoiceGroup(String choiceLabel, int choiceType, String[] stringElements, Image[] imageElements) {
        label = choiceLabel == null ? "" : choiceLabel;
        setType(choiceType);
        for (int i = 0; i < stringElements.length; i++) {
            try {
                strings.add(stringElements[i]);
                images.add(imageElements != null && i < imageElements.length ? imageElements[i] : null);
            } catch (Exception e) {
            }
        }
        selectedFlags = new boolean[strings.size()];
    }

    ChoiceGroup(String choiceLabel, int choiceType, boolean validateChoiceType) {
        label = choiceLabel == null ? "" : choiceLabel;
        setType(choiceType);
    }

    ChoiceGroup(String choiceLabel, int choiceType, String[] stringElements, Image[] imageElements, boolean validateChoiceType) {
        label = choiceLabel == null ? "" : choiceLabel;
        setType(choiceType);
        for (int i = 0; i < stringElements.length; i++) {
            try {
                strings.add(stringElements[i]);
                images.add(imageElements[i]);
            } catch (Exception e) {
            }
        }
    }

    void setType(int choiceType) {
        if (choiceType == IMPLICIT || choiceType == POPUP) {
            choiceType = EXCLUSIVE;
        }
        type = choiceType;
    }

    public int append(String stringPart, Image imagePart) {
        strings.add(stringPart);
        images.add(imagePart);
        return strings.size();
    }

    public void delete(int itemNum) {
        strings.remove(itemNum);
        images.remove(itemNum);
    }

    public void deleteAll() {
        strings.clear();
        images.clear();
    }

    public int getFitPolicy() {
        return fitPolicy;
    }

    public Font getFont(int itemNum) {
        return Font.getDefaultFont();
    }

    public Image getImage(int elementNum) {
        return images.get(elementNum);
    }

    public int getSelectedFlags(boolean[] selectedArray) {
        return 1;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public String getString(int elementNum) {
        return strings.get(elementNum);
    }

    public void insert(int elementNum, String stringPart, Image imagePart) {
        strings.add(elementNum, stringPart);
        images.add(elementNum, imagePart);
    }

    public boolean isSelected(int elementNum) {
        return false;
    }

    public void set(int elementNum, String stringPart, Image imagePart) {
        strings.set(elementNum, stringPart);
        images.set(elementNum, imagePart);
    }

    public void setFitPolicy(int policy) {
        fitPolicy = policy;
    }

    public void setFont(int itemNum, Font font) {
    }

    public void setSelectedFlags(boolean[] selectedArray) {
        if (selectedFlags == null || selectedFlags.length != selectedArray.length) {
            throw new IllegalArgumentException();
        }
        System.arraycopy(selectedArray, 0, selectedFlags, 0, selectedArray.length);
    }

    public void setSelectedIndex(int elementNum, boolean selected) {
        selectedIndex = elementNum;
    }

    public int size() {
        return strings.size();
    }

    protected void render(PlatformGraphics gc, int x, int y, int w, int h) {
        int color = gc.getColor();
        if (label != null && label.length() > 0) {
            gc.drawString(label, x, y, Graphics.TOP | Graphics.LEFT);
            y += Displayable.ITEM_H;
        }
        bounds.clear();
        for (int c = 0; c < size(); c++) {
            if (c == selectedIndex) {
                gc.setColor(color);
            } else {
                gc.setColor(0xff202020);
            }
            int cx = x;
            int boxw = Displayable.ITEM_H;
            if (type == MULTIPLE) {
                drawRect(gc, cx, y + 2, boxw - 4, boxw - 4, selectedFlags[c]);
            } else if (type == EXCLUSIVE) {
                drawCircle(gc, cx, y + 2, boxw - 4, boxw - 4, selectedIndex == c);
            }
            int[] bound = new int[]{cx, y, cx + w, y + Displayable.ITEM_H};
            bounds.add(bound);
            cx = Displayable.ITEM_H;
            gc.drawString(getString(c), cx, y, Graphics.LEFT | Graphics.TOP);
            gc.setColor(color);
            y += Displayable.ITEM_H;
        }
        String s = "";
        if (type == MULTIPLE) {
            s = "ENTER to change";
        } else if (type == EXCLUSIVE) {
            s = "LEFT/RIGHT/ENTER to change";
        }
        gc.setColor(0xff808080);
        gc.drawString(s, x, y, Graphics.LEFT | Graphics.TOP);
    }

    void drawRect(PlatformGraphics gc, int x, int y, int w, int h, boolean selected) {
        //画复选框
        gc.drawRect(x, y, w, h);
        if (selected) {
            gc.fillRect(x + 3, y + 3, w - 5, h - 5);
        }
    }

    void drawCircle(PlatformGraphics gc, int x, int y, int w, int h, boolean selected) {
        //
        gc.drawArc(x, y, w, h, 0, 360);
        if (selected) {
            gc.fillArc(x + 2, y + 2, w - 4, h - 4, 0, 360);
        }
    }

    public boolean keyReleased(int key) {
        if (key == Mobile.NOKIA_LEFT) {
            selectedIndex--;
            if (selectedIndex < 0) {
                selectedIndex = size() - 1;
            }
            return true;
        } else if (key == Mobile.NOKIA_RIGHT) {
            selectedIndex++;
            if (selectedIndex >= size()) {
                selectedIndex = 0;
            }
            return true;
        }
        return false;
    }

    protected boolean pointerReleased(int x, int y) {
        if (type == EXCLUSIVE) {
            for (int i = 0; i < bounds.size(); i++) {
                int[] bound = bounds.get(i);
                if (bound[0] <= x && x <= bound[2] && bound[1] <= y && y <= bound[3]) {
                    selectedIndex = i;
                    break;
                }
            }
            return true;
        }
        return false;
    }

    protected void doActive() {
        if (type == MULTIPLE) {
            selectedFlags[selectedIndex] = !selectedFlags[selectedIndex];
        } else if (type == EXCLUSIVE) {
            selectedIndex++;
            if (selectedIndex >= size()) {
                selectedIndex = 0;
            }
        }
    }

    boolean needDoubleClick() {
        return true;
    }

    @Override
    public int getPreferredHeight() {
        int h = strings.size() * Displayable.ITEM_H + Displayable.ITEM_H + Displayable.ITEM_PAD;
        if (label != null && label.length() > 0) {
            h += Displayable.ITEM_H;
        }
        return h;
    }
}
