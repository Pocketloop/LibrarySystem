package ch.makery.address.view  

import ch.makery.address.model.Book

import ch.makery.address.MainApp 

import scalafx.scene.control.{TextField, TableColumn, Label, Alert} 

import scalafxml.core.macros.sfxml 

import scalafx.stage.Stage 

import scalafx.Includes._ 

import ch.makery.address.util.DateUtil._ 

import scalafx.event.ActionEvent 


@sfxml 
class PersonEditDialogController ( 

    private val    bookNameField : TextField,
    private val     authorField  : TextField,
    private val        ISBNField : TextField,
    private val     editionField : TextField,
    private val   publisherField : TextField,
    private val  lastBorrowedField : TextField

){ 

  var         dialogStage : Stage  = null 

  private var _book     : Book = null

  var         okClicked            = false 

 

  def book = _book

  def book_=(x : Book) {

        _book = x

        bookNameField.text = _book.bookName.value

        authorField.text  = _book.author.value

        ISBNField.text    = _book.ISBN.value

        editionField.text= _book.edition.value.toString

        publisherField.text      = _book.publisher.value

        lastBorrowedField.text  = _book.date.value.asString

        lastBorrowedField.setPromptText("dd.mm.yyyy");

  } 

 

  def handleOk(action :ActionEvent){ 

 

     if (isInputValid()) { 

        _book.bookName <== bookNameField.text

        _book.author  <== authorField.text

        _book.ISBN    <== ISBNField.text

        _book.publisher      <== publisherField.text

        _book.edition.value = editionField.getText().toInt

        _book.date.value       = lastBorrowedField.text.value.parseLocalDate;

 

        okClicked = true; 

        dialogStage.close() 

    } 

  } 

 

  def handleCancel(action :ActionEvent) { 

        dialogStage.close(); 

  } 

  def nullChecking (x : String) = x == null || x.length == 0 

 

  def isInputValid() : Boolean = { 

    var errorMessage = "" 

 

    if (nullChecking(bookNameField.text.value))

      errorMessage += "No valid book name!\n"

    if (nullChecking(authorField.text.value))

      errorMessage += "No valid author name!\n"

    if (nullChecking(ISBNField.text.value)) 

      errorMessage += "No valid ISBN!\n"

    if (nullChecking(editionField.text.value))

      errorMessage += "No valid edition!\n"

    else { 

      try { 

        Integer.parseInt(editionField.getText());

      } catch { 

          case e : NumberFormatException => 

            errorMessage += "No valid edition (must be an integer)!\n"

      } 

    } 

    if (nullChecking(publisherField.text.value))

      errorMessage += "No valid publisher!\n"

    if (nullChecking(lastBorrowedField.text.value))

      errorMessage += "No valid last borrowed!\n"

    else { 

      if (!lastBorrowedField.text.value.isValid) {

          errorMessage += "No valid last borrowed. Use the format dd.mm.yyyy!\n";

      } 

    } 

 

    if (errorMessage.length() == 0) { 

        return true; 

    } else { 

        // Show the error message. 

        val alert = new Alert(Alert.AlertType.Error){ 

          initOwner(dialogStage) 

          title = "Invalid Fields" 

          headerText = "Please correct invalid fields" 

          contentText = errorMessage 

        }.showAndWait() 

 

        return false; 

    } 

   } 

}  