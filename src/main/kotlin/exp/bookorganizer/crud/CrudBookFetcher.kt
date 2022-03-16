package exp.bookorganizer.crud

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.dao.DataAccessResourceFailureException
import exp.bookorganizer.Book
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@DgsComponent
class CrudBookFetcher (private val repository: CrudBookRepository) {
    val logger: Logger = LoggerFactory.getLogger("CrudBookFetcher")

    @DgsQuery
    suspend fun booksByTitle(@InputArgument title: String): List<Book> {
        try {
            return repository.findByTitle(title)
        } catch (e: DataAccessResourceFailureException) {
            logger.error("Error accessing the database during `booksByTitle` query")
            // In practice one would likely do more than rethrow the same exception,
            // for example a custom exception with more detailed fields.
            throw e
        }
        // One would also catch additional errors.
    }

    @DgsQuery
    suspend fun booksByAuthor(@InputArgument author: String): List<Book> {
        try {
            return repository.findByAuthor(author)
        } catch (e: DataAccessResourceFailureException) {
            logger.error("Error accessing the database during `booksByAuthor` query")
            throw e
        }
    }

    @DgsQuery
    suspend fun booksByGenre(@InputArgument genre: String): List<Book> {
        try {
            return repository.findByGenre(genre)
        } catch (e: DataAccessResourceFailureException) {
            logger.error("Error accessing the database during `booksByGenre` query")
            throw e
        }
    }
}