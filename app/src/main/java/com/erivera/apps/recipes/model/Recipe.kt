package com.erivera.apps.recipes.model

import com.erivera.apps.recipes.model2.RecipeListModelInterface
import com.google.gson.annotations.SerializedName

class Recipe(@SerializedName("uri") val uri: String,
             @SerializedName("label") val label: String,
             @SerializedName("image") val image: String,
             @SerializedName("source") val source: String,
             @SerializedName("yield") val yield: Int,
             @SerializedName("ingredientLines") val ingredientLines: Array<String>,
             @SerializedName("healthLabels") val healthLabels: Array<String>) : RecipeListModelInterface