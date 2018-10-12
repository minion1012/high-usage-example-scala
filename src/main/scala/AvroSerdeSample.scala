
import java.util.concurrent.TimeUnit
import java.util.{Collections, Properties}

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.scala.kstream.KStream
import org.apache.kafka.streams.scala.{Serdes, StreamsBuilder}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

object AvroSerdeExample extends App {


  val config: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-scala-application")
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    p
  }


  val genericAvroSerde = new GenericAvroSerde

  val isKeySerde = false

  genericAvroSerde.configure(Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
    "http://localhost:8081"), isKeySerde)

  implicit val sds = Consumed.`with`(Serdes.String, genericAvroSerde)


  val builder = new StreamsBuilder()

  val avroStream: KStream[String, GenericRecord] = builder.stream[String, GenericRecord]("person")

  avroStream.foreach((k, v) => println("GenericData => " + k + " : " + v))

  val mappedStream = avroStream.mapValues(person => getPerson(person))
  mappedStream.foreach((k, v) => println("Mapped to Object => FirstName: " + v.firstName + " and LastName: " + v.lastName))

  //  Kafka Streams part
  val streams: KafkaStreams = new KafkaStreams(builder.build(), config)

  streams.cleanUp()

  streams.start()

  // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
  sys.ShutdownHookThread {
    streams.close(10, TimeUnit.SECONDS)
  }

  private def getPerson(value: GenericRecord) = {
    val person = Person(value.get("firstName").toString, value.get("lastName").toString)
    person
  }

}
