name := "high-usage-example-scala"

version := "0.1"

scalaVersion := "2.12.7"

val workAround = {
  sys.props += "packaging.type" -> "jar"
  ()
}

resolvers += "confluent.io" at "http://packages.confluent.io/maven/"

libraryDependencies += "io.confluent" % "kafka-avro-serializer" % "5.0.0"

libraryDependencies += "org.apache.kafka" %% "kafka" % "2.0.0"

libraryDependencies += "org.apache.kafka" %% "kafka-streams-scala" % "2.0.0"

libraryDependencies += "io.confluent" % "kafka-streams-avro-serde" % "5.0.0"

libraryDependencies += "com.typesafe" % "config" % "1.3.2"

// compile avro to scala
// detail: https://github.com/julianpeeters/sbt-avrohugger
sourceGenerators in Compile += (avroScalaGenerate in Compile).taskValue
avroSourceDirectories in Compile += baseDirectory.value / "avro"

enablePlugins(DockerPlugin)