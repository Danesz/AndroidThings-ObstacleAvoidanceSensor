package com.danieldallos.androidthings.obstacleavoidancesensor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.danieldallos.obstacleavoidancesensor.R;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AndroidIotTemperature.MainActivity";

    private static final String OBSTACLE_SENSOR_GPIO_PIN_NAME = "BCM21";

    private static final String YELLOW_PIN_NAME = "BCM16";
    private static final String RED_PIN_NAME = "BCM20";


    private Gpio mObstacleSensorGpio;
    private Gpio mYellowLed, mRedLed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PeripheralManagerService service = new PeripheralManagerService();
        Log.d(TAG, "Available GPIOs: " + service.getGpioList());


        try {
            // Step 1. Create GPIO connection.
            mObstacleSensorGpio = service.openGpio(OBSTACLE_SENSOR_GPIO_PIN_NAME);
            // Step 2. Configure as an input.
            mObstacleSensorGpio.setDirection(Gpio.DIRECTION_IN);
            // Step 3. Enable edge trigger events.
            mObstacleSensorGpio.setEdgeTriggerType(Gpio.EDGE_BOTH);
            // Step 4. Set Active type to LOW, then it will trigger HIGH events
            mObstacleSensorGpio.setActiveType(Gpio.ACTIVE_LOW);
            // Step 5. Register an event callback.
            mObstacleSensorGpio.registerGpioCallback(mCallback);
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }

        try {
            // Step 1. Create GPIO connection.
            mYellowLed = service.openGpio(YELLOW_PIN_NAME);
            mRedLed = service.openGpio(RED_PIN_NAME);

            // Step 2. Configure as an output with default LOW (false) value.
            mYellowLed.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mRedLed.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);


        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }
    }


    // Step 5. Register an event callback.
    private GpioCallback mCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            try {
                Log.i(TAG, "GPIO changed, obstacle is close: " + gpio.getValue());

                if (gpio.getValue()){
                    // obstacle is detected, do an action!
                    // ....
                    // now just turn on the LED
                    mRedLed.setValue(true);

                } else {
                    // obstacle is far away, do an action!
                    // ....
                    // now just turn off the LED
                    mRedLed.setValue(false);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Step 6. Return true to keep callback active.
            return true;
        }
    };


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();

        // Step 7. Close the resource
        if (mObstacleSensorGpio != null) {
            // unregister from the callback
            mObstacleSensorGpio.unregisterGpioCallback(mCallback);
            try {
                mObstacleSensorGpio.close();
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }

        // Step 7. Close the resource
        if (mYellowLed != null) {
            try {
                mYellowLed.close();
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }


        // Step 7. Close the resource
        if (mRedLed != null) {
            try {
                mRedLed.close();
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    }
}
