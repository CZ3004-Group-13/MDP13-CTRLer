package ntu.scse.mdp13;

import static ntu.scse.mdp13.map.Robot.ROBOT_MOTOR_FORWARD;
import static ntu.scse.mdp13.map.Robot.ROBOT_MOTOR_REVERSE;
import static ntu.scse.mdp13.map.Robot.ROBOT_MOTOR_STOP;
import static ntu.scse.mdp13.map.Robot.STM_COMMAND_FORWARD;
import static ntu.scse.mdp13.map.Robot.STM_COMMAND_REVERSE;
import static ntu.scse.mdp13.map.Robot.STM_COMMAND_STOP;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;


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
    private BoardMap finder = new BoardMap();
    Button btnReset;
    ImageButton btnForward;
    ImageButton btnReverse;

    Toolbar bottomSheetToolbar;
    TabLayout bottomSheetTabLayout; //bottom_sheet_tabs
    ViewPager bottomSheetViewPager; //bottom_sheet_viewpager


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activty_main);
        mapCanvas = findViewById(R.id.pathGrid);
        btnReset = (Button) this.findViewById(R.id.btn_reset);
        btnForward = (ImageButton) this.findViewById(R.id.btn_accelerate);
        btnReverse = (ImageButton) this.findViewById(R.id.btn_reverse);
        bottomSheetToolbar = (Toolbar) this.findViewById(R.id.bottom_sheet_toolbar);
        bottomSheetTabLayout = (TabLayout) this.findViewById(R.id.topTabs);
        bottomSheetViewPager = (ViewPager) this.findViewById(R.id.viewpager);

        setupBottomSheet();

        finder = mapCanvas.getFinder();

        btnReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mapCanvas.setSolving(false);
                finder.resetGrid();
                mapCanvas.invalidate();
            }
        });

        setupLongClicks(btnForward, ROBOT_MOTOR_FORWARD);
        setupLongClicks(btnReverse, ROBOT_MOTOR_REVERSE);

    }

    private void setupLongClicks(View btn, int direction) {
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mapCanvas.getFinder().getRobo().motorRotate(direction);
            }
        });
        btn.setOnTouchListener(new View.OnTouchListener() {

            private int DELAYms = 100;
            private Handler mHandler;

            @Override public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        MessageFragment.sendMessage("BHLD -> RPI:\t\t", (direction == ROBOT_MOTOR_FORWARD ? STM_COMMAND_FORWARD : STM_COMMAND_REVERSE));
                        mHandler.postDelayed(mAction, DELAYms);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        MessageFragment.sendMessage("BRLS -> RPI:\t\t", STM_COMMAND_STOP);
                        MessageFragment.addSeparator();
                        mapCanvas.getFinder().getRobo().setMotor(ROBOT_MOTOR_STOP);
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    mapCanvas.getFinder().getRobo().motorRotate(direction);
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
