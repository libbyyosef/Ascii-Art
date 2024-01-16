package ascii_art;

import ascii_art.img_to_char.BrightnessImgCharMatcher;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

import java.util.*;

public class Shell {
    private static final String WRONG_INPUT_MSG = "Did not %s due to incorrect %s";
    private static final int MIN_PIXELS_PER_CHAR = 2;
    private static final String CURRENT_CHARS_NUM_MSG = "Width set to <%d>";
    private static final int INITIAL_CHARS_IN_ROW = 64;
    private static final String OUT_OF_RANGES_MSG = "Did not change due to exceeding boundaries";
    private static final String FILE_NAME = "out.html";
    private static final String FONT = "Courier New";
    private static final char ZERO = '0';
    private static final char NINE = '9';
    private static final String UP = "up";
    private static final String DOWN = "down";
    private static final String ADD = "add";
    private static final String REMOVE = "remove";
    private static final String CHARS = "chars";
    private static final String RENDER = "render";
    private static final String CONSOLE = "console";
    private static final int RESIZE_FACTOR = 2;
    private static final String EXIT = "exit";
    private static final String RES = "res ";
    private static final String ARROWS = ">>> ";
    private static final String ALL = "all";
    private static final String CHANGE = "change";
    private static final String FORMAT = "format";
    private static final String SPACE = "space";
    private static final String COMMAND = "command";
    private static final int ASCII_START_VAL = 32;
    private static final int ASCII_END_VAL = 126;
    private static final char SPACE_CHAR = 32;

    private static final char HYPHEN = '-';
    private static final String SPACE_STR = " ";
    private final Scanner scanner;
    private final int minCharsInRow;
    private final int maxCharsInRow;
    private int charsInRow;
    private final Image img;
    private final HashSet<Character> characters;


