package controllers

import de.htwg.se.pokelite.PokemonLite

import javax.inject._
import play.api._
import play.api.mvc._
import play.twirl.api.Html

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

    val gameController = PokemonLite.controller;
    val tui = PokemonLite.tui


    /**
     * Create an Action to render an HTML page.
     *
     * The configuration in the `routes` file means that this method
     * will be called when the application receives a `GET` request with
     * a path of `/`.
     */

    def game() = Action { request =>
        val command = request.getQueryString("command")
        command match {
            case Some(name) => Ok(s"Hallo $name")
            case None => BadRequest("Name nicht gefunden")
        }
        tui.processInputLine(command.getOrElse(""))
        val gameString = gameController.game.toString;
        val gameField = views.html.game(gameString)
        Ok(gameField)
    }

    def index() = Action { implicit request: Request[AnyContent] =>
        Ok(views.html.index())
    }

}
