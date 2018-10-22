package com.erivera.apps.recipes.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.erivera.apps.recipes.R
import com.erivera.apps.recipes.presenter.RecipePresenter
import com.erivera.apps.recipes.utils.HeaderItemDecoration
import com.erivera.apps.recipes.adapter.RecipeAdapter
import com.erivera.apps.recipes.model2.RecipeListModelInterface
import kotlinx.android.synthetic.main.activity_recipe.*

class RecipeActivity : AppCompatActivity() {
    private lateinit var slideUp : Animation
    private lateinit var slideDown : Animation
    lateinit var presenter : RecipePresenter
    private lateinit var adapter : RecipeAdapter
    lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down)
        presenter = RecipePresenter(this)
        initToolbar()
        initRecycler()
        initListeners()
    }

    override fun onResume() {
        super.onResume()
        presenter.notifyPresenterOfOnResume()
    }

    private fun initToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        toolbarTitle.text = title
    }

    private fun initRecycler() {
        layoutManager = LinearLayoutManager(this)
        recipeRecycler.layoutManager = layoutManager
        adapter = RecipeAdapter(this)
        recipeRecycler.adapter = adapter
        recipeRecycler.addItemDecoration(HeaderItemDecoration(recipeRecycler, adapter as HeaderItemDecoration.StickyHeaderInterface))
        recipeRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                presenter.notifyPresenterOfOnScrolled()
            }
        })
    }

    private fun initListeners(){
        toTop.setOnClickListener { presenter.notifyPresenterOfToTopButtonClick() }
    }

    fun notifyViewToSmoothScrollToTop(){
        recipeRecycler.smoothScrollToPosition(0)
        toTop.startAnimation(slideDown)
        toTop.visibility = View.GONE
    }

    fun notifyViewToSetPosition(position: Int) {
        val firstPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
        if(firstPosition >= position){
            recipeRecycler.scrollToPosition(position)
        }
        else{
            recipeRecycler.scrollToPosition(position)
            val smoothScroller = object : LinearSmoothScroller(this) {
                override fun getVerticalSnapPreference(): Int {
                    return LinearSmoothScroller.SNAP_TO_START
                }
            }
            smoothScroller.targetPosition = position
            layoutManager.startSmoothScroll(smoothScroller)
        }
    }

    fun notifyViewOfTodayTagDetection(){
        val firstItem = layoutManager.findFirstVisibleItemPosition()
        if(firstItem == 0){
            toTop.startAnimation(slideDown)
            toTop.visibility = View.GONE
        }
        else{
            toTop.startAnimation(slideUp)
            toTop.visibility = View.VISIBLE
        }
    }

    fun notifyViewToDisplayErrorDialog() {
        Toast.makeText(this,"Error when retrieving list.",Toast.LENGTH_SHORT).show()
    }

    fun notifyViewOfNewRecipeList(modelInterfaceList: List<RecipeListModelInterface>) {
        adapter.recipeModelInterfaceList = modelInterfaceList
        adapter.notifyDataSetChanged()
    }

    fun notifyViewOfNewRecipeListAfter(initialSize: Int, newSize : Int){
        adapter.notifyItemRangeInserted(initialSize,newSize)
    }

    fun notifyViewToChangeVisibilityOfProgressBar(visibility: Int) {
        progressBar.visibility = visibility
    }
}
