package exp.bookorganizer

import org.springframework.data.annotation.Id;

data class Book (
    @Id val id: Long?,
    val title: String?,
    val author: String?,
    val genre: String?,
    val subgenre: String?,
    val collection: String?,
    val location: String?
)