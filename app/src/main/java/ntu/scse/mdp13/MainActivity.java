package ntu.scse.mdp13;

import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


import biz.laenger.android.vpbs.BottomSheetUtils;
import ntu.scse.mdp13.R;
import ntu.scse.mdp13.bluetooth.BluetoothFragment;
import ntu.scse.mdp13.bluetooth.PagerAdapter;
import ntu.scse.mdp13.bluetooth.PagerAdapter.TabItem;

import ntu.scse.mdp13.map.GridMapCanvas;
import ntu.scse.mdp13.map.BoardMap;

public class MainActivity extends AppCompatActivity {

    GridMapCanvas gridMapCanvas;
    private BoardMap finder = new BoardMap();
    Button btnReset;

    Toolbar bottomSheetToolbar;
    TabLayout bottomSheetTabLayout; //bottom_sheet_tabs
    ViewPager bottomSheetViewPager; //bottom_sheet_viewpager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activty_main);
        gridMapCanvas = findViewById(R.id.pathGrid);
        btnReset = (Button) this.findViewById(R.id.btn_reset);
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
