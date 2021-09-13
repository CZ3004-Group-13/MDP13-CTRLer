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

public class MainActivity extends AppCompatActivity {

    private PathGrid pathGrid;
    private PathFinder finder = new PathFinder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activty_main);

        pathGrid = findViewById(R.id.pathGrid);
        finder = pathGrid.getFinder();

        Button btnReset = (Button)this.findViewById(R.id.btn_reset);

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
}
