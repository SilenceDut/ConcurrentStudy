#理解ThreadLocal
[理解ThreadLocal](http://www.jianshu.com/p/439edc2b557d)
在Android的消息机制中，Handler是非常重要的一部分，而完全要理解Handler的机制，首先应该理解ThreadLocal,关于ThreadLocal，见到很多地方叫做线程本地变量，也有些地方叫做线程本地存储，其实意思差不多。可能很多人都知道ThreadLocal为变量在每个线程中都创建了一个副本，那么每个线程可以访问自己内部的副本变量，这样的词容易让人产生误解或者迷惑。
首先，从最新的ThreadLocal源码看，ThreadLocal并未创建任何本地变量，也没有copy副本的存在，是直接用的Thread对象的成员变量，因此叫做**"线程变量帮助类"**其实更合适，它的作用就是拿到当前线程对象的Object[] value数组，然后进行存储和取值，因为这属于每个线程的内部变量数组，因此也不存在共享，所以也就没有线程安全的问题。
先看一个例子：

![ThreadLocal例子.png](http://upload-images.jianshu.io/upload_images/1437930-92f186e075c57a89.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

例子可以看出不同的线程得到的值是不同的,说明ThreadLocal可以使同一个变量在不同的线程里有不同的值，为什么同一个变量在不同的线程的会表现出不同的值呢，源码说明一切：

先看set方法：

![set(T value).png](http://upload-images.jianshu.io/upload_images/1437930-328ea1d62255c9eb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


![得到当前Thread对象的Values值.png](http://upload-images.jianshu.io/upload_images/1437930-b6ad63a97b07c233.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


可以看出ThreadLocal的Values引用直接指向Thread的localValues值。看下put()方法的实现。

![put.png](http://upload-images.jianshu.io/upload_images/1437930-d2ee1e1f2c843cb2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

很好理解，可以简单看做用单个数组来实现的简易hashmap的，hashmap的key是当前ThreadLocal对象的hash值与当前数组长度的求模运算，存入在数组的index位置，value就是当前的存入值，这个值总是放在index+1的位置，可以理解为index和index+1这两个位置就是hashmap的Entry。好像在jdk1.7之前就是用hashmap来实现的，原理都是一样的。这样是Thread类更加的轻量化。

![get()](http://upload-images.jianshu.io/upload_images/1437930-22ea3f450380786b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

通过上面的分析get函数也很好理解了。先得到当前线程对象的Values对象，然后得到Values中的Object[] table数组，从数组中取出值。