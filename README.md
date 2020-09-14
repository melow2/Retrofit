# Retrofit Master 
```
https://jsonplaceholder.typicode.com/
```
## Data Class
```
class Albums : ArrayList<AlbumsItem>()
```
```
data class AlbumsItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("userId")
    val userId: Int
)
```
#
## Interface With URL End Points
```
interface AlbumService{
    @GET("/albums")
    suspend fun getAlbums():Response<Albums>
}
```
#
## Retrofit Instance Class
```
class RetrofitInstance {
    companion object {
        private val BASE_URL = "https://jsonplaceholder.typicode.com/"
        fun getInstance(): Retrofit? {
            return Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
        }
    }
}
```
#
## View Data
```
class MainActivity : AppCompatActivity() {

    private val observer:Observer<Response<Albums>> = Observer<Response<Albums>> {
        val albumsList = it.body()?.listIterator()
        if(albumsList!=null){
            while(albumsList.hasNext()){
                val albumsItem  = albumsList.next()
                tv_album.append(albumsItem.toString()+"\n\n")
                Timber.d(albumsItem.toString())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.plant(Timber.DebugTree())
        val retService = RetrofitInstance.getInstance().create(AlbumService::class.java)
        val responseLiveData: LiveData<Response<Albums>> = liveData {
            val response = retService.getAlbums()
            emit(response)
        }
        responseLiveData.observe(this,observer)
    }
}
```
#
## Query Parameters
```
https://jsonplaceholder.typicode.com/albums?userId=3
```
### Clean Code
```
@GET("/albums")
suspend fun getSortedAlbums(@Query("userId") userId:Int):Response<Albums>
```
#
## Path Parameters
```
https://jsonplaceholder.typicode.com/albums/3
```
### Clean Code
```
@GET("/albums/{id}")
suspend fun getAlbum(@Path("id")albumId:Int):Response<AlbumsItem>
```
#
## Logging Interceptor
```
// https://ko.wikipedia.org/wiki/HTTP_상태_코드
class RetrofitInstance {
    companion object {
        private val BASE_URL = "https://jsonplaceholder.typicode.com/"
        val interceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
        }.build()

        fun getInstance(): Retrofit {
            return Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
        }
    }
}
```