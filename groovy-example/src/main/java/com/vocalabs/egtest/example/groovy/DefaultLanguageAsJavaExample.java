package com.vocalabs.egtest.example.groovy;

import java.util.List;
import java.util.regex.Pattern;
import com.vocalabs.egtest.annotation.*;

@EgDefaultLanguage(EgLanguage.JAVA)
public class DefaultLanguageAsJavaExample {

    @Eg(language = EgLanguage.GROOVY, given = {"['a', 'b', 'c']"}, returns = "3")
    public static int listSize(List<String> items) {
        return items.size();
    }

    //
    // @Eg:  given the specified input, returns the specified value
    //

    @Eg(given = {"\"World\""}, returns = "\"Hello, World!\"")
    public static String greet(String target) {
        return "Hello, "+target+"!";
    }

    // Package-private methods are fair game

    @Eg(given = {"1", "2"}, returns = "3")
    @Eg(given = {"1", "Integer.MAX_VALUE"}, returns = "Integer.MIN_VALUE")
    static int add(int a, int b) {
        return a + b;
    }

    // Floating-point return types have a delta (margin of error); the default is 0.0.

    @Eg(given = {"1.0", "3.0"}, returns = "0.33333", delta = 0.001)
    @Eg(given = {"1.0", "0.0"}, returns = "Double.POSITIVE_INFINITY")
    static double divide(double numerator, double divisor) {
        return numerator / divisor;
    }

    //
    // @EgMatch/@EgNoMatch: String pattern matching, for regular expressions or boolean functions
    //

    /**
     * Regular expression to match address portions of typical real-world email addresses.
     * It does NOT attempt to match all valid RFC 2822 addresses.
     */
    @EgMatch("dleppik@vocalabs.com")
    @EgMatch("dleppik@vocalabs.example.com")
    @EgNoMatch("dleppik")
    @EgNoMatch("dleppik@vocalabs@example.com")
    @EgNoMatch("David Leppik <dleppik@vocalabs.com>")
    public static final Pattern
            SIMPLE_EMAIL_RE = Pattern.compile("^[\\w+.\\-=&|/?!#$*]+@[\\w.\\-]+\\.[\\w]+$");

    /** Boolean function wrapping {@link #SIMPLE_EMAIL_RE} */
    @EgMatch("dleppik@vocalabs.com")
    @EgNoMatch("dleppik")
    public static boolean validEmail(String email) {
        return SIMPLE_EMAIL_RE.matcher(email).matches();
    }

    //
    // @EgException: for when failure is an option
    //

    @EgException(value = {"null"}, willThrow = NullPointerException.class)
    public static String methodWhichCannotHandleNulls(Object thing1) {
        return thing1.toString();
    }

    @EgException({"null", "\"hello\""})
    @EgException({"\"hello\"", "null"})
    static String anotherMethodWhichCannotHandleNulls(Object thing1, Object thing2) {
        return thing1.toString() + thing2.toString();
    }


    //
    // Non-static usage:
    // Non-static methods and variables are tested using the default zero-argument constructor
    // unless constructor arguments are specified
    //

    private final int min;
    private final int max;

    public DefaultLanguageAsJavaExample(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public DefaultLanguageAsJavaExample() {
        this(0, 5);
    }

    @Eg(given = "4", returns = "true")
    @Eg(construct = {"8", "9"},
        given = "4",
        returns = "false")
    boolean inRange(int num) {
        return num >= min  &&  num < max;
    }

    //
    // Putting it all together
    //

    /**
     * Return true if the string starts with one of the four ASCII vowels (not including Y).
     * @param s a non-null String with at least one character.
     */
    @EgMatch("Alaska")
    @Eg(given = "\"Alaska\"", returns = "true") // Same as above
    @EgMatch("elephant")
    @EgMatch("I")
    @EgMatch("October")
    @EgMatch("underground")
    @EgNoMatch("yes")
    @EgNoMatch("æon")
    @EgException({"\"\""}) // Empty string, throws something (we don't care what)
    @EgException(value = {"null"}, willThrow = NullPointerException.class)
    boolean startsWithAsciiVowel(String s) {
        switch (s.toLowerCase().charAt(0)) {
            case 'a': return true;
            case 'e': return true;
            case 'i': return true;
            case 'o': return true;
            case 'u': return true;
            default:  return false;
        }
    }

    @EgDefaultLanguage(EgLanguage.GROOVY)
    public static class WithInnerGroovy {

        @Eg(given = "[2, 4, 6]", returns = "3")
        public static int count(List<?> items) {
            return items.size();
        }
    }
}
