package image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * A package-private class of the package image.
 *
 * @author Dan Nirel
 */
class FileImage implements Image {
    private static final int RESIZE_FACTOR = 2;
    private static final Color DEFAULT_COLOR = Color.WHITE;

    private final Color[][] pixelArray;
    private final java.awt.image.BufferedImage img;

    /**
     * constructor of file Image object, add white pixels padding to the image if nedded
     *
     * @param filename name of image file
     * @throws IOException throws IOException
     */
    public FileImage(String filename) throws IOException {
        java.awt.image.BufferedImage im = ImageIO.read(new File(filename));
        this.img = im;
        int origWidth = im.getWidth(), origHeight = im.getHeight();
        int newWidth = origWidth;
        int newHeight = origHeight;
        /*padding width*/
        while ((int) (Math.ceil((Math.log(newWidth) / Math.log(2))))
                != (int) (Math.floor(((Math.log(newWidth) / Math.log(2)))))) {
            newWidth += RESIZE_FACTOR;
        }
        /*padding height*/
        while ((int) (Math.ceil((Math.log(newHeight) / Math.log(2))))
                != (int) (Math.floor(((Math.log(newHeight) / Math.log(2)))))) {
            newHeight += RESIZE_FACTOR;
        }

        pixelArray = new Color[newHeight][newWidth];
        int rowsPadding = (newHeight - origHeight) / 2;
        int colsPadding = (newWidth - origWidth) / 2;
        /*setting white color at padding pixels and setting the other pixels to the color in the original
        image*/
        for (int rows = 0; rows < newHeight; rows++) {
            for (int cols = 0; cols < newWidth; cols++) {
                /*if it is at padding pixels - white*/
                if (cols < colsPadding || colsPadding + origWidth <= cols || rowsPadding > rows || rowsPadding + origHeight <= rows) {
                    pixelArray[rows][cols] = DEFAULT_COLOR;
                } else {
                    /*original image colors*/
                    int originPixelColor = this.img.getRGB(cols - colsPadding, rows - rowsPadding);
                    pixelArray[rows][cols] = new Color(originPixelColor);

                }
            }
        }
    }

    /**
     * @return width of image
     */
    @Override
    public int getWidth() {
        return pixelArray[0].length;
    }

    /**
     * @return height of image
     */
    @Override
    public int getHeight() {
        return pixelArray.length;
    }

    /**
     * return the pixel at location (x,y)
     *
     * @param x column
     * @param y row
     * @return the pixel at row y and column x
     */
    @Override
    public Color getPixel(int x, int y) {
        if(y>=pixelArray.length||x>=pixelArray[0].length||y<0||x<0){
            return DEFAULT_COLOR;
        }
        Color pixel = pixelArray[y][x];
        return new Color(pixel.getRed(), pixel.getGreen(), pixel.getBlue());
    }
}