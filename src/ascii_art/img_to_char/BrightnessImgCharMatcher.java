package ascii_art.img_to_char;


import image.Image;
import image.SubImage;

import java.awt.*;
import java.util.HashMap;

public class BrightnessImgCharMatcher {
    private static final int NUM_OF_PIXELS = 16;
    private static final int RGB_MAX_VAL = 255;
    private static final double RED_FACTOR = 0.2126;
    private static final double GREEN_FACTOR = 0.7152;
    private static final double BLUE_FACTOR = 0.0722;
    private static final double MIN_DIFFERENCE_BEFORE_INITIALIZATION = -1;

    private final Image img;
    private final String font;
    private double minBrightness;
    private double maxBrightness;

    private final HashMap<Character, Double> brightnesses;
    private final HashMap<Character, Double> brightnessesBefore;

    /**
     * constructor of BrightnessImgCharMatcher
     *
     * @param img  image interface
     * @param font font of the chars
     */
    public BrightnessImgCharMatcher(Image img, String font) {
        this.img = img;
        this.font = font;
        this.brightnesses = new HashMap<>();
        this.brightnessesBefore = new HashMap<>();
        minBrightness = 1;//will be updated at first inserting of characters
        maxBrightness = 0;////will be updated at first inserting of characters
    }

    /**
     * this method counts the white pixels of a char and return it divided by the total number of pixels
     * in our  case each char has 16*16 pixels, as the exercise instructions
     *
     * @param character character
     * @return number of white pixels of a char divided by total number of pixels of char
     */
    private double NormalizeBrightnessByDivision(Character character) {
        int numWhitePixels = 0;
        /*converts the pixels into true and false 2 dimensions array*/
        boolean[][] charBrightness = CharRenderer.getImg(character, NUM_OF_PIXELS, font);
        for (int i = 0; i < charBrightness.length; i++) {
            for (int j = 0; j < charBrightness[0].length; j++) {
                if (charBrightness[i][j]) {
                    numWhitePixels++;
                }
            }
        }
        return numWhitePixels / (double) (charBrightness.length * charBrightness[0].length);
    }


    /**
     * calculate the linear stretch of a character according to its brightness and the minimum and maximum
     * brightness
     *
     * @param character calc this character linear stretch
     * @return new value of brightness after stretch
     */
    private double NormalizeBrightnessByLinearStretch(Character character) {
        return (brightnessesBefore.get(character) - minBrightness) / (maxBrightness - minBrightness);
    }


    /**
     * calc the grey color of a given color
     *
     * @param pixel color of a pixel
     * @return new color of pixel - grey color
     */
    private double calcGreyPixel(Color pixel) {
        return pixel.getRed() * RED_FACTOR + pixel.getGreen() * GREEN_FACTOR + pixel.getBlue() * BLUE_FACTOR;
    }


    /**
     * calc the brightness of subImage, counts its grey values and divided by number of pixels and maximum
     * rgb value
     *
     * @param subImage a subImage of the image
     * @return the brightness value of a given subImage
     */
    private double subImageBrightness(SubImage subImage) {
        double sumGreyPixels = 0;
        int edgeSize = subImage.getWidth();
        int col = subImage.getCol();//y
        int row = subImage.getRow();//x
        /*iterate pixels in subImage*/
        for (int j = col; j < col + edgeSize; j++) {
            for (int i = row; i < row + edgeSize; i++) {
                Color pixel = subImage.getPixel(j, i);
                double greyPixel = this.calcGreyPixel(pixel);
                sumGreyPixels += greyPixel;
            }
        }
        return sumGreyPixels / (double) (edgeSize * edgeSize * RGB_MAX_VAL);
    }

    /**
     * given a subImage, replace it with the character that its brightness is the most similar to the
     * subImage brightness
     *
     * @param subImage subImage of the Image
     * @return the most similar character (similar by brightness values)
     */
    private Character replaceSubImageWithChar(SubImage subImage) {
        double subImageBrightness = subImageBrightness(subImage);
        double difference;
        double minDifference = MIN_DIFFERENCE_BEFORE_INITIALIZATION;
        Character closestChar = null;
        for (var ch : brightnesses.entrySet()) {
            difference = Math.abs(subImageBrightness - ch.getValue());
            if (minDifference == MIN_DIFFERENCE_BEFORE_INITIALIZATION) {
                minDifference = difference;
                closestChar = ch.getKey();
            }
            if (difference < minDifference) {
                minDifference = difference;
                closestChar = ch.getKey();
            }
        }
        return closestChar;
    }

    /**
     * if there are more than character, applied on them the linear stretch
     *
     * @param charSet characters of user input
     */
    private void updateLinearStretchCharBrightness(Character[] charSet) {
        for (var ch : charSet) {
            if (brightnessesBefore.containsKey(ch)) {
                double linearStretch = NormalizeBrightnessByLinearStretch(ch);
                brightnesses.put(ch, linearStretch);
            }
        }
    }

    /**
     * inserting to the characters and their brightnesses(before linear stretch) only if they are not in it
     * already and there is no other character with this brightness already, also update min and max variables
     *
     * @param charSet characters of user input
     */
    private void updateCharsBrightness(Character[] charSet) {
        for (var ch : charSet) {
            if (!brightnessesBefore.containsKey(ch)) {
                double brightness = NormalizeBrightnessByDivision(ch);
                if (!brightnessesBefore.containsValue(brightness)) {
                    brightnessesBefore.put(ch, brightness);
                    if (brightness <= minBrightness) {
                        minBrightness = brightness;
                    } else if (brightness >= maxBrightness) {
                        maxBrightness = brightness;
                    }
                }
            }
        }

    }

    /**
     * for each subImage, replace it with the most fit character from the user input characters, return the
     * image assemble from characters only
     *
     * @param numCharsInRow number of subImages in a row
     * @param charSet       characters that the user insert, only them will assemble the image
     * @return 2 dimension array of characters that assemble the image
     */
    public char[][] chooseChars(int numCharsInRow, Character[] charSet) {
        int edgeSize = img.getWidth() / numCharsInRow;
        updateCharsBrightness(charSet);
        if (charSet.length > 1) {
            updateLinearStretchCharBrightness(charSet);
        }
        int row = img.getHeight() / edgeSize;
        char[][] chars = new char[row][numCharsInRow];
        int i = 0, j = 0;
        for (Image subImage : img.subImages(edgeSize)) {
            chars[i][j] = replaceSubImageWithChar((SubImage) subImage);
            j++;
            if (j == numCharsInRow) {
                j = 0;
                i++;
            }
        }
        return chars;
    }
}
