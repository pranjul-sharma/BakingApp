package in.p2psystem.bakingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.p2psystem.bakingapp.R;
import in.p2psystem.bakingapp.RecipeDetailActivity;
import in.p2psystem.bakingapp.model.Recipe;

import static in.p2psystem.bakingapp.RecipeDetailActivity.RECIPE_KEY;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.ListViewHolder> {

    private final Context context;
    List<Recipe> recipes;


    public RecipeListAdapter(Context context, List<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_recipe_card,parent,false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        final Recipe recipe = recipes.get(position);
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if(windowManager!=null) windowManager.getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        holder.recipeImageView.setMinimumHeight((int)(height/3.75));
        holder.recipeName.setText(recipe.getRecipeName());

        Picasso.get().load(recipe.getRecipeImgUrl())
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(holder.recipeImageView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RecipeDetailActivity.class);
                intent.putExtra(RECIPE_KEY,recipe);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(recipes == null) return 0;
        return recipes.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder{
        @Nullable
        @BindView(R.id.image_recipe)
        ImageView recipeImageView;

        @Nullable
        @BindView(R.id.text_recipe_name)
        TextView recipeName;

        @Nullable
        @BindView(R.id.cardview_list)
        CardView cardView;
        public ListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
