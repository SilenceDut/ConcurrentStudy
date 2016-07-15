#通过单例模式理解synchronized,volatile以及原子类AtomicReference
##synchronized 关键字
synchronized关键字是用来控制线程同步的，就是在多线程的环境下，控制synchronized代码段不被多个线程同时执行。是一种阻塞性的锁,
synchronized既可以加在一段代码上，也可以加在方法上。synchronized(this)及非static的synchronized方法，只能防止多个线程同
时执行`同一个对象`的同步代码段。

当synchronized锁住一个对象后，别的线程如果也想拿到这个对象的锁，就必须等待这个线程执行完成释放锁，才能再次给对象加锁，这样才
达到线程同步的目的。即使两个不同的代码段，都要锁同一个对象，那么这两个代码段也不能在多线程环境下同时运行。

所以我们在用synchronized关键字的时候，能缩小代码段的范围就尽量缩小，能在代码段上加同步就不要再整个方法上加同步。这叫减小锁的
粒度，使代码更大程度的并发。原因是基于以上的思想，锁的代码段太长了，别的线程是不是要等很久，

如果用synchronized加在`静态方法`上，就相当于用`××××.class`锁住整个方法内的代码块,此时是锁住该类的Class对象,相当于一个全局,
锁。使用synchronized修饰的方法或者代码块可以看成是一个原子操作。

一个线程执行互斥代码过程如下：

    1. 获得同步锁；

    2. 清空工作内存；

    3. 从主内存拷贝对象副本到工作内存；

    4. 执行代码(计算或者输出等)；

    5. 刷新主内存数据；

    6. 释放同步锁。

所以，synchronized既保证了多线程的并发有序性，又保证了多线程的内存可见性。

##volatile（非阻塞性的）
volatile的特性

当我们声明共享变量为volatile后，对这个变量的读/写将会很特别。理解volatile特性的一个好方法是：把对volatile变量的单个读/写，
看成是使用同一个监视器锁对这些单个读/写操作做了同步。它的工作原理是，它对写和读都是直接操作工作主存的。下面我们通过具体的示例来
说明，请看下面的示例代码：
 
    class VolatileFeaturesExample {
        volatile long vl = 0L;  //使用volatile声明64位的long型变量
        public void set(long l) {
            vl = l;   //单个volatile变量的写
        }
    
        public void getAndIncrement () {
            vl++;    //复合（多个）volatile变量的读/写
        }
    
    
        public long get() {
            return vl;   //单个volatile变量的读
        }
    }
假设有多个线程分别调用上面程序的三个方法，这个程序在语意上和下面程序等价：

    class VolatileFeaturesExample {
        long vl = 0L;               // 64位的long型普通变量
        
        public synchronized void set(long l) {     //对单个的普通 变量的写用同一个监视器同步
            vl = l;
        }
    
        public void getAndIncrement () { //普通方法调用
            long temp = get();           //调用已同步的读方法
            temp += 1L;                  //普通写操作
            set(temp);                   //调用已同步的写方法
        }
        public synchronized long get() { 
        //对单个的普通变量的读用同一个监视器同步
            return vl;
        }
    }
临界区代码的执行具有原子性。这意味着即使是64位的long型和double型变量，只要它是volatile变量，对该变量的读写就将具有原子性。

如果是多个volatile操作或类似于volatile++这种复合操作，这些操作整体上不具有原子性。

简而言之，volatile变量自身具有下列特性：

- 可见性: 对一个volatile变量的读，总是能看到（任意线程）对这个volatile变量最后的写入。
- 对 volatile 变量的写操作，不允许和它之前的读写操作打乱顺序；对 volatile 变量的读操作，不允许和它之后的读写乱序。



