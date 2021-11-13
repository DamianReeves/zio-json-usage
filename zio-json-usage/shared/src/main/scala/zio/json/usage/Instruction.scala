package zio.json.usage
import zio.json._
sealed trait Instruction
object Instruction {
  implicit val codec: JsonCodec[Instruction] = DeriveJsonCodec.gen[Instruction]
  final case class Push(value: Int)                               extends Instruction
  final case class AndThen(left: Instruction, right: Instruction) extends Instruction
  case object Pop                                                 extends Instruction
}
