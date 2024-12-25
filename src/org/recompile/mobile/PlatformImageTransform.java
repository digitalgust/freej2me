package org.recompile.mobile;

import java.awt.geom.AffineTransform;

public class PlatformImageTransform {
    //for drawImage
    public AffineTransform transform = new AffineTransform();
    public int width;
    public int height;
    public int transformType;

    //for drawRegion
    public int regionWidth;
    public int regionHeight;
    public int regionX;
    public int regionY;

    public void reset() {
        transform.setToIdentity();
        transformType = 0;
        regionWidth = 0;
        regionHeight = 0;
        regionX = 0;
        regionY = 0;
    }
}
