# 并发的一些基础知识
## java内存模型
简单的讲，Java 内存模型将内存分为共享内存和本地内存。共享内存又称为堆内存，指的就是线程之间共享的内存，包含所有的实例域、静态域和数组元素。每个线程都有一个私有的，只对自己可见的内存，称之为本地内存。java内存模型中的内存结构如下图所示：

![](media/foundation.png)

共享内存中共享变量虽然由所有的线程共享，但是为了提高效率，线程并不直接使用这些变量，每个线程都会在自己的本地内存中存储一个共享内存的副本，使用这个副本参与运算。由于这个副本的参与，导致了线程之间对共享内存的读写存在可见性问题。

## 重排序
在执行程序时，为了提高性能，编译器和处理器常常会对指令做重排序，`指令重排序`包括下面三种：

- 编译器优化重排序，在不改变单线程程序语义的前提下。
- 指令级并行的重排序，如果不存在数据依赖性，处理器可以改变语句对应机器指令的执行顺序。
- 内存系统重排序，由于处理器可以使用缓存和读写缓冲区，这使得加载和存储操作看起来可能是乱序执行的。

这些重排序可能会导致多线程出现的内存可见性问题。

- 对于编译器，JMM的编译器重排序会禁止特定类型的重排序
- 对于处理器重排序，JMM的处理器重排序规则会要求java编译器在生成执行序列时，插入特定类型的内存屏障(Menory Barriers)指令，通过内存屏障指令来禁止特定类型的处理器重排序。

>JMM属于语言级的内存模型，它确保在不同的编译器和不同的处理器平台上，通过禁止特定类型的编译器重排序和处理器重排序为程序员提供一致的内存可见性保证。


## 上下文切换

首先我们知道，即使是单核的cpu也支持多线程的程序，cpu通过不停的给每个线程分配时间片来实现这个机制，这个时间片就是cpu分配给各个
线程的执行时间，由于这个时间片非常的短，所以我们感觉好像就是多个线程在同时执行一样，一般事件时间长为几十毫秒。当执行完一个时间
片后需要切换到下一个任务，在切换之前cpu需要保持现在这个线程的状态，然后再去执行下一个线程，当cpu再次切换到原来的线程时，需要
先读取之前的任务的一个状态，然后再继续执行，这样从保存到再加载就是一个上下文切换的过程。上下文的切换时需要开销的，所以并不见得
多线程就比单个线程快，而是应该根据具体的任务与硬件的配置来控制多线程的数量。线程过多可能造成CPU利用率达到100%。如果能够减少上
下文切换必然能提高程序的运行效率：

1. 无锁并发编程(比如：取模分段)

2. CAS算法，Java的Atomic包采用此算法

3. 合理使用线程

>[CAS（比较与交换，Compare and swap,是一种有名的无锁算法函数）](http://www.ibm.com/developerworks/cn/java/j-jtp04186) :对竞争资源不用加锁，而是假设没有冲突去完成某项操作，如果因为冲突失败就不断重试，直到成功为止。
以此来减少上下文切换。上面所说的循环CAS操作就是上述所说的乐观锁。
