package com.example.coroutinesinkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import kotlinx.coroutines.*


/*
=====================COROUTINES IN KOTLIN==================================

A thread decribes in which context a set of instructions should be executed
Each thread executes its own instructions

Threading -> main thread executes everything in an app generally, but when
many things happen in a single thread, it's UI is also needed to update in
the same thread, which might cause ambiguity in the functioning of the app

Suppose we made a network call after which we need to update the ui, so
while the network call is being made we need to freeze the UI till we get
a result from the network call and no other instructions are executed.

So to handle this we should make the network call on a separate thread

Coroutines are executed inside a thread. we can launch multiple coroutines
inside a thread

Coroutines are suspendable -> we can start a coroutine, execute its instructions
and then pause it in the middle of execution wheneven we want to and the we
can continue the execution afterwards, but threads cannot do this

A coroutiune started in one thread can switch theread it is running in
So coroutines are basically lightweight threads

On Android, coroutines help to manage long-running tasks that might otherwise
block the main thread and cause your app to become unresponsive. So it is used for
asynchronous programming in Android
 */

class MainActivity : AppCompatActivity() {

    val TAG = "CoroutineInfo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv: TextView = findViewById(R.id.Dummy)

        // launch a basic coroutine
        GlobalScope.launch {
            // Any coroutine started in GlobalScope means that this coroutine will live as long as our app does
            delay(3000) // just like sleep() function of threads, coroutines
            /* have delay function which suspends the execution of coroutines till a particular time
            delay only pauses the current coroutines but will not block the whole thread
            whereas sleep() function pauses the whole thread
             */
            Log.d(TAG, "Coroutine Says Hello from thread ${Thread.currentThread().name}")
        } // executed on separate thread

        // executed on main thread
        Log.d("CoroutineInfo", "Hello from the Thread: ${Thread.currentThread().name}")
        // All coroutines are cancelled when the main thread dies or when the app is finished


        /*
        Suspend functions -> they can only be executed/called from another suspend function or inside a
        coroutine.
        Eg: delay() is a suspend function and can only be called inside a coroutine or inside a suspend function
         */
        GlobalScope.launch {
            val networkCall1 = pseudoNetworkCall1()
            val networkCall2 = pseudoNetworkCall2()
            Log.d(TAG, networkCall1)
            Log.d(TAG,networkCall2)
            /*
            since netowrk call 1 and network call 2 are being executed in the same thread, and both of then have
            a delay of 5 seconds, so the delay time for both the suspend functions would add up which means
            for 10 seconds nothing will get printed on the Logcat window.
             */
        }

        /*
        Coroutine Contexts -> this describes the context in which the coroutine will be started
         */
        GlobalScope.launch(Dispatchers.IO) {
            /* => Depending upon the use of coroutine, we pass different kinds on Dispatchers in launch()
            function:
            1. Dispatchers.Main -> starts a coroutine on main thread, meant for UI operations bcz UI can only
            be modified or updated from the main thread
            2. Dispatchers.IO -> for data operations like network operations, reading and writing to files,
            database etc.
            3. Dispatchers.Default -> meant for complex and long running operations so that the main thread
            does'nt get blocked.
            4. Dispatchers.Unconfined -> not confined to a specific thread, a suspend function called from a
            coroutine in Dispatchers.Unconfined context is not limited to a particular thread
            5. newSingleThreadContext()  -> lets us create a thread with our own context and also lets us
            give a custom name to our thread

            => Coroutine Context can be switched form within a coroutine
             */

            Log.d(TAG,"Starting coroutine in thread: ${Thread.currentThread().name}")

            val ansOfNetworkCall = pseudoNetworkCall1()
            // switching context of our coroutine from within a coroutine
            withContext(Dispatchers.Main) {
               Log.d(TAG,"Setting text to textView in thread: ${Thread.currentThread().name}")
               tv.text = ansOfNetworkCall
            }
        }

        /*
        => runBlocking -> it is a function that will start a coroutine in the main thread and also
        block the main thread
        it is similar to Thread.sleep() but inside runBlocking {} we can call suspend functions also
         */
        Log.d(TAG, "Before RunBlocking")
        runBlocking {
            Log.d(TAG, "Start of RunBlocking")
            delay(5000L)
            Log.d(TAG, "End of RunBlocking")

            //we can also launch another coroutine inside runBlocking
            // RunBlocking Thread 1
            launch (Dispatchers.IO) {
                delay(3000L)
                Log.d(TAG, "Finished IO coroutine 1")
            }
            // RunBlocking Thread 2
            launch (Dispatchers.IO) {
                delay(3000L)
                Log.d(TAG, "Finished IO coroutine 2")
            }

            // The delay in RunBlocking Thread 1 and thread2 won't addup because they are in 2 different threads
            // and both threads will execute simultaneously
        }
        Log.d(TAG, "After RunBlocking")
    }

    suspend fun pseudoNetworkCall1() : String {
        delay(5000)
        return "Pseudo Network Call 1 Completed!"
    }

    suspend fun pseudoNetworkCall2() : String {
        delay(5000)
        return "Pseudo Network Call 2 Completed!"
    }
}