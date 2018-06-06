package antonkozyriatskyi.circularprogressindicator;

/**
 * Created by Anton on 06.06.2018.
 */

public final class PatternProgressTextAdapter implements CircularProgressIndicator.ProgressTextAdapter {

    private String pattern;

    public PatternProgressTextAdapter(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String formatText(int currentProgress) {
        return String.format(pattern, (double) currentProgress);
    }
}
