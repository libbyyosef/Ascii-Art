package image;

import java.util.Iterator;

/**
 * A package-private class of the package image.
 */
class ImageIterator implements Iterable<Image> {
    private final Image image;
    private final int subImageWidth;
    private final int subImageHeight;

    /**
     * constructs imageIterator object
     *
     * @param image      image to iterate on its subImages
     * @param edgeLength each subImage is a square, so its length is its edge length and its width and height
     */
    public ImageIterator(Image image, int edgeLength) {
        this.subImageWidth = edgeLength;
        this.subImageHeight = edgeLength;
        this.image = image;
    }


    /**
     * @return new Image iterator that iterates  on sub images of image
     */
    @Override
    public Iterator<Image> iterator() {
        return new Iterator<>() {
            int x = 0, y = 0;

            @Override
            public boolean hasNext() {
                return y + subImageHeight <= image.getHeight();
            }

            @Override
            public Image next() {
                SubImage subImage1 = new SubImage(image, x, y, subImageWidth);
                x += subImageWidth;
                if (x + subImageWidth > image.getWidth()) {
                    x = 0;
                    y += subImageHeight;
                }
                return subImage1;
            }
        };
    }
}

