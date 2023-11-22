/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.visitor;

import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.TransformVisitorInterface;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToJsonTransformerVisitor implements TransformVisitorInterface {

    // PRIVATE
    private Path filePath;
    private final static Charset ENCODING = StandardCharsets.UTF_8;

    /**
     * Constructor.
     *
     * @param fileName full name of an existing, readable file.
     */
    public void ReadWithScanner(String fileName) {
        filePath = Paths.get(fileName);
    }

//    public ToJsonTransformerVisitor(Path filePath) {
//        this.filePath = filePath;
//    }


    /**
     * Template method that calls {@link #processLine(String)}.
     */
    public final void processLineByLine() throws IOException {
        try (Scanner scanner = new Scanner(filePath, ENCODING.name())) {
            while (scanner.hasNextLine()) {
                processLine(scanner.nextLine());
            }
        }
    }

    /**
     * Overridable method for processing lines in different ways.
     *
     * <P>This simple default implementation expects simple name-value pairs, separated by an
     * '=' sign. Examples of valid input:
     * <tt>height = 167cm</tt>
     * <tt>mass =  65kg</tt>
     * <tt>disposition =  "grumpy"</tt>
     * <tt>this is the name = this is the value</tt>
     */
    protected void processLine(String line) {
        //use a second Scanner to parse the content of each line
        try (Scanner scanner = new Scanner(line)) {
            scanner.useDelimiter("=");
            if (scanner.hasNext()) {
                //assumes the line has a certain structure
                String name = scanner.next();
                String value = scanner.next();
                log("Name is : " + quote(name.trim()) + ", and Value is : " + quote(value.trim()));
            } else {
                log("Empty or invalid line. Unable to process.");
            }
        }
    }


    private static void log(Object object) {
        System.out.println(Objects.toString(object));
    }

    private String quote(String text) {
        String QUOTE = "'";
        return QUOTE + text + QUOTE;
    }

    //////////////////////////////////////////////////////////////////


    /**
     * @param searchText is non-null, but may have no content,
     *                   and represents what the user has input in a search box.
     */
    public void SearchBoxParser(String searchText) {
        if (searchText == null) {
            throw new IllegalArgumentException("Search Text cannot be null.");
        }
        this.searchText = searchText;
    }

    /**
     * Parse the user's search box input into a Set of String tokens.
     *
     * @return Set of Strings, one for each word in fSearchText; here "word"
     * is defined as either a lone word surrounded by whitespace, or as a series
     * of words surrounded by double quotes, "like this"; also, very common
     * words (and, the, etc.) do not qualify as possible search targets.
     */
    public Set<String> parseSearchText() {
        Set<String> result = new LinkedHashSet<>();

        boolean returnTokens = true;
        String currentDelims = WHITESPACE_AND_QUOTES;
        StringTokenizer parser = new StringTokenizer(
                searchText, currentDelims, returnTokens
        );

        String token = null;
        while (parser.hasMoreTokens()) {
            token = parser.nextToken(currentDelims);
            if (!isDoubleQuote(token)) {
                addNonTrivialWordToResult(token, result);
            } else {
                currentDelims = flipDelimiters(currentDelims);
            }
        }
        return result;
    }

    // PRIVATE
    private String searchText;
    private static final Set<String> COMMON_WORDS = new LinkedHashSet<>();
    private static final String DOUBLE_QUOTE = "\"";

    //the parser flips between these two sets of delimiters
    private static final String WHITESPACE_AND_QUOTES = " \t\r\n\"";
    private static final String QUOTES_ONLY = "\"";

    /**Very common words to be excluded from searches.*/
    static {
        COMMON_WORDS.add("a");
        COMMON_WORDS.add("and");
        COMMON_WORDS.add("be");
        COMMON_WORDS.add("for");
        COMMON_WORDS.add("from");
        COMMON_WORDS.add("has");
        COMMON_WORDS.add("i");
        COMMON_WORDS.add("in");
        COMMON_WORDS.add("is");
        COMMON_WORDS.add("it");
        COMMON_WORDS.add("of");
        COMMON_WORDS.add("on");
        COMMON_WORDS.add("to");
        COMMON_WORDS.add("the");
    }

    /**
     * Use to determine if a particular word entered in the
     * search box should be discarded from the search.
     */
    private boolean isCommonWord(String searchTokenCandidate) {
        return COMMON_WORDS.contains(searchTokenCandidate);
    }

    private boolean textHasContent(String text) {
        return (text != null) && (!text.trim().equals(""));
    }

    private void addNonTrivialWordToResult(String token, Set<String> result) {
        if (textHasContent(token) && !isCommonWord(token.trim())) {
            result.add(token.trim());
        }
    }

    private boolean isDoubleQuote(String token) {
        return token.equals(DOUBLE_QUOTE);
    }

    private String flipDelimiters(String currentDelims) {
        String result = null;
        if (currentDelims.equals(WHITESPACE_AND_QUOTES)) {
            result = QUOTES_ONLY;
        } else {
            result = WHITESPACE_AND_QUOTES;
        }
        return result;
    }


    //////////////////////////////////////////////////////////

    /**
     * The Matcher.find method attempts to match *parts* of the input
     * to the given pattern.
     */
    private static void matchParts(String text) {
        log(NEW_LINE + "Match PARTS:");
        //note the necessity of the comments flag, since our regular
        //expression contains comments:
        Pattern pattern = Pattern.compile(REGEXP, Pattern.COMMENTS);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            log("Num groups: " + matcher.groupCount());
            log("Package: " + matcher.group(1));
            log("Class: " + matcher.group(2));
        }
    }

    /**
     * The Matcher.matches method attempts to match the *entire*
     * input to the given pattern all at once.
     */
    private static void matchAll(String text) {
        log(NEW_LINE + "Match ALL:");
        Pattern pattern = Pattern.compile(REGEXP, Pattern.COMMENTS);
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            log("Num groups: " + matcher.groupCount());
            log("Package: " + matcher.group(1));
            log("Class: " + matcher.group(2));
        } else {
            log("Input does not match pattern.");
        }
    }

    //PRIVATE

    private static final String NEW_LINE = System.getProperty("line.separator");

    private static void log(String msg) {
        System.out.println(Objects.toString(msg));
    }

    /**
     * A commented regular expression for fully-qualified type names which
     * follow the common naming conventions, for example, "com.myappBlah.Thing".
     * <p>
     * Thus, the "dot + capital letter" is sufficient to define where the
     * package names end.
     * <p>
     * This regular expression uses two groups, one for the package, and one
     * for the class. Groups are defined by parentheses. Note that ?: will
     * define a group as "non-contributing"; that is, it will not contribute
     * to the return values of the <tt>group</tt> method.
     * <p>
     * As you can see, regular expressions are often cryptic.
     */
    private static final String REGEXP =
            "#Group1 - Package prefix without last dot: " + NEW_LINE +
                    "( (?:\\w|\\.)+ ) \\." + NEW_LINE +
                    "#Group2 - Class name starts with uppercase: " + NEW_LINE +
                    "( [A-Z](?:\\w)+ )";


    @Override
    public void visit(PayloadMessageInterface payloadMessageInterface) {

    }
}
