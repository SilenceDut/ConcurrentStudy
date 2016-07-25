关于Asnynck有很多的源码解读，但一些解读现在看来已经不在适用了，比如AsyncTask类必须在UI Thread当中加载，AsyncTask的对象必须在UI Thread当中实例化等一些结论都是基于以前版本的代码来解读的，现在看来已经不是这样的了。
首先有几条结论（基于android-23源码）：
- API 16 以前必须在主线程加载 AsyncTask，API 16 以后就不用了。

      因为AsynckTask主要目的是在后台执行异步任务，然后和UI线程进行交互，所以需要得到UI线程的Handler,之前的AsyncTask加载时，是得到当前加载线程的Handler,而最新的源码中，总是得到UI线程的Looper来创建和UI交互的Handler。

![](http://upload-images.jianshu.io/upload_images/1437930-96fcae5723db872f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


- 因为每个AsynckTask只能执行一次，否则会出现异常。但如果要处理多个后台任务，你需要创建多个AsynckTask并执行execute()（这样的设计真是很好吗？？？）


![](http://upload-images.jianshu.io/upload_images/1437930-79956345cac6a6c2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![](http://upload-images.jianshu.io/upload_images/1437930-0edc7db732d629ec.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


    从代码来看，AsynckTask有三种状态，就绪PENDING，运行RUNNING,结束FINISHED,所以只有
就绪状态的可以正确执行。
    AsyncTask初始化的时候创建两个**全局静态**的线程池，注意是静态的，这点很重要，由上面分析可知，每个AsynckTask只能执行一次，所有要想执行多个异步任务，只能新建一个AsynckTask对象，静态变量使线程池得以复用。
- API 4-11 默认是AsnckTask任务并发执行，API11后默认是顺序执行，任务是顺序执行，必须等一个任务结束才能执行下一个。但是可以通过executeOnExecutor（AsynckTask.THREAD_POOL_EXECUTOR）来进行并行执行任务。

![官方注释很详细](http://upload-images.jianshu.io/upload_images/1437930-59eb11a914c32b11.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![默认线程池](http://upload-images.jianshu.io/upload_images/1437930-e55c6a8b9fa5bb50.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

    THREAD_POOL_EXECUTOR是默认的并行执行任务的线程池，BlockingQueue队列的长度是128。以自己的8核手机为例，其核心线程数是9个，最大线程是17，所能最大加入的任务数是128+17=145，如果超出这个任务数，就会报出RejectExecutionException异常。这也是为什么新的API将任务默认为串行的原因。

    





