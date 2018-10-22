package com.erivera.apps.recipes.presenter

import android.view.View
import com.erivera.apps.recipes.model2.RecipeListModelInterface
import com.erivera.apps.recipes.activity.RecipeActivity
import com.erivera.apps.recipes.model.Recipe
import com.erivera.apps.recipes.model2.RecipeListHeaderModel
import com.erivera.apps.recipes.model2.RecipeListRecipeModel
import com.erivera.apps.recipes.model2.RecipeListShowMoreModel
import com.erivera.apps.recipes.network.EdamamService
import com.erivera.apps.recipes.network.ResultListener

class RecipePresenter(val activity: RecipeActivity) {
    companion object {
        const val START_INDEX = 0
        const val SIZE = 20
        const val SEARCH_QUERY = "tacos"
    }

    private val edamamService = EdamamService()

    fun notifyPresenterOfOnResume(){
        runInitialDataLoad()
    }

    private fun runInitialDataLoad() {
        activity.notifyViewToChangeVisibilityOfProgressBar(View.VISIBLE)
        edamamService.getRecipes(SEARCH_QUERY, START_INDEX, SIZE, object : ResultListener{
            override fun onSuccess(recipes: List<Recipe>) {
                generateAndDisplayRecipeList(recipes)
                activity.notifyViewToChangeVisibilityOfProgressBar(View.GONE)
            }
            override fun onFailure() {
                activity.notifyViewToDisplayErrorDialog()
                activity.notifyViewToChangeVisibilityOfProgressBar(View.GONE)

            }
        })
    }

    private fun generateAndDisplayRecipeList(recipes: List<Recipe>){
        val sortedRecipes = recipes.sortedBy { it.label }
        val mergedList = mergeSortedRecipesWithHeader(sortedRecipes)
        mergedList.add(RecipeListShowMoreModel(SIZE))
        activity.notifyViewOfNewRecipeList(mergedList)
    }

    //generates custom list of recipes models with alphabetical headers to
    //purpose of a wrapper model is to help alleviate processing during binding
    private fun mergeSortedRecipesWithHeader(recipes: List<Recipe>) : MutableList<RecipeListModelInterface> {
        val mergedList = mutableListOf<RecipeListModelInterface>()
        val booleanArray = Array(26){ false }
        for(recipe in recipes){
            val index = recipe.label[0].toLowerCase() - 'a'
            if(index >= 0 && index < booleanArray.size && !booleanArray[index]){
                val header = RecipeListHeaderModel(recipe.label[0].toUpperCase().toString())
                mergedList.add(header)
                booleanArray[index] = true
            }
            val recipeListModel = RecipeListRecipeModel()
            recipeListModel.recipe = recipe
            recipeListModel.recipeName = recipe.label
            recipeListModel.recipeImageUrl = recipe.image
            recipe.healthLabels.forEach { recipeListModel.recipeHealthLabels += "$it," }
            recipeListModel.recipeDrawable = null
            mergedList.add(recipeListModel)
        }
        return mergedList
    }

    fun notifyPresenterOfOnScrolled() {
        activity.notifyViewOfTodayTagDetection()
    }

    fun notifyPresenterOfToTopButtonClick() {
        activity.notifyViewToSmoothScrollToTop()
    }

    fun notifyPresenterOfShowMoreClick(recipeListModelInterface: RecipeListModelInterface) {
        if(recipeListModelInterface is RecipeListShowMoreModel){
            //TODO: add background loading to load additional items
        }
    }

    fun notifyPresenterOfContentClick(recipeListModelInterface: RecipeListModelInterface) {
        if(recipeListModelInterface is RecipeListRecipeModel){
            recipeListModelInterface.recipe?.let {
                //TODO: this will include start detail activity for recipe item click
            }
        }
    }
}