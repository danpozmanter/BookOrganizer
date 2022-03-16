package exp.bookorganizer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BookOrganizerApplication

fun main(args: Array<String>) {
	runApplication<BookOrganizerApplication>(*args)
}
