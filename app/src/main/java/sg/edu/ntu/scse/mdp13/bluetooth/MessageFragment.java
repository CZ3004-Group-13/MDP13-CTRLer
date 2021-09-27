package sg.edu.ntu.scse.mdp13.bluetooth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.edu.ntu.scse.mdp13.R;

public class MessageFragment extends Fragment {

    private static final String TAG = "MessageFragment";

    @BindView(R.id.messageButton) Button send;
    @BindView(R.id.messageReceivedTextView) TextView messageReceivedTextView;
    @BindView(R.id.typeBoxEditText) EditText typeBoxEditText;
    @BindView(R.id.scrollView2D) ScrollView scrollView;

    private static String statusWindowTxt = "";

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
        ButterKnife.bind(this, rootView);
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
