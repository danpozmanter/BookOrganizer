package exp.bookorganizer

data class BookInput (
    val title: String,
    val author: String,
    val genre: String?,
    val subgenre: String?,
    val collection: String?,
    val location: String?
) {
    fun toBook(): Book {
        return Book(
            id=null,
            title=title,
            author=author,
            genre=genre,
            subgenre=subgenre,
            collection=collection,
            location=location
        )
    }
}