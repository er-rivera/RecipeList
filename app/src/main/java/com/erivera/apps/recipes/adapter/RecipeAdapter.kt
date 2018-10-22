package com.erivera.apps.recipes.adapter

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.erivera.apps.recipes.R
import com.erivera.apps.recipes.activity.RecipeActivity
import com.erivera.apps.recipes.model2.*
import com.erivera.apps.recipes.utils.HeaderItemDecoration
import com.erivera.apps.recipes.utils.inflate
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_item_header.view.*
import kotlinx.android.synthetic.main.recycler_item_recipe.view.*
import kotlinx.android.synthetic.main.recycler_item_show_more.view.*

class RecipeAdapter(private val activity : RecipeActivity) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), HeaderItemDecoration.StickyHeaderInterface {
    companion object {
        const val VIEW_TYPE_DATE_HEADER = 0
        const val VIEW_TYPE_RECIPE = 1
        const val VIEW_TYPE_EMPTY = 2
        const val VIEW_TYPE_SHOW_MORE = 3
    }

    var recipeModelInterfaceList : List<RecipeListModelInterface> = mutableListOf()
    private var currentFloatingHeader : ViewHolderHeader? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DATE_HEADER -> ViewHolderHeader(parent.inflate(R.layout.recycler_item_header,false))
            VIEW_TYPE_EMPTY -> ViewHolderEmptyHeader(parent.inflate(R.layout.recycler_item_empty, false))
            VIEW_TYPE_RECIPE -> ViewHolderContent(parent.inflate(R.layout.recycler_item_recipe, false))
            VIEW_TYPE_SHOW_MORE -> ViewHolderShowMore(parent.inflate(R.layout.recycler_item_show_more, false))
            else -> ViewHolderEmptyHeader(parent.inflate(R.layout.recycler_item_empty,false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            recipeModelInterfaceList[position] is RecipeListHeaderModel -> VIEW_TYPE_DATE_HEADER
            recipeModelInterfaceList[position] is RecipeListRecipeModel -> VIEW_TYPE_RECIPE
            recipeModelInterfaceList[position] is RecipeListEmptyModel -> VIEW_TYPE_EMPTY
            recipeModelInterfaceList[position] is RecipeListShowMoreModel -> VIEW_TYPE_SHOW_MORE
            else -> VIEW_TYPE_DATE_HEADER
        }
    }

    override fun getItemCount(): Int {
        return recipeModelInterfaceList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_DATE_HEADER -> bindHolderHeader(holder.itemView, position)
            VIEW_TYPE_RECIPE -> bindHolderContent(holder as ViewHolderContent, position)
        }
    }

    override fun bindHeaderData(header: View, headerPosition: Int) {
        currentFloatingHeader = ViewHolderHeader(header)
        bindHolderHeader(header, headerPosition)
    }

    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        var headerPosition = 0
        var currentPosition = itemPosition
        do {
            if (this.isHeader(currentPosition)) {
                headerPosition = currentPosition
                break
            }
            currentPosition -= 1
        } while (currentPosition >= 0)
        return headerPosition
    }

    override fun getHeaderLayout(headerPosition: Int): Int {
        return R.layout.recycler_item_header
    }

    override fun isHeader(itemPosition: Int): Boolean {
        if (itemPosition in recipeModelInterfaceList.indices) {
            return recipeModelInterfaceList[itemPosition] is RecipeListHeaderModel
        }
        return false
    }

    override fun isInvalidModel(itemPosition: Int): Boolean {
        return recipeModelInterfaceList[itemPosition] is RecipeListShowMoreModel
    }

    private fun bindHolderHeader(view: View, position: Int) {
        val viewHolder = ViewHolderHeader(view)
        val listItem = recipeModelInterfaceList[position]
        if (listItem is RecipeListHeaderModel) {
            viewHolder.headerTextView.text = listItem.letter
        }
    }

    private fun bindHolderContent(holder: ViewHolderContent, position: Int) {
        val listItem = recipeModelInterfaceList[position]
        if(listItem is RecipeListRecipeModel){
            holder.recipeNameTextView.text =listItem.recipeName
            holder.recipeDescriptionTextView.text =listItem.recipeHealthLabels
            Picasso.get().load(listItem.recipeImageUrl).error(R.drawable.no_photo).into(holder.recipeImageView)
        }
    }

    inner class ViewHolderHeader(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerTextView: TextView = itemView.headerTextView
    }

    inner class ViewHolderShowMore(itemView: View) : RecyclerView.ViewHolder(itemView){
        val text : TextView = itemView.showMoreTextView
        private val layout : ConstraintLayout = itemView.showMoreContainer
        init{
            layout.setOnClickListener(clickListener(this))
        }
    }

    inner class ViewHolderEmptyHeader(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class ViewHolderContent(item: View) : RecyclerView.ViewHolder(item){
        val recipeNameTextView: TextView = itemView.recipeTitleTextView
        val recipeImageView : ImageView = itemView.photoImageView
        val recipeDescriptionTextView : TextView = itemView.recipeHealthLabels
        private val container : ConstraintLayout = itemView.itemRecipeConstraintLayout
        init{
            container.setOnClickListener(clickListener(this))
        }
    }

    fun clickListener(viewHolder : RecyclerView.ViewHolder) : View.OnClickListener
    {
        return View.OnClickListener {
            if(viewHolder.adapterPosition != RecyclerView.NO_POSITION){
                val listItem = recipeModelInterfaceList[viewHolder.adapterPosition]
                when(viewHolder){
                    is ViewHolderShowMore -> {
                        activity.presenter.notifyPresenterOfShowMoreClick(listItem)
                    }
                    is ViewHolderContent -> {
                        activity.presenter.notifyPresenterOfContentClick(listItem)
                    }
                }
            }
        }

    }
}