package com.example.android.bakingapp.ui.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.android.bakingapp.R;

/**
 * Created by Mauricio Torres on 03/03/2018.
 *
 */

public class BakingService extends IntentService {
    public static final String ACTION_BAKING = "com.example.android.bakingapp.action.baking";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public BakingService() {
        super("BakingService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_BAKING.equals(action)) {
                handleActionBaking();
            }
        }
    }

    private void handleActionBaking() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
        //Trigger data update to handle the GridView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_recipe_ingredients);
        //Now update all widgets
        RecipeWidgetProvider.updateBakingWidgets(this, appWidgetManager, appWidgetIds);
    }

    public static void startActionWBacking(Context context) {
        Intent intent = new Intent(context, BakingService.class);
        intent.setAction(ACTION_BAKING);
        context.startService(intent);
    }
}
