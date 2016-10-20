import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexerRule {

    private String state; // probably not required
    private Pattern pattern;
    private Matcher matcher;
    private int priority;
    private String name; // rule name

    public LexerRule(String regex, String state, int priority, String name) {
        this.pattern = Pattern.compile(regex);
        this.matcher = pattern.matcher("");
        this.priority = priority;
        this.state = state;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public MatchState getMatchState(String input) {
        matcher.reset(input);

        if (matcher.matches()) {
            return new MatchState(input.length(), true);
        } else if (matcher.hitEnd()) {
            return new MatchState(input.length(), false);
        } else {
            return new MatchState(0, false);
        }
    }

    public String getState() {
        return state;
    }

    public static class MatchState {

        int matchLength;
        boolean isFullyMatched;

        public MatchState(int matchLength, boolean isFullyMatched) {

            this.matchLength = matchLength;
            this.isFullyMatched = isFullyMatched;
        }

        public int getMatchLength() {
            return matchLength;
        }

        public boolean isFullyMatched() {
            return isFullyMatched;
        }
    }
}
