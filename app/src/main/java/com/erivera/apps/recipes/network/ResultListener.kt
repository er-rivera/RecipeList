package com.erivera.apps.recipes.network

import com.erivera.apps.recipes.model.Recipe

interface ResultListener {
    fun onSuccess(recipes : List<Recipe>)

    fun onFailure()
}