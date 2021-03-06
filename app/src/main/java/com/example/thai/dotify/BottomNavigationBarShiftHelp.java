package com.example.thai.dotify;

import android.annotation.SuppressLint;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import java.lang.reflect.Field;

/**
 * the BottomNavigationBarShiftHelp object is a helper object for the navigation bar located in the bottom of our app
 */
@SuppressLint("RestrictedApi")
class BottomNavigationBarShiftHelp {
    /**
     * Helper class to stop the shift in the bottom navigation bar
     * @param view the navigation bar view
     */
    static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("ShiftHelp", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("ShiftHelp", "Unable to change value of shift mode", e);
        }
    }
}
