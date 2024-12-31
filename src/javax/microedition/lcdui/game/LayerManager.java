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
package javax.microedition.lcdui.game;

import java.util.ArrayList;

import java.awt.Shape;

import javax.microedition.lcdui.Graphics;

import org.recompile.freej2me.FreeJ2ME;


public class LayerManager {

    protected ArrayList<Layer> layers;

    //	protected Image canvas;
//	protected PlatformGraphics gc;
    protected Shape clip;

    protected int viewX;
    protected int viewY;
    protected int viewWidth;
    protected int viewHeight;


    public LayerManager() {
        layers = new ArrayList<Layer>();

        viewWidth = FreeJ2ME.getMobile().getPlatform().lcdWidth;
        viewHeight = FreeJ2ME.getMobile().getPlatform().lcdHeight;

//		canvas = Image.createImage(width, height);
//		gc = canvas.platformImage.getGraphics();
    }

    public void append(Layer l) {
        layers.add(l);
    }

    public Layer getLayerAt(int index) {
        return layers.get(index);
    }

    public int getSize() {
        return layers.size();
    }

    public void insert(Layer l, int index) {
        layers.add(index, l);
    }

    public void paint(Graphics g, int x, int y) {
        int clipX = g.getClipX();
        int clipY = g.getClipY();
        int clipW = g.getClipWidth();
        int clipH =  g.getClipHeight();


        // translate the LayerManager co-ordinates to Screen co-ordinates
        g.translate(x - viewX, y - viewY);
        // set the clip to view window
        g.clipRect(viewX, viewY, viewWidth, viewHeight);

        // draw last to first
        for (int i = layers.size() ; --i >= 0; ) {
            Layer comp = layers.get(i);
            if (comp.visible) {
                comp.paint(g);
            }
        }
        g.translate(-x + viewX, -y + viewY);
        g.setClip(clipX, clipY, clipW, clipH);
    }


    public void remove(Layer l) {
        layers.remove(l);
    }

    public void setViewWindow(int wx, int wy, int wwidth, int wheight) {
        viewX = wx;
        viewY = wy;
        viewWidth = wwidth;
        viewHeight = wheight;
    }

}
