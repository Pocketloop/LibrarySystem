package ch.makery.address.model

import scalafx.beans.property.{StringProperty, IntegerProperty, ObjectProperty}
import java.time.LocalDate
import ch.makery.address.util.Database
import ch.makery.address.util.DateUtil._
import scalikejdbc._
import scala.util.{ Try, Success, Failure }

class Book (val bookNameS : String, val authorS : String) extends Database {
	def this()     = this(null, null)
	var bookName  = new StringProperty(bookNameS)
	var author   = new StringProperty(authorS)
	var ISBN     = new StringProperty("ABC123")
	var edition = IntegerProperty(1234)
	var publisher = new StringProperty("some publisher")
	var date       = ObjectProperty[LocalDate](LocalDate.of(1999, 2, 21))


	def save() : Try[Int] = {
		if (!(isExist)) {
			Try(DB autoCommit { implicit session =>
				sql"""
					insert into person (firstName, lastName,
						street, postalCode, city, date) values
						(${bookName.value}, ${author.value}, ${ISBN.value},
							${edition.value},${publisher.value}, ${date.value.asString})
				""".update.apply()
			})
		} else {
			Try(DB autoCommit { implicit session =>
				sql"""
				update person
				set
				firstName  = ${bookName.value} ,
				lastName   = ${author.value},
				street     = ${ISBN.value},
				postalCode = ${edition.value},
				city       = ${publisher.value},
				date       = ${date.value.asString}
				 where firstName = ${bookName.value} and
				 lastName = ${author.value}
				""".update.apply()
			})
		}

	}
	def delete() : Try[Int] = {
		if (isExist) {
			Try(DB autoCommit { implicit session =>
			sql"""
				delete from person where
					firstName = ${bookName.value} and lastName = ${author.value}
				""".update.apply()
			})
		} else
			throw new Exception("Person not Exists in Database")
	}
	def isExist : Boolean =  {
		DB readOnly { implicit session =>
			sql"""
				select * from person where
				firstName = ${bookName.value} and lastName = ${author.value}
			""".map(rs => rs.string("firstName")).single.apply()
		} match {
			case Some(x) => true
			case None => false
		}

	}
}
object Book extends Database{
	def apply (
							bookNameS : String,
							authorS : String,
							ISBNS : String,
							editionI : Int,
							publisherS : String,
							dateS : String
		) : Book = {

		new Book(bookNameS, authorS) {
			ISBN.value     = ISBNS
			edition.value = editionI
			publisher.value    = publisherS
			date.value       = dateS.parseLocalDate
		}
		
	}
	def initializeTable() = {
		DB autoCommit { implicit session => 
			sql"""
			create table person (
			  id int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), 
			  firstName varchar(64), 
			  lastName varchar(64), 
			  street varchar(200),
			  postalCode int,
			  city varchar(100),
			  date varchar(64)
			)
			""".execute.apply()
		}
	}
	
	def getAllPersons : List[Book] = {
		DB readOnly { implicit session =>
			sql"select * from person".map(rs => Book(rs.string("firstName"),
				rs.string("lastName"),rs.string("street"), 
				rs.int("postalCode"),rs.string("city"), rs.string("date") )).list.apply()
		}
	}
}
