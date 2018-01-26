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
import java.util.concurrent.TimeoutException;

public class RabbitMQReplayLoaderServiceImpl implements ReplayLoaderService {

  private static final String TASK_QUEUE_NAME = "parse";
  private static final int PREFETCH_COUNT = 1;
  private final String host;
  private final String user;
  private final String password;
  private final Integer port;
  private final ProducerConsumer producerConsumer;

  private boolean hasMessage = false;
  private boolean wantsMessage = false;
  private boolean processedMessage = false;
  private String parseReplay;

  public RabbitMQReplayLoaderServiceImpl(
      String host,
      String user,
      String password,
      Integer port) {

    this.host = host;
    this.user = user;
    this.password = password;
    this.port = port;

    this.producerConsumer = new ProducerConsumer();
  }

  @Override
  public void loadReplaysToParse() {
    Thread rabbitmqConnect = new Thread(new RabbitmqConnect(), "rabbitmqConnect");
    rabbitmqConnect.start();
  }

  @Override
  public File returnNextReplayToPlay() throws Exception {
    // block if there are no replays
    System.out.println(" [x] Waiting for a new message");

    String replay_specification = producerConsumer.consumeStart();

    System.out.println(" [x] Consume start '" + replay_specification + "'");
    return processRmqMsg(replay_specification);
  }

  @Override
  public void finishedProcessing(File replay) {
    System.out.println(" [x] Consume finish '" + replay.getName() + "'");
    producerConsumer.consumeFinish();
  }

  private File processRmqMsg(String replay_specification) {
    // let's keep rabbitmq msg simple, it's just path to replay file
    return new File(replay_specification);
  }

  private class RabbitmqConnect implements Runnable {

    @Override
    public void run() {
      try {
        connectToRabbitMQ();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (TimeoutException e) {
        e.printStackTrace();
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
          try {
            System.out.println(" [x] Received '" + message + "'");

            producerConsumer.produce(message);

            System.out.println(" [x] Done '" + message + "'");
            channel.basicAck(envelope.getDeliveryTag(), false);
          } catch (InterruptedException e) { // if something happens
            System.out.println(" [x] Reject message '" + message + "', interrupted!");
            channel.basicReject(envelope.getDeliveryTag(), true);
          }
        }
      };
    }
  }

  public static class ProducerConsumer {

    // message shared by producer and consumer
    String message;

    boolean hasMessage = false;
    boolean wantsMessage = false;
    boolean finishedMessage = false;

    void produce(String value) throws InterruptedException {
      synchronized (this) {
        hasMessage = true;
        while (!wantsMessage) {
          wait();
          Thread.sleep(10);
        }

        message = value;

        // notifies the consumer thread that now it can start consuming
        notify();

        while (!finishedMessage) {
          wait();
          Thread.sleep(10);
        }
      }
    }

    String consumeStart() throws InterruptedException {
      synchronized (this) {
        wantsMessage = true;
        finishedMessage = false;

        int i = 0;
        while (!hasMessage) {
          System.err.print(".");
          if (++i % 30 == 0) {
            System.err.print("\n");
          }
          wait(100);
        }

        // notifies the producer thread that now it can start waiting for finished consumption%
        notify();

        hasMessage = false;
        return message;
      }
    }

    void consumeFinish() {
      synchronized (this) {
        wantsMessage = false;
        finishedMessage = true;

        // notifies the producer thread that now it can start producing again
        notify();
      }
    }
  }
}
