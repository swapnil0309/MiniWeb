package com.example.miniweb

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    var searchView: SearchView?=null
    var webview:WebView?=null
    var progressBar:ProgressBar?=null
    var inputMethodManager:InputMethodManager?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        searchView=findViewById(R.id.search_view)
        webview=findViewById(R.id.webView)
        progressBar=findViewById(R.id.progress_bar)
        inputMethodManager=getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager


        webview!!.settings.javaScriptEnabled=true
        webview!!.webViewClient=object:WebViewClient(){
            override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    url:String
            ): Boolean {
                    view!!.loadUrl(url)
                    return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (!(progressBar!!.isShown))
                {
                    progressBar?.visibility=View.VISIBLE
                    view!!.visibility=View.GONE
                }
                searchView!!.onActionViewExpanded()
                searchView!!.setQuery(webview!!.url,false)
                searchView!!.clearFocus()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (progressBar!!.isShown)
                {
                    progressBar?.visibility=View.GONE
                    view!!.visibility=View.VISIBLE
                }
                backward.isEnabled=true
                backward.setImageResource(R.drawable.undo_orange)
                if (!(webview!!.canGoForward()))
                {
                    forward.isEnabled=false
                    forward.setImageResource(R.drawable.redo_blue)
                }
                home.isEnabled=true
                home.setImageResource(R.drawable.home_orange)
            }
        }
        searchView?.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                try
                {
                    val bool:Boolean=URLUtil.isValidUrl(query)

                    if (bool)
                    {
                        webview!!.loadUrl(query)
                    }
                    else
                    {
                        webview!!.loadUrl("https://www.google.com/search?q="+query.replace("",""))
                    }
                    mainScreen!!.visibility=View.GONE
                    backward.isEnabled=true
                    backward.setImageResource(R.drawable.undo_orange)
                    home.isEnabled=true
                    home.setImageResource(R.drawable.home_orange)
                    inputMethodManager!!.hideSoftInputFromWindow(currentFocus?.windowToken,0)

                }
                catch (e:Exception)
                {
                    Toast.makeText(this@MainActivity,""+e.printStackTrace(),Toast.LENGTH_SHORT).show()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }
    
    fun imageClicked(view: View) {
    var id=view.id

        var ourID:String?=""

        if (isInternetConnected())
        {
            ourID=view.resources.getResourceEntryName(id)
            webview?.loadUrl("https://www."+ourID+".com")
            webview!!.visibility=View.VISIBLE
            mainScreen!!.visibility=View.GONE
            searchView!!.onActionViewExpanded()
            searchView!!.setQuery(webview!!.url,false)
            searchView!!.clearFocus()
            backward.isEnabled=true
            backward.setImageResource(R.drawable.undo_orange)
            home.isEnabled=true
            home.setImageResource(R.drawable.home_orange)
            forward.isEnabled=true
            forward.setImageResource(R.drawable.redo_blue)


        }
        else
        {
            Toast.makeText(
                    this,
                    "Oops!! There is no internet connection.Please ensure you are connected",
                    Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    fun iconClicked(view: View){

        when(view.id){
            R.id.home->{
                homeDisplay()
            }
            R.id.backward->{
                backwardDisplay()
            }
            R.id.forward->{
                forwardDisplay()
            }
        }
    }
    fun forwardDisplay(){
        if (webview!!.canGoForward())
        {
            webview!!.goForward()
            backward.isEnabled=true
            backward.setImageResource(R.drawable.undo_orange)
            home.isEnabled=true
            home.setImageResource(R.drawable.home_orange)
            searchView!!.onActionViewExpanded()
            searchView!!.setQuery(webview!!.url,false)
            searchView!!.clearFocus()
            inputMethodManager!!.hideSoftInputFromWindow(currentFocus?.windowToken,0)
            if (!(webview!!.isShown))
            {
                webview!!.visibility=View.VISIBLE
                mainScreen!!.visibility=View.GONE
            }

        }
        home.isEnabled=true
        home.setImageResource(R.drawable.home_orange)
    }
    fun homeDisplay(){
        if(webview?.isShown as Boolean)
        {
            webview?.visibility=View.GONE
            mainScreen?.visibility=View.VISIBLE
        }
        backward?.isEnabled=false
        forward?.isEnabled=false
        home?.isEnabled=false
        home.setImageResource(R.drawable.gome_blue)
        backward.setImageResource(R.drawable.undo_blue)
        forward.setImageResource(R.drawable.redo_blue)
        searchView!!.onActionViewExpanded()
        searchView!!.setQuery("",false)
        searchView!!.clearFocus()
        inputMethodManager!!.hideSoftInputFromWindow(currentFocus?.windowToken,0)


    }
    fun backwardDisplay()
    {
     if(webview!!.canGoBack())
     {
         webview!!.goBack()
         if(webview!!.canGoForward())
         {
             forward?.isEnabled=true
             forward.setImageResource(R.drawable.redo_orange)
         }
         if(!(webview!!.isShown))
         {
             webview!!.visibility=View.VISIBLE
             mainScreen!!.visibility=View.GONE
         }
     }
     else if (webview!!.isShown){
         webview!!.visibility=View.GONE
         mainScreen!!.visibility=View.VISIBLE
         backward.isEnabled=false
         backward.setImageResource(R.drawable.undo_blue)
         if (webview!!.canGoForward())
         {
             forward.isEnabled=true
             forward.setImageResource(R.drawable.redo_orange)
         }



     }
    }
    fun isInternetConnected():Boolean{
        val connectivityManager:ConnectivityManager=getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkinfo=connectivityManager.activeNetworkInfo
        if(networkinfo!=null && networkinfo!!.isConnected)
            return true
        else
            return false

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK && webview!!.canGoBack())
        {
            webview!!.goBack()
            return true
        } else if (!(mainScreen.isShown))
        {
            mainScreen.visibility=View.VISIBLE
            webView!!.visibility=View.GONE
            searchView!!.setQuery("",false)
            searchView!!.clearFocus()
            backward.isEnabled=false
            backward.setImageResource(R.drawable.undo_blue)
            if (webview!!.canGoForward())
            {
                forward.isEnabled=true
                forward.setImageResource(R.drawable.redo_orange)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}

private infix fun WebViewClient.WebViewClient(unit: Unit) {

}
