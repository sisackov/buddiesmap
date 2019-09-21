package com.buddiesmap.recommendations;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.buddiesmap.R;

import org.json.JSONException;
import org.json.JSONObject;

public class RecommendationsDialog extends Dialog {


    private final EditText mTitleText;
    private final EditText mPhoneText;
    private final EditText mLocationText;
    private String mUserId;

    /**
     * Creates a dialog window that uses the default dialog theme.
     * <p>
     * The supplied {@code context} is used to obtain the window manager and
     * base theme used to present the dialog.
     *
     * @param context the context in which the dialog should run
     */
    public RecommendationsDialog(@NonNull Context context, @NonNull String userId) {
        super(context);
        mUserId = userId;

        setContentView(R.layout.recommendation_dialog);
        setTitle(R.string.recommendation_dialog_title);

        mTitleText = findViewById(R.id.dialogEditTitle);
        mPhoneText = findViewById(R.id.dialogEditPhone);
        mLocationText = findViewById(R.id.dialogEditLocation);

        Button sendButton = findViewById(R.id.dialogButtonSend);
        sendButton.setOnClickListener(view -> sendRecommendation());
    }

    private void sendRecommendation() {
        JSONObject json = new JSONObject();
        try {
            json.put("recommenderId", mUserId);
            json.put("title", mTitleText.getText());
            json.put("phone", mPhoneText.getText());
            json.put("location", mLocationText.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new AsyncHTTPPostRequest(json).execute();

    }


}
