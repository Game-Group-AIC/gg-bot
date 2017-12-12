package aic.gas.sc.gg_bot.replay_parser.main;

public class Config {

  private String rabbitmqHost;
  private String rabbitmqUser;
  private String rabbitmqPassword;
  private Integer rabbitmqPort;

  public String getRabbitmqHost() {
    return rabbitmqHost;
  }

  public void setRabbitmqHost(String rabbitmqHost) {
    this.rabbitmqHost = rabbitmqHost;
  }

  public String getRabbitmqUser() {
    return rabbitmqUser;
  }

  public void setRabbitmqUser(String rabbitmqUser) {
    this.rabbitmqUser = rabbitmqUser;
  }

  public String getRabbitmqPassword() {
    return rabbitmqPassword;
  }

  public void setRabbitmqPassword(String rabbitmqPassword) {
    this.rabbitmqPassword = rabbitmqPassword;
  }

  public Integer getRabbitmqPort() {
    return rabbitmqPort;
  }

  public void setRabbitmqPort(Integer rabbitmqPort) {
    this.rabbitmqPort = rabbitmqPort;
  }
}
