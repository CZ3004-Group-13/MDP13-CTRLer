package ntu.scse.mdp13.bluetooth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import ntu.scse.mdp13.R;

public class MessageFragment extends Fragment {

    private static final String TAG = "MessageFragment";

    private static String statusWindowTxt = "";

    Button send;
    static TextView messageReceivedTextView;
    EditText typeBoxEditText;
    static ScrollView scrollView;

    // initializations
    public MessageFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bthmessage, container, false);
        send = (Button) rootView.findViewById(R.id.messageButton);
        messageReceivedTextView = (TextView) rootView.findViewById(R.id.messageReceivedTextView);
        typeBoxEditText = (EditText) rootView.findViewById(R.id.typeBoxEditText);
        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView2D);

        //messageReceivedTextView.setMovementMethod(new ScrollingMovementMethod());


        // get shared preferences
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

    public static void sendMessage(String prefix, String sentText) {
        BluetoothService.getInstance(null, null).sendMessage(sentText);
        statusWindowTxt += prefix + sentText + '\n';
        messageReceivedTextView.setText(statusWindowTxt);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    public static void addSeparator() {
        statusWindowTxt += "----------------------------------------------------" + '\n';
        messageReceivedTextView.setText(statusWindowTxt);
    }

    public static void receiveMessage(String msg) {
        try {
            statusWindowTxt += "RPI -> BLTH:\t\t" + msg + "\n";
            statusWindowTxt = statusWindowTxt.replace("\n\n", "\n");

            messageReceivedTextView.setText(statusWindowTxt);
            scrollView.fullScroll(View.FOCUS_DOWN);
        } catch (Exception e) {
            //user switched fragment
            return;
        }

    }
}
