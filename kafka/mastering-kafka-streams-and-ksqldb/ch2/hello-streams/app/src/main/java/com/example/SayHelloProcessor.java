package com.example;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;

public class SayHelloProcessor implements Processor<Void, String> {

  @Override
  public void init(ProcessorContext context) {
  }

  @Override
  public void process(Void key, String value) {
    System.out.println("(Processor API) Hello, " + value);
  }

  // No special clean up needed in context
  @Override
  public void close() {
  }
}
