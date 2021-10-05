package ntu.scse.mdp13;

import static ntu.scse.mdp13.map.Robot.ROBOT_MOTOR_FORWARD;
import static ntu.scse.mdp13.map.Robot.ROBOT_MOTOR_REVERSE;
import static ntu.scse.mdp13.map.Robot.ROBOT_MOTOR_STOP;
import static ntu.scse.mdp13.map.Robot.ROBOT_SERVO_CENTRE;
import static ntu.scse.mdp13.map.Robot.ROBOT_SERVO_LEFT;
import static ntu.scse.mdp13.map.Robot.ROBOT_SERVO_RIGHT;
import static ntu.scse.mdp13.map.Robot.STM_COMMAND_CENTRE;
import static ntu.scse.mdp13.map.Robot.STM_COMMAND_FORWARD;
import static ntu.scse.mdp13.map.Robot.STM_COMMAND_LEFT;
import static ntu.scse.mdp13.map.Robot.STM_COMMAND_REVERSE;
import static ntu.scse.mdp13.map.Robot.STM_COMMAND_RIGHT;
import static ntu.scse.mdp13.map.Robot.STM_COMMAND_STOP;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;


import java.util.ArrayList;

import biz.laenger.android.vpbs.BottomSheetUtils;
import ntu.scse.mdp13.bluetooth.BluetoothFragment;
import ntu.scse.mdp13.bluetooth.MessageFragment;
import ntu.scse.mdp13.bluetooth.PagerAdapter;
import ntu.scse.mdp13.bluetooth.PagerAdapter.TabItem;

import ntu.scse.mdp13.map.MapCanvas;
import ntu.scse.mdp13.map.BoardMap;
import ntu.scse.mdp13.map.Robot;

public class MainActivity extends AppCompatActivity {

    MapCanvas mapCanvas;
    private BoardMap _map = new BoardMap();
    Button btnReset;
    Button btnTarget;
    ImageButton btnForward;
    ImageButton btnReverse;
    ImageButton btnLeft;
    ImageButton btnRight;

    Toolbar bottomSheetToolbar;
    TabLayout bottomSheetTabLayout; //bottom_sheet_tabs
    ViewPager bottomSheetViewPager; //bottom_sheet_viewpager

