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

