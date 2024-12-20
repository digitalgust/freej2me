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

import org.recompile.mobile.Mobile;
import org.recompile.mobile.PlatformImage;
import org.recompile.mobile.PlatformGraphics;

public abstract class Displayable
{
	final static int ITEM_H = 18;

	public PlatformImage platformImage;

	public int width = 0;

	public int height = 0;

	public boolean fullScreen = false;
	
	protected String title = "";

	protected ArrayList<Command> commands = new ArrayList<Command>();

	protected ArrayList<Item> items = new ArrayList<Item>();
	//for save container(form) commands and item commands
	protected ArrayList<Command> combinedCommands = new ArrayList<Command>();

	protected CommandListener commandlistener;

	protected boolean listCommands = false;
	
	protected int currentCommand = 0;

	protected int currentItem = -1;

	public Ticker ticker;

	protected Command options, optionsLeft, optionsRight;

	public Displayable()
	{
		width = Mobile.getPlatform().lcdWidth;
		height = Mobile.getPlatform().lcdHeight;
	}

	public void addCommand(Command cmd)
	{ 
		try
		{
			commands.add(cmd);
		}
		catch (Exception e)
		{
			System.out.println("Problem Adding Command: "+e.getMessage());
		}
	}

	public void removeCommand(Command cmd) { commands.remove(cmd); }
	
	public int getWidth() { return width; }

	public int getHeight() { return height; }
	
	public String getTitle() { return title; }

	public void setTitle(String text) { title = text; }        

	public boolean isShown() { return true; }

	public Ticker getTicker() { return ticker; }

	public void setTicker(Ticker tick) { ticker = tick; }
	
	public void setCommandListener(CommandListener listener) { commandlistener = listener; }

	protected void sizeChanged(int width, int height) { }

	public Display getDisplay() { return Mobile.getDisplay(); }

	public ArrayList<Command> getCommands() { return commands; }


	public void keyPressed(int key) { }
	public void keyReleased(int key) { }
	public void keyRepeated(int key) { }
	public void pointerDragged(int x, int y) { }
	public void pointerPressed(int x, int y) { }
	public void pointerReleased(int x, int y) { }
	public void showNotify() { }
	public void hideNotify() { }

	public void notifySetCurrent() {
		render();
	}

	public void render()
	{
		if(listCommands==true)
		{
			renderCommands();
		}
		else
		{
			renderItems();
		}
	}

