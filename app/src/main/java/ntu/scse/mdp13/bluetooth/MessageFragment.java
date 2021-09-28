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
    TextView messageReceivedTextView;
    EditText typeBoxEditText;
    ScrollView scrollView;

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
                String sentText = "" + typeBoxEditText.getText().toString();
                BluetoothService.getInstance(null, null).sendMessage(sentText);
                statusWindowTxt += sentText + '\n';
                messageReceivedTextView.setText(statusWindowTxt);
                scrollView.fullScroll(View.FOCUS_DOWN);
                typeBoxEditText.setText("");
            }
        });

        return rootView;
    }

    public static void addTextToStatusWindow(Activity activity, String stringToAdd) {
        try {
            TextView statusWindow = activity.findViewById(R.id.messageReceivedTextView);
            ScrollView scrollView = activity.findViewById(R.id.scrollView2D);

            statusWindowTxt += stringToAdd + "\n";
            statusWindowTxt = statusWindowTxt.replace("\n\n", "\n");

            statusWindow.setText(statusWindowTxt);
            scrollView.fullScroll(View.FOCUS_DOWN);
        } catch (Exception e) {
            //user switched fragment
            return;
        }

    }
}
