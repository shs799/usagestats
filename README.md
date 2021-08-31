# usagestats

**If you want an applicationlication can get application-usage information, and protect your privacy, this it is.**

### How to set up it?

1. If version>=1.3, this applicationlication will notice "no-data" message, click get-permission.
(if no permission, this applicationlication will show blanklist.)
(if version<=1.2 and no permission, you should grant the usage_access_permission manually.)
2. Find this applicationlication click it and turn switch on

### How to use it after set up?

1. Click round-gear-button 
2. Select a range 
3. Click applicationly
4. Wait a moment, you can get the list 
5. Select an applicationlication. The usage statistics information will be shown.

### What is the advantage about this applicationlication?

There are so many application in app-store. They are more powerful then this. However, nobody knows whether they Compliance their privacy information.

In addition, there are so many advertisement in this application. It is annoying.

We think if applicationlication can access important privacy information, it should not access Internet.

This applicationlication has **NO-ACCESS-INTERNET-PERMISSION**, **NO-ADVERTISEMENT**, **NO IN-application-PURCHASES**.

It is OPEN-SOURCE. Pull-request are welcome.

# Interesting component

### IntVMap

IntVMap is a Integer-to-Object-Map based on SparseArrayCompat. It implemented the MutableMap interface, So you can downgrade to SparseArrayCompat faster. 
It is based on AndroidX package, so it can run everywhere.

**Iot of bug exist**. Reporting are welcome.

```kotlin
//downgrade tutorial from ArrayMap to IntVMap
//from:
val map:MutableMap<Int,V>=ArrayMap<Int,V>()
//to:
val map:MutableMap<Int,V>=IntVMap<V>()

//bind exist SparseArrayCompat to MutableMap
val sparseArrayCompat=SparseArrayCompat()
val map:MutableMap<Int,V>=IntVMap<V>(sparseArrayCompat)
```

### MapDowngrade

Before:

1. Parallel compute in ConcurrentMap.
2. PutAll to a slim-Map such as ArrayMap.(It is too slow)
3. Get or Put.

After:

1. Parallel compute in ConcurrentMap.
2. PutAll to a slim-Map such as ArrayMap. (when transfering, read operation is available,but write operation will wait after transfer finish.)
3. Get or Put.
