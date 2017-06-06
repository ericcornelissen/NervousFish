package com.nervousfish.nervousfish.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A {@link android.preference.PreferenceActivity} which implements and proxies the necessary calls
 * to be used with AppCompat.
 */
public abstract class AAppCompatPreferenceActivity extends PreferenceActivity {

    private AppCompatDelegate mDelegate;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    /**
     * Gets the support action bar.
     *
     * @return the support bar as {@link ActionBar}
     */
    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    /**
     * Sets the support action bar.
     * @param toolbar The {@link Toolbar} to set
     */
    public void setSupportActionBar(@Nullable final Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentView(@LayoutRes final int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentView(final View view) {
        getDelegate().setContentView(view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentView(final View view, final ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addContentView(final View view, final ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onTitleChanged(final CharSequence title, final int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    /**
     * Invalidates the option menu.
     */
    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

    /**
     * Returns the {@link AppCompatDelegate}.
     * @return the {@link AppCompatDelegate}
     */
    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }
}
