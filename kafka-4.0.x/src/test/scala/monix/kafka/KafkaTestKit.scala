package monix.kafka

import org.scalatest.Suite
import _root_.io.github.embeddedkafka.EmbeddedKafka

trait KafkaTestKit extends EmbeddedKafka { self: Suite =>

  sys.addShutdownHook {
    EmbeddedKafka.stop()
  }
}
