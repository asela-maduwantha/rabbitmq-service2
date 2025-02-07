import com.rabbitmq.client.{ConnectionFactory, DeliverCallback, CancelCallback}

object Consumer {
  private val QUEUE_NAME = "queue"

  def main(args: Array[String]): Unit = {
    val factory = new ConnectionFactory()
    factory.setHost("localhost")
    factory.setPort(5672)
    factory.setUsername("admin")
    factory.setPassword("Dilshan@123")

    val connection = factory.newConnection()
    val channel = connection.createChannel()

    channel.queueDeclare(QUEUE_NAME, false, false, false, null)

    val deliverCallback: DeliverCallback = (_, delivery) => {
      val message = new String(delivery.getBody, "UTF-8")
      println(s"[o] New Message")
      println(message)
      println("__________________________________")

      // Manually acknowledge the message
      channel.basicAck(delivery.getEnvelope.getDeliveryTag, false)
    }

    val cancelCallback: CancelCallback = _ => println("Consumer cancelled")

    // Set autoAck to false for manual acknowledgment
    channel.basicConsume(QUEUE_NAME, false, deliverCallback, cancelCallback)

    // Ensure consumer is set up and ready to receive messages
    println("Waiting for messages. To exit press CTRL+C")
    while (true) {
      Thread.sleep(1000)
    }
  }
}
