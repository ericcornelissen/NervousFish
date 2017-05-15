package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.nervousfish.nervousfish.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example activity to verify identity in Bluetooth connections.
 */
public class GridActivity extends Activity {

    private static final Logger LOGGER = LoggerFactory.getLogger("GridActivity");
    private static final View.OnClickListener onClickListener = new View.OnClickListener() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick(final View v) {
            String button = v.getContentDescription().toString();
            LOGGER.info("button '" + button + "' clicked");
        }

    };

    /**
     * Stuff that needs to be done when the new activity being created.
     *
     * @param savedInstanceState The saved state
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.grid);

        this.initButtons();

        LOGGER.info("GridActivity created");
    }

    /**
     * Initialize all the buttons.
     */
    private void initButtons() {
        this.findViewById(R.id.button00).setOnClickListener(onClickListener);
        this.findViewById(R.id.button01).setOnClickListener(onClickListener);
        this.findViewById(R.id.button02).setOnClickListener(onClickListener);
        this.findViewById(R.id.button03).setOnClickListener(onClickListener);
        this.findViewById(R.id.button10).setOnClickListener(onClickListener);
        this.findViewById(R.id.button11).setOnClickListener(onClickListener);
        this.findViewById(R.id.button12).setOnClickListener(onClickListener);
        this.findViewById(R.id.button13).setOnClickListener(onClickListener);
        this.findViewById(R.id.button20).setOnClickListener(onClickListener);
        this.findViewById(R.id.button21).setOnClickListener(onClickListener);
        this.findViewById(R.id.button22).setOnClickListener(onClickListener);
        this.findViewById(R.id.button23).setOnClickListener(onClickListener);
        this.findViewById(R.id.button30).setOnClickListener(onClickListener);
        this.findViewById(R.id.button31).setOnClickListener(onClickListener);
        this.findViewById(R.id.button32).setOnClickListener(onClickListener);
        this.findViewById(R.id.button33).setOnClickListener(onClickListener);
    }

}
