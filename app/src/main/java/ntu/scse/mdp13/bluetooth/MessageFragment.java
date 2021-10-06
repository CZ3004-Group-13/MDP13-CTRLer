package ntu.scse.mdp13.bluetooth;

import static ntu.scse.mdp13.leaderboard.TimerDialogFragment.BLUETOOTH_RUN_DONE;
import static ntu.scse.mdp13.map.Robot.ROBOT_MOTOR_FORWARD;
import static ntu.scse.mdp13.map.Robot.ROBOT_MOTOR_REVERSE;
import static ntu.scse.mdp13.map.Robot.ROBOT_MOTOR_STOP;
import static ntu.scse.mdp13.map.Robot.STM_COMMAND_FORWARD;
import static ntu.scse.mdp13.map.Robot.STM_COMMAND_REVERSE;
import static ntu.scse.mdp13.map.Robot.STM_COMMAND_STOP;
import static ntu.scse.mdp13.map.Target.BLUETOOTH_TARGET_IDENTIFIER;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import ntu.scse.mdp13.R;
import ntu.scse.mdp13.leaderboard.TimerDialogFragment;
import ntu.scse.mdp13.map.BoardMap;
import ntu.scse.mdp13.map.MapCanvas;
import ntu.scse.mdp13.map.Target;

public class MessageFragment extends Fragment {

    private static String statusWindowTxt = "";
    private static TimerDialogFragment timerDialog;

    Button send;
    static TextView messageReceivedTextView;
    EditText typeBoxEditText;
    static ScrollView scrollView;

    // initializations
    public MessageFragment() {}

    public static TimerDialogFragment getTimerDialog() { return timerDialog; }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);
        send = (Button) rootView.findViewById(R.id.messageButton);
        messageReceivedTextView = (TextView) rootView.findViewById(R.id.messageReceivedTextView);
        typeBoxEditText = (EditText) rootView.findViewById(R.id.typeBoxEditText);
        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView2D);
        timerDialog = new TimerDialogFragment();

        //messageReceivedTextView.setMovementMethod(new ScrollingMovementMethod());


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = "" + typeBoxEditText.getText().toString();
                sendMessage("TXTB -> RPI:\t\t", input);
                typeBoxEditText.setText("");
            }
        });

        return rootView;
    }

    public static void sendMessage(String prefix, String txt) {
        BluetoothService.getInstance(null, null).sendMessage(txt);
        statusWindowTxt += prefix + txt + '\n';
        messageReceivedTextView.setText(statusWindowTxt);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    public static void addSeparator() {
        statusWindowTxt += "----------------------------------------------------" + '\n';
        messageReceivedTextView.setText(statusWindowTxt);
    }

    public static void receiveMessage(Activity activity, String msg) {
        try {
            BoardMap _map = ((MapCanvas) activity.findViewById(R.id.pathGrid)).getFinder();

            statusWindowTxt += "RPI -> BLTH:\t\t" + msg + "\n";
            statusWindowTxt = statusWindowTxt.replace("\n\n", "\n");

            String[] parts = msg.split("\\|");
            switch(parts[0]) {
                case STM_COMMAND_FORWARD:
                case STM_COMMAND_REVERSE:
                    _map.getRobo().motorRotate(msg.equals("f") ? ROBOT_MOTOR_FORWARD : ROBOT_MOTOR_REVERSE);
                    MessageFragment.sendMessage("MFIN -> RPI:\t\t", STM_COMMAND_STOP);
                    MessageFragment.addSeparator();
                    _map.getRobo().setMotor(ROBOT_MOTOR_STOP);
                    break;
                case BLUETOOTH_TARGET_IDENTIFIER:
                    //MapCanvas.drawTargetImage(1,2);
                    int targetid = Integer.parseInt(parts[1]);
                    int imageid = Integer.parseInt(parts[2]);;
                    Target t = _map.getTargets().get(targetid-1);
                    t.setImg(imageid);
                    if (_map.hasReceivedAllTargets()) timerDialog.setBegan(false);
                    break;
                case BLUETOOTH_RUN_DONE:
                    timerDialog.setBegan(false);
                    break;
            }

            messageReceivedTextView.setText(statusWindowTxt);
            scrollView.fullScroll(View.FOCUS_DOWN);
        } catch (Exception e) {
            //user switched fragment
            return;
        }

    }
}
