package controllers

import de.htwg.se.pokelite.PokemonLite
import de.htwg.se.pokelite.model.GameInterface

import javax.inject._
import play.api._
import play.api.mvc._
import play.twirl.api.Html

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton //maybe as AbstractController(controllerComponents)
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

    val gameController = PokemonLite.controller;
    val tui = PokemonLite.tui


    def rules = Action {
        Ok(views.html.rules())
    }

    def playerNames = Action {
        Ok(views.html.playerNames())
    }

    def addPlayers = Action { implicit request =>
        val player1Name = request.body.asFormUrlEncoded.get("player1Name").mkString
        val player2Name = request.body.asFormUrlEncoded.get("player2Name").mkString
        val game = gameController.game

        /*game.state.initPlayers()
        println("0 state " + game.state)
        game.state.addPlayer(player1Name)
        game.state.addPlayer(player2Name)*/

        gameController.initPlayers()
        gameController.addPlayer(player1Name)
        gameController.addPlayer(player2Name)


        println("1 state " + gameController.game.state)


        val gameString = gameController.game.toString;
        val gameField = views.html.game(gameString)

        Redirect(routes.HomeController.game).flashing(
            "player1Name" -> player1Name,
            "player2Name" -> player2Name,
            "gameState" -> gameController.game.state.toString()
        )

        //Ok(gameField)

      /* wui needed
      def setName(name: String) = Action {
          controller.create_player(name)
          if (!wui.getState().isInstanceOf[..]) {
            if(wui.getState().isInstanceOf[..]) {
              Redirect(" ")
            } else {
              Redirect(" ")
            }
          } else {
            Ok( )
          }
        }
       */
    }

    def game = Action { implicit request =>
        val command: Option[String] = request.getQueryString("command")

        println("command: " + command)
        println("2 state " + gameController.game.state)

        /*command match {
            case Some(name) => Ok(s"Hallo $name")
            case None => BadRequest("Name nicht gefunden")
        }*/

        tui.processInputLine(command.getOrElse(""))
        val gameString = gameController.game.toString;
        val gameField = views.html.game(gameString)

        Ok(gameField)
    }


    /**
     * Create an Action to render an HTML page.
     *
     * The configuration in the `routes` file means that this method
     * will be called when the application receives a `GET` request with
     * a path of `/`.
     */

    def index = Action { implicit request: Request[AnyContent] =>
        Ok(views.html.index())
    }

}
