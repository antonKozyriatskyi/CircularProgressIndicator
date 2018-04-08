package antonkozyriatskyi.circularprogressindicatorexample;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

/**
 * Created by Anton on 13.03.2018.
 */

public class ColorPickerDialogFragment extends BottomSheetDialogFragment {

    private OnColorSelectedListener onColorSelectedListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_color_picker, container, false);

        final SeekBar red = rootView.findViewById(R.id.sb_red);
        final SeekBar green = rootView.findViewById(R.id.sb_green);
        final SeekBar blue = rootView.findViewById(R.id.sb_blue);

        final PorterDuff.Mode mode = PorterDuff.Mode.SRC_ATOP;

        red.getProgressDrawable().setColorFilter(Color.rgb(red.getProgress(), 0, 0), mode);
        green.getProgressDrawable().setColorFilter(Color.rgb(0, green.getProgress(), 0), mode);
        blue.getProgressDrawable().setColorFilter(Color.rgb(0, 0, blue.getProgress()), mode);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            red.getThumb().setColorFilter(Color.rgb(red.getProgress(), 0, 0), mode);
            green.getThumb().setColorFilter(Color.rgb(0, green.getProgress(), 0), mode);
            blue.getThumb().setColorFilter(Color.rgb(0, 0, blue.getProgress()), mode);
        }

        final View colorResult = rootView.findViewById(R.id.color_result);
        Button selectColor = rootView.findViewById(R.id.btn_select_color_result);

        DefaultSeekbarChangeListener seekBarChangeListener = new DefaultSeekbarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int redProgress = red.getProgress();
                int greenProgress = green.getProgress();
                int blueProgress = blue.getProgress();

                colorResult.setBackgroundColor(Color.rgb(redProgress, greenProgress, blueProgress));

                switch (seekBar.getId()) {
                    case R.id.sb_red:
                        seekBar.getProgressDrawable().setColorFilter(Color.rgb(redProgress, 0, 0), mode);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            seekBar.getThumb().setColorFilter(Color.rgb(redProgress, 0, 0), mode);
                        }
                        break;
                    case R.id.sb_green:
                        seekBar.getProgressDrawable().setColorFilter(Color.rgb(0, greenProgress, 0), mode);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            seekBar.getThumb().setColorFilter(Color.rgb(0, greenProgress, 0), mode);
                        }
                        break;
                    case R.id.sb_blue:
                        seekBar.getProgressDrawable().setColorFilter(Color.rgb(0, 0, blueProgress), mode);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            seekBar.getThumb().setColorFilter(Color.rgb(0, 0, blueProgress), mode);
                        }
                        break;
                }
            }
        };

        red.setOnSeekBarChangeListener(seekBarChangeListener);
        green.setOnSeekBarChangeListener(seekBarChangeListener);
        blue.setOnSeekBarChangeListener(seekBarChangeListener);

        selectColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onColorSelectedListener.onColorChosen(ColorPickerDialogFragment.this,
                        red.getProgress(), green.getProgress(), blue.getProgress());
                dismiss();
            }
        });

        return rootView;
    }

    public void setOnColorSelectedListener(OnColorSelectedListener onColorSelectedListener) {
        this.onColorSelectedListener = onColorSelectedListener;
    }

    interface OnColorSelectedListener {
        void onColorChosen(ColorPickerDialogFragment dialog, int r, int g, int b);
    }
}
