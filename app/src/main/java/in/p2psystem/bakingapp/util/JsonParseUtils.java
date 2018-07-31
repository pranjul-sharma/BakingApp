package in.p2psystem.bakingapp.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import in.p2psystem.bakingapp.model.Recipe;
import in.p2psystem.bakingapp.model.RecipeIngredient;
import in.p2psystem.bakingapp.model.RecipeStep;

public class JsonParseUtils {

    public static List<Recipe> parseRecipeData(String data) {
        List<Recipe> recipes = new ArrayList<>();

        JsonParser parser = new JsonParser();
        JsonElement rootNode = parser.parse(data);
        Gson gson = new GsonBuilder().create();
        JsonArray array = rootNode.getAsJsonArray();
        for (JsonElement element : array) {
            JsonObject object = element.getAsJsonObject();
            JsonElement idNode = object.get("id");
            JsonElement nameNode = object.get("name");
            JsonElement ingredientsNode = object.get("ingredients");
            Type ingredientsType = new TypeToken<List<RecipeIngredient>>() {
            }.getType();
            List<RecipeIngredient> ingredients = gson.fromJson(ingredientsNode, ingredientsType);
            JsonElement stepsNode = object.get("steps");
            Type stepsType = new TypeToken<List<RecipeStep>>() {
            }.getType();
            List<RecipeStep> steps = gson.fromJson(stepsNode, stepsType);
            JsonElement servingsNode = object.get("servings");
            JsonElement imageNode = object.get("image");
            String imgurl = null;
            if (!imageNode.getAsString().equals(""))
                imgurl = imageNode.getAsString();
            Recipe recipe = new Recipe(
                    idNode.getAsInt(), nameNode.getAsString(),
                    ingredients, steps, servingsNode.getAsInt(),
                    imgurl);

            recipes.add(recipe);
        }

        return recipes;
    }
}
