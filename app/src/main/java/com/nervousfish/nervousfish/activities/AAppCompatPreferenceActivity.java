package com.nervousfish.nervousfish.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nervousfish.nervousfish.annotations.DesignedForExtension;

import org.apache.commons.lang3.Validate;

/**
 * A {@link PreferenceActivity} which implements and proxies the necessary calls
 * to be used with AppCompat.
 */
public abstract class AAppCompatPreferenceActivity extends PreferenceActivity {

    private AppCompatDelegate mDelegate;

    /**
     * {@inheritDoc}
     */
    @Override
    @DesignedForExtension
    protected void onCreate(final Bundle savedInstanceState) {
        Validate.notNull(savedInstanceState);
        this.mDelegate = AppCompatDelegate.create(this, null);
        this.mDelegate.installViewFactory();
        this.mDelegate.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Validate.notNull(savedInstanceState);
        this.mDelegate.onPostCreate(savedInstanceState);
    }

    /**
     * Gets the support action bar.
     *
     * @return the support bar as {@link ActionBar}
     */
    public final ActionBar getSupportActionBar() {
        return this.mDelegate.getSupportActionBar();
    }

    /**
     * Sets the support action bar.
     * @param toolbar The {@link Toolbar} to set
     */
    public final void setSupportActionBar(@Nullable final Toolbar toolbar) {
        Validate.notNull(toolbar);
        this.mDelegate.setSupportActionBar(toolbar);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public final MenuInflater getMenuInflater() {
        return this.mDelegate.getMenuInflater();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setContentView(@LayoutRes final int layoutResID) {
        Validate.notNull(layoutResID);
        this.mDelegate.setContentView(layoutResID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setContentView(final View view) {
        Validate.notNull(view);
        this.mDelegate.setContentView(view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setContentView(final View view, final ViewGroup.LayoutParams params) {
        Validate.notNull(view);
        Validate.notNull(params);
        this.mDelegate.setContentView(view, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void addContentView(final View view, final ViewGroup.LayoutParams params) {
        Validate.notNull(view);
        Validate.notNull(params);
        this.mDelegate.addContentView(view, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void onPostResume() {
        super.onPostResume();
        this.mDelegate.onPostResume();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void onTitleChanged(final CharSequence title, final int color) {
        super.onTitleChanged(title, color);
        this.mDelegate.setTitle(title);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mDelegate.onConfigurationChanged(newConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void onStop() {
        super.onStop();
        this.mDelegate.onStop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void onDestroy() {
        super.onDestroy();
        this.mDelegate.onDestroy();
    }

    /**
     * Invalidates the option menu.
     */
    @Override
    public final void invalidateOptionsMenu() {
        this.mDelegate.invalidateOptionsMenu();
    }
}
