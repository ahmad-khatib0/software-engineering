
public class RequestCounter {
  final static private int NUMTHREADS = 50000;
  private int count = 0;

  synchronized public void inc() {
    count++;
  }

  public int getVal() {
    return this.count;
  }

  public static void main(String[] args) throws InterruptedException {
    final RequestCounter counter = new RequestCounter();
    for (int i = 0; i < NUMTHREADS; i++) {
      // lambda runnable creation
      Runnable thread = () -> {
        counter.inc();
      };
      new Thread(thread).start();
    }

    // 5-second delay in main to allow the threads to finish:
    Thread.sleep(5000);
    System.out.println("Value should be " + NUMTHREADS + "It is: " + counter.getVal());
    // the answer mostly will not be correct (arround 49999 or 98 ...) why?
    // The answer lies in how abstract, high-level programming language statements,
    // in Java in this case, are executed on a machine. In this example,
    // to perform an increment of a counter, the CPU must:
    // • Load the current value into a register.
    // • Increment the register value.
    // • Write the results back to the original memory location.
    // This simple increment is actually a sequence of three machine-level
    // operations. at the machine level these three operations are independent and
    // not treated as a single atomic operation. By atomic, we mean an operation
    // that cannot be interrupted and hence once started will run to completion.
    // As the increment operation is not atomic at the machine level, one thread can
    // load the counter value into a CPU register from memory, but before
    // it writes the incremented value back, the scheduler preempts the thread
    // and allows another thread to start. This thread loads the old value of
    // the counter from memory and writes back the incremented value. Eventually
    // the original thread executes again and writes back its incremented value,
    // which just happens to be the same as what is already in memory.
    // When we lose updates in this manner, it is called a race condition. Race
    // conditions can occur whenever multiple threads make changes to some shared
    // state, in this case a simple counter. Essentially, different interleavings of
    // the threads can produce different results.

    //
    // To fix the counterexample, you therefore just need to identify the
    // inc() method as a critical section and make it a synchronized method
  }
}
