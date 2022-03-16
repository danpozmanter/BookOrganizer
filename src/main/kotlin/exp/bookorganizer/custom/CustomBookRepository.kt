package exp.bookorganizer.custom

import com.netflix.graphql.dgs.exceptions.DgsBadRequestException
import exp.bookorganizer.Book
import io.r2dbc.spi.Row
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import java.rmi.UnexpectedException
import java.util.function.BiFunction

@Component
class BookMapper: BiFunction<Row, Any, Book> {
    override fun apply(row: Row, o: Any): Book {
        // Basic mapping from db rows to the target object (in this case a Book instance).
        return Book(
            row.get("id", Number::class.java)?.toLong() ?: throw UnexpectedException("Error: id"),
            row.get("title", String::class.java),
            row.get("author", String::class.java),
            row.get("genre", String::class.java),
            row.get("subgenre", String::class.java),
            row.get("collection", String::class.java),
            row.get("location", String::class.java),
        )
    }
}

@Component
class CustomBookRepository(private val client: DatabaseClient, private val mapper: BookMapper) {

    suspend fun findByFields(
        title: String? = null,
        author: String? = null,
        genre: String? = null,
        subgenre: String? = null,
        collection: String? = null,
        location: String? = null
    ): List<Book> {
        // Quick check at least one field has been specified.
        // One could leave this open and return *all* books, or have that be a separate query
        // to ensure it is being done on purpose.
        if(
            (title == null) && (author == null) && (genre == null) && (subgenre == null)
            && (collection == null) && (location == null)
        ) { throw DgsBadRequestException("findByFields: No field provided for query") }
        // Dynamically construct a query based on any combination of provided fields:
        var fieldSql = ""
        val fieldMapping = mutableMapOf(
            "title" to title,
            "author" to author,
            "genre" to genre,
            "subgenre" to subgenre,
            "collection" to collection,
            "location" to location
        )
        for ((fieldName, value) in fieldMapping) {
            if ((value != null) && value.isNotEmpty()) {
                if (fieldSql.isNotEmpty()) {
                    fieldSql += "AND "
                }
                fieldSql += " $fieldName = :$fieldName"
            } else { fieldMapping.remove(fieldName) }
        }
        var spec = client.sql("SELECT * FROM book WHERE$fieldSql")
        for ((fieldName, value) in fieldMapping) {
            spec = spec.bind(fieldName, value!!) // At this point any `value` that is null has been removed.
        }
        return spec.map(mapper::apply).all().collectList().awaitSingle()
    }
}