package com.erivera.apps.recipes.network

import com.erivera.apps.recipes.model2.EdamamSearchHit
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface EdamamApi {
    companion object {
        const val APP_ID = "169a91bc"
        const val APP_KEY = "335fa229a55c87c95325d44dfb0bd800"
    }

    @GET("/search")
    fun getRecipeSearchResult(@Query("q") searchQuery: String,
                   @Query("app_id") appID : String,
                   @Query("app_key") appKey : String,
                   @Query("from") fromIndex: String,
                   @Query("to") toIndex: String) : Call<EdamamSearchHit>
}