package exp.bookorganizer.crud

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import org.springframework.dao.DataAccessResourceFailureException
import exp.bookorganizer.Book
import exp.bookorganizer.BookInput
import graphql.schema.DataFetchingEnvironment
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@DgsComponent
class CrudBookMutation (private val repository: CrudBookRepository) {
    val logger: Logger = LoggerFactory.getLogger("CrudBookMutation")

    @DgsMutation
    suspend fun addBook(dataFetchingEnvironment: DataFetchingEnvironment, @InputArgument input: BookInput): Book {
        // For convenience, I am reusing the CrudBookRepository and the Book data class.
        // However, in practice I might want to use an additional repository a separate data class for inserts,
        // To allow a stricter Book definition with a non-nullable ID for use within the application generally.
        try {
            return repository.save(input.toBook())
        } catch (e: DataAccessResourceFailureException) {
            // Catch and rethrow
            logger.error("Error accessing the database during `addBook`")
            throw e
        }
    }
}