`volatile不能保证原子性`,原子性不是volatile来保证的,如果操作本来就具有原子性,volatile就会保证这个原子性不会被打破,`
理解为加上同步。比如上面例子中的get和set函数。以及下面这段线程不安全的singleton（单例模式）实现，尽管使用了volatile：

    public class UnSafeSingleton {
        private static volatile UnSafeSingleton sInstance = null; 
     
        private UnSafeSingleton() {}
     
        public static UnSafeSingleton getInstance() {
            if (sInstance == null) {
                sInstance = new UnSafeSingleton();
            }
            return sInstance;
        }
    }


关于原子性的理解:只对`单个操作`就有原子性,比如i++这种就不是单个操作。

>但是,轻易不要用volatile来替代synchronized来避免并发问题,因为很多原子操作自己可能并没那么了解,除非你是并发专家。《java编程思想》的作者是这样建议的。

##单例模式的应用
**先看一个"饿汉式"的模式**

    public class Singleton {  
        private static Singleton instance = new Singleton();  
        private Singleton (){}  
        public static Singleton getInstance() {  
        return instance;  
        }  
    }
这种方式比较常用，但容易产生垃圾对象。

优点：没有加锁，执行效率会提高。

缺点：类加载时就初始化，浪费内存。

它基于 Classloader 机制避免了多线程的同步问题，不过，instance 在类装载时就实例化，虽然导致类装载的原因有很多种，在单例模式
中大多数都是调用 getInstance 方法， 但是也不能确定有其他的方式（或者其他的静态方法,比如反射）导致类装载，这时候初始化 instance
 显然没有达到 lazy loading 的效果。

**静态内部类的单例模式**

    public class Singleton {  
        private static class SingletonHolder {  
        private static final Singleton INSTANCE = new Singleton();  
        }  
        private Singleton (){}  
        public static final Singleton getInstance() {  
        return SingletonHolder.INSTANCE;  
        }  
    }  

这种方式能达到双检锁方式一样的功效，但实现更简单。对静态域使用延迟初始化，应使用这种方式而不是双检锁方式。这种方式只适用于静
态域的情况，双检锁方式可在实例域需要延迟初始化时使用。
这种方式同样利用了 classloder 机制来保证初始化 instance 时只有一个线程，它跟上种方式不同的是：上种方式只要 Singleton
类被装载了，那么 instance 就会被实例化（没有达到 lazy loading 效果），而这种方式是 Singleton 类被装载了，instance 不
一定被初始化。因为 SingletonHolder 类没有被主动使用，只有显示通过调用 getInstance 方法时，才会显示装载 SingletonHolder 
类，从而实例化 instance。
以上的两种是通过静态加载来完成,但有些时候单例类需要一些传入一些参数,这些参数并没有在初始化的时候完成。

**一个双重锁定式的单例模式**

       public class Singleton {  
           private volatile static Singleton sSingleton;  
           private Singleton (){}  
           public static Singleton getSingleton() {  
           if (sSingleton == null) {  
               synchronized (Singleton.class) {  
                   if (sSingleton == null) {  
                       sSingleton = new Singleton();  
                   }  
               }  
           }  
           return sSingleton;  
           }  
       }  

讨论下volatile关键字的必要性,如果没有volatile关键字,问题可能会出在singleton = new Singleton();这句,用伪代码表示

    inst = allocat()； // 分配内存  
    sSingleton = inst;      // 赋值
    constructor(inst); // 真正执行构造函数  

可能会由于虚拟机的优化等导致赋值操作先执行,而构造函数还没完成,导致其他线程访问得到singleton变量不为null,但初始化还未完成,导致程序崩溃。

##AtomicReference是作用,无阻塞的,乐观锁
**单例模式的一种新的方式——原子操作**
这种方式在RxJava中被使用。

    public class Singleton {  
        private static final AtomicReference<Singleton> INSTANCE = new AtomicReference<Singleton>();
        
        private Singleton (){}  
        
        public static  Singleton getInstance() {  
            for (;;) {
                Singleton current = INSTANCE.get();
                if (current != null) {
                    return current;
                }
                current = new Singleton();
                if (INSTANCE.compareAndSet(null, current)) {
                    return current;
                } 
            } 
        }  
    }  

AtomicReference是作用是对"对象"进行原子操作。它是通过"volatile"和"Unsafe提供的[CAS（比较与交换，Compare and swap,
是一种有名的无锁算法函数）](http://www.ibm.com/developerworks/cn/java/j-jtp04186)实现原子操作。

(01) current是volatile类型。这保证了：当某线程修改value的值时，其他线程看到的value值都是最新的value值，即修改之后的
volatile的值。

(02) 通过CAS设置value。这保证了：当某线程池通过CAS函数(如compareAndSet函数)设置value时，它的操作是原子的，即线程在
操作value时不会被中断。

CAS是一种无阻塞的锁,采用不断比较设值的方式来避免并发问题,不会有锁的等待和上下文切换问题,性能消耗较小。

>但同样除非你非常熟悉这些Atomic**类,否则最好是使用synchronized来处理并发问题。

##其他一些相关的类Lock
synchronized是不错，但它并不完美。它有一些功能性的限制：比如它无法中断一个正在等候获得锁的线程；

####ReentrantLock

java.util.concurrent.lock 中的Lock 框架是锁定的一个抽象，它允许把锁定的实现作为 Java 类，而不是作为语言的特性来实现。
这就为Lock 的多种实现留下了空间，各种实现可能有不同的调度算法、性能特性或者锁定语义。
ReentrantLock 类实现了Lock ，它拥有与synchronized 相同的并发性和内存语义，但是添加了类似锁投票、定时锁等候和可中断
锁等候的一些特性。此外，它还提供了在激烈争用情况下更佳的性能。（换句话说，当许多线程都想访问共享资源时，JVM 可以花更少
的时候来调度线程，把更多时间用在执行线程上。）

    class Outputter1 {    
        private Lock lock = new ReentrantLock();// 锁对象    
        public void output(String name) {           
            lock.lock();      // 得到锁    
            try {    
                for(int i = 0; i < name.length(); i++) {    
                    System.out.print(name.charAt(i));    
                }    
            } finally {    
                lock.unlock();// 释放锁    
            }    
        }    
    }    

需要注意的是，用synchronized修饰的方法或者语句块在代码执行完之后锁自动释放，而是用Lock需要我们手动释放锁，所以为了保证锁最终被释
放(发生异常情况)，要把互斥区放在try内，释放锁放在finally内。

####读写锁ReadWriteLock
读-写锁定允许对共享数据进行更高级别的并发访问。虽然一次只有一个线程（writer 线程）可以修改共享数据，但在许多情况下，任何数量的线程
可以同时读取共享数据（reader 线程）




