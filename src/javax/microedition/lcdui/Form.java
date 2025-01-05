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
import org.recompile.mobile.PlatformImage;


public class Form extends Screen {

    public ItemStateListener listener;

    public Form(String title) {
        setTitle(title);
        platformImage = new PlatformImage(width, height);
        render();
    }

    public Form(String title, Item[] itemarray) {
        setTitle(title);

        if (items != null) {
            for (int i = 0; i < itemarray.length; i++) {
                items.add(itemarray[i]);
            }
        }
        platformImage = new PlatformImage(width, height);
        render();
    }


    public int append(Image img) {
        items.add(new ImageItem("", img, 0, ""));
        render();
        return items.size() - 1;
    }

    public int append(Item item) {
        items.add(item);
        render();
        return items.size() - 1;
    }

    public int append(String str) {
        items.add(new StringItem("", str));
        render();
        return items.size() - 1;
    }

    public void delete(int itemNum) {
        items.remove(itemNum);
        render();
    }

    public void deleteAll() {
        items.clear();
        render();
    }

    public Item get(int itemNum) {
        return items.get(itemNum);
    }

    public int getHeight() {
        return FreeJ2ME.getMobile().getPlatform().lcdHeight;
    }

    public int getWidth() {
        return FreeJ2ME.getMobile().getPlatform().lcdWidth;
    }

    public void insert(int itemNum, Item item) {
        items.add(itemNum, item);
        render();
    }

    public void set(int itemNum, Item item) {
        items.set(itemNum, item);
        render();
    }

    public void setItemStateListener(ItemStateListener iListener) {
        listener = iListener;
    }

    public int size() {
        return items.size();
    }

	/*
		Draw form, handle input
	*/



    public void notifySetCurrent() {
        render();
    }

}
