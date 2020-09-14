package com.khs.retrofit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Response
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val observer1:Observer<Response<Albums>> = Observer<Response<Albums>> {
        val albumsList = it.body()?.listIterator()
        if(albumsList!=null){
            while(albumsList.hasNext()){
                val albumsItem  = albumsList.next()
                tv_album.append(albumsItem.toString()+"\n\n")
                Timber.d(albumsItem.toString())
            }
        }
    }

    private val observer2:Observer<Response<AlbumsItem>> = Observer<Response<AlbumsItem>> {
        val item  = it.body()
        item?.let {
            tv_album.append(item.toString()+"\n\n")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.plant(Timber.DebugTree())
        val retService = RetrofitInstance.getInstance().create(AlbumService::class.java)
        // getObserver1(retService)
        getObserver2(retService)
    }

    private fun getObserver2(retService: AlbumService) {
        val responseLiveData2: LiveData<Response<AlbumsItem>> = liveData {
            val response = retService.getAlbum(3)
            // val response = retService.getSortedAlbums(3)
            emit(response)
        }
        responseLiveData2.observe(this,observer2)
    }

    private fun getObserver1(retService: AlbumService) {
        val responseLiveData1: LiveData<Response<Albums>> = liveData {
            val response = retService.getAlbums()
            // val response = retService.getSortedAlbums(3)
            emit(response)
        }
        responseLiveData1.observe(this,observer1)
    }
}