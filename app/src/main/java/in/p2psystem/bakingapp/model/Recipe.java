package in.p2psystem.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Recipe implements Parcelable {
    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
    private int recipeId;
    private String recipeName;
    private List<RecipeIngredient> recipeIngredients;
    private List<RecipeStep> recipeSteps;
    private int recipeServings;
    private String recipeImgUrl;

    public Recipe(int recipeId, String recipeName, List<RecipeIngredient> recipeIngredients, List<RecipeStep> recipeSteps, int recipeServings, String recipeImgUrl) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.recipeIngredients = recipeIngredients;
        this.recipeSteps = recipeSteps;
        this.recipeServings = recipeServings;
        this.recipeImgUrl = recipeImgUrl;
    }

    protected Recipe(Parcel in) {
        recipeId = in.readInt();
        recipeName = in.readString();
        recipeIngredients = in.createTypedArrayList(RecipeIngredient.CREATOR);
        recipeSteps = in.createTypedArrayList(RecipeStep.CREATOR);
        recipeServings = in.readInt();
        recipeImgUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(recipeId);
        dest.writeString(recipeName);
        dest.writeTypedList(recipeIngredients);
        dest.writeTypedList(recipeSteps);
        dest.writeInt(recipeServings);
        dest.writeString(recipeImgUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public int getRecipeServings() {
        return recipeServings;
    }

    public List<RecipeIngredient> getRecipeIngredients() {
        return recipeIngredients;
    }

    public List<RecipeStep> getRecipeSteps() {
        return recipeSteps;
    }

    public String getRecipeImgUrl() {
        return recipeImgUrl;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public String getRecipeIngredientsList() {
        StringBuilder builder = new StringBuilder();
        int i = 1;
        for(RecipeIngredient ingredient : recipeIngredients) {
            builder.append(i++).append(". ").append(ingredient.getIngredient()).append("\n");
        }
        return builder.toString();
    }

}

