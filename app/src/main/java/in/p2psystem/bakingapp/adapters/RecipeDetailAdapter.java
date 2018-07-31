package in.p2psystem.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.p2psystem.bakingapp.R;
import in.p2psystem.bakingapp.model.Recipe;
import in.p2psystem.bakingapp.model.RecipeIngredient;

public class RecipeDetailAdapter extends RecyclerView.Adapter<RecipeDetailAdapter.DetailViewHolder> {
    private final Context context;
    private final Recipe recipe;
    private final String TAG = RecipeDetailAdapter.class.getSimpleName();
    private final int VIEW_HEADER = 0;
    private final int VIEW_STEPS = 1;
    private final RecipeDetailClickListener clickListener;
    private DetailViewHolder viewHolder;

    public RecipeDetailAdapter(Context context, Recipe recipe, RecipeDetailClickListener clickListener) {
        this.context = context;
        this.recipe = recipe;
        this.clickListener = clickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return VIEW_HEADER;
        else return VIEW_STEPS;
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId;
        switch (viewType) {
            case VIEW_HEADER:
                layoutId = R.layout.item_recipe_detail;
                break;
            case VIEW_STEPS:
                layoutId = R.layout.item_step_basic;
                break;
            default:
                throw new IllegalArgumentException("Invalid value of viewtype. Found: " + viewType);
        }
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, final int position) {
        viewHolder = holder;
        if (getItemViewType(position) == VIEW_HEADER) {
            Picasso.get().load(recipe.getRecipeImgUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(holder.imgRecipeIv);

            holder.nameRecipeTv.setText(recipe.getRecipeName());

            holder.servingRecipeTv.setText(context.getResources().getString(R.string.servings_count, recipe.getRecipeServings()));

            StringBuilder builder = new StringBuilder();
            builder.append("Ingredients:\n");
            int i = 1;
            for (RecipeIngredient ingredient : recipe.getRecipeIngredients()) {
                builder.append("\t" + i++).append(". ").append(ingredient.toString()).append("\n");
            }
            holder.ingredientsRecipeTv.setText(builder.toString());
        } else if (getItemViewType(position) == VIEW_STEPS) {
            String step = context.getResources().getString(R.string.step_formatter,
                    recipe.getRecipeSteps().get(position - 1).getId() + 1, recipe.getRecipeSteps().get(position - 1).getShortDescription());
            holder.stepRecipeTv.setText(step);
        }
    }

    @Override
    public int getItemCount() {
        int steps = recipe.getRecipeSteps().size();
        return steps + 1;
    }

    public int getViewHolderPosition() {
        if (viewHolder != null) return viewHolder.getAdapterPosition();
        return 0;
    }

    public interface RecipeDetailClickListener {
        void onRecipeStepClick(int position);
    }

    public class DetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Nullable
        @BindView(R.id.img_recipe)
        ImageView imgRecipeIv;

        @Nullable
        @BindView(R.id.ingredients_recipe)
        TextView ingredientsRecipeTv;

        @Nullable
        @BindView(R.id.name_recipe)
        TextView nameRecipeTv;

        @Nullable
        @BindView(R.id.recipe_step)
        TextView stepRecipeTv;

        @Nullable
        @BindView(R.id.servings_recipe)
        TextView servingRecipeTv;

        public DetailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            clickListener.onRecipeStepClick(clickedPosition - 1);
        }
    }
}
