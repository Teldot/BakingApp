package com.example.android.bakingapp.utils;

import com.example.android.bakingapp.data.entities.Step;

/**
 * Created by US on 14/02/2018.
 */

public class StepsUtility {
    public static String getNextStepDesc(Step[] steps, int currentStep, int maxLenght) {
        if (currentStep >= steps.length - 1) return null;
        int nxtStep = currentStep + 1;
        String res = steps[nxtStep].Id + ". " + steps[nxtStep].ShortDescription;
        return (res.length() > maxLenght) ? res.substring(0, maxLenght) + "..." : res;
    }

    public static String getPreviousStepDesc(Step[] steps, int currentStep, int maxLenght) {
        if (currentStep < 1) return null;
        int pvsStep = currentStep - 1;
        String res = (steps[pvsStep].Id == 0) ? "" : String.valueOf(steps[pvsStep].Id + ". ");
        res += steps[pvsStep].ShortDescription;
        return (res.length() > maxLenght) ? res.substring(0, maxLenght) + "..." : res;
    }


}
