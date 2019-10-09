package com.robotemi.sdk.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.robotemi.sdk.NlpResult;
import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;
import com.robotemi.sdk.activitystream.ActivityStreamPublishMessage;
import com.robotemi.sdk.listeners.OnBeWithMeStatusChangedListener;
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener;
import com.robotemi.sdk.listeners.OnLocationsUpdatedListener;
import com.robotemi.sdk.listeners.OnRobotReadyListener;

import java.util.List;

/*
 * Created by Shubham Jindal
 */
public class BetweenLocationActivity extends AppCompatActivity implements
        Robot.NlpListener,
        OnRobotReadyListener,
        Robot.ConversationViewAttachesListener,
        Robot.WakeupWordListener,
        Robot.ActivityStreamPublishListener,
        Robot.TtsListener,
        OnBeWithMeStatusChangedListener,
        OnGoToLocationStatusChangedListener,
        OnLocationsUpdatedListener {

    private Robot robot;
    public Locations Data = new Locations();

    /*
        Setting up all the event listeners
     */

    @Override
    protected void onStart() {
        super.onStart();
        robot.addOnRobotReadyListener(this);
        robot.addNlpListener(this);
        robot.addOnBeWithMeStatusChangedListener(this);
        robot.addOnGoToLocationStatusChangedListener(this);
        robot.addConversationViewAttachesListenerListener(this);
        robot.addWakeupWordListener(this);
        robot.addTtsListener(this);
        robot.addOnLocationsUpdatedListener(this);
    }

    /*
        Removing the event listeners upon leaving the app.
     */

    @Override
    protected void onStop() {
        super.onStop();
        robot.removeOnRobotReadyListener(this);
        robot.removeNlpListener(this);
        robot.removeOnBeWithMeStatusChangedListener(this);
        robot.removeOnGoToLocationStatusChangedListener(this);
        robot.removeConversationViewAttachesListenerListener(this);
        robot.removeWakeupWordListener(this);
        robot.removeTtsListener(this);
        robot.removeOnLocationsUpdateListener(this);
    }

    /*
        Places this application in the top bar for a quick access shortcut.
     */

    @Override
    public void onRobotReady(boolean isReady) {
        if (isReady) {
            try {
                final ActivityInfo activityInfo = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
                robot.onStart(activityInfo);
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_between_location);
        robot = Robot.getInstance(); // get an instance of the robot in order to begin using its features.
        Resources res = getResources();
        ImageView imageView = (ImageView) findViewById(R.id.ImageViewBetween);
        String imageName = Data.LocationData.get(Data.currentLocation).Image;
        int resID = res.getIdentifier(imageName , "drawable", getPackageName());
        imageView.setImageResource(resID);
    }

    public void continueTour(View view) {
        Data.currentLocation++;
        robot.speak(TtsRequest.create("I will now take you to the " + Data.LocationData.get(Data.currentLocation).Name, true));
    }

    public void endTour(View view) {
        Data.currentLocation = 0;
        robot.speak(TtsRequest.create("I will now go back to the Entrance.", true));
    }

    /*
        Hiding keyboard after every button press
     */

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public void onNlpCompleted(NlpResult nlpResult) {
        //do something with nlp result. Base the action specified in the AndroidManifest.xml
        Toast.makeText(BetweenLocationActivity.this, nlpResult.action, Toast.LENGTH_SHORT).show();

        switch (nlpResult.action) {
            case "home.welcome":
                robot.tiltAngle(23, 5.3F);
                break;

            case "home.dance":
                long t= System.currentTimeMillis();
                long end = t+5000;
                while(System.currentTimeMillis() < end) {
                    robot.skidJoy(0F, 1F);
                }
                break;

            case "home.sleep":
                robot.goTo("home base");
                break;
        }
    }

    @Override
    public void onWakeupWord(String wakeupWord) {
        // Do anything on wakeup. Follow, go to location, or even try creating dance moves.
    }

    @Override
    public void onTtsStatusChanged(TtsRequest ttsRequest) {

        // Do whatever you like upon the status changing. after the robot finishes speaking
        String location = Data.LocationData.get(Data.currentLocation).Name;
        if (ttsRequest.getSpeech().equals(("I will now take you to the " + location)) &&
                ttsRequest.getStatus().toString().equals("COMPLETED")) {
            robot.goTo(location.toLowerCase().trim());
        }

        if (ttsRequest.getSpeech().equals("I will now go back to the Entrance.") &&
                ttsRequest.getStatus().toString().equals("COMPLETED")) {
            robot.goTo("Entrance".toLowerCase().trim());
        }

    }

    @Override
    public void onBeWithMeStatusChanged(String status) {
        //  When status changes to "lock" the robot recognizes the user and begin to follow.
        switch(status) {
            case "abort":
                // do something i.e. speak
                robot.speak(TtsRequest.create("Abort", false));
                break;

            case "calculating":
                robot.speak(TtsRequest.create("Calculating", false));
                break;

            case "lock":
                robot.speak(TtsRequest.create("Lock", false));
                break;

            case "search":
                robot.speak(TtsRequest.create("search", false));
                break;

            case "start":
                robot.speak(TtsRequest.create("Start", false));
                break;

            case "track":
                robot.speak(TtsRequest.create("Track", false));
                break;
        }
    }


    @Override
    public void onGoToLocationStatusChanged(String location, String status) {
        switch (status) {
            case "start":
//                robot.speak(TtsRequest.create("Starting", false));
                break;

            case "calculating":
//                robot.speak(TtsRequest.create("Calculating", false));
                break;

            case "going":
//                robot.speak(TtsRequest.create("Going", false));
                break;

            case "complete":
//                robot.speak(TtsRequest.create("Completed", false));
                if (location.equals("entrance")) {
                    final Intent intent = new Intent(this, MainActivity.class);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(intent);
                        }

                    }, 3000);
                }
                if (Data.currentLocation == Data.LocationData.size() - 1) {
                    robot.speak(TtsRequest.create(Data.LocationData.get(Data.currentLocation).Description, true));
                    final Intent intent = new Intent(this, EndLocationActivity.class);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(intent);
                        }

                    }, 3000);
                }
                if (location.toLowerCase().equals(Data.LocationData.get(Data.currentLocation).Name.toLowerCase())) {
                    robot.speak(TtsRequest.create(Data.LocationData.get(Data.currentLocation).Description, true));
                    Resources res = getResources();
                    ImageView imageView = (ImageView) findViewById(R.id.ImageViewBetween);
                    String imageName = Data.LocationData.get(Data.currentLocation).Image;
                    int resID = res.getIdentifier(imageName , "drawable", getPackageName());
                    imageView.setImageResource(resID);
                }
                break;

            case "abort":
//                robot.speak(TtsRequest.create("Cancelled", false));
                break;
        }
    }

    @Override
    public void onConversationAttaches(boolean isAttached) {
        if (isAttached) {
            //Do something as soon as the conversation is displayed.
        }
    }

    @Override
    public void onPublish(ActivityStreamPublishMessage message) {
        //After the activity stream finished publishing (photo or otherwise).
        //Do what you want based on the message returned.
    }

    @Override
    public void onLocationsUpdated(List<String> locations) {

        //Saving or deleting a location will update the list.

        Toast.makeText(this, "Locations updated :\n" + locations, Toast.LENGTH_LONG).show();
    }
}
