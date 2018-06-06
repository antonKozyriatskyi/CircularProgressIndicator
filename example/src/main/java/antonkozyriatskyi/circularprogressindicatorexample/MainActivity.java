package antonkozyriatskyi.circularprogressindicatorexample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, ColorPickerDialogFragment.OnColorSelectedListener {

    private Button dotColor;
    private SeekBar dotWidth;

    private CircularProgressIndicator circularProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        circularProgress = findViewById(R.id.circular_progress);
        circularProgress.setMaxProgress(10000);

        Button progressColor = findViewById(R.id.btn_progress_color);
        Button progressBackgroundColor = findViewById(R.id.btn_background_color);
        Button textColor = findViewById(R.id.btn_text_color);
        dotColor = findViewById(R.id.btn_dot_color);

        progressColor.setOnClickListener(this);
        progressBackgroundColor.setOnClickListener(this);
        textColor.setOnClickListener(this);
        dotColor.setOnClickListener(this);

        SeekBar progress = findViewById(R.id.sb_progress);
        SeekBar progressStrokeWidth = findViewById(R.id.sb_progress_width);
        SeekBar textSize = findViewById(R.id.sb_text_size);
        dotWidth = findViewById(R.id.sb_dot_width);

        progress.setOnSeekBarChangeListener(this);
        progressStrokeWidth.setOnSeekBarChangeListener(this);
        textSize.setOnSeekBarChangeListener(this);
        dotWidth.setOnSeekBarChangeListener(this);

        CheckBox drawDot = findViewById(R.id.cb_draw_dot);
        drawDot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                circularProgress.setShouldDrawDot(isChecked);
                dotWidth.setEnabled(isChecked);
                dotColor.setEnabled(isChecked);
            }
        });
        CheckBox useCustomTextAdapter = findViewById(R.id.cb_custom_text_adapter);
        useCustomTextAdapter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                circularProgress.setProgressTextAdapter(isChecked ? TIME_TEXT_ADAPTER : null);
            }
        });
    }

    @Override
    public void onClick(View v) {
        ColorPickerDialogFragment dialog = new ColorPickerDialogFragment();
        dialog.setOnColorSelectedListener(this);
        String tag = null;
        switch (v.getId()) {
            case R.id.btn_progress_color:
                tag = "progressColor";
                break;
            case R.id.btn_background_color:
                tag = "progressBackgroundColor";
                break;
            case R.id.btn_text_color:
                tag = "textColor";
                break;
            case R.id.btn_dot_color:
                tag = "dotColor";
                break;
        }

        dialog.show(getSupportFragmentManager(), tag);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_progress:
                circularProgress.setCurrentProgress(progress);
                break;
            case R.id.sb_progress_width:
                circularProgress.setProgressStrokeWidthDp(progress);
                break;
            case R.id.sb_dot_width:
                circularProgress.setDotWidthDp(progress);
                break;
            case R.id.sb_text_size:
                circularProgress.setTextSizeSp(progress);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onColorChosen(ColorPickerDialogFragment dialog, int r, int g, int b) {
        String tag = dialog.getTag();
        int color = Color.rgb(r, g, b);

        assert tag != null;

        switch (tag) {
            case "progressColor":
                circularProgress.setProgressColor(color);
                break;
            case "progressBackgroundColor":
                circularProgress.setProgressBackgroundColor(color);
                break;
            case "textColor":
                circularProgress.setTextColor(color);
                break;
            case "dotColor":
                circularProgress.setDotColor(color);
                break;
        }
    }

    private static final CircularProgressIndicator.ProgressTextAdapter TIME_TEXT_ADAPTER = new CircularProgressIndicator.ProgressTextAdapter() {
        @Override
        public String formatText(int time) {
            int hours = time / 3600;
            time %= 3600;
            int minutes = time / 60;
            int seconds = time % 60;
            StringBuilder sb = new StringBuilder();
            if (hours < 10) {
                sb.append(0);
            }
            sb.append(hours).append(":");
            if (minutes < 10) {
                sb.append(0);
            }
            sb.append(minutes).append(":");
            if (seconds < 10) {
                sb.append(0);
            }
            sb.append(seconds);
            return sb.toString();
        }
    };
}
