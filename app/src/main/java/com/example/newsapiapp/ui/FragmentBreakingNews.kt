@file:Suppress("DEPRECATION")

package com.example.newsapiapp.ui

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapiapp.R
import com.example.newsapiapp.adapters.ArticleAdapter
import com.example.newsapiapp.adapters.ItemClicklistner
import com.example.newsapiapp.db.Article
import com.example.newsapiapp.mvvm.NewsDatabase
import com.example.newsapiapp.mvvm.NewsRepo
import com.example.newsapiapp.mvvm.NewsViewModel
import com.example.newsapiapp.mvvm.NewsViewModelFac
import com.example.newsapiapp.wrapper.Resource
import de.hdodenhof.circleimageview.CircleImageView

class FragmentBreakingNews : Fragment(), ItemClicklistner, MenuProvider{


    lateinit var viewModel : NewsViewModel
    lateinit  var newsAdapter : ArticleAdapter
    lateinit var rv : RecyclerView
    lateinit var pb : ProgressBar
    var isClicked: Boolean = false
    var isOpened : Boolean = false
    var addingResponselist = arrayListOf<Article>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_breaking_news, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (activity as AppCompatActivity).supportActionBar?.setTitle("Breaking News")


        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.CREATED)

        setHasOptionsMenu(true)



        val sportCat: CircleImageView = view.findViewById(R.id.sportsImage)
        val techCat: CircleImageView = view.findViewById(R.id.techImage)
        val breakingImage: CircleImageView = view.findViewById(R.id.breakingImage)
        val businessCat: CircleImageView = view.findViewById(R.id.businessImage)

        val noWifi : ImageView = view.findViewById(R.id.noWifi)
        val noWifiText : TextView = view.findViewById(R.id.noWifiText)



        val dao = NewsDatabase.getInstance(requireActivity()).newsDao
        val repository = NewsRepo(dao)
        val factory = NewsViewModelFac(repository, requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]


        rv = view.findViewById(R.id.rvBreakingNews)
        pb = view.findViewById(R.id.paginationProgressBar)


        val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nInfo = cm.activeNetworkInfo
        if (nInfo !=null && nInfo.isConnected){

            setUpRecyclerView()
            isClicked = true
            loadBreakingNews()



        } else {

            // IF THERE IS NO INTERNET THEN DISPLAY THIS
            noWifi.visibility = View.VISIBLE
            noWifiText.visibility = View.VISIBLE
        }


        // we only want this to work if we click on category
        // otherwise we want to load only breaking news

        val catlistener = View.OnClickListener {


            when (it.id){

                R.id.sportsImage->{

                    (activity as AppCompatActivity).supportActionBar?.setTitle("Sports")


                    isClicked = true
                    viewModel.getCategory("sports")
                    loadCategoryNews()
                    setUpRecyclerView()


                }



                R.id.techImage->{


                    (activity as AppCompatActivity).supportActionBar?.setTitle("Tech")

                    isClicked = true

                    viewModel.getCategory("tech")
                    loadCategoryNews()
                    setUpRecyclerView()


                }


                R.id.breakingImage->{

                    (activity as AppCompatActivity).supportActionBar?.setTitle("Breaking News")

                    isClicked = true


                    loadBreakingNews()


                }


                R.id.businessImage->{


                    (activity as AppCompatActivity).supportActionBar?.setTitle("Business")

                    isClicked = true

                    viewModel.getCategory("business")
                    loadCategoryNews()
                    setUpRecyclerView()


                }


            }


        }

        techCat.setOnClickListener(catlistener)
        breakingImage.setOnClickListener(catlistener)
        businessCat.setOnClickListener(catlistener)
        sportCat.setOnClickListener(catlistener)




    }


    private fun loadCategoryNews() {

        viewModel.categoryNews.observe(viewLifecycleOwner, Observer {response->


            when (response){

                is Resource.Success->{
                    hideProgressBar()
                    response.data?.let{newsresponse->

                        addingResponselist = newsresponse.articles as ArrayList<Article>
                        newsAdapter.setlist(newsresponse.articles)

                    }
                }


                is Resource.Error->{
                    hideProgressBar()
                    response.message?.let{messsage->
                        Log.i("BREAKING FRAG", messsage.toString())

                    }
                }

                is Resource.Loading->{
                    showProgressBar()
                }

            }




        })
    }

    private fun loadBreakingNews() {

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer {response->


            when (response){

                is Resource.Success->{
                    hideProgressBar()
                    response.data?.let{newsresponse->

                        addingResponselist = newsresponse.articles as ArrayList<Article>
                        newsAdapter.setlist(newsresponse.articles)

                    }
                }


                is Resource.Error->{
                    hideProgressBar()
                    response.message?.let{messsage->
                        Log.i("BREAKING FRAG", messsage.toString())

                    }
                }

                is Resource.Loading->{
                    showProgressBar()
                }

            }




        })
    }


    fun showProgressBar(){

        pb.visibility = View.VISIBLE

    }


    fun hideProgressBar(){


        pb.visibility = View.INVISIBLE


    }

    private fun setUpRecyclerView() {

        newsAdapter = ArticleAdapter()
        newsAdapter.setItemClickListener(this)
        rv.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        }


    }

    override fun onItemClicked(position: Int, article: Article) {

// GOING TO ANOTHER FRAGMENT

        val action = FragmentBreakingNewsDirections.actionFragmentBreakingNewsToFragmentArticle(article)
        view?.findNavController()?.navigate(action)

        Toast.makeText(context, "check ${article.title}", Toast.LENGTH_SHORT).show()



    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

        menuInflater.inflate(R.menu.menu, menu)

        val deleteIcon = menu.findItem(R.id.deleteAll)
        deleteIcon.setVisible(false)

        val menuItem = menu.findItem(R.id.searchNews)
        val searchView = menuItem.actionView as androidx.appcompat.widget.SearchView


        searchView.setOnSearchClickListener {


            val savedIcon = menu.findItem(R.id.savedNewsFrag)
            savedIcon.setVisible(false)
            isOpened = true


        }


        searchView.queryHint = "Search News"

        searchView.setOnCloseListener(androidx.appcompat.widget.SearchView.OnCloseListener {
            isOpened

            val savedIcon = menu.findItem(R.id.savedNewsFrag)
            savedIcon.setVisible(true)
            isOpened.not()

        })


        searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {

                newFilterItems(p0)

                return true


            }

            override fun onQueryTextChange(p0: String?): Boolean {

                newFilterItems(p0)

                return true


            }


        })

        super.onCreateOptionsMenu(menu, menuInflater)


    }

    private fun newFilterItems(p0: String?) {

        var newfilteredlist = arrayListOf<Article>()

        for (i in addingResponselist) {



            if (i.title!!.contains(p0!!)) {

                newfilteredlist.add(i)
            }





        }

        setUpRecyclerView()
        newsAdapter.filteredList(newfilteredlist)




    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {


        if (menuItem.itemId == R.id.savedNewsFrag){

            view?.findNavController()?.navigate(R.id.action_fragmentBreakingNews_to_fragmentSavedNews)
        }


        return true

    }


}