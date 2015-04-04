package net.nikore.kafka.ws.json;

public class JsonMessage {
  private String status;
  private String message;

  public JsonMessage(String status, String message) {
    this.status = status;
    this.message = message;
  }

  public JsonMessage() {
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
