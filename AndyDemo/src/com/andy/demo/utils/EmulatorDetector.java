package com.andy.demo.utils;

import java.io.File;

import android.os.Build;

public class EmulatorDetector {

    private static final String TAG = "EmulatorDetector";

    private static int rating = -1;

    /**
     * Detects if app is currenly running on emulator, or real device.
     * 
     * @return true for emulator, false for real devices
     */
    public static boolean isEmulator() {

        if (rating < 0) { // rating is not calculated yet
            int newRating = 0;

            if (Build.PRODUCT.equals("sdk") || Build.PRODUCT.equals("google_sdk") || Build.PRODUCT.equals("sdk_x86") ||
                Build.PRODUCT.equals("vbox86p")) {
                newRating++;
            }

            if (Build.MANUFACTURER.equals("unknown") || Build.MANUFACTURER.equals("Genymotion")) {
                newRating++;
            }

            if (Build.BRAND.equals("generic") || Build.BRAND.equals("generic_x86")) {
                newRating++;
            }

            if (Build.DEVICE.equals("generic") || Build.DEVICE.equals("generic_x86") || Build.DEVICE.equals("vbox86p")) {
                newRating++;
            }

            if (Build.MODEL.equals("sdk") || Build.MODEL.equals("google_sdk") ||
                Build.MODEL.equals("Android SDK built for x86") || Build.MODEL.toLowerCase().contains("bluestacks") ||
                Build.MODEL.toLowerCase().contains("genymotion")) {
                newRating++;
            }

            if (Build.HARDWARE.equals("goldfish") || Build.HARDWARE.equals("vbox86")) {
                newRating++;
            }

            if (Build.FINGERPRINT.contains("generic/sdk/generic") ||
                Build.FINGERPRINT.contains("generic_x86/sdk_x86/generic_x86") ||
                Build.FINGERPRINT.contains("generic/google_sdk/generic") ||
                Build.FINGERPRINT.contains("generic/vbox86p/vbox86p")) {
                newRating++;
            }
            if (isRunningInBluestacks()) {
                newRating++;
                return true;
            }
            rating = newRating;
        }

        return rating > 4;
    }

    /**
     * Returns string with human-readable listing of Build.* parameters used in {@link #isEmulator()} method.
     * 
     * @return all involved Build.* parameters and its values
     */
//    public static String getDeviceListing() {
//        return "Build.PRODUCT: " + Build.PRODUCT + "\n" + "Build.MANUFACTURER: " + Build.MANUFACTURER + "\n" +
//               "Build.BRAND: " + Build.BRAND + "\n" + "Build.DEVICE: " + Build.DEVICE + "\n" + "Build.MODEL: " +
//               Build.MODEL + "\n" + "Build.HARDWARE: " + Build.HARDWARE + "\n" + "Build.FINGERPRINT: " +
//               Build.FINGERPRINT;
//    }

    /**
     * Prints all Build.* parameters used in {@link #isEmulator()} method to logcat.
     */
//    public static void logcat() {
//        Log.d(TAG, getDeviceListing());
//    }
    
    
    private static String knownBluestacksFiles[] = {
        "bstcmd_shim",
        "bstfolder_ctl",
        "bstfolderd",
        "bstshutdown",
        "bstshutdown_core"
    };
    
    /**
     * @return
     */
   private  static boolean isRunningInBluestacks() {
      boolean status = false;
      for (String s : knownBluestacksFiles) {
        File file = new File("/system/bin/" + s);
        if (file.exists()) {
          status = true;
          break;
        }
      }
//      String text = "No running in Bluestacks";
//      if (status) {
//        text = "Running in Bluestacks";
//      }
      return status;
    }
    
    
    

}
