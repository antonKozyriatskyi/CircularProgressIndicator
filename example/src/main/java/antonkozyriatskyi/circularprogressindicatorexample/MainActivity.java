package antonkozyriatskyi.circularprogressindicatorexample;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;
import java.util.HashMap;

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
        final SeekBar progressBackgroundStrokeWidth = findViewById(R.id.sb_progress_background_width);
        SeekBar textSize = findViewById(R.id.sb_text_size);
        dotWidth = findViewById(R.id.sb_dot_width);

        progress.setOnSeekBarChangeListener(this);
        progressStrokeWidth.setOnSeekBarChangeListener(this);
        progressBackgroundStrokeWidth.setOnSeekBarChangeListener(this);
        textSize.setOnSeekBarChangeListener(this);
        dotWidth.setOnSeekBarChangeListener(this);

        CheckBox drawDot = findViewById(R.id.cb_draw_dot);
        drawDot.setOnCheckedChangeListener((buttonView, isChecked) -> {
            circularProgress.setShouldDrawDot(isChecked);
            dotWidth.setEnabled(isChecked);
            dotColor.setEnabled(isChecked);
        });
        CheckBox useCustomTextAdapter = findViewById(R.id.cb_custom_text_adapter);
        useCustomTextAdapter.setOnCheckedChangeListener((buttonView, isChecked) -> circularProgress.setProgressTextAdapter(isChecked ? TIME_TEXT_ADAPTER : null));
        CheckBox fillBackground = findViewById(R.id.cb_fill_background);
        fillBackground.setChecked(circularProgress.isFillBackgroundEnabled());
        fillBackground.setOnCheckedChangeListener((buttonView, isChecked) -> circularProgress.setFillBackgroundEnabled(isChecked));

        CheckBox showText = findViewById(R.id.cb_show_text);
        showText.setChecked(circularProgress.isShowTextEnabled());
        showText.setOnCheckedChangeListener((buttonView, isChecked) -> circularProgress.setShowTextEnabled(isChecked));



        RadioGroup progressCap = findViewById(R.id.rg_cap);
        progressCap.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_cap_butt) {
                circularProgress.setProgressStrokeCap(CircularProgressIndicator.CAP_BUTT);
            } else if (checkedId == R.id.rb_cap_round) {
                circularProgress.setProgressStrokeCap(CircularProgressIndicator.CAP_ROUND);
            }
        });

        circularProgress.setOnProgressChangeListener((progress1, maxProgress) -> Log.d("PROGRESS", String.format("Current: %1$.0f, max: %2$.0f", progress1, maxProgress)));

        SwitchCompat animationSwitch = findViewById(R.id.sw_enable_animation);
        animationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> circularProgress.setAnimationEnabled(isChecked));

        Spinner gradientType = findViewById(R.id.sp_gradient_type);

        final ArrayList<HashMap<String, String>> gradients = new ArrayList<>();
        HashMap<String, String> gradient = new HashMap<>();
        gradient.put("type", "No gradient");
        gradient.put("value", "0");
        gradients.add(gradient);

        gradient = new HashMap<>();
        gradient.put("type", "Linear");
        gradient.put("value", "1");
        gradients.add(gradient);

        gradient = new HashMap<>();
        gradient.put("type", "Radial");
        gradient.put("value", "2");
        gradients.add(gradient);

        gradient = new HashMap<>();
        gradient.put("type", "Sweep");
        gradient.put("value", "3");
        gradients.add(gradient);

        gradientType.setAdapter(
                new SimpleAdapter(
                        this,
                        gradients,
                        android.R.layout.simple_dropdown_item_1line,
                        new String[]{"type"},
                        new int[]{android.R.id.text1}
                ));

        gradientType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                circularProgress.setGradient(Integer.parseInt(gradients.get(position).get("value")), Color.MAGENTA);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        ColorPickerDialogFragment dialog = new ColorPickerDialogFragment();
        dialog.setOnColorSelectedListener(this);
        String tag = null;
        int id = v.getId();
        if (id == R.id.btn_progress_color) {
            tag = "progressColor";
        } else if (id == R.id.btn_background_color) {
            tag = "progressBackgroundColor";
        } else if (id == R.id.btn_text_color) {
            tag = "textColor";
        } else if (id == R.id.btn_dot_color) {
            tag = "dotColor";
        }

        dialog.show(getSupportFragmentManager(), tag);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int id = seekBar.getId();
        if (id == R.id.sb_progress) {
            circularProgress.setCurrentProgress(progress);
        } else if (id == R.id.sb_progress_width) {
            circularProgress.setProgressStrokeWidthDp(progress);
        } else if (id == R.id.sb_dot_width) {
            circularProgress.setDotWidthDp(progress);
        } else if (id == R.id.sb_text_size) {
            circularProgress.setTextSizeSp(progress);
        } else if (id == R.id.sb_progress_background_width) {
            circularProgress.setProgressBackgroundStrokeWidthDp(progress);
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

    private static final CircularProgressIndicator.ProgressTextAdapter TIME_TEXT_ADAPTER = time -> {
        int hours = (int) (time / 3600);
        time %= 3600;
        int minutes = (int) (time / 60);
        int seconds = (int) (time % 60);
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
    };
}
