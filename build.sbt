ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.0"

lazy val root = (project in file("."))
  .settings(
    name := "consumer"
  )

libraryDependencies ++= Seq(
  "com.rabbitmq" % "amqp-client" % "5.5.0" ,
  "org.slf4j" % "slf4j-simple" % "1.7.30"
)