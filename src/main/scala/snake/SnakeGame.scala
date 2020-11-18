package snake
import introprog.BlockGame
import introprog.examples.TestBlockGame
import javafx.util.Pair

abstract class SnakeGame(title: String) extends BlockGame(
  title, dim = (50, 30), blockSize = 15, background = Colors.Background,
  framesPerSecond = 50, messageAreaHeight = 3
) {
  var entities: Vector[Entity] = Vector.empty

  var players: Vector[Player] = Vector.empty

  sealed trait State
  case object Starting extends State
  case object Playing  extends State
  case object GameOver extends State
  case object Quitting extends State

  var state: State = Starting

/** All methods taking part in trait State utilizes inherited methods from BlockGame, as imported in file.    */

  def enterStartingState(): Unit = { // rensa, meddela "tryck space för start"
    clearWindow()
    drawCenteredText("Press space to start", pixelWindow.foreground, blockSize)
    }

  def enterPlayingState(): Unit = { // rensa, rita alla entiteter
    clearWindow()
    entities.foreach(_.erase)
    entities.foreach(_.draw)
    state = Playing
  }

  def enterGameOverState(): Unit = {
    drawCenteredText("GAME OVER", pixelWindow.foreground, blockSize)
    state = GameOver
  } // meddela "game over"

  def enterQuittingState(): Unit = {
    println("Goodbye!")
    pixelWindow.hide()
    state = Quitting
  }

  def randomFreePos(): Pos = { // Pulls random Pos until some free pos, used by fruit.
    var isNotFree: Boolean = true
    var rndPos = Pos.random(Dim(dim))
    while (isNotFree) {
      if (pixelWindow.getPixel(rndPos.dim.x * blockSize, rndPos.dim.y * blockSize) == Colors.Background) {
        isNotFree = false
        rndPos
      } else rndPos = Pos.random(Dim(dim))
    }
    rndPos
  }

  override def onKeyDown(key: String): Unit = {
    println(s"""key "$key" pressed""")
    state match {
      case Starting => if (key == " ") enterPlayingState()
      case Playing => players.foreach(_.handleKey(key))
      case GameOver =>
        if (key == " ") enterPlayingState()
        else if(key == "Escape") enterQuittingState()
      case _ =>
    }
  }

  override def onClose(): Unit = enterQuittingState()

  def isGameOver(): Boolean  //Abstract method, implements in subclass

  override def gameLoopAction(): Unit = {
    if (state == Playing) {
      entities.foreach(_.erase)
      entities.foreach(_.update)
      entities.foreach(_.draw)
      if (isGameOver) enterGameOverState()
    }
  }


  def startGameLoop(): Unit = {
    pixelWindow.show()  // Enables restart in spite of closed window...
    enterStartingState()
    gameLoop(stopWhen = state == Quitting)
  }

  def play(playerNames: String*): Unit // Abstract, implements in subclass
}
