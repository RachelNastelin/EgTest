package com.vocalabs.egtest.example;

import java.util.regex.Pattern;
import com.vocalabs.egtest.annotation.*;

public class ExampleForReadme {

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

    @Eg(given = {"1", "2"}, returns = "3")
    @Eg(given = {"1", "Integer.MAX_VALUE"}, returns = "Integer.MIN_VALUE")
    public static int add(int a, int b) {
        return a + b;
    }

    @EgException(value = {"null"}, willThrow = NullPointerException.class)
    public static String methodWhichCannotHandleNulls(Object thing1) {
        return thing1.toString();
    }

    @Eg(given = {"\"World\""}, returns = "\"Hello, World!\"")
    public static String greet(String target) {
        return "Hello, "+target+"!";
    }

    @EgException({"null", "\"hello\""})
    @EgException({"\"hello\"", "null"})
    public static String anotherMethodWhichCannotHandleNulls(Object thing1, Object thing2) {
        return thing1.toString() + thing2.toString();
    }
}