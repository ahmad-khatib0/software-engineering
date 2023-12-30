package dining.philosophers;

//
// Correct implementation of Dining Philosophers
//

public class DiningPhilosophers {

  private final static int NUMCHOPSTICKS = 5;
  private final static int NUMPHILOSOPHERS = 5;

  public static void main(String[] args) throws Exception {

    final Philosopher[] ph = new Philosopher[NUMPHILOSOPHERS];
    Object[] chopSticks = new Object[NUMCHOPSTICKS];

    for (int i = 0; i < NUMCHOPSTICKS; i++) {
      chopSticks[i] = new Object();
    }

    for (int i = 0; i < NUMPHILOSOPHERS; i++) {
      Object leftChopStick = chopSticks[i];
      Object rightChopStick = chopSticks[(i + 1) % chopSticks.length];
      if (i == NUMPHILOSOPHERS - 1) {
        // The last philosopher picks up the right fork first
        ph[i] = new Philosopher(rightChopStick, leftChopStick);
      } else {
        // all others pick up the left chop stick first
        ph[i] = new Philosopher(leftChopStick, rightChopStick);
      }

      // More formally we are imposing an ordering on the acquisition of shared
      // resources, such that:
      // chopStick[0] < chopStick[1] < chopStick[2] < chopStick[3] < chopStick[4]
      // This means each thread will always attempt to acquire chopstick[0] before
      // chop stick[1], and chopstick[1] before chopstick[2], and so on. For
      // Philosopher 4, this means they will attempt to acquire chopstick[0] before
      // chopstick[4], thus
      // ╒═══════════════════════════════════════════════════════╕
      // BREAKING THE POTENTIAL FOR A CIRCULAR WAIT DEADLOCK.
      // └───────────────────────────────────────────────────────┘
      //
      // Deadlocks are a complicated topic and this section has just scratched the
      // surface. You’ll see deadlocks in many distributed systems. For example, a
      // user request acquires a lock on some data in a Students database table, and
      // must then update rows in the Classes table to reflect student attendance.
      // Simultaneously another user request acquires locks on the Classes table, and
      // next must update some information in the Students table. If these requests
      // interleave such that each request acquires locks in an overlapping fashion,
      // we have a deadlock.

      Thread th = new Thread(ph[i], "Philosopher " + i);
      th.start();
    }

  }

}
