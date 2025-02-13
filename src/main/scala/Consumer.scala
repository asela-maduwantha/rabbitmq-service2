import com.rabbitmq.client.{CancelCallback, ConnectionFactory, DeliverCallback}
import scala.util.{Failure, Success, Try, Using}

object Consumer {
  private val QUEUE_NAME = "queue"

  def main(args: Array[String]): Unit = {
    val factory = new ConnectionFactory()
    factory.setHost("localhost")
    factory.setPort(5672)
    factory.setUsername("guest")
    factory.setPassword("guest")

    Try(factory.newConnection()) match {
      case Success(connection) =>
        val channel = connection.createChannel()
          channel.queueDeclare(QUEUE_NAME, false, false, false, null)

          val deliverCallback: DeliverCallback = (_, delivery) => {
            Try(new String(delivery.getBody, "UTF-8")) match {
              case Success(message) =>
                println(s"[x] New Message: " + message)
                Try(channel.basicAck(delivery.getEnvelope.getDeliveryTag, false)) match {
                  case Success(_) =>
                  case Failure(exception) => println(s"Failed to acknowledge message: ${exception.getMessage}")
                }

              case Failure(exception) => println(s"Failed to process message: ${exception.getMessage}")
            }
          }

          val cancelCallback: CancelCallback = _ => println("Consumer cancelled")

          Try(channel.basicConsume(QUEUE_NAME, false, deliverCallback, cancelCallback)) match {
            case Success(_) => println("Waiting for messages. To exit press CTRL+C")
            case Failure(exception) => println(s"Failed to start consuming messages: ${exception.getMessage}")
          }

          println("Press [CTRL+C] to exit.")
          sys.addShutdownHook {
            connection.close()
            println("Connection closed")
          }
        Thread.sleep(Long.MaxValue)
      case Failure(exception) =>
        println(s"Failed to establish connection: ${exception.getMessage}")
    }
  }
}
