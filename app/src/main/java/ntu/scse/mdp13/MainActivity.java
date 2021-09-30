package ntu.scse.mdp13;

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
import ntu.scse.mdp13.R;
import ntu.scse.mdp13.bluetooth.BluetoothFragment;
import ntu.scse.mdp13.bluetooth.PagerAdapter;
import ntu.scse.mdp13.bluetooth.PagerAdapter.TabItem;

import ntu.scse.mdp13.map.GridMapCanvas;
import ntu.scse.mdp13.map.BoardMap;
import ntu.scse.mdp13.map.Robot;

public class MainActivity extends AppCompatActivity {

    GridMapCanvas gridMapCanvas;
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
        gridMapCanvas = findViewById(R.id.pathGrid);
        btnReset = (Button) this.findViewById(R.id.btn_reset);
        btnForward = (ImageButton) this.findViewById(R.id.btn_accelerate);
        btnReverse = (ImageButton) this.findViewById(R.id.btn_reverse);
        bottomSheetToolbar = (Toolbar) this.findViewById(R.id.bottom_sheet_toolbar);
        bottomSheetTabLayout = (TabLayout) this.findViewById(R.id.topTabs);
        bottomSheetViewPager = (ViewPager) this.findViewById(R.id.viewpager);

        setupBottomSheet();

        finder = gridMapCanvas.getFinder();

        btnReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                gridMapCanvas.setSolving(false);
                finder.resetGrid();
                gridMapCanvas.invalidate();
            }
        });

        setupLongClicks(btnForward, Robot.ROBOT_MOTOR_FORWARD);
        setupLongClicks(btnReverse, Robot.ROBOT_MOTOR_REVERSE);

    }

    private void setupLongClicks(View btn, int direction) {
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                gridMapCanvas.getFinder().getRobo().motorRotate(direction);
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
                        mHandler.postDelayed(mAction, DELAYms);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        gridMapCanvas.getFinder().getRobo().setMotor(Robot.ROBOT_MOTOR_STOP);
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    gridMapCanvas.getFinder().getRobo().motorRotate(direction);
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
