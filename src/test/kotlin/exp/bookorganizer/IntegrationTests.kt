package exp.bookorganizer

import com.netflix.graphql.dgs.reactive.internal.DefaultDgsReactiveQueryExecutor
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class IntegrationTests {

    @Autowired
    lateinit var dgsQueryExecutor: DefaultDgsReactiveQueryExecutor

    @Test
    fun `List books by title`(): Unit = runBlocking {
        val book = dgsQueryExecutor.executeAndExtractJsonPathAsObject("""
			{
				booksByTitle(title: "History of the World Part One") {
					title
					author
					genre
				}
			}
			""", "data.booksByTitle[0]", Book::class.java).block()
        Assertions.assertThat(book?.author).isEqualTo("Mel Brooks")
    }

    @Test
    fun `No fields yields exception`(): Unit = runBlocking {
        val exception = assertThrows<com.netflix.graphql.dgs.exceptions.QueryException> {
            dgsQueryExecutor.executeAndExtractJsonPathAsObject("""
                {
                    booksByFields {
                        title
                        author
                        genre
                    }
                }
                """, "data.booksByTitle[0]", Book::class.java).block()
        }
        Assertions.assertThat(exception.errors[0].message).contains("DgsBadRequestException")
    }
}