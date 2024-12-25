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
package org.recompile.mobile;

import com.nokia.mid.ui.DirectGraphics;
import org.recompile.freej2me.FreeJ2ME;

import java.awt.*;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;

public class PlatformImage extends javax.microedition.lcdui.Image {
    protected BufferedImage canvas;
    protected PlatformGraphics gc;

    public boolean isNull = false;

    public PlatformGraphics getGraphics() {
        return gc;
    }

    protected void createGraphics() {
        gc = new PlatformGraphics(this);
        gc.setColor(0x000000);
    }

    public PlatformImage(int Width, int Height) {
        // Create blank Image
        width = Width;
        height = Height;

        canvas = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_ARGB);
        createGraphics();

        gc.setColor(0xFFFFFF);
        gc.fillRect(0, 0, width, height);
        gc.setColor(0x000000);

        platformImage = this;
    }

    public PlatformImage(String name) {
        // Create Image from resource name
        // System.out.println("Image From Resource Name");
        BufferedImage temp;

        InputStream stream = FreeJ2ME.getMobile().getPlatform().loader.getMIDletResourceAsStream(name);

        if (stream == null) {
            System.out.println("Couldn't Load Image Stream (can't find " + name + ")");
            isNull = true;
        } else {
            try {
                temp = ImageIO.read(stream);
                width = (int) temp.getWidth();
                height = (int) temp.getHeight();

                canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                createGraphics();

                gc.drawImage2(temp, 0, 0);
            } catch (Exception e) {
                System.out.println("Couldn't Load Image Stream " + name);
                e.printStackTrace();
                isNull = true;
            }
        }
        platformImage = this;
    }

    public PlatformImage(InputStream stream) {
        // Create Image from InputStream
        // System.out.println("Image From Stream");
        BufferedImage temp;
        try {
            temp = ImageIO.read(stream);
            width = (int) temp.getWidth();
            height = (int) temp.getHeight();

            canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            createGraphics();

            gc.drawImage2(temp, 0, 0);
        } catch (Exception e) {
            System.out.println("Couldn't Load Image Stream");
            isNull = true;
        }

        platformImage = this;
    }

    public PlatformImage(Image source) {
        // Create Image from Image
        width = source.platformImage.width;
        height = source.platformImage.height;

        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        createGraphics();

        gc.drawImage2(source.platformImage.getCanvas(), 0, 0);

        platformImage = this;
    }

    public PlatformImage(byte[] imageData, int imageOffset, int imageLength) {
        // Create Image from Byte Array Range (Data is PNG, JPG, etc.)
        try {
            InputStream stream = new ByteArrayInputStream(imageData, imageOffset, imageLength);

            BufferedImage temp;

            temp = ImageIO.read(stream);
            width = (int) temp.getWidth();
            height = (int) temp.getHeight();

            canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            createGraphics();

            gc.drawImage2(temp, 0, 0);
        } catch (Exception e) {
//			System.out.println("Couldn't Load Image Data From Byte Array");
//			canvas = new BufferedImage(Mobile.getPlatform().lcdWidth, Mobile.getPlatform().lcdHeight, BufferedImage.TYPE_INT_ARGB);
//			createGraphics();
//			//System.out.println(e.getMessage());
            e.printStackTrace();
            isNull = true;
        }

        platformImage = this;
    }

    public PlatformImage(int[] rgb, int Width, int Height, boolean processAlpha) {
        // createRGBImage (Data is ARGB pixel data)
        width = Width;
        height = Height;

        if (width < 1) {
            width = 1;
        }
        if (height < 1) {
            height = 1;
        }

        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        createGraphics();

        gc.drawRGB(rgb, 0, width, 0, 0, width, height, true);

        platformImage = this;
    }

    public PlatformImage(Image image, int x, int y, int Width, int Height, int transform) {
        // Create Image From Sub-Image, Transformed //
        BufferedImage sub = image.platformImage.canvas.getSubimage(x, y, Width, Height);

        PlatformImageTransform pt = midpTransformImage(sub, transform);
        canvas = getTransformedImage(sub, pt);
        createGraphics();

        width = (int) canvas.getWidth();
        height = (int) canvas.getHeight();

        platformImage = this;
    }

    public void getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height) {
        canvas.getRGB(x, y, width, height, rgbData, offset, scanlength);
    }

    public int getARGB(int x, int y) {
        return canvas.getRGB(x, y);
    }

    public int getPixel(int x, int y) {
        int[] rgbData = {0};
        canvas.getRGB(x, y, 1, 1, rgbData, 0, 1);
        return rgbData[0];
    }

    public void setPixel(int x, int y, int color) {
        int[] rgbData = {color};
        gc.drawRGB(rgbData, 0, 1, x, y, 1, 1, false);
    }


    /**
     * 多种平台变换
     */
    static ThreadLocal<PlatformImageTransform> imageTransform = new ThreadLocal<PlatformImageTransform>() {
        @Override
        protected PlatformImageTransform initialValue() {
            return new PlatformImageTransform();
        }
    };

    public static PlatformImageTransform midpTransformImage(BufferedImage image, int regionX, int regionY, int regionWidth, int regionHeight, int transform) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        PlatformImageTransform pt = imageTransform.get();
        pt.reset();
        pt.transformType = transform;
        AffineTransform af = pt.transform;

        switch (transform) {
            case Sprite.TRANS_NONE:
                pt.width = width;
                pt.height = height;
                pt.regionX = regionX;
                pt.regionY = regionY;
                pt.regionWidth = regionWidth;
                pt.regionHeight = regionHeight;
                break;

            case Sprite.TRANS_ROT90:
                af.translate(height, 0);
                af.rotate(Math.PI / 2);
                pt.width = height;
                pt.height = width;
                pt.regionX = height - regionY - regionHeight;
                pt.regionY = regionX;
                pt.regionWidth = regionHeight;
                pt.regionHeight = regionWidth;
                break;

            case Sprite.TRANS_ROT180:
                af.translate(width, height);
                af.rotate(Math.PI);
                pt.width = width;
                pt.height = height;
                pt.regionX = width - regionX - regionWidth;
                pt.regionY = height - regionY - regionHeight;
                pt.regionWidth = regionWidth;
                pt.regionHeight = regionHeight;
                break;

            case Sprite.TRANS_ROT270:
                af.translate(0, width);
                af.rotate(Math.PI * 3 / 2);
                pt.width = height;
                pt.height = width;
                pt.regionX = regionY;
                pt.regionY = width - regionX - regionWidth;
                pt.regionWidth = regionHeight;
                pt.regionHeight = regionWidth;
                break;

            case Sprite.TRANS_MIRROR:
                af.translate(width, 0);
                af.scale(-1, 1);
                pt.width = width;
                pt.height = height;
                pt.regionX = width - regionX - regionWidth;
                pt.regionY = regionY;
                pt.regionWidth = regionWidth;
                pt.regionHeight = regionHeight;
                break;

            case Sprite.TRANS_MIRROR_ROT90:
                af.translate(height, 0);
                af.rotate(Math.PI / 2);
                af.translate(width, 0);
                af.scale(-1, 1);
                pt.width = height;
                pt.height = width;
                pt.regionX = height - regionY - regionHeight;
                pt.regionY = width - regionX - regionWidth;
                pt.regionWidth = regionHeight;
                pt.regionHeight = regionWidth;
                break;

            case Sprite.TRANS_MIRROR_ROT180:
                af.translate(width, 0);
                af.scale(-1, 1);
                af.translate(width, height);
                af.rotate(Math.PI);
                pt.width = width;
                pt.height = height;
                pt.regionX = regionX;
                pt.regionY = height - regionY - regionHeight;
                pt.regionWidth = regionWidth;
                pt.regionHeight = regionHeight;
                break;

            case Sprite.TRANS_MIRROR_ROT270:
                af.translate(0, width);
                af.rotate(Math.PI * 3 / 2);
                af.translate(width, 0);
                af.scale(-1, 1);
                pt.width = height;
                pt.height = width;
                pt.regionX = regionY;
                pt.regionY = regionX;
                pt.regionWidth = regionHeight;
                pt.regionHeight = regionWidth;
                break;
        }

        return pt;
    }

    public static PlatformImageTransform midpTransformImage(BufferedImage image, int transform) {
        int width = image.getWidth();
        int height = image.getHeight();

        PlatformImageTransform pt = imageTransform.get();
        pt.reset();
        pt.transformType = transform;
        AffineTransform af = pt.transform;

        switch (transform) {
            case Sprite.TRANS_NONE:
                break;

            case Sprite.TRANS_ROT90:
                af.translate(height, 0);
                af.rotate(Math.PI / 2);
                pt.width = height;
                pt.height = width;
                break;

            case Sprite.TRANS_ROT180:
                af.translate(width, height);
                af.rotate(Math.PI);
                break;

            case Sprite.TRANS_ROT270:
                af.translate(0, width);
                af.rotate(Math.PI * 3 / 2);
                pt.width = height;
                pt.height = width;
                break;

            case Sprite.TRANS_MIRROR:
                af.translate(width, 0);
                af.scale(-1, 1);
                break;

            case Sprite.TRANS_MIRROR_ROT90:
                af.translate(height, 0);
                af.rotate(Math.PI / 2);
                af.translate(width, 0);
                af.scale(-1, 1);
                pt.width = height;
                pt.height = width;
                break;

            case Sprite.TRANS_MIRROR_ROT180:
                af.translate(width, 0);
                af.scale(-1, 1);
                af.translate(width, height);
                af.rotate(Math.PI);
                break;

            case Sprite.TRANS_MIRROR_ROT270:
                af.translate(0, width);
                af.rotate(Math.PI * 3 / 2);
                af.translate(width, 0);
                af.scale(-1, 1);
                pt.width = height;
                pt.height = width;
                break;
        }

        return pt;
    }


    public static PlatformImageTransform nokiaTransformImage(BufferedImage image, int manipulation) {
        //DirectGraphics manipulation order : rotate -> vertical mirror-> horizontal mirror
        //Sprite manipulation order: horizontal mirror -> rotate
        final int HV = DirectGraphics.FLIP_HORIZONTAL | DirectGraphics.FLIP_VERTICAL;
        final int HV90 = DirectGraphics.FLIP_HORIZONTAL | DirectGraphics.FLIP_VERTICAL | DirectGraphics.ROTATE_90;
        final int HV180 = DirectGraphics.FLIP_HORIZONTAL | DirectGraphics.FLIP_VERTICAL | DirectGraphics.ROTATE_180;
        final int HV270 = DirectGraphics.FLIP_HORIZONTAL | DirectGraphics.FLIP_VERTICAL | DirectGraphics.ROTATE_270;
        final int H90 = DirectGraphics.FLIP_HORIZONTAL | DirectGraphics.ROTATE_90;
        final int H180 = DirectGraphics.FLIP_HORIZONTAL | DirectGraphics.ROTATE_180;
        final int H270 = DirectGraphics.FLIP_HORIZONTAL | DirectGraphics.ROTATE_270;
        final int V90 = DirectGraphics.FLIP_VERTICAL | DirectGraphics.ROTATE_90;
        final int V180 = DirectGraphics.FLIP_VERTICAL | DirectGraphics.ROTATE_180;
        final int V270 = DirectGraphics.FLIP_VERTICAL | DirectGraphics.ROTATE_270;
        switch (manipulation) {
            case V180:
            case DirectGraphics.FLIP_HORIZONTAL:
                return midpTransformImage(image, Sprite.TRANS_MIRROR);
            case H180:
            case DirectGraphics.FLIP_VERTICAL:
                return midpTransformImage(image, Sprite.TRANS_MIRROR_ROT180);
            case HV90:
            case DirectGraphics.ROTATE_90:
                return midpTransformImage(image, Sprite.TRANS_ROT270);
            case HV:
            case DirectGraphics.ROTATE_180:
                return midpTransformImage(image, Sprite.TRANS_ROT180);
            case HV270:
            case DirectGraphics.ROTATE_270:
                return midpTransformImage(image, Sprite.TRANS_ROT90);
            case V270:
            case H90:
                //return PlatformImage.transformImage(PlatformImage.transformImage(image, Sprite.TRANS_MIRROR), Sprite.TRANS_ROT270);
                return midpTransformImage(image, Sprite.TRANS_MIRROR_ROT270);
            case V90:
            case H270:
                return midpTransformImage(image, Sprite.TRANS_MIRROR_ROT90);
            case 0: /* No Manipulation */
            case HV180:
                break;
            default:
                System.out.println("manipulateImage " + manipulation + " not defined");
        }
        PlatformImageTransform pt = new PlatformImageTransform();
        pt.transformType = Sprite.TRANS_NONE;
        pt.width = image.getWidth();
        pt.height = image.getHeight();
        return pt;
    }

    public BufferedImage getCanvas() {
        return canvas;
    }

    public static BufferedImage getTransformedImage(BufferedImage image, PlatformImageTransform pt) {
        if (pt.transformType == Sprite.TRANS_NONE) {
            return image;
        }
        BufferedImage transimage = new BufferedImage(pt.width, pt.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gc = transimage.createGraphics();
        gc.drawImage(image, pt.transform, null);

        return transimage;
    }
}
