package com.example.android.bakingapp.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.entities.Step;
import com.example.android.bakingapp.utils.BitmapUtility;

/**
 * Created by Mauricio Torres on 04/02/2018.
 */

public class RecipeStepListAdapter extends RecyclerView.Adapter<RecipeStepListAdapter.RecipeStepListAdapterViewHolder> {
    private Step[] stepsData;
    private final Context mContext;
    private final RecipeStepListAdapterOnClickHandler mClickHandler;

    public interface RecipeStepListAdapterOnClickHandler {
        void OnClick(Step step);
    }

    public RecipeStepListAdapter(RecipeStepListAdapterOnClickHandler handler, Context context) {
        mContext = context;
        mClickHandler = handler;
    }

    @Override
    public RecipeStepListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.recipe_steps_list_item, parent, false);
        return new RecipeStepListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeStepListAdapterViewHolder holder, int position) {
        Step currentStep = getStepsData()[position];
        if (currentStep == null) return;
        if (currentStep.Thumbnail != null)
            holder.ivRecipeStepImage.setImageBitmap(BitmapUtility.getImage(currentStep.Thumbnail));
        else holder.ivRecipeStepImage.setVisibility(View.GONE);
        String desc = (currentStep.Id == 0 ? "" : (currentStep.Id + ". ")) + currentStep.ShortDescription;
        holder.tvRecipeDesc.setText(desc);
    }

    @Override
    public int getItemCount() {
        if (stepsData == null) return 0;
        else return stepsData.length;
    }

    public void swapData(Step[] mData) {
        if (mData == null || mData.length == 0) {
            stepsData = null;
            this.notifyDataSetChanged();
            return;
        }

        stepsData = mData;
        this.notifyDataSetChanged();
    }

    public Step[] getStepsData() {
        return stepsData;
    }

    public class RecipeStepListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView tvRecipeDesc;
        public final ImageView ivRecipeStepImage;

        public RecipeStepListAdapterViewHolder(View view) {
            super(view);
            tvRecipeDesc = (TextView) itemView.findViewById(R.id.item_recipe_step_desc);
            ivRecipeStepImage = (ImageView) itemView.findViewById(R.id.item_recipe_step_img);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Step step = getStepsData()[pos];

            mClickHandler.OnClick(step);
        }
    }
}