    /**
     * constructor of the user input shell
     *
     * @param img image to make grey and assembled by ascii values
     */
    public Shell(Image img) {
        /*given code*/
        minCharsInRow = Math.max(1, img.getWidth() / img.getHeight());
        maxCharsInRow = img.getWidth() / MIN_PIXELS_PER_CHAR;
        charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow),
                minCharsInRow);
        /*end of given code*/
        scanner = new Scanner(System.in);
        this.img = img;
        this.characters = new HashSet<>();
        /*initialize the characters to be 0-9 as requested*/
        for (char c = ZERO; c <= NINE; c++) {
            characters.add(c);
        }
    }

    /**
     * this method add char c if add is true and remove char c if remove is true
     *
     * @param add    true if it should add char c, false otherwise
     * @param remove true if it should remove character c, false otherwise
     * @param c      char to add or remove
     */
    private void addOrRemove(boolean add, boolean remove, char c) {
        if (add) {
            characters.add(c);
        } else if (remove) {
            characters.remove(c);
        }
    }


    /**
     * adds or removes all the chars from left char to right char
     *
     * @param leftChar  the left char in the range of user input to start add/remove from
     * @param rightChar the right char in the range of user input to start add/remove from
     * @param add       true if it should add the characters in range left char - true char
     * @param remove    true if it should add the characters in range left char - true char
     */
    private void addCharsInRange(char leftChar, char rightChar, boolean add, boolean remove) {
        if (leftChar == rightChar) {
            addOrRemove(add, remove, leftChar);
        } else if (leftChar < rightChar) {
            for (char c = leftChar; c <= rightChar; c++) {
                addOrRemove(add, remove, c);
            }
        } else {
            for (char c = leftChar; c >= rightChar; c--) {
                addOrRemove(add, remove, c);
            }
        }
    }

    /**
     * prints failure message if got wrong input of add or remove command
     *
     * @param add    true if it to add something, false otherwise
     * @param remove true if fail to remove something,false otherwise
     */
    private void printFailureMsg(boolean add, boolean remove) {
        if (add) {
            System.out.println(String.format(WRONG_INPUT_MSG, ADD, FORMAT));
        }
        if (remove) {
            System.out.println(String.format(WRONG_INPUT_MSG, REMOVE, FORMAT));
        }
    }

    /**
     * add or remove the valid chars that the user requested, if not valid, prints relevant message
     *
     * @param add    true if it should add the chars in input, false otherwise(before the validity check)
     * @param remove true if it should remove the chars in input, false otherwise(before the validity check)
     * @param words
     */
    private void addRemoveCases(boolean add, boolean remove, String[] words) {
        if (words.length != 2) {
            printFailureMsg(add, remove);
            return;
        }
        if (words[1].length() == 1) {
            /*format add _ or remove _ when _ is a valid char*/
            addOrRemove(add, remove, words[1].charAt(0));
        } else if (words[1].equals(ALL)) {
            /*format add all or remove all*/
            addCharsInRange((char) ASCII_START_VAL, (char) ASCII_END_VAL, add, remove);
        } else if (words[1].equals(SPACE)) {
            /*format add _ or remove _ when _ is space: ' '*/
            addOrRemove(add, remove, SPACE_CHAR);
        } else if (words[1].length() == 3 && words[1].charAt(1) == HYPHEN &&
                (int) words[1].charAt(0) <= ASCII_END_VAL && (int) words[1].charAt(0) >= ASCII_START_VAL
                && (int) words[1].charAt(2) <= ASCII_END_VAL && (int) words[1].charAt(2) >= ASCII_START_VAL) {
            /*format add a-c or remove a-c if the range is a-c*/
            addCharsInRange(words[1].charAt(0), words[1].charAt(2), add, remove);
        } else {
            printFailureMsg(add, remove);
        }
    }

    /**
     * prints message of invalid command input
     */
    private void printGeneralWrongInputMessage() {
        System.out.println(String.format(WRONG_INPUT_MSG, CHANGE, COMMAND));
    }

    /**
     * prints all the characters that currently exist at characters data structure
     */
    private void handleCharsInput() {
        for (Character ch : characters) {
            System.out.print(ch + SPACE_STR);
        }
        System.out.print('\n');
    }

    /**
     * operating the users input and for valid input perform as requested, else prints relevant invalid
     * input message
     */
    public void run() {
        System.out.println(ARROWS);
        String input = scanner.nextLine();
        boolean printToHtml = true;
        while (!input.strip().equals(EXIT)) {
            String[] words = input.strip().split(SPACE_STR);
            if (input.strip().equals(CHARS)) {
                handleCharsInput();
            } else if (input.startsWith(ADD + SPACE_STR)) {
                addRemoveCases(true, false, words);
            } else if (input.startsWith(REMOVE + SPACE_STR)) {

                addRemoveCases(false, true, words);
            } else if (input.startsWith(RES)) {
                updateRes(words);
            } else if (input.strip().equals(RENDER)) {
                renderOrConsole(printToHtml);
            } else if (input.strip().equals(CONSOLE)) {
                printToHtml = false;
            } else {
                printGeneralWrongInputMessage();
            }
            System.out.println(ARROWS);
            input = scanner.nextLine();
        }
    }

    /**
     * call the print method of the platform to print to it
     *
     * @param printToHtml parameter that indicate to what platform to output the dinal image, true if it is
     *                    to out.html, false if it is to console
     */
    private void renderOrConsole(boolean printToHtml) {
        BrightnessImgCharMatcher brightnessImgCharMatcher = new BrightnessImgCharMatcher(img, FONT);
        Character[] chars = Arrays.copyOf(characters.toArray(), characters.size(), Character[].class);
        char[][] chars2dArray = brightnessImgCharMatcher.chooseChars(charsInRow, chars);
        if (printToHtml) {
            renderImg(chars2dArray);
        } else {
            console(chars2dArray);
            console(chars2dArray);
        }
    }

    /**
     * prints final processed image to console
     *
     * @param chars2dArray 2 dimension array of the chars that assembles final image to output to console
     */
    private void console(char[][] chars2dArray) {
        ConsoleAsciiOutput asciiOutput = new ConsoleAsciiOutput();
        asciiOutput.output(chars2dArray);
    }

    /**
     * prints final processed image to out.html
     *
     * @param chars2dArray 2 dimension array of the chars that assembles final image to output to html file
     *                     out.html
     */
    private void renderImg(char[][] chars2dArray) {
        HtmlAsciiOutput htmlOutput = new HtmlAsciiOutput(FILE_NAME, FONT);
        htmlOutput.output(chars2dArray);
    }

    /**
     * if the input illigal, resize res by the request input,else, prints relevant message
     *
     * @param words user input
     */
    private void updateRes(String[] words) {
        if (words.length != 2) {
            /*illegal input*/
            printGeneralWrongInputMessage();
            return;
        }
        if (words[1].equals(UP)) {
            /*if in range, make bigger by 2, else prints relevant message*/
            if (charsInRow * RESIZE_FACTOR <= maxCharsInRow && charsInRow * RESIZE_FACTOR >= minCharsInRow) {
                charsInRow *= RESIZE_FACTOR;
                System.out.println(String.format(CURRENT_CHARS_NUM_MSG, charsInRow));
            } else {
                System.out.println(OUT_OF_RANGES_MSG);
            }

        } else if (words[1].equals(DOWN)) {
            /*if in range, make smaller by 2, else prints relevant message*/
            if (charsInRow / RESIZE_FACTOR >= minCharsInRow && charsInRow / RESIZE_FACTOR <= maxCharsInRow) {
                charsInRow /= RESIZE_FACTOR;
                System.out.println(String.format(CURRENT_CHARS_NUM_MSG, charsInRow));
            } else {
                System.out.println(OUT_OF_RANGES_MSG);
            }
        } else {
            /*illegal input*/
            printGeneralWrongInputMessage();
        }
    }
}



