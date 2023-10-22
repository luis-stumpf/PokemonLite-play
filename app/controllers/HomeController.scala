package controllers

import de.htwg.se.pokelite.controller.impl.Controller
import de.htwg.se.pokelite.model.states.{DesicionState, FightingState, GameOverState, InitPlayerPokemonState, InitPlayerState, InitState, SwitchPokemonState}

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

    val controller = new Controller();

    def init(): Action[AnyContent] = Action {
        controller.initPlayers()
        Redirect(routes.HomeController.field)
    }

    def initPlayers(): Action[AnyContent] = Action { request =>
        val input = request.getQueryString("input").getOrElse("")
        controller.addPlayer(input)
        Redirect(routes.HomeController.field)
    }

    def initPokemons(): Action[AnyContent] = Action { request =>
        val input = request.getQueryString("input").getOrElse("")
        controller.addPokemons(input)
        Redirect(routes.HomeController.field)
    }

    def desicion(): Action[AnyContent] = Action { request =>
        val input = request.getQueryString("input").getOrElse("")
        controller.nextMove(input)
        Redirect(routes.HomeController.field)
    }

    def fighting(): Action[AnyContent] = Action { request =>
        val input = request.getQueryString("input").getOrElse("")
        controller.attackWith(input)
        Redirect(routes.HomeController.field)
    }

    def switch(): Action[AnyContent] = Action { request =>
        val input = request.getQueryString("input").getOrElse("")
        controller.selectPokemon(input)
        Redirect(routes.HomeController.field)
    }

    def restartGame(): Action[AnyContent] = Action {
        controller.restartTheGame()
        Redirect(routes.HomeController.field)
    }

    def game(): Action[AnyContent] = Action { request =>
        val command = request.getQueryString("command")
        val input: String = command.getOrElse("");
        if (input.charAt(0) == 'y') controller.undoMove()
        else if (input.charAt(0) == 'z') controller.redoMove()
        else
            controller.game.state match {
                case InitState() => controller.initPlayers()
                case InitPlayerState() => controller.addPlayer( input )
                case InitPlayerPokemonState() => controller.addPokemons( input )
                case DesicionState() => controller.nextMove( input )
                case FightingState() => controller.attackWith( input )
                case SwitchPokemonState() => controller.selectPokemon( input )
                case GameOverState() => controller.restartTheGame()
            }
        val gameString = controller.game.toString;
        val gameField = views.html.game(gameString, controller.game.state.toString, controller.game.turn)
        Ok(gameField)
    }

    def field(): Action[AnyContent] = Action {
        val gameString = controller.game.toString;
        val gameField = views.html.field(gameString, controller.game.state.toString, controller.game.turn)
        Ok(gameField)
    }


    def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
        Ok(views.html.index())
    }

}
