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
import org.recompile.mobile.PlatformImage;

import java.util.ArrayList;

public class TextBox extends Screen
{

	private String text;
	private int max;
	private int constraints;
	private String mode;
	private Ticker ticker;


	public TextBox(String Title, String value, int maxSize, int Constraints)
	{
		title = Title;
		text = value;
		max = maxSize;
		constraints = Constraints;
		platformImage = new PlatformImage(width, height);
	}

	public void delete(int offset, int length)
	{
		text = text.substring(0, offset) + text.substring(offset+length);
		render();
	}

	public int getCaretPosition() { return 0; }

	public int getChars(char[] data)
	{
		for(int i=0; i<text.length(); i++)
		{
			data[i] = text.charAt(i);
		}
		return text.length();
	}

	public int getConstraints() { return constraints; }

	public int getMaxSize() { return max; }

	public String getString() { return text; }

	@Override
	public String getTitle() {
		return title;
	}

	public void insert(char[] data, int offset, int length, int position)
	{
		StringBuilder out = new StringBuilder();
		out.append(text, 0, position);
		out.append(data, offset, length);
		out.append(text.substring(position));
		text = out.toString();
		render();
	}

	public void insert(String src, int position)
	{
		StringBuilder out = new StringBuilder();
		out.append(text, 0, position);
		out.append(src);
		out.append(text.substring(position));
		text = out.toString();
		render();
	}

	public void setChars(char[] data, int offset, int length)
	{
		StringBuilder out = new StringBuilder();
		out.append(data, offset, length);
		text = out.toString();
		render();
	}

	public void setConstraints(int Constraints) { constraints = Constraints;  }

	public void setInitialInputMode(String characterSubset) { mode = characterSubset; }

	public int setMaxSize(int maxSize) { max = maxSize; return max; }

	public void setString(String value) { text = value; render();}

	public void setTicker(Ticker tick) { ticker = tick; }

	public void setTitle(String s) { title = s; render();}

	public int size() { return text.length(); }


	public void keyReleased(int key)
	{
		if(listCommands==true)
		{
			keyPressedCommands(key);
			return;
		}

		switch(key)
		{
			case Mobile.NOKIA_SOFT1: doLeftCommand(); break;
			case Mobile.NOKIA_SOFT2: doRightCommand(); break;
			case Mobile.NOKIA_SOFT3: doDefaultCommand(); break;
			case Mobile.KEY_NUM0:
			case Mobile.KEY_NUM1:
			case Mobile.KEY_NUM2:
			case Mobile.KEY_NUM3:
			case Mobile.KEY_NUM4:
			case Mobile.KEY_NUM5:
			case Mobile.KEY_NUM6:
			case Mobile.KEY_NUM7:
			case Mobile.KEY_NUM8:
			case Mobile.KEY_NUM9:
			case Mobile.KEY_STAR:
			case Mobile.KEY_POUND: doDefaultCommand(); break;
		}
		render();
	}

	protected void doDefaultCommand() {

        if (FreeJ2ME.getMobile().getPlatform().getInputFrame() == null) {
            FreeJ2ME.getMobile().getPlatform().openInputFrame(null, this, text);
        }
    }

	public void pointerReleased(int x, int y) {
		super.pointerReleased(x, y);
		//

		ArrayList<Command> cmds = getCombinedCommands();
		for (int i = 0; i < cmds.size(); i++) {
			Command c = cmds.get(i);
			if (c.isInRange(x, y)) {
				doCommand(i);
				currentCommand = i;
				render();
				return;
			}
		}
		if (!listCommands) {
			if (options != null) {
				if (options.isInRange(x, y)) {
					listCommands = true;
					render();
					return;
				}
			}
			doDefaultCommand();//open text input
		} else {
			if (optionsLeft != null && optionsLeft.isInRange(x, y)) {
				doLeftCommand();
				render();
			} else if (optionsRight != null && optionsRight.isInRange(x, y)) {
				doRightCommand();
				render();
			}
		}
	}

    public void pointerPressed(int x, int y) {
        super.pointerPressed(x, y);
    }
	public void render()
	{
		super.render();
		PlatformGraphics gc = platformImage.getGraphics();
		gc.setClip(0, 0, width, height);
        if (text == null || text.length() == 0) {
            gc.setColor(0xffc0c0c0);
            gc.drawString("press ENTER", 3, 30, Graphics.LEFT | Graphics.TOP);
        } else {
            gc.setColor(0xff000000);
            gc.drawString(text, 3, 30, Graphics.LEFT | Graphics.TOP);
        }
        if (this.getDisplay().getCurrent() == this)
		{
			FreeJ2ME.getMobile().getPlatform().repaint(platformImage, 0, 0, width, height);
		}
	}
}
