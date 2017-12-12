package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import aic.gas.sc.gg_bot.replay_parser.service.ReplayLoaderService;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeoutException;

public class RabbitMQReplayLoaderServiceImpl implements ReplayLoaderService {

  private static final String TASK_QUEUE_NAME = "parse";
  private static final int PREFETCH_COUNT = 1;
  private final String host;
  private final String user;
  private final String password;
  private final Integer port;
  private final ArrayBlockingQueue<String> replays_to_parse;

  RabbitMQReplayLoaderServiceImpl(
      String host,
      String user,
      String password,
      Integer port) {

    this.host = host;
    this.user = user;
    this.password = password;
    this.port = port;

    this.replays_to_parse = new ArrayBlockingQueue<>(PREFETCH_COUNT);
  }

  @Override
  public void loadReplaysToParse() {
    try {
      connectToRabbitMQ();
    } catch (IOException | TimeoutException e) {
      throw new RuntimeException(e);
    }
  }

  private void connectToRabbitMQ() throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(host);
    factory.setVirtualHost("/");
    factory.setPort(port);
    factory.setUsername(user);
    factory.setPassword(password);

    final Connection connection = factory.newConnection();
    final Channel channel = connection.createChannel();
    final Consumer consumer = createConsumer(channel);

    Map<String, Object> args = new HashMap<>();
    args.put("x-dead-letter-exchange", TASK_QUEUE_NAME + ".dead");
    args.put("x-dead-letter-routing-key", TASK_QUEUE_NAME);
    channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, args);

    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    channel.basicQos(PREFETCH_COUNT);
    channel.basicConsume(TASK_QUEUE_NAME, false, consumer);
  }

  private Consumer createConsumer(final Channel channel) {
    return new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(
          String consumerTag, Envelope envelope,
          AMQP.BasicProperties properties, byte[] body) throws IOException {

        String message = new String(body, "UTF-8");

        System.out.println(" [x] Received '" + message + "'");
        try {
          replays_to_parse.put(message); // block until someone consumes the message

          System.out.println(" [x] Done");
          channel.basicAck(envelope.getDeliveryTag(), false);
        } catch (InterruptedException e) { // if something happens
          System.out.println(" [x] Reject message '" + message + "', interrupted!");
          channel.basicReject(envelope.getDeliveryTag(), true);
        }
      }
    };
  }

  @Override
  public File returnNextReplayToPlay() throws Exception {
    // block if there are no replays
    String replay_specification = replays_to_parse.take();
    System.out.println(" [x] Consumed '" + replay_specification + "'");
    return processRmqMsg(replay_specification);
  }

  private File processRmqMsg(String replay_specification) {
    // let's keep rabbitmq msg simple, it's just path to replay file
    return new File(replay_specification);
  }
}
