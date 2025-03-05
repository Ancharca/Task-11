package Program;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSharpCodeProcessor {

    public static Pattern FIRST_PATTERN = Pattern.compile(
            "\\s*(?<!\\w)\\s*(int|long|short|byte)\\s+(\\w+)\\s*=\\s*\\2\\s*\\+\\s*1\\s*(?<!\\w)*\\s;" //int x = x +1;
    );
    public static Pattern SECOND_PATTERN = Pattern.compile(
            "\\s*(?<!\\w)\\s*(\\w+)\\s*=\\s*\\1\\s*\\+\\s*1"
    );

    // для удаления однострочных комментариев
    public static Pattern SINGLE_LINE_COMMENT_PATTERN = Pattern.compile(
            "//.*"
    );

    // для удаления многострочных комментариев
    public static Pattern MULTI_LINE_COMMENT_PATTERN = Pattern.compile(
            "/\\*.*?\\*/", Pattern.DOTALL
    );

    public Stream<String> processCode(Stream<String> lines) {
        String code = lines.reduce("", (acc, line) -> acc + line + "\n");

        code = removeComments(code); //удаление коментариев
        List<String> segments = splitCodeIntoSegments(code); //делим по ( ) { } ; / \

        List<String> processedSegments = new ArrayList<>(); //делим на сегменты
        for (String segment : segments) {
            processedSegments.add(processSegment(segment));
        }
        String processedCode = String.join("", processedSegments);
        return Stream.of(processedCode.split("\n"));
    }

    public String removeComments(String code) { //удвление комментариев
        Matcher multiLineMatcher = MULTI_LINE_COMMENT_PATTERN.matcher(code);
        code = multiLineMatcher.replaceAll("");

        Matcher singleLineMatcher = SINGLE_LINE_COMMENT_PATTERN.matcher(code);
        code = singleLineMatcher.replaceAll("");

        return code;
    }

    public List<String> splitCodeIntoSegments(String code) { //делим на сегменты по разделителям
        List<String> segments = new ArrayList<>();
        StringBuilder currentSegment = new StringBuilder();

        for (char c : code.toCharArray()) {
            if (c == '{' || c == '}' || c == '(' || c == ')' || c ==';' || c == '\\' || c == '\n') {
                if (currentSegment.length() > 0) {
                    segments.add(currentSegment.toString());
                    currentSegment = new StringBuilder();
                }
                segments.add(String.valueOf(c));
            } else {
                currentSegment.append(c);
            }
        }

        if (currentSegment.length() > 0) {
            segments.add(currentSegment.toString());
        }

        return segments;
    }

    public String processSegment(String segment) {
        Matcher typedFirstMatcher = FIRST_PATTERN.matcher(segment);
        Matcher typedSecondMatcher = SECOND_PATTERN.matcher(segment);

        if (typedFirstMatcher.matches()) {
            String type = typedFirstMatcher.group(1);
            String variable = typedFirstMatcher.group(2);
            return type + " " + variable + "++;";
        } else if (typedSecondMatcher.matches()) {
            String variable = typedSecondMatcher.group(1);
            return variable + "++;";
        } else {
            return segment;
        }
    }
}