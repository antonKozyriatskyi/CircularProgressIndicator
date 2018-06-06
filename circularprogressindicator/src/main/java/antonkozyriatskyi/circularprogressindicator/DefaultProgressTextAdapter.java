package antonkozyriatskyi.circularprogressindicator;

/**
 * Created by Anton on 06.06.2018.
 */

public final class DefaultProgressTextAdapter implements CircularProgressIndicator.ProgressTextAdapter {

    @Override
    public String formatText(int currentProgress) {
        return String.valueOf(currentProgress);
    }
}
