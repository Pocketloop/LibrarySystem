package ch.makery.address 
import scalafx.application.JFXApp 
import scalafx.application.JFXApp.PrimaryStage 
import scalafx.scene.Scene 
import scalafx.Includes._ 
import scalafxml.core.{NoDependencyResolver, FXMLView, FXMLLoader} 
import javafx.{scene => jfxs} 
import scalafx.collections.ObservableBuffer
import ch.makery.address.model.Book
import ch.makery.address.view.PersonEditDialogController 
import scalafx.stage.{ Stage, Modality } 
import scalafx.scene.image.Image
import ch.makery.address.util.Database

object MainApp extends JFXApp { 
  //initialize database
  Database.setupDB()

  /**
    * The data as an observable list of Persons.
    */
  val bookData = new ObservableBuffer[Book]()

  /**
    * Constructor
    */

  //assign all person into personData array
  bookData ++= Book.getAllPersons

  // transform path of RootLayout.fxml to URI for resource location. 

  val rootResource = getClass.getResource("view/RootLayout.fxml") 

  // initialize the loader object. 

  val loader = new FXMLLoader(rootResource, NoDependencyResolver) 

  // Load root layout from fxml file. 

  loader.load(); 

  // retrieve the root component BorderPane from the FXML  

  val roots = loader.getRoot[jfxs.layout.BorderPane] 

  // initialize stage 
  val cssResource = getClass.getResource("view/DarkTheme.css")
  roots.stylesheets = List(cssResource.toExternalForm)

  stage = new PrimaryStage { 

    title = "Library System"
    icons += new Image("file:resources/images/download.png")

    scene = new Scene { 

      root = roots 

    } 

  } 

  // actions for display person overview window  

  def showBookOverview() = {

    val resource = getClass.getResource("view/BookOverview.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver) 
    loader.load(); 
    val roots = loader.getRoot[jfxs.layout.AnchorPane] 
    this.roots.setCenter(roots) 

  }  

  // call to display PersonOverview when app start 

  showBookOverview()

  def showPersonEditDialog(book: Book): Boolean = {

    val resource = getClass.getResourceAsStream("view/BookEditDialog.fxml")

    val loader = new FXMLLoader(null, NoDependencyResolver)

    loader.load(resource);

    val roots2  = loader.getRoot[jfxs.Parent]

    val control = loader.getController[PersonEditDialogController#Controller]

    roots2.stylesheets = List(cssResource.toExternalForm)

    val dialog = new Stage() {

      initModality(Modality.APPLICATION_MODAL)

      initOwner(stage)

      scene = new Scene {

        root = roots2

      }

    }

    control.dialogStage = dialog

    control.book = book

    dialog.showAndWait() 

    control.okClicked 

  }  

} 