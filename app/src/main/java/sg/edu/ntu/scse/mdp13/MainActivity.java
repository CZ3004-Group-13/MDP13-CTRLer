package sg.edu.ntu.scse.mdp13;

import android.os.Bundle;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import androidx.appcompat.app.AppCompatActivity;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineStart;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.GlobalScope;
import sg.edu.ntu.scse.mdp13.map2d.PathGrid;
import sg.edu.ntu.scse.mdp13.pathFinder.PathFinder;

public class MainActivity extends AppCompatActivity implements OnItemSelectedListener {

    private PathGrid pathGrid;
    private PathFinder finder = new PathFinder();
    private long speed = 0;
    private int algorithmToApply = 0;

    public static final long FAST = 30L;
    public static final long AVG = 60L;
    public static final long SLOW = 120L;
    public static final int BFS_ALGO = 1;
    public static final int DFS_ALGO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activty_main);

        this.setSpeedSpinner();
        this.setAlgoSpinner();

        pathGrid = findViewById(R.id.pathGrid);
        finder = pathGrid.getFinder();

        Button btnSolve = (Button)this.findViewById(R.id.btn_solve);
        Button btnReset = (Button)this.findViewById(R.id.btn_reset);

        btnSolve.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                // setting solving status to true , so that
                // user can't manipulate the gird while opeartions
                pathGrid.setSolving(true);


                //Since UI needs to be updated and work is less,
                //performing the operation in Main Thread


                //GlobalScope.launch(Dispatchers.Main) {

                    btnSolve.setEnabled(false);
                    btnSolve.setBackgroundColor(Color.parseColor("#DFBF9C"));
                    btnSolve.setTextColor(Color.parseColor("#000000"));

                    btnReset.setEnabled(false);
                    btnReset.setBackgroundColor(Color.parseColor("#9D9FA2"));

                    switch (algorithmToApply) {
                        case BFS_ALGO:
                            makeToast(finder.solveBFS(speed));
                            break;
                        default:
                            makeToast(finder.solveDFS(speed));
                    }

                    btnSolve.setEnabled(true);
                    btnSolve.setBackgroundColor(Color.parseColor("#333333"));
                    btnSolve.setTextColor(Color.parseColor("#FFFFFF"));
                    btnReset.setEnabled(true);
                    btnReset.setBackgroundColor(Color.parseColor("#333333"));
                //}
        }});

        btnReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                pathGrid.setSolving(false);
                finder.resetGrid();
                pathGrid.invalidate();
            }
        });
    }

    private void makeToast(boolean found) {
        if (found) {
            Toast toast = Toast.makeText(this, "Path Found.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, -200);
            toast.show();
        } else {
            Toast toast = Toast.makeText(this, "No Path Found.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, -200);
            toast.show();
        }
    }

    private void setAlgoSpinner() {
        Spinner algoSpinner = findViewById(R.id.algo_spinner);
        algoSpinner.setOnItemSelectedListener((OnItemSelectedListener)this);
        algoSpinner.setAdapter((SpinnerAdapter)
            ArrayAdapter.createFromResource(
                this,
                R.array.algo_array,
                R.layout.spinner_list_text
            )
        );
    }

    private void setSpeedSpinner() {
        Spinner speedSpinner = findViewById(R.id.speed_spinner);
        speedSpinner.setOnItemSelectedListener((OnItemSelectedListener)this);
        speedSpinner.setAdapter((SpinnerAdapter)
            ArrayAdapter.createFromResource(
                    this,
                    R.array.speed_array,
                    R.layout.spinner_list_text
            )
        );
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch(parent.getId()) {
            case R.id.speed_spinner:
                speed = parent.getSelectedItem().toString() == "Fast"
                        ? FAST : parent.getSelectedItem().toString() == "Average"
                        ? AVG : SLOW;
                break;

            case R.id.algo_spinner:
                algorithmToApply = parent.getSelectedItem().toString() == "BFS"
                        ? BFS_ALGO : DFS_ALGO;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
