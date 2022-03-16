package exp.bookorganizer.custom

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import exp.bookorganizer.Book
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessResourceFailureException

@DgsComponent
class CustomBookFetcher (private val repository: CustomBookRepository) {
    val logger: Logger = LoggerFactory.getLogger("CustomBookFetcher")

    @DgsQuery
    suspend fun booksByFields(
        @InputArgument title: String?,
        @InputArgument author: String?,
        @InputArgument genre: String?,
        @InputArgument subgenre: String?,
        @InputArgument collection: String?,
        @InputArgument location: String?
    ): List<Book> {
        // In practice, we might find observability logic here,
        // as well as logic to handle errors communicating with the database via the repository.
        try {
            return repository.findByFields(
                title, author, genre, subgenre, collection, location
            )
        } catch (e: DataAccessResourceFailureException) {
            logger.error("Error accessing the database during `booksByFields` query")
            throw e
        }
    }
}