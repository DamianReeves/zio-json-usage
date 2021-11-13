package zio.json.usage

import zio.test._

object InstructionSpec extends DefaultRunnableSpec {
  def spec = suite("Instruction Spec")(
    suite("Instruction")(
      test("can be encoded to JSON") {
        val instruction = Instruction.sequence(
          Instruction.Push(10),
          Instruction.Push(20),
          Instruction.Push(42),
          Instruction.Pop
        )
        val json        = instruction.toJson
        assertTrue(json != "{}")
      }
    )
  )
}
