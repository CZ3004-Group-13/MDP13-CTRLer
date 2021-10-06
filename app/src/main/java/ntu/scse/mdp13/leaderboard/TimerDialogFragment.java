package ntu.scse.mdp13.leaderboard;

import android.annotation.SuppressLint;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import biz.laenger.android.vpbs.ViewPagerBottomSheetDialogFragment;
import ntu.scse.mdp13.R;
import ntu.scse.mdp13.bluetooth.MessageFragment;
import ntu.scse.mdp13.map.BoardMap;
import ntu.scse.mdp13.map.MapCanvas;

public class TimerDialogFragment extends ViewPagerBottomSheetDialogFragment {
    static CountDownTimer timer;
    Toolbar bottomSheetToolbar;
    Button btnTimer;
    TextView timeLbl;
    TextView swipeLbl;

    boolean hasBegan = false;
    String currentTime = "";

    public static final String BLUETOOTH_RUN_DONE = "DONE";

    public TimerDialogFragment() {}

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        final View contentView = View.inflate(getContext(), R.layout.dialog_timer, null);
        bottomSheetToolbar = contentView.findViewById(R.id.bottom_sheet_toolbar);
        btnTimer = contentView.findViewById(R.id.btn_timer);
        timeLbl = contentView.findViewById(R.id.txtTimer);
        swipeLbl = contentView.findViewById(R.id.txtLblStopTimer);
        bottomSheetToolbar.setTitle("Timer");
        currentTime = (String) timeLbl.getText();
        dialog.setContentView(contentView);

        setupTimerButton();

        btnTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hasBegan = !hasBegan;
                setupTimerButton();
            }
        });
    }

    private void setupTimerButton() {
        btnTimer.setText(hasBegan ? "Stop"
                : !currentTime.equals("00:00:00") ? "Restart"
                : "Start");
        btnTimer.setTextColor(hasBegan ? getResources().getColor(R.color.red) : getResources().getColor(R.color.yellow));
        swipeLbl.setVisibility(currentTime.equals("00:00:00") ? View.GONE : View.VISIBLE);
        this.setCancelable(!hasBegan);
        if (hasBegan) {
            MessageFragment.sendMessage("LDRB -> RPI:\t\t", "BANANAS");

            final long totalTime = 30 * 60 * 1000;//30 mins
            final TextView textView = (TextView) timeLbl;

            timer = new CountDownTimer(totalTime, 10) {

                @SuppressLint("SetTextI18n")
                public void onTick(long millisUntilFinished) {
                    int secondsInt = (int) ((totalTime - millisUntilFinished) / 1000 % 60);
                    int msInt = (int) (millisUntilFinished%100);
                    String seconds, mssecs;
                    seconds = secondsInt < 10 ? "0" + secondsInt : "" + secondsInt;
                    mssecs = msInt < 10 ? "0" + msInt : "" + msInt;

                    String mins = "0" + ((totalTime - millisUntilFinished) / 1000 / 60) + "";
                    currentTime = mins + ":" + seconds + ":" + mssecs;
                    textView.setText(currentTime);
                }

                public void onFinish() {
                    timer = null;
                }

            };
            timer.start();
        } else {
            if (timer != null) timer.cancel();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        getActivity().findViewById(R.id.toolbar_top).setVisibility(View.VISIBLE);
        BoardMap _map = ((MapCanvas) getActivity().findViewById(R.id.pathGrid)).getFinder();
        _map.defaceTargets();
    }

    public void setBegan(boolean b) {
        this.hasBegan = b;
        setupTimerButton();
    }
}