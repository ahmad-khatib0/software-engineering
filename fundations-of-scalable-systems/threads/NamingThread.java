
class NamingTread implements Runnable {

  private String name;

  public NamingTread(String threadName) {
    name = threadName;
    System.out.println("Constructor called: " + threadName);
  }

  public void run() {
    // Display info about this thread
    System.out.println("Run called : " + name);
    System.out.println(name + " : " + Thread.currentThread());
    // and now terminate ....
  }

  public static void main(String[] args) {

    NamingThread name0 = new NamingThread("thread0");
    NamingThread name1 = new NamingThread("thread1");
    NamingThread name2 = new NamingThread("thread2");

    // create the thread
    Thread t0 = new Thread(name0);
    Thread t1 = new Thread(name1);
    Thread t2 = new Thread(name2);

    t0.start();
    t1.start();
    t2.start();

    // delay the main thread for a second (1000 milliseconds)
    try {
      Thread.currentThread().sleep(1000);
    } catch (InterruptedException e) {
    }

    // Display info about the main thread and terminate
    System.out.println(Thread.currentThread());

  }
}
