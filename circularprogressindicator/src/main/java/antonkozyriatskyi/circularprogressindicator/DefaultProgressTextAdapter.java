package antonkozyriatskyi.circularprogressindicator;

import androidx.annotation.NonNull;

/**
 * Created by Anton on 06.06.2018.
 */

public final class DefaultProgressTextAdapter implements CircularProgressIndicator.ProgressTextAdapter {

    @NonNull
    @Override
    public String formatText(double currentProgress) {
        return String.valueOf((int) currentProgress);
    }
}
