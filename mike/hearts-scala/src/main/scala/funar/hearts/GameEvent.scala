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
		case PlayerAfter(player: Player, callback: Player => Game[A])
		case GameOver(callback: Option[Player] => Game[A])
		case GetCommand(callback: GameCommand => Game[A])
		case Done(result: A)
	}

	import Game.*
	//   def put(key: Key, value: Value): DB[Unit] = Put(key, value, Return)
	def recordEventM(event: GameEvent): Game[Unit] = RecordEvent(event, Done(_))
	def isPlayValidM(player: Player, card: Card): Game[Boolean] =
		IsPlayValid(player, card, Done(_))
	def isTrickFullM: Game[Option[(Trick, Player)]] = IsTrickFull(Done(_))
	def playerAfterM(player: Player) = PlayerAfter(player, Done(_))
	def gameOverM = GameOver(Done(_))

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
				case PlayerAfter(player, callback) =>
					PlayerAfter(player, { nextPlayer => callback(nextPlayer).flatMap(f)})
				case GameOver(callback) =>
					GameOver({ winnerOption => callback(winnerOption).flatMap(f)} )
				case GetCommand(callback) =>
					GetCommand( { command => callback(command).flatMap(f) })
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
						isTrickFullM.flatMap { trickPlayerOption =>
							trickPlayerOption match {
								case Some((trick, trickTaker)) =>
									recordEventM(TrickTaken(trickTaker, trick)) >>
									gameOverM >>= { gameOver =>
										gameOver match {
											case Some(winner) =>
												for {
													_ <- recordEventM(GameEnded(winner))
												} yield Some(winner)
											case None =>
												for {
													_ <- recordEventM(PlayerTurnChanged(trickTaker))
												} yield None
										}
									}
								case None =>
									for {
										nextPlayer <- playerAfterM(player)
										_ <- recordEventM(PlayerTurnChanged(nextPlayer))
									} yield None
							}
						}
					} else {
						recordEventM(IllegalCardAttempted(player, card)).map { _ => None }
					}
				}
		}
	}

	// das gesamte Spiel, angestoßen durch das Karten-Austeilen
	def tableLoopM(command: GameCommand): Game[Option[Player]] =
		tableProcessCommandM(command).flatMap { winnerOption =>
			winnerOption match {
				case None =>
					GetCommand(tableLoopM)
				case Some(winner) => Game.Done(Some(winner))
			}

		}
}
