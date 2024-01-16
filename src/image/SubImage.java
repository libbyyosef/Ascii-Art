package image;

import java.awt.*;


public class SubImage implements Image {
    private final int row;
    private final int col;

    private final int subImageSize;
    Image image;

    public SubImage(Image image, int row, int col, int subImageSize) {
        this.image = image;
        this.row = row;
        this.col = col;
        this.subImageSize = subImageSize;
    }


    /**
     * @param x location of left top corner of the sub image in the image
     * @param y location of left top corner of the sub image in the image
     * @return the pixel in the original image at location(x,y)
     */
    @Override
    public Color getPixel(int x, int y) {
        Color color = image.getPixel(y, x);
        return new Color(color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * @return size of an edge of subImage square-the width
     */
    @Override
    public int getWidth() {
        return subImageSize;
    }

    /**
     * @return size of an edge of subImage square-the height
     */
    @Override
    public int getHeight() {
        return subImageSize;
    }

    /**
     * @return the row of the subImage
     */
    public int getRow() {
        return row;
    }

    /**
     * @return the column of the subImage
     */
    public int getCol() {
        return col;
    }

}



