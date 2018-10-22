package com.erivera.apps.recipes.model2

import android.graphics.drawable.Drawable
import com.erivera.apps.recipes.model.Recipe

class RecipeListRecipeModel : RecipeListModelInterface{
    var recipeName : String = ""
    var recipeDrawable : Drawable? = null
    var recipeImageUrl : String = ""
    var recipeHealthLabels : String = ""
    var recipe : Recipe? = null
}