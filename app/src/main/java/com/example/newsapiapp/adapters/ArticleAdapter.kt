package com.example.newsapiapp.adapters

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.newsapiapp.R
import com.example.newsapiapp.Utils
import com.example.newsapiapp.db.Article

class ArticleAdapter() : RecyclerView.Adapter<ArticleHolder>() {

    var newslist = listOf<Article>()
    private var listener: ItemClicklistner? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.newlist, parent, false )
        val viewHolder = ArticleHolder(view)
        return viewHolder


    }

    override fun getItemCount(): Int {
        return newslist.size
    }

    override fun onBindViewHolder(holder: ArticleHolder, position: Int) {

        val article = newslist[position]

        val requestOption = RequestOptions()


        holder.itemView.apply {


            // for image

            Glide.with(this).load(article.urlToImage).apply(requestOption).listener(object: RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {

                    holder.pb.visibility = View.VISIBLE

                    return  false


                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {

                    holder.pb.visibility = View.GONE

                    return  false

                }


            }).transition(DrawableTransitionOptions.withCrossFade()).into(holder.imageView)

            holder.textTitle.setText(article.title)
            holder.tvSource.setText(article.source!!.name)
            holder.tvDescription.setText(article.description)
            holder.tvPubslishedAt.setText(Utils.DateFormat(article.publishedAt))








        }


        // storing the position and articles in the click event
        // in case we go to another framgnet
        holder.itemView.setOnClickListener {
            listener?.onItemClicked(position, article)
        }



    }

    fun setItemClickListener(listener : ItemClicklistner){
        this.listener = listener

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setlist(articles: List<Article>) {
        this.newslist = articles
        notifyDataSetChanged()

    }


    @SuppressLint("NotifyDataSetChanged")
    fun filteredList(newfilteredlist: List<Article>){
        this.newslist = newfilteredlist
        notifyDataSetChanged()
    }

}

class ArticleHolder(itemView: View) : ViewHolder(itemView){



    val textTitle : TextView = itemView.findViewById(R.id.tvTitle)
    val tvSource : TextView = itemView.findViewById(R.id.tvSource)
    val tvDescription : TextView = itemView.findViewById(R.id.tvDescription)
    val tvPubslishedAt : TextView = itemView.findViewById(R.id.tvPublishedAt)

    val imageView : ImageView = itemView.findViewById(R.id.ivArticleImage)
    val pb : ProgressBar = itemView.findViewById(R.id.pbImage)





}

interface ItemClicklistner{

    fun onItemClicked(position: Int, article: Article)
}