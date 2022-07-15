package ch.makery.address.view
import ch.makery.address.model.Book
import ch.makery.address.MainApp
import scalafx.scene.control.{TableView, TableColumn, Label, Alert}
import scalafxml.core.macros.sfxml
import scalafx.beans.property.{StringProperty} 
import ch.makery.address.util.DateUtil._ 
import scalafx.Includes._ 
import scalafx.event.ActionEvent 
import scalafx.scene.control.Alert.AlertType 
import scala.util.{Failure, Success}

@sfxml
class BookOverviewController(
  
    private val bookTable : TableView[Book],
    private val bookNameColumn : TableColumn[Book, String],
    private val authorColumn : TableColumn[Book, String],

    private val bookNameLabel : Label,
    private val authorLabel : Label,
    private val ISBNLabel : Label,
    private val publisherLabel : Label,
    private val editionLabel :  Label,
    private val lastBorrowedLabel : Label
    
    ) {

  private def showBookDetails (book : Option[Book]) = {

    book match {

      case Some(book) =>

      // Fill the labels with info from the book object.
      bookNameLabel.text <== book.bookName
      authorLabel.text  <== book.author
      ISBNLabel.text    <== book.ISBN
      editionLabel.text      <== book.publisher;
      publisherLabel.text = book.edition.value.toString
      lastBorrowedLabel.text   = book.date.value.asString

      case None => 
        // Person is null, remove all the text. 
      bookNameLabel.text = ""
        authorLabel.text  = ""
      ISBNLabel.text    = ""
      publisherLabel.text= ""
      editionLabel.text      = ""
      lastBorrowedLabel.text  = ""
    }     
  } 
  // initialize Table View display contents model
  bookTable.items = MainApp.bookData
  // initialize columns's cell values
  bookNameColumn.cellValueFactory = {_.value.bookName}
  authorColumn.cellValueFactory  = {_.value.author}

  showBookDetails(None);

  bookTable.selectionModel().selectedItem.onChange(
      (_, _, newValue) => showBookDetails(Some(newValue))
  )

  /**
 * Called when the user clicks on the delete button.
 */

  def handleDeleteBook(action : ActionEvent) = {
      val selectedIndex = bookTable.selectionModel().selectedIndex.value
      val selecteBook = bookTable.selectionModel().selectedItem.value
      if (selectedIndex >= 0) {
        selecteBook.delete() match {
          case Success(x) =>
            bookTable.items().remove(selectedIndex);
          case Failure(e) =>
            val alert = new Alert(Alert.AlertType.Warning) {
              initOwner(MainApp.stage)
              title = "Failed to Save"
              headerText = "Database Error"
              contentText = "Database problem filed to save changes"
            }.showAndWait()
        }
      } else {
        // Nothing selected.
        val alert = new Alert(AlertType.Warning){
          initOwner(MainApp.stage)
          title       = "No Selection"
          headerText  = "No Book Selected"
          contentText = "Please select a book in the table."
        }.showAndWait()
      }
    }

  def handleNewBook(action : ActionEvent) = {
    val book = new Book ("","")
    val okClicked = MainApp.showPersonEditDialog(book);
        if (okClicked) {
          book.save() match {
            case Success(x) =>
              MainApp.bookData += book
            case Failure(e) =>
              val alert = new Alert(Alert.AlertType.Warning) {
                initOwner(MainApp.stage)
                title = "Failed to Save"
                headerText = "Database Error"
                contentText = "Database problem filed to save changes"
              }.showAndWait()
          }
        }
  }


  def handleEditBook(action : ActionEvent) = {
    val selectedBook = bookTable.selectionModel().selectedItem.value
    if (selectedBook != null) {
        val okClicked = MainApp.showPersonEditDialog(selectedBook)


        if (okClicked) {
          selectedBook.save() match {
            case Success(x) =>
              showBookDetails(Some(selectedBook))
            case Failure(e) =>
              val alert = new Alert(Alert.AlertType.Warning) {
                initOwner(MainApp.stage)
                title = "Failed to Save"
                headerText = "Database Error"
                contentText = "Database problem filed to save changes"
              }.showAndWait()
          }
        }


    } else {
        // Nothing selected.
        val alert = new Alert(Alert.AlertType.Warning){
          initOwner(MainApp.stage)
          title       = "No Selection"
          headerText  = "No Book Selected"
          contentText = "Please select a book in the table."
        }.showAndWait()
    }
  }
 
  
}
