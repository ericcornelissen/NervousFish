package com.nervousfish.nervousfish.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity used to select a verification method.
 */
public class SelectVerificationMethodActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("SelectVerificationMethodActivity");

    private IServiceLocator serviceLocator;

    /**
     * Creates a new {@link SelectVerificationMethodActivity} activity.
     *
     * @param savedInstanceState state previous instance of this activity
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_select_verification_method);

        final Intent intent = this.getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);

        LOGGER.info("Activity created");
    }

    /**
     * Open the correct activity given a verification method.
     *
     * @param view The view on which the click was performed
     */
    public void openVerificationMethod(final View view) {
        final Intent intent = new Intent();
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);

        final int method = view.getId();
        switch (method) {
            case R.id.select_visual_verification:
                LOGGER.error("Selected visual verification method, opening activity");
                intent.setComponent(new ComponentName(this, VisualVerificationActivity.class));
                this.startActivity(intent);
                break;
            case R.id.select_rhythm_verification:
                // TODO: open correct (Rhythm) activity
                LOGGER.error("Selected rhythm verification method, opening activity");
                intent.setComponent(new ComponentName(this, WaitForSlaveActivity.class));
                this.startActivity(intent);
                break;
            default:
                LOGGER.error("unknown verification method selected: " + method);
                intent.setComponent(new ComponentName(this, MainActivity.class));
                this.startActivity(intent);
                break;
        }
    }

}
