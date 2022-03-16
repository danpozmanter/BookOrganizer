package exp.bookorganizer

import com.netflix.graphql.dgs.reactive.internal.DefaultDgsReactiveQueryExecutor
import exp.bookorganizer.crud.CrudBookRepository
import exp.bookorganizer.custom.CustomBookRepository
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
class DataFetcherTests {

	@Autowired
	lateinit var dgsQueryExecutor: DefaultDgsReactiveQueryExecutor

	@MockBean
	lateinit var byFieldsRepo: CustomBookRepository

	@MockBean
	lateinit var byFieldRepo: CrudBookRepository

	@Test
	fun `List books by author and genre`(): Unit = runBlocking {
		// Given the following books, check that the fetcher behaves as expected.
		// In practice if mocking, I would likely go deeper than the repository method, as that
		// contains logic one would want to test.
		Mockito.`when`(byFieldsRepo.findByFields(author = "Olaf", genre = "Sci-Fi")).thenReturn(
			listOf<Book>(
			Book(0, "Cats", "Olaf", "Sci-Fi", null, "Personal", "Office"),
			Book(1, "Summer", "Olaf", "Fantasy", null, "Personal", "Office"),
		))
		val books = dgsQueryExecutor.executeAndExtractJsonPathAsObject("""
		{
			booksByFields(author: "Olaf", genre: "Sci-Fi") {
				title
				author
				genre
			}
		}
        """, "data.booksByFields", Array<Book>::class.java).block()
		assertThat(books).isNotNull
		assertThat(books?.size).isGreaterThanOrEqualTo(2)
		assertThat(books?.get(0)?.title ?: "").isEqualTo("Cats")
		assertThat(books?.get(1)?.title ?: "").isEqualTo("Summer")
	}

	@Test
	fun `List books by title`(): Unit = runBlocking {
		Mockito.`when`(byFieldRepo.findByTitle("Cats")).thenReturn(listOf<Book>(
			Book(1, "Dogs", "Olaf", "Sci-Fi", null, "Personal", "Office")
		))
		val book = dgsQueryExecutor.executeAndExtractJsonPathAsObject("""
			{
				booksByTitle(title: "Cats") {
					title
					author
					genre
				}
			}
			""", "data.booksByTitle[0]", Book::class.java).block()!!
		assertThat(book.author).isEqualTo("Olaf")
	}
}