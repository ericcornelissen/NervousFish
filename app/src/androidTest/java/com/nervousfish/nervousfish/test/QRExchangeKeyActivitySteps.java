package com.nervousfish.nervousfish.test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;

import com.nervousfish.nervousfish.*;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.activities.QRExchangeKeyActivity;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.modules.qr.QRGenerator;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ServiceLocator;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertTrue;


@CucumberOptions(features = "features")
public class QRExchangeKeyActivitySteps {

    private final IServiceLocator serviceLocator = (IServiceLocator) BaseTest.accessConstructor(ServiceLocator.class, Instrumentation.filesDir);

    @Rule
    public ActivityTestRule<QRExchangeKeyActivity> mActivityRule =
            new ActivityTestRule<>(QRExchangeKeyActivity.class, true, false);

    @Given("^I am viewing QRExchange activity$")
    public void iAmViewingQRExchangeActivity() {

        final Intent intent = new Intent();
        intent.putExtra(ConstantKeywords.SERVICE_LOCATOR, this.serviceLocator);
        mActivityRule.launchActivity(intent);

    }



    @When("^I press the generate button$")
    public void iPressTheGenerateButton(){
        onView(withId(R.id.generate_QR_button)).perform(click());
    }

    @When("^I press the back button$")
    public void iPressTheBackButton(){
        onView(withId(R.id.back_button_QR_exchange)).perform(click());
    }



    @Then("^I should see a popup with my qr code$")
    public void iShouldSeeAPopupWithMyQRCode() {
        AlertDialog dialog = (AlertDialog) BaseTest.getField(mActivityRule.getActivity(), "lastDialog");
        assertTrue(dialog.isShowing());

        QRExchangeKeyActivity QRExchangeActivity = mActivityRule.getActivity();
        IKey publicKey = (IKey) BaseTest.getField(QRExchangeActivity, "publicKey");
        Bitmap qrTest = QRGenerator.encode(publicKey.getType() + " "
                + publicKey.getName() + " " + publicKey.getKey());

        EspressoTestsMatchers etMatchers = new EspressoTestsMatchers();
        onView(withId(R.id.QR_code_image)).check(matches(etMatchers.withDrawable(qrTest)));
    }

    @Then("^I should return from the QRExchangeKeyActivity$")
    public void iShouldReturnFromTheQRExchangeKeyActivity() {
        assertTrue(mActivityRule.getActivity().isDestroyed());
    }


    private static class EspressoTestsMatchers {

        public Matcher<View> withDrawable(final Bitmap resource) {
            return new DrawableMatcher(resource);
        }

    }

    private static class DrawableMatcher extends TypeSafeMatcher<View> {


        private final Bitmap expected;

        public DrawableMatcher(Bitmap expected) {
            super(View.class);
            this.expected = expected;
        }

        @Override
        protected boolean matchesSafely(View target) {
            if (!(target instanceof ImageView)){
                return false;
            }
            ImageView imageView = (ImageView) target;


            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

            return bitmap.sameAs(expected);
        }


        @Override
        public void describeTo(Description description) {

        }
    }
}