    ArrayList longpress = new ArrayList();


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activty_main);
        mapCanvas = findViewById(R.id.pathGrid);
        btnReset = (Button) this.findViewById(R.id.btn_reset);
        btnTarget = (Button) this.findViewById(R.id.btn_target);
        btnForward = (ImageButton) this.findViewById(R.id.btn_accelerate);
        btnReverse = (ImageButton) this.findViewById(R.id.btn_reverse);
        btnLeft = (ImageButton) this.findViewById(R.id.btn_left);
        btnRight = (ImageButton) this.findViewById(R.id.btn_right);
        bottomSheetToolbar = (Toolbar) this.findViewById(R.id.bottom_sheet_toolbar);
        bottomSheetTabLayout = (TabLayout) this.findViewById(R.id.topTabs);
        bottomSheetViewPager = (ViewPager) this.findViewById(R.id.viewpager);

        setupBottomSheet();

        _map = mapCanvas.getFinder();

        btnTarget.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int n = 0;
                while (n < _map.getTargets().size()) {
                    String message;
                    message = "OBS|[" + n + "," + _map.getTargets().get(n).getX() + "," + _map.getTargets().get(n).getY() + "," + _map.getTargets().get(n).getF() + "]";
                    MessageFragment.sendMessage("MAP -> RPI:\t\t ", message + '\n');
                    n++;
                }}
        });

        btnReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mapCanvas.setSolving(false);
                _map.resetGrid();
                mapCanvas.invalidate();
            }
        });

        setupLongClicks(btnForward, ROBOT_MOTOR_FORWARD);
        setupLongClicks(btnReverse, ROBOT_MOTOR_REVERSE);
        setupLongClicks(btnLeft, ROBOT_SERVO_LEFT);
        setupLongClicks(btnRight, ROBOT_SERVO_RIGHT);
    }

    private void setupLongClicks(View btn, int direction) {
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (longpress.size() < 1) {
                    switch (v.getId()) {
                        case R.id.btn_accelerate:
                        case R.id.btn_reverse:
                            _map.getRobo().motorRotate(direction);
                            Log.d("ROBOT", "CLICK " + _map.getRobo().toString());
                            break;
                         //case R.id.btn_left:
                         //case R.id.btn_right:
                             //_map.getRobo().servoTurn(direction);
                             //Log.d("ROBOT", "CLICK " + _map.getRobo().toString());
                             //break;
                    }
                }
                longpress.clear();
            }
        });
        btn.setOnTouchListener(new View.OnTouchListener() {

            private int DELAYms = 250;
            private Handler mHandler;

            @Override public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        switch (v.getId()) {
                            case R.id.btn_accelerate:
                            case R.id.btn_reverse:
                                MessageFragment.sendMessage("BHLD -> RPI:\t\t", (direction == ROBOT_MOTOR_FORWARD ? STM_COMMAND_FORWARD : STM_COMMAND_REVERSE));
                                Log.d("ROBOT TOUCH DOWN", _map.getRobo().toString());
                                break;
                            case R.id.btn_left:
                            case R.id.btn_right:
                                MessageFragment.sendMessage("BHLD -> RPI:\t\t", (direction == ROBOT_SERVO_LEFT ? STM_COMMAND_LEFT : STM_COMMAND_RIGHT));
                                Log.d("ROBOT TOUCH DOWN", _map.getRobo().toString());
                                break;
                        }
                        mHandler.postDelayed(mAction, DELAYms);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        switch (v.getId()) {
                            case R.id.btn_accelerate:
                            case R.id.btn_reverse:
                                MessageFragment.sendMessage("BRLS -> RPI:\t\t", STM_COMMAND_STOP);
                                _map.getRobo().setMotor(ROBOT_MOTOR_STOP);
                                Log.d("ROBOT TOUCH UP", _map.getRobo().toString());
                                break;
                            case R.id.btn_left:
                            case R.id.btn_right:
                                MessageFragment.sendMessage("BRLS -> RPI:\t\t", STM_COMMAND_CENTRE);
                                _map.getRobo().setServo(ROBOT_SERVO_CENTRE);
                                Log.d("ROBOT TOUCH UP", _map.getRobo().toString());
                                break;
                        }
                        MessageFragment.addSeparator();
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;


                }

                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    switch (btn.getId()) {
                        case R.id.btn_accelerate:
                        case R.id.btn_reverse:
                            _map.getRobo().motorRotate(direction);
                            Log.d("ROBOT RUNNABLE", _map.getRobo().toString());
                            break;
                        case R.id.btn_left:
                        case R.id.btn_right:
                            _map.getRobo().servoTurn(direction);
                            Log.d("ROBOT RUNNABLE", "TOUCH " + _map.getRobo().toString());
                            break;
                    }
                    longpress.add(1);
                    mHandler.postDelayed(this, DELAYms);
                }
            };

        });
    }

    private void setupBottomSheet() {
        bottomSheetToolbar.setTitle(R.string.bottom_sheet_title);
        final PagerAdapter sectionsPagerAdapter = new PagerAdapter(getSupportFragmentManager(), this, TabItem.CONNECTION, TabItem.MESSAGE);
        bottomSheetViewPager.setOffscreenPageLimit(1);
        bottomSheetViewPager.setAdapter(sectionsPagerAdapter);
        bottomSheetTabLayout.setupWithViewPager(bottomSheetViewPager);
        BottomSheetUtils.setupViewPager(bottomSheetViewPager);
    }

    public void bluetoothOnClickMethods(View v) {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof BluetoothFragment) {
                BluetoothFragment bluetooth_fragment = (BluetoothFragment) fragment;
                bluetooth_fragment.myClickMethod(v, this);
            }
        }
    }
}
