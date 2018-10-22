package com.erivera.apps.recipes.network

import android.util.Log
import com.erivera.apps.recipes.model.Recipe
import com.erivera.apps.recipes.model2.EdamamSearchHit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory

class EdamamService {
    companion object {
        private var edamamApi : EdamamApi? = null
        private val BASE_URL = "https://api.edamam.com"


        @Synchronized
        fun getEdamamInstance(): EdamamApi {
            if (edamamApi == null) {
                val retrofit = retrofit2.Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                edamamApi = retrofit.create(EdamamApi::class.java)
            }
            return edamamApi!!
        }
    }

    fun getRecipes(searchQuery : String, fromIndex: Int, toIndex: Int, resultListener: ResultListener){
        getEdamamInstance().getRecipeSearchResult(searchQuery,EdamamApi.APP_ID,
                EdamamApi.APP_KEY,fromIndex.toString(),toIndex.toString()).enqueue(object : Callback<EdamamSearchHit>{
            override fun onResponse(call: Call<EdamamSearchHit>?, response: Response<EdamamSearchHit>?) {
                val hits = response?.body()?.hits
                if(hits != null && !hits.isEmpty()){
                    val list = mutableListOf<Recipe>()
                    for(hit in hits){
                        list.add(hit.recipe)
                    }
                    resultListener.onSuccess(list)
                }
                else{
                    resultListener.onFailure()
                }
            }

            override fun onFailure(call: Call<EdamamSearchHit>?, t: Throwable?) {
                resultListener.onFailure()
            }
        })
    }
}