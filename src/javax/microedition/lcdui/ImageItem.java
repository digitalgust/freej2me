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

import javax.microedition.lcdui.game.Sprite;

import static javax.microedition.lcdui.Displayable.ITEM_PAD;

public class ImageItem extends Item {

    public static final int LAYOUT_CENTER = 3;
    public static final int LAYOUT_DEFAULT = 0;
    public static final int LAYOUT_LEFT = 1;
    public static final int LAYOUT_NEWLINE_AFTER = 0x200;
    public static final int LAYOUT_NEWLINE_BEFORE = 0x100;
    public static final int LAYOUT_RIGHT = 2;

    private Image image;
    private String altText;
    private int appearance;
    private int layout;

    public ImageItem(String label, Image img, int Layout, String alt) {
        setLabel(label);
        layout = Layout;
        image = img;
        altText = alt;
    }

    public ImageItem(String label, Image img, int Layout, String alt, int appearanceMode) {
        setLabel(label);
        layout = Layout;
        image = img;
        altText = alt;
        appearance = appearanceMode;
    }

    public String getAltText() {
        return altText;
    }

    public int getAppearanceMode() {
        return appearance;
    }

    public Image getImage() {
        return image;
    }

    public int getLayout() {
        return layout;
    }

    public void setAltText(String text) {
        altText = text;
    }

    public void setImage(Image img) {
        image = img;
    }

    public void setLayout(int Layout) {
        layout = Layout;
    }

    @Override
    public int getPreferredHeight() {
        int h = 0;
//        h += altText != null ? Displayable.ITEM_H : 0;
        if (image != null) {
            h += image.height;
        }
        return h + ITEM_PAD;
    }


    @Override
    protected void render(PlatformGraphics gc, int x, int y, int w, int h) {
        if (image != null) {
            gc.drawImage(image, x, y + 3, Graphics.TOP | Graphics.LEFT);
//            y += image.height;
            x += image.width + ITEM_PAD;
        }
        if (altText != null) {
            //gc.setColor(0xff000000);
            gc.drawString(getLabel(), x, y, Graphics.TOP | Graphics.LEFT);
            y += Displayable.ITEM_H;
        }
        //gc.drawRegion(getImage(), 0, 0, w, h, Sprite.TRANS_NONE, x, y, Graphics.TOP | Graphics.LEFT);
    }
}
