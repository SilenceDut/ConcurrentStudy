####线程池的优点
- 重用线程池中的线程,减少因对象创建,销毁所带来的性能开销;
- 能有效的控制线程的最大并发数,提高系统资源利用率,同时避免过多的资源竞争,避免堵塞;
- 能够多线程进行简单的管理,使线程的使用简单、高效。

####线程池相关的类
**Executor**,所有线程池的接口,只要一个方法

    public interface Executor {
        void execute(Runnable command);
    }

**ThreadPoolExecutor**是线程池的具体实现类,构造方法如下:

![](http://upload-images.jianshu.io/upload_images/1437930-083b0ce0bcbf6d49.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

**corePoolSize**———— 线程池的核心线程数,线程池中运行的线程数也永远不会超过 corePoolSize 个,默认情况下可以一直存活。可以通过设置allowCoreThreadTimeOut为True,此时**核心线程数就是0**,此时keepAliveTime控制所有线程的超时时间。
**maximumPoolSize**————线程池允许的最大线程数;
**keepAliveTime**———— 指的是空闲线程结束的超时时间;
**unit** ————是一个枚举，表示 keepAliveTime 的单位;
**workQueue**———— 表示存放任务的BlockingQueue<Runnable队列。

线程池的工作过程如下：

1、线程池刚创建时，里面没有一个线程。任务队列是作为参数传进来的。不过，就算队列里面有任务，线程池也不会马上执行它们。

2、当调用 execute() 方法添加一个任务时，线程池会做如下判断：

    a. 如果正在运行的线程数量小于 corePoolSize，那么马上创建线程运行这个任务；

    b. 如果正在运行的线程数量大于或等于 corePoolSize，那么将这个任务放入队列。

    c. 如果这时候队列满了，而且正在运行的线程数量小于 maximumPoolSize，那么还是要创建非核心线程立刻运行这个任务；

    d. 如果队列满了，而且正在运行的线程数量大于或等于 maximumPoolSize，那么线程池会抛出异常RejectExecutionException。

3、当一个线程完成任务时，它会从队列中取下一个任务来执行。

4、当一个线程无事可做，超过一定的时间（keepAliveTime）时，线程池会判断，如果当前运行的线程数大于 corePoolSize，那么这个线程就被停掉。所以线程池的所有任务完成后，它最终会收缩到 corePoolSize 的大小。

**常见的线程池**
SingleThreadExecutor：  

    public static ExecutorService newSingleThreadExecutor() {
        return new FinalizableDelegatedExecutorService
            (new ThreadPoolExecutor(1, 1,
                                    0L, TimeUnit.MILLISECONDS,
                                    new LinkedBlockingQueue<Runnable>()));
    }
//单个后台线程  (其缓冲队列是无界的)  
创建一个单线程的线程池。这个线程池只有一个核心线程在工作，也就是相当于单线程串行执行所有任务。如果这个唯一的线程因为异常结束，那么会有一个新的线程来替代它。此线程池保证所有任务的执行顺序按照任务的提交顺序执行。  
  
FixedThreadPool：  

     public static ExecutorService newFixedThreadPool(int nThreads) {
         return new ThreadPoolExecutor(nThreads, nThreads,
                                       0L, TimeUnit.MILLISECONDS,
                                       new LinkedBlockingQueue<Runnable>());
     }
//只有核心线程的线程池,大小固定 (其缓冲队列是无界的)  
创建固定大小的线程池。每次提交一个任务就创建一个线程，直到线程达到线程池的最大大小。线程池的大小一旦达到最大值就会保持不变，如果某个线程因为执行异常而结束，那么线程池会补充一个新线程。  
  
CachedThreadPool：   

     public static ExecutorService newCachedThreadPool() {
         return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                       60L, TimeUnit.SECONDS,
                                       new SynchronousQueue<Runnable>());
     }
//无界线程池，可以进行自动线程回收  
创建一个可缓存的线程池。如果线程池的大小超过了处理任务所需要的线程，那么就会回收部分空闲（60秒不执行任务）的线程，当任务数增加时，此线程池又可以智能的添加新线程来处理任务。此线程池不会对线程池大小做限制，线程池大小完全依赖于操作系统（或者说JVM）能够创建的最大线程大小。SynchronousQueue是一个是缓冲区为1的阻塞队列。 
  
ScheduledThreadPool：  

    public static ExecutorService newCachedThreadPool(int corePoolSize) {
         return new ScheduledThreadPool(corePoolSize, Integer.MAX_VALUE,
                                                    DEFAULT_KEEPALIVE_MILLIS, MILLISECONDS,
                                                    new DelayedWorkQueue());
    }
                  
//创建一个大小无限的线程池。此线程池支持定时以及周期性执行任务的需求。 
创建一个周期性执行任务的线程池。如果闲置,非核心线程池会在DEFAULT_KEEPALIVE_MILLIS时间内回收。

**一些常用的方法:**

带返回结果的提交任务:
    
    void execute(Runnable command){
        ...
    }
    
    public Future<?> submit(Runnable task) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<Void> ftask = newTaskFor(task, null);
        execute(ftask);
        return ftask;
    }

    public <T> Future<T> submit(Callable<T> task) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<T> ftask = newTaskFor(task);
        execute(ftask);
        return ftask;
    }
    
    //将Runnable和Callable转化为FutureTask
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new FutureTask<T>(callable);
    }
    
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new FutureTask<T>(runnable, value);
    }
    

