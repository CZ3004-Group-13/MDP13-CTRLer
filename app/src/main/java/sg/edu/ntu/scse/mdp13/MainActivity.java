package sg.edu.ntu.scse.mdp13;

import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import sg.edu.ntu.scse.mdp13.bluetooth.BluetoothFragment;
import sg.edu.ntu.scse.mdp13.bluetooth.PagerAdapter;
import sg.edu.ntu.scse.mdp13.bluetooth.PagerAdapter.TabItem;

import com.google.android.material.tabs.TabLayout;

import biz.laenger.android.vpbs.BottomSheetUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import sg.edu.ntu.scse.mdp13.map.GridMapCanvas;
import sg.edu.ntu.scse.mdp13.map.BoardMap;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.pathGrid) GridMapCanvas gridMapCanvas;
    private BoardMap finder = new BoardMap();
    @BindView(R.id.btn_reset) Button btnReset;

    @BindView(R.id.bottom_sheet_toolbar) Toolbar bottomSheetToolbar;
    @BindView(R.id.topTabs) TabLayout bottomSheetTabLayout; //bottom_sheet_tabs
    @BindView(R.id.viewpager) ViewPager bottomSheetViewPager; //bottom_sheet_viewpager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activty_main);
        ButterKnife.bind(this);

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
