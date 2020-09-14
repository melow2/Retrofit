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