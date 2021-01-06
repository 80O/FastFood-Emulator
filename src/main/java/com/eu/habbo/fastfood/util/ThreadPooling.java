package com.eu.habbo.fastfood.util;

import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ThreadPooling
{
    public final int threads;
    private final ScheduledExecutorService scheduledPool;
    private volatile boolean canAdd;

    public ThreadPooling(Integer threads)
    {
        System.out.println("[Thread Pool] Initializing...!");
        this.threads = threads;
        System.out.println("[Thread Pool] Using " + this.threads + " threads!");
        this.scheduledPool = Executors.newScheduledThreadPool(this.threads, new DefaultThreadFactory("ArcturusThreadFactory"));
        this.canAdd = true;
        System.out.println("[Thread Pool] Loaded!");
    }

    public void run(Runnable run)
    {
        try
        {
            if (this.canAdd)
            {
                this.run(run, 0);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public ScheduledFuture run(Runnable run, long delay)
    {
        try
        {
            if (this.canAdd)
            {
                return this.scheduledPool.schedule(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            run.run();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, delay, TimeUnit.MILLISECONDS);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public void shutDown()
    {
        this.canAdd = false;

        this.scheduledPool.shutdownNow();
        while(!this.scheduledPool.isTerminated()) {
        }
    }

    public ScheduledExecutorService getService()
    {
        return this.scheduledPool;
    }
}
