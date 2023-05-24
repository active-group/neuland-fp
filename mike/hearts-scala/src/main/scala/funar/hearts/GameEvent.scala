package funar.hearts

import cats._
import cats.implicits._

import Cards._

enum GameEvent {
	case HandDealt(player: Player, hand: Hand)
	case PlayerTurnChanged(player: Player)
	case LegalCardPlayed(player: Player, card: Card)
	case IllegalCardAttempted(player: Player, card: Card)
	case TrickTaken(player: Player, trick: Trick)
	case GameEnded(won: Player)
}

object GameEvent {
	def eventsWinner(events: Iterable[GameEvent]): Option[Player] =
		events.find({ case GameEnded(won) => true
									case _ => false }) match {
										case Some(GameEnded(won)) => Some(won)
										case _ => None
									}
}

enum GameCommand {
	case DealHands(hands: Map[Player, Hand])
	case PlayCard(player: Player, card: Card)
}

object GameM {
	enum Game[A] {
		case RecordEvent(event: GameEvent, callback: Unit => Game[A])
		case IsPlayValid(player: Player, card: Card, callback: Boolean => Game[A])
		case IsTrickFull(callback: Option[(Trick, Player)] => Game[A])
		case Done(result: A)
	}

	import Game.*
	//   def put(key: Key, value: Value): DB[Unit] = Put(key, value, Return)
	def recordEventM(event: GameEvent): Game[Unit] = RecordEvent(event, Done)
	def isPlayValidM(player: Player, card: Card): Game[Boolean] =
		IsPlayValid(player, card, Done)
	def isTrickFull: Game[Option[(Trick, Player)]] = IsTrickFull(Done)

	given gameMonad : Monad[Game[_]] with {
		override def pure[A](x: A): Game[A] = Done(x)

		override def flatMap[A, B](fa: Game[A])(f: A => Game[B]): Game[B] =
			fa match {
				case RecordEvent(event, callback) =>
					RecordEvent(event, { _ => callback(()).flatMap(f)})
				case IsPlayValid(player, card, callback) =>
					IsPlayValid(player, card, { isValid =>
						callback(isValid).flatMap(f)
					})
				case IsTrickFull(callback) =>
					IsTrickFull({ trickPlayerOption => callback(trickPlayerOption).flatMap(f) })
				case Done(result) => f(result)
			}

		/*
		enum Either[+A, +B] {
		  case Left[+A](a: A)
      case Right[+A](b: B)
		}
		*/
		override def tailRecM[A, B](a: A)(f: A => Game[Either[A, B]]): Game[B] = ???

	}
	def tableProcessCommandM(command: GameCommand): Game[Option[Player]] = {
		import GameCommand.*
		import GameEvent.*
		command match {
			case DealHands(hands) => {
				val dealHandMs: Seq[Game[Unit]] = hands.map { case (player, hand) =>
					recordEventM(HandDealt(player, hand))
				}.toSeq
				/*
				for {
					_ <- Foldable[Seq].sequence_(recordMs)
				} yield None
				*/
				val dealHandsM: Game[Unit] = Foldable[Seq].sequence_(dealHandMs)
				dealHandsM.map { _ => None }
			}
			case PlayCard(player, card) =>
				/*
				for {
					isValid <- isPlayValidM(player, card)
					result <- ???
				} yield result
				*/
				isPlayValidM(player, card).flatMap { isValid =>
					if (isValid) {
						recordEventM(LegalCardPlayed(player, card)) >> // wie Semikolon in Java
						isTrickFull.flatMap { trickPlayerOption =>
							???
						}
					} else {
						recordEventM(IllegalCardAttempted(player, card)).map { _ => None }
					}
				}
		}
	}
}
