package com.example.newsapiapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.findFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.newsapiapp.R
import com.example.newsapiapp.Utils
import com.example.newsapiapp.db.SavedArticle
import com.example.newsapiapp.db.Source
import com.example.newsapiapp.mvvm.NewsDatabase
import com.example.newsapiapp.mvvm.NewsRepo
import com.example.newsapiapp.mvvm.NewsViewModel
import com.example.newsapiapp.mvvm.NewsViewModelFac
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FragmentArticle : Fragment() {

    lateinit var viewModel: NewsViewModel


    lateinit var args : FragmentArticleArgs

    var stringCheck = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_article, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (activity as AppCompatActivity).supportActionBar?.setTitle("Article View")



        val dao = NewsDatabase.getInstance(requireActivity()).newsDao
        val repository = NewsRepo(dao)
        val factory = NewsViewModelFac(repository, requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]

        args = FragmentArticleArgs.fromBundle(requireArguments())

        // initialize the views of Art Frag


        val fab = view.findViewById<FloatingActionButton>(R.id.fab)

        val textTitle: TextView = view.findViewById(R.id.tvTitle)
        val tSource: TextView = view.findViewById(R.id.tvSource)
        val tDescription: TextView = view.findViewById(R.id.tvDescription)
        val tPubslishedAt: TextView = view.findViewById(R.id.tvPublishedAt)
        val imageView: ImageView = view.findViewById(R.id.articleImage)

       val source = Source(args.article.source!!.id, args.article.source!!.name)


        textTitle.setText(args.article.title)
        tSource.setText(source.name)
        tDescription.setText(args.article.description)
        tPubslishedAt.setText(Utils.DateFormat(args.article.publishedAt))

        Glide.with(requireActivity()).load(args.article.urlToImage).into(imageView)



        // all the news are saved in the list
        viewModel.getSavedNews.observe(viewLifecycleOwner, Observer {


            for (i in it){


                if (args.article.title == i.title){

                    stringCheck = i.title


                }


            }



        })


        fab.setOnClickListener {



            if (args.article.title == stringCheck ){


                Log.e("fragArg", "exists")

               // Toast.makeText(context, "Article exists in saved list", Toast.LENGTH_SHORT).show()

            } else {


                viewModel.insertArticle(SavedArticle(0, args.article.description!!,
                    args.article.publishedAt!!, source,
                    args.article.title!!, args.article.url!!,
                    args.article.urlToImage!!))



                Log.e("fragArg", "saved")
               // Toast.makeText(context, "SAVED SUCCESSFULLY", Toast.LENGTH_SHORT).show()
                view.findNavController().navigate(R.id.action_fragmentArticle_to_fragmentSavedNews)


            }




        }


    }


}