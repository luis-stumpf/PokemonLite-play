package controllers

import de.htwg.se.pokelite.controller.impl.Controller
import de.htwg.se.pokelite.model.impl.pokePlayer.PokePlayer
import de.htwg.se.pokelite.model.{PokePack, Pokemon, PokemonType}
import de.htwg.se.pokelite.model.PokemonType.{Glurak, Simsala}
import de.htwg.se.pokelite.model.states.{DesicionState, FightingState, GameOverState, InitPlayerPokemonState, InitPlayerState, InitState, SwitchPokemonState}

import javax.inject._
import play.api.mvc._
import play.api.libs.json._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

    val controller: Controller = Controller()

    def decision(): Action[AnyContent] = Action { request =>
        val move  = (request.body.asJson.get \ "move").validate[Int]
        move.fold(
            errors => {
                BadRequest(Json.obj("status" -> "OK", "message" -> JsError.toJson(errors)))
            },
            move => {
                if (0 < move && move < 3) {
                    print(move.toString)
                    print(controller.game.state)
                    controller.nextMove(move.toString)
                    Ok("move accepted")
                }
                else {
                    BadRequest("move not accepted")
                }
            }
        )

    }

    def fighting(): Action[AnyContent] = Action { request =>
        val move  = (request.body.asJson.get \ "move").validate[Int]
        move.fold(
            errors => {
                BadRequest(Json.obj("status" -> "OK", "message" -> JsError.toJson(errors)))
            },
            move => {
                if (0 < move && move < 5) {
                    controller.attackWith(move.toString)
                    Ok("move accepted")
                }
                else {
                    BadRequest("move not accepted")
                }
            }
        )
    }

    def switch(): Action[AnyContent] = Action { request =>
        val move  = (request.body.asJson.get \ "move").validate[Int]
        move.fold(
            errors => {
                BadRequest(Json.obj("status" -> "OK", "message" -> JsError.toJson(errors)))
            },
            move => {
                if (0 < move && move < 5) {
                    controller.selectPokemon(move.toString)
                    Ok("move accepted")
                }
                else {
                    BadRequest("move not accepted")
                }
            }
        )
    }

    def gameJson(): Action[AnyContent] = Action { implicit request =>
        val gameJson = controller.game.toJson
        Ok(gameJson)
    }

    def game(): Action[AnyContent] = Action { implicit request =>

        val PokePlayer(namePlayer1, pokemonsPlayer1, currentPokePlayer1) = controller.game.player1.getOrElse( PokePlayer( "Luis", PokePack( List( Some( Pokemon.apply( Glurak ) ) ) ) ) )
        val PokePlayer(namePlayer2, pokemonsPlayer2, currentPokePlayer2) = controller.game.player2.getOrElse( PokePlayer( "Timmy", PokePack( List( Some( Pokemon.apply( Simsala ) ) ) ) ) )

        val currentPokemonPlayer1 = pokemonsPlayer1.contents.apply(currentPokePlayer1).get
        val currentPokemonPlayer2 = pokemonsPlayer2.contents.apply(currentPokePlayer2).get

        val gameField = views.html.game(
            controller.game.state.toString,
            controller.game.turn,
            namePlayer1,
            namePlayer2,
            currentPokemonPlayer1,
            currentPokemonPlayer2,
            pokemonsPlayer1,
            pokemonsPlayer2
        )

        Ok(gameField)
    }

    def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
        Ok(views.html.index())
    }

    def initPlayers(): Action[AnyContent] = Action { implicit request =>
        val player1Name = request.body.asFormUrlEncoded.get("player1Name").mkString
        val player2Name = request.body.asFormUrlEncoded.get("player2Name").mkString
        val allPokemons = PokemonType.values

        controller.initPlayers()
        controller.addPlayer(player1Name)
        controller.addPlayer(player2Name)

        Ok(views.html.playerPokemons(player1Name, player2Name, allPokemons))
    }

    def initPokemons(): Action[AnyContent] = Action { implicit request =>
        val player1Pokemon1 = request.body.asFormUrlEncoded.get("player1Pokemon1").mkString
        val player1Pokemon2 = request.body.asFormUrlEncoded.get("player1Pokemon2").mkString
        val player1Pokemon3 = request.body.asFormUrlEncoded.get("player1Pokemon3").mkString
        val player2Pokemon1 = request.body.asFormUrlEncoded.get("player2Pokemon1").mkString
        val player2Pokemon2 = request.body.asFormUrlEncoded.get("player2Pokemon2").mkString
        val player2Pokemon3 = request.body.asFormUrlEncoded.get("player2Pokemon3").mkString


        controller.addPokemons(player1Pokemon1 + player1Pokemon2 + player1Pokemon3)
        controller.addPokemons(player2Pokemon1 + player2Pokemon2 + player2Pokemon3)

        Redirect(routes.HomeController.game)
    }

    def playerNames(): Action[AnyContent] = Action {
        Ok(views.html.playerNames())
    }

    def restartGame(): Action[AnyContent] = Action {
        controller.restartTheGame()

        Redirect(routes.HomeController.index())

    }

    def rules(): Action[AnyContent] = Action {
        Ok(views.html.rules())
    }

}
