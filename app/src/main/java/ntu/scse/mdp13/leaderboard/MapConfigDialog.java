package ntu.scse.mdp13.leaderboard;

import static ntu.scse.mdp13.map.BoardMap.TARGET_CELL_CODE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Map;

import biz.laenger.android.vpbs.ViewPagerBottomSheetDialogFragment;
import ntu.scse.mdp13.R;
import ntu.scse.mdp13.map.MapCanvas;
import ntu.scse.mdp13.map.Target;

public class MapConfigDialog extends ViewPagerBottomSheetDialogFragment {
    Toolbar bottomSheetToolbar;
    Button saveBtn;
    LinearLayout linearLayout;

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        final View contentView = View.inflate(getContext(), R.layout.dialog_map_config, null);
        bottomSheetToolbar = contentView.findViewById(R.id.config_bottom_sheet_toolbar);
        bottomSheetToolbar.setTitle("Map Configurations");
        saveBtn = contentView.findViewById(R.id.btn_save);
        linearLayout = (LinearLayout) contentView.findViewById(R.id.linear_scroll_savedMap);
        dialog.setContentView(contentView);

        //getActivity().getPreferences(Context.MODE_PRIVATE).edit().clear().commit();
        MapCanvas _map = (MapCanvas) getActivity().findViewById(R.id.pathGrid);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

                int n = 0;
                String message = "";
                while (n < _map.getFinder().getTargets().size()) {
                    message += (n+1) + "," + _map.getFinder().getTargets().get(n).getX() + "," + (21-_map.getFinder().getTargets().get(n).getY()) + "," + _map.getFinder().getTargets().get(n).getF() + "|";
                    n++;
                }

                if (!message.equals("")) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(message, message);
                    editor.apply();
                    readSavedMaps(_map);
                }
            }
        });
        readSavedMaps(_map);
    }

    private void readSavedMaps(MapCanvas _map) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPref.getAll();
        linearLayout.removeAllViews();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Button button = new Button(getContext());
            button.setText(entry.getValue().toString());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String msg = (String) button.getText();
                    String[] obss = msg.split("\\|");
                    _map.getFinder().resetGrid();
                    for(int i=0; i <= obss.length-1 ; i++) {
                        String[] xy = obss[i].split(",");
                        _map.getFinder().getTargets().add(new Target(Integer.parseInt(xy[1]), 21-Integer.parseInt(xy[2]), Integer.parseInt(xy[0]), Integer.parseInt(xy[3])));
                        _map.getFinder().getBoard()[_map.getFinder().getTargets().get(i).getX()][_map.getFinder().getTargets().get(i).getY()] = TARGET_CELL_CODE;
                    }
                }
            });
            button.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    sharedPref.edit().remove((String) button.getText()).commit();
                    linearLayout.removeView(button);
                    return true;
                }
            });
            linearLayout.addView(button);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        getActivity().findViewById(R.id.toolbar_top).setVisibility(View.VISIBLE);
    }
}