	public void renderItems()
	{
		PlatformGraphics gc = platformImage.getGraphics();
		// Draw Background:
		gc.setColor(0xFFFFFF);
		gc.fillRect(0,0,width,height);
		gc.setColor(0x000000);

		// Draw Title:
		gc.drawString(title, width/2, 2, Graphics.HCENTER);
		gc.drawLine(0, 20, width, 20);
		gc.drawLine(0, height-20, width, height-20);

		if(items.size()>0)
		{
			if(currentItem<0) { currentItem = 0; }
			// Draw list items //
			int ah = height - 50; // allowed height
			int max = (int)Math.floor(ah / ITEM_H); // max items per page
			if(items.size()<max) { max = items.size(); }

			int page = 0;
			page = (int)Math.floor(currentItem/max); // current page
			int first = page * max; // first item to show
			int last = first + max - 1;

			if(last>=items.size()) { last = items.size()-1; }
			
			int y = 25;
			for(int i=first; i<=last; i++)
			{	
				if(currentItem == i)
				{
					gc.fillRect(0, y, width, ITEM_H);
					gc.setColor(0xFFFFFF);
				}
				Item item = items.get(i);
				item.setBondle(0, y, getWidth(), ITEM_H);
				item.render(gc, 0, y, getWidth(), ITEM_H);

				gc.setColor(0x000000);
				if (items.get(i) instanceof ImageItem) {
					gc.drawImage(((ImageItem) items.get(i)).getImage(), width / 2, y, Graphics.HCENTER);
				}
				y+=ITEM_H;
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
		switch(combinedCommands.size())
		{
			case 0: break;
			case 1:
				Command cmd = combinedCommands.get(0);
				cmd.setBondle(0, height-ITEM_H, width/3, ITEM_H);
				gc.drawString(combinedCommands.get(0).getLabel(), 3, height-ITEM_H, Graphics.LEFT);
				gc.drawString(""+(currentItem+1)+" of "+items.size(), width-3, height-17, Graphics.RIGHT);
				break;
			case 2:
				cmd = combinedCommands.get(0);
				cmd.setBondle(0, height - ITEM_H, width / 3, ITEM_H);
				cmd = combinedCommands.get(1);
				cmd.setBondle(width * 2 / 3, height - ITEM_H, width / 3, ITEM_H);
				gc.drawString(combinedCommands.get(0).getLabel(), 3, height-ITEM_H, Graphics.LEFT);
				gc.drawString(combinedCommands.get(1).getLabel(), width-3, height-ITEM_H, Graphics.RIGHT);
				break;
			default:
				gc.drawString("Options", 3, height-17, Graphics.LEFT);
		}

		if(this.getDisplay().getCurrent() == this)
		{
			Mobile.getPlatform().repaint(platformImage, 0, 0, width, height);
		}
	}

	protected void renderCommands()
	{
		PlatformGraphics gc = platformImage.getGraphics();

		// Draw Background:
		gc.setColor(0xFFFFFF);
		gc.fillRect(0,0,width,height);
		gc.setColor(0x000000);
		
		// Draw Title:
		gc.drawString("Options", width/2, 2, Graphics.HCENTER);
		gc.drawLine(0, 20, width, 20);
		gc.drawLine(0, height-20, width, height-20);

		combinAll();
		if(combinedCommands.size()>0)
		{
			if(currentCommand<0) { currentCommand = 0; }
			// Draw commands //
			int ah = height - 50; // allowed height
			int max = (int)Math.floor(ah / ITEM_H); // max items per page
			if(combinedCommands.size()<max) { max = combinedCommands.size(); }

			int page = 0;
			page = (int)Math.floor(currentCommand/max); // current page
			int first = page * max; // first item to show
			int last = first + max - 1;

			if(last>= combinedCommands.size()) { last = combinedCommands.size()-1; }

			int y = 25;
			for(int i=first; i<=last; i++)
			{	
				if(currentCommand == i)
				{
					gc.fillRect(0,y,width,ITEM_H);
					gc.setColor(0xFFFFFF);
				}

				Command cmd = combinedCommands.get(i);
				cmd.setBondle(0, y, width, ITEM_H);
				gc.drawString(cmd.getLabel(), width/2, y, Graphics.HCENTER);

				gc.setColor(0x000000);
				y+=ITEM_H;
			}
		}
		gc.drawString("Okay", 3, height-17, Graphics.LEFT);
		gc.drawString("Back", width-3, height-17, Graphics.RIGHT);

		if(this.getDisplay().getCurrent() == this)
		{
			Mobile.getPlatform().repaint(platformImage, 0, 0, width, height);
		}
	}

	private void combinAll(){
		Item curItem = null;
		if (currentItem >= 0 && currentItem < items.size()) {
			curItem = items.get(currentItem);
		}

		combinedCommands.clear();
		if (curItem != null) combinedCommands.addAll(curItem.getCommands());
		combinedCommands.addAll(commands);
	}

	private Item getCurrentItem(){
		Item curItem = null;
		if (currentItem >= 0 && currentItem < items.size()) {
			curItem = items.get(currentItem);
		}
		return curItem;
	}

	protected void keyPressedCommands(int key)
	{
		combinAll();
		switch(key)
		{
			case Mobile.KEY_NUM2: currentCommand--; break;
			case Mobile.KEY_NUM8: currentCommand++; break;
			case Mobile.NOKIA_UP: currentCommand--; break;
			case Mobile.NOKIA_DOWN: currentCommand++; break;
			case Mobile.NOKIA_SOFT1: doLeftCommand(); break;
			case Mobile.NOKIA_SOFT2: doRightCommand(); break;
			case Mobile.KEY_NUM5: doDefaultCommand(); break;
		}
		if(currentCommand>= combinedCommands.size()) { currentCommand = 0; }
		if(currentCommand<0) { currentCommand = combinedCommands.size()-1; }
		if(listCommands==true) { renderCommands(); }
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

	protected void doDefaultCommand()
	{
		//doCommand(0);
		Item curItem=getCurrentItem();
		if(curItem!=null){
			curItem.doActive();
		}
	}

	protected void doLeftCommand()
	{
		if(combinedCommands.size()>2)
		{
			if(listCommands == true)
			{
				doCommand(currentCommand);
			}
			else
			{
				listCommands = true;
				currentCommand = 0;
				render();
			}
			return;
		}
		else
		{
			if(combinedCommands.size()>0 && combinedCommands.size()<=2)
			{
				doCommand(0);
			}
		}
	}

	protected void doRightCommand()
	{
		if(listCommands==true)
		{
			listCommands = false;
			currentCommand = 0;
			render();
		}
		else
		{
			if(combinedCommands.size()>0 && combinedCommands.size()<=2)
			{
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
}
