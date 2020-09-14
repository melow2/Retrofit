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
#
## Connect Time Out
```
class RetrofitInstance {
    companion object {
        private val BASE_URL = "https://jsonplaceholder.typicode.com/"
        val interceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        // default = 10seconds
        val client = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
                .connectTimeout(30,TimeUnit.SECONDS) // 30초후 시도를 멈춤.
                .readTimeout(20,TimeUnit.SECONDS)    
                .writeTimeout(25,TimeUnit.SECONDS)

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
#
## GET vs POST
GET은 서버로부터 데이터를 요청하는 데만 사용된다. GET은 browser의 history에 남기 때문에 안전하지 않고,
POST는 요청에 따라 request에 저장되기때문에 안전하다. 따라서 POST는 서버로 데이터를 전송하는 데 사용된다.
### POST Request
```
@POST("/albums")
suspend fun uploadAlbum(@Body album:AlbumsItem):Response<AlbumsItem>

val retService = RetrofitInstance.getInstance().create(AlbumService::class.java)
private fun uploadAlbum(retService: AlbumService) {
    val album = AlbumsItem(101,"Test Title",3)
    val postResponse:LiveData<Response<AlbumsItem>> = liveData {
        val response = retService.uploadAlbum(album)
        emit(response)
    }
    postResponse.observe(this, Observer {
        val receivedAlbumsItem = it.body()
        tv_album.append(receivedAlbumsItem.toString()+"\n\n")
    })
}
```