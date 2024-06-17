package com.manning.apisecurityinaction.controller;

import java.util.*;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;

public class DroolsAccessController extends ABACAccessController {
  private final KieContainer kieContainer;

  public DroolsAccessController() {
    // Load all rules found in the classpath
    this.kieContainer = KieServices.get().getKieClasspathContainer();
  }

  @Override
  boolean checkPermitted(Map<String, Object> subject,
      Map<String, Object> resource,
      Map<String, Object> action,
      Map<String, Object> env) {

    var session = kieContainer.newKieSession(); // Start a new Drools session.
    try {
      var decision = new Decision();
      // Create a Decision object and set it as a global variable named “decision.”
      session.setGlobal("decision", decision);

      // Insert facts for each category of attributes.
      session.insert(new Subject(subject));
      session.insert(new Resource(resource));
      session.insert(new Action(action));
      session.insert(new Environment(env));

      // Run the rule engine to see which rules match
      // the request and check the decision.
      session.fireAllRules();
      return decision.isPermitted();

    } finally {
      session.dispose(); // Dispose of the session when finished.
    }
  }

  // Drools likes to work with strongly typed values, so you can wrap each
  // collection of attributes in a distinct class to make it simpler to write
  // rules that match each one,

  // Wrapper for subject-related attributes
  public static class Subject extends HashMap<String, Object> {
    Subject(Map<String, Object> m) {
      super(m);
    }
  }

  // Wrapper for resource-related attributes
  public static class Resource extends HashMap<String, Object> {
    Resource(Map<String, Object> m) {
      super(m);
    }
  }

  public static class Action extends HashMap<String, Object> {
    Action(Map<String, Object> m) {
      super(m);
    }
  }

  public static class Environment extends HashMap<String, Object> {
    Environment(Map<String, Object> m) {
      super(m);
    }
  }
}
