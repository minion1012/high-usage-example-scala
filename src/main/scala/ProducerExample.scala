import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object ProducerExample {
  def main(args: Array[String]): Unit = {

    val topic = "myTopic"
    val brokers = "localhost:9092"
    val props = new Properties()
    props.put("bootstrap.servers", brokers)
    props.put("client.id", "ScalaProducerExample")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)
    var a = 0;

    // for loop execution with a range
    for (a <- 1 to 10) {
      val key = "key" + a
      val value = "value-" + a
      val data = new ProducerRecord[String, String](topic, key, value)

      println("producing " + key)
      producer.send(data)
    }

    producer.close()
  }
}
