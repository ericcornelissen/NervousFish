package com.nervousfish.nervousfish.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.data_objects.SimpleKey;
import com.nervousfish.nervousfish.events.ContactReceivedEvent;
import com.nervousfish.nervousfish.modules.database.IDatabase;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The main activity class that shows a list of all people with their public keys
 */
public final class FirstUseActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("FirstUseActivity");

    private IServiceLocator serviceLocator;

    /**
     * Creates the new activity, should only be called by Android
     *
     * @param savedInstanceState Don't touch this
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        this.serviceLocator = (IServiceLocator) intent.getSerializableExtra(ConstantKeywords.SERVICE_LOCATOR);
        this.setContentView(R.layout.activity_first_use);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        LOGGER.info("FirstUseActivity created");
    }

    /**
     * Gets triggered when the GetStarted button is clicked.
     *
     * @param v - the {@link View} clicked
     */
    public void onGetStartedClick(final View v) {

    }
}
