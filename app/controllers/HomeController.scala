package controllers

import de.htwg.se.pokelite.PokemonLite
import de.htwg.se.pokelite.model.GameInterface

import javax.inject._
import play.api._
import play.api.mvc._
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  private val gameController: GameInterface = PokemonLite.controller.game

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(gameController.toString)
  }
}
