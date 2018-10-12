import java.util.{Properties, UUID}

import org.apache.avro.Schema.Parser
import org.apache.avro.generic.GenericData
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.log4j.BasicConfigurator

case class Person(firstName: String, lastName: String)

class AvroProducer {

  val kafkaBootstrapServer = "localhost:9092"
  val schemaRegistryUrl = "http://localhost:8081"

  val props = new Properties()
  props.put("bootstrap.servers", kafkaBootstrapServer)
  props.put("schema.registry.url", schemaRegistryUrl)
  props.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer")
  props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer")
  props.put("acks", "1")

  val producer = new KafkaProducer[String, GenericData.Record](props)
  val schemaParser = new Parser

  val key = "key1"
  val valueSchemaJson =
    s"""
    {
      "namespace": "com.avro.junkie",
      "type": "record",
      "name": "Person",
      "fields": [
        {"name": "firstName", "type": "string"},
        {"name": "lastName", "type": "string"}
      ]
    }
  """
  val valueSchemaAvro = schemaParser.parse(valueSchemaJson)
  val avroRecord = new GenericData.Record(valueSchemaAvro)

  val person = new Person("Mary", "Red-" + UUID.randomUUID().toString)

  avroRecord.put("firstName", person.firstName)
  avroRecord.put("lastName", person.lastName)

  def start = {
    try {
      val record = new ProducerRecord("person", key, avroRecord)
      val ack = producer.send(record).get()
    }
    catch {
      case e: Throwable => println(e.getMessage, e)
    }
  }
}

object Main {
  def main(args: Array[String]) {
    BasicConfigurator.configure()

    val producer = new AvroProducer
    producer.start
  }
}
