# kafka-websocket

kafka-websocket is a simple websocket server interface to the kafka distributed message broker. It supports clients
subscribing to topics, including multiple topics at once, and sending messages to topics. Messages may be either text
or binary, the format for each is described below.

A client may produce and consume messages on the same connection.

## Creating topics

Clients can use a REST endpoint to create a topic. A example would be:

/api/create?topic=my_topic

## Consuming from topics

Clients subscribe to topics by specifying them in a query parameter when connecting to kafka-websocket:

/v2/broker/?topics=my_topic,my_other_topic

If no topics are given, the client will not receive messages. The format of messages sent to clients is determined by
the subprotocol negotiated: kafka-text or kafka-binary. If no subprotocol is specified, kafka-text is used.

By default, a new, unique group.id is generated per session. The group.id for a consumer can be controlled by passing a
group.id as an additional query parameter: ?group.id=my_group_id

## Producing to topics

Clients publish to topics by connecting to /v2/broker/ and sending either text or binary messages that include a topic
and a message. Text messages may optionally include a key to influence the mapping of messages to partitions. A client
need not subscribe to a topic to publish to it.

There is also a REST API you can use /api/send?topic=my_topic&message=myMessage (either get or post) to send data to the broker. 


## Message transforms

By default, kafka-websocket will pass messages to and from kafka as is. If your application requires altering messages
in transit, for example to add a timestamp field to the body, you can implement a custom transform class. Transforms
extend us.b3k.kafka.ws.transforms.Transform and can override the initialize methods, or the transform methods for text
and binary messages.

Transforms can be applied to messages received from clients before they are sent to kafka (inputTransform) or to
messages received from kafka before they are sent to clients (outputTransform). See conf/server.properties for an
example of configuring the transform class.

## Text messages

Text messages are JSON objects with two mandatory attributes: topic and message. They may also include an optional key
attribute:

{ "topic" : "my_topic", "message" : "my amazing message" }

{ "topic" : "my_topic", "key" : "my_key123", "message" : "my amazing message" }

## Configuration

See property files in conf/