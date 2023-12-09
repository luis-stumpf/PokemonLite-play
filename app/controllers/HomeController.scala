package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.actor._
import de.htwg.se.pokelite.controller.{AttackEvent, GameOver, PlayerChanged, PokemonChanged, StateChanged, UnknownCommand}
import de.htwg.se.pokelite.controller.impl.Controller
import de.htwg.se.pokelite.model.impl.pokePlayer.PokePlayer
import de.htwg.se.pokelite.model.{AttackType, PokePack, Pokemon, PokemonArt, PokemonType}
import de.htwg.se.pokelite.model.PokemonType.{Glurak, Simsala}
import de.htwg.se.pokelite.model.commands.{AddPokemonCommand, ChangeStateCommand}
import de.htwg.se.pokelite.model.states.{DesicionState, FightingState, GameOverState, InitPlayerPokemonState, InitPlayerState, InitState, SwitchPokemonState}
import de.htwg.se.pokelite.util.Observer

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.streams.ActorFlow

import scala.swing.Reactor


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents, implicit val system: ActorSystem) extends BaseController {

    val controller: Controller = new Controller()

    var timestamp = 0
    var lastMsg = ""

    def chat: Action[AnyContent] = Action {
        implicit request => {
            Ok(views.html.chat())
        }
    }

    def receiveMsg: Action[AnyContent] = Action {
        implicit request => {
            println("received")
            val req = request.body.asJson
            val msg = req.get("msg").toString()
            timestamp += 1
            lastMsg = msg
            Ok(Json.obj(
                "timestamp" -> timestamp.toString)
            )
        }
    }

    def getChat: Action[AnyContent] = Action {
        implicit request => {

            val req = request.body.asJson
            val ts = req.get("timestamp").toString()
            while (ts.equals("\"" + timestamp.toString + "\"")) {}
            Ok(Json.obj(
                "timestamp" -> timestamp.toString,
                "msg" -> lastMsg)
            )
        }
    }

    def decision(): Action[AnyContent] = Action { request =>
        val move  = (request.body.asJson.get \ "move").validate[Int]
        move.fold(
            errors => {
                BadRequest(Json.obj("status" -> "OK", "message" -> JsError.toJson(errors)))
            },
            move => {
                if (0 < move && move < 3) {
                    controller.nextMove(move.toString)
                    Ok("move accepted")
                }
                else {
                    BadRequest("move not accepted")
                }
            }
        )
    }

    def initPlayerNames(): Action[JsValue] = Action(parse.json) { implicit request => //parsing the request body explicitly as JSON
        val playerName1 = (request.body \ "playerName1").as[String]
        val playerName2 = (request.body \ "playerName2").as[String]

        controller.initPlayers()
        controller.addPlayer(playerName1)
        controller.addPlayer(playerName2)

        Ok("ok")
    }

    def initPlayerPokemons(): Action[JsValue] = Action(parse.json) { implicit request =>
        /*
        val player1Pokemon1 = (request.body \ "player1Pokemon1").as[String]
        val player1Pokemon2 = (request.body \ "player1Pokemon2").as[String]
        val player1Pokemon3 = (request.body \ "player1Pokemon3").as[String]
        val player2Pokemon1 = (request.body \ "player2Pokemon1").as[String]
        val player2Pokemon2 = (request.body \ "player2Pokemon2").as[String]
        val player2Pokemon3 = (request.body \ "player2Pokemon3").as[String]
        */

        val playerPokemons1 = (request.body \ "playerPokemons1").as[Array[Int]].mkString("")
        val playerPokemons2 = (request.body \ "playerPokemons2").as[Array[Int]].mkString("")

        controller.addPokemons(playerPokemons1)
        controller.addPokemons(playerPokemons2)

        Ok("ok")
    }

    /*implicit val playerNameWrites: Writes[PokePlayer] = new Writes[PokePlayer] {
        override def writes(p: PokePlayer): JsValue = Json.obj(
            "name" -> p.name
        )
    }

    implicit val pokemonTypeWrites: Writes[PokemonType] = new Writes[PokemonType] {
        override def writes(pokemonType: PokemonType): JsValue = Json.obj(
            "name" -> pokemonType.name,
            "hp" -> pokemonType.hp,
            "attacks" -> Json.toJson(pokemonType.attacks),
            "pokemonArt" -> Json.toJson(pokemonType.pokemonArt)
        )
    }

    implicit val attackTypeWrites: Writes[AttackType] = new Writes[AttackType] {
        override def writes(attackType: AttackType): JsValue = Json.obj(
            "name" -> attackType.name,
            "damage" -> attackType.damage
        )
    }

    implicit val pokemonArtWrites: Writes[PokemonArt] = new Writes[PokemonArt] {
        override def writes(pokemonArt: PokemonArt): JsValue = Json.obj(
            "name" -> pokemonArt.toString
        )
    }*/

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


    def stateJson(): Action[AnyContent] = Action { implicit request =>
        val stateJson = controller.game.state.toJson
        Ok(stateJson)
    }
    def allPokemonsJson() : Action[AnyContent] = Action { implicit request =>
        val allPokemonsAsJson = Json.toJson(PokemonType.values.map(pokemon => pokemon.toJson))
        Ok(allPokemonsAsJson)
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


    def initPlayers(): Action[JsValue] = Action(parse.json) { implicit request => //parsing the request body explicitly as JSON
        val player1Name = (request.body \ "player1Name").as[String]
        val player2Name = (request.body \ "player2Name").as[String]

        controller.initPlayers()
        controller.addPlayer(player1Name)
        controller.addPlayer(player2Name)

        val redirectUrl = routes.HomeController.playerPokemons.url

        Ok(Json.obj(
            "redirect" -> redirectUrl
        )).as("application/json")
    }

    def playerPokemons(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
        val player1Name = controller.game.player1.getOrElse("Player1").toString
        val player2Name = controller.game.player2.getOrElse("Player2").toString

        Ok(views.html.playerPokemons(player1Name, player2Name, PokemonType.values))
    }

    def initPokemons(): Action[JsValue] = Action(parse.json) { implicit request =>
        val player1Pokemon1 = (request.body \ "player1Pokemon1").as[String]
        val player1Pokemon2 = (request.body \ "player1Pokemon2").as[String]
        val player1Pokemon3 = (request.body \ "player1Pokemon3").as[String]
        val player2Pokemon1 = (request.body \ "player2Pokemon1").as[String]
        val player2Pokemon2 = (request.body \ "player2Pokemon2").as[String]
        val player2Pokemon3 = (request.body \ "player2Pokemon3").as[String]


        controller.addPokemons(player1Pokemon1 + player1Pokemon2 + player1Pokemon3)
        controller.addPokemons(player2Pokemon1 + player2Pokemon2 + player2Pokemon3)

        val redirectUrl = routes.HomeController.game.url

        Ok(Json.obj(
            "redirect" -> redirectUrl
        )).as("application/json")
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

    def socket() = WebSocket.accept[String, String] { request =>
        ActorFlow.actorRef { out =>
            println("Connect received")
            PokemonLiteWebSocketActor.create(out)
        }
    }

    object PokemonLiteWebSocketActor {
        def create(out: ActorRef) = {
            Props(new PokemonLiteWebSocketActor(out))
        }
    }


    class PokemonLiteWebSocketActor(out: ActorRef) extends Actor with Reactor {

        listenTo(controller)

        def receive: Receive = {
            case msg: String =>
                out ! (controller.game.toJson)
                println("Send Json to client" + msg)
            case _ => println("Unknown Message")
        }

        reactions += {
            case event: StateChanged => sendUpdateGamefield()
            case event: PlayerChanged => sendJsonToClient()
            case event: GameOver => sendJsonToClient()
            case event: PokemonChanged => sendUpdateGamefieldWithGifs()
            case event: AttackEvent => sendUpdateGamefield()
            case event: UnknownCommand => sendJsonToClient()
            case _ => sendJsonToClient()

        }

        def sendJsonToClient(): Unit = {
            out ! ("test")
        }

        def sendUpdateGamefield(): Unit = {
            if (controller.game.state != InitPlayerState()) out ! ("updateGamefield")
        }

        def sendUpdateGamefieldWithGifs(): Unit = {
            out ! ("updateGamefieldWithGifs")
        }

    }

}
