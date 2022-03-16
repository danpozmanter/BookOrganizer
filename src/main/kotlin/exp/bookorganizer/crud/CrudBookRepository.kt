package exp.bookorganizer.crud

import exp.bookorganizer.Book
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Component

@Component
interface CrudBookRepository: CoroutineCrudRepository<Book, Long> {

    @Query("SELECT * FROM book WHERE title = :title")
    suspend fun findByTitle(title: String): List<Book>

    @Query("SELECT * FROM book WHERE author = :author")
    suspend fun findByAuthor(author: String): List<Book>

    @Query("SELECT * FROM book WHERE genre = :genre")
    suspend fun findByGenre(genre: String): List<Book>
}