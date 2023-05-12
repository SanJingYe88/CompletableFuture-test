package it.com.controller;

import it.com.entity.Dept;
import it.com.entity.R;
import it.com.executor.DefaultExecutor;
import it.com.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Slf4j
@RestController
@RequestMapping("thenApply")
public class ThenApplyController {

    @Autowired
    private DeptService deptService;

    @GetMapping("/t1")
    public R t1() throws ExecutionException, InterruptedException {
        log.info("进入 t1");
        // 1、创建异步任务
        CompletableFuture<Dept> future = CompletableFuture.supplyAsync(() -> {
            log.info("进入 supplyAsync");
            Dept dept = deptService.getByIdSleep(1);
            log.info("离开 supplyAsync, dept:{}", dept);
            return dept;
        }, DefaultExecutor.getExecutorService()).thenApply(dept -> {
            log.info("进入 thenApply");
            Dept dept2 = deptService.getByIdSleep(dept.getId());
            log.info("离开 thenApply, dept2:{}", dept2);
            return dept2;
        });
        log.info("进入 main,获取结果");
        Dept result = future.get();
        log.info("进入 main,获取结果， result:{}",result);
        return R.ok();

        /**
         * 2023-05-12 07:44:47.284  INFO 36354 --- [nio-8080-exec-2] it.com.controller.ThenApplyController    : 进入 t1
         * 2023-05-12 07:44:47.289  INFO 36354 --- [            lh1] it.com.controller.ThenApplyController    : 进入 supplyAsync
         * 2023-05-12 07:44:47.289  INFO 36354 --- [nio-8080-exec-2] it.com.controller.ThenApplyController    : 进入 main,获取结果
         * 2023-05-12 07:44:47.289  INFO 36354 --- [            lh1] it.com.service.DeptService               : 进入 getByIdSleep, id:1
         * 2023-05-12 07:44:47.291  INFO 36354 --- [            lh1] it.com.service.DeptService               : 执行中，3s后完成...
         * 2023-05-12 07:44:50.291  INFO 36354 --- [            lh1] it.com.controller.ThenApplyController    : 离开 supplyAsync, dept:Dept(id=1, name=研发一部)
         * 2023-05-12 07:44:50.292  INFO 36354 --- [            lh1] it.com.controller.ThenApplyController    : 进入 thenApply
         * 2023-05-12 07:44:50.292  INFO 36354 --- [            lh1] it.com.service.DeptService               : 进入 getByIdSleep, id:1
         * 2023-05-12 07:44:50.292  INFO 36354 --- [            lh1] it.com.service.DeptService               : 执行中，3s后完成...
         * 2023-05-12 07:44:53.295  INFO 36354 --- [            lh1] it.com.controller.ThenApplyController    : 离开 thenApply, dept2:Dept(id=1, name=研发一部)
         * 2023-05-12 07:44:53.295  INFO 36354 --- [nio-8080-exec-2] it.com.controller.ThenApplyController    : 进入 main,获取结果， result:Dept(id=1, name=研发一部)
         */

        /**
         * 1.thenApply 可以获取上一个任务的结果，并异步执行自己的任务，并返回结果
         * 2.thenApply 用的是 supplyAsync 同一个线程
         * 3.future.get() 会阻塞当前线程
         */
    }

    @GetMapping("/t2")
    public R t2() throws ExecutionException, InterruptedException {
        log.info("进入 t2");
        // 1、创建异步任务
        CompletableFuture<Dept> future = CompletableFuture.supplyAsync(() -> {
            log.info("进入 supplyAsync");
            Dept dept = deptService.getByIdSleep(1);
            log.info("离开 supplyAsync, dept:{}", dept);
            return dept;
        }, DefaultExecutor.getExecutorService()).thenApplyAsync(dept -> {
            log.info("进入 thenApply");
            Dept dept2 = deptService.getByIdSleep(dept.getId());
            log.info("离开 thenApply, dept2:{}", dept2);
            return dept2;
        });
        log.info("进入 main,获取结果");
        Dept result = future.get();
        log.info("进入 main,获取结果， result:{}",result);
        return R.ok();

        /**
         * 2023-05-12 07:50:09.328  INFO 36571 --- [nio-8080-exec-2] it.com.controller.ThenApplyController    : 进入 t2
         * 2023-05-12 07:50:09.333  INFO 36571 --- [            lh1] it.com.controller.ThenApplyController    : 进入 supplyAsync
         * 2023-05-12 07:50:09.333  INFO 36571 --- [nio-8080-exec-2] it.com.controller.ThenApplyController    : 进入 main,获取结果
         * 2023-05-12 07:50:09.333  INFO 36571 --- [            lh1] it.com.service.DeptService               : 进入 getByIdSleep, id:1
         * 2023-05-12 07:50:09.334  INFO 36571 --- [            lh1] it.com.service.DeptService               : 执行中，3s后完成...
         * 2023-05-12 07:50:12.334  INFO 36571 --- [            lh1] it.com.controller.ThenApplyController    : 离开 supplyAsync, dept:Dept(id=1, name=研发一部)
         * 2023-05-12 07:50:12.336  INFO 36571 --- [onPool-worker-1] it.com.controller.ThenApplyController    : 进入 thenApply
         * 2023-05-12 07:50:12.336  INFO 36571 --- [onPool-worker-1] it.com.service.DeptService               : 进入 getByIdSleep, id:1
         * 2023-05-12 07:50:12.336  INFO 36571 --- [onPool-worker-1] it.com.service.DeptService               : 执行中，3s后完成...
         * 2023-05-12 07:50:15.338  INFO 36571 --- [onPool-worker-1] it.com.controller.ThenApplyController    : 离开 thenApply, dept2:Dept(id=1, name=研发一部)
         * 2023-05-12 07:50:15.339  INFO 36571 --- [nio-8080-exec-2] it.com.controller.ThenApplyController    : 进入 main,获取结果， result:Dept(id=1, name=研发一部)
         */

        /**
         * 1.thenApply 可以获取上一个任务的结果，并异步执行自己的任务，并返回结果
         * 2.thenApply 用的是 supplyAsync 不同的线程
         * 3.future.get() 会阻塞当前线程
         */
    }


    @GetMapping("/t3")
    public R t3() throws ExecutionException, InterruptedException {
        log.info("进入 t3");
        // 1、创建异步任务
        CompletableFuture<Dept> future = CompletableFuture.supplyAsync(() -> {
            log.info("进入 supplyAsync");
            Dept dept = deptService.getByIdSleep(1);
            log.info("离开 supplyAsync, dept:{}", dept);
            return dept;
        }, DefaultExecutor.getExecutorService()).thenApplyAsync(dept -> {
            log.info("进入 thenApply");
            Dept dept2 = deptService.getByIdSleep(dept.getId());
            log.info("离开 thenApply, dept2:{}", dept2);
            return dept2;
        }, DefaultExecutor.getExecutorService());
        log.info("进入 main,获取结果");
        Dept result = future.get();
        log.info("进入 main,获取结果， result:{}",result);
        return R.ok();

        /**
         * 2023-05-12 07:52:55.796  INFO 36652 --- [nio-8080-exec-2] it.com.controller.ThenApplyController    : 进入 t3
         * 2023-05-12 07:52:55.802  INFO 36652 --- [            lh1] it.com.controller.ThenApplyController    : 进入 supplyAsync
         * 2023-05-12 07:52:55.802  INFO 36652 --- [nio-8080-exec-2] it.com.controller.ThenApplyController    : 进入 main,获取结果
         * 2023-05-12 07:52:55.802  INFO 36652 --- [            lh1] it.com.service.DeptService               : 进入 getByIdSleep, id:1
         * 2023-05-12 07:52:55.803  INFO 36652 --- [            lh1] it.com.service.DeptService               : 执行中，3s后完成...
         * 2023-05-12 07:52:58.804  INFO 36652 --- [            lh1] it.com.controller.ThenApplyController    : 离开 supplyAsync, dept:Dept(id=1, name=研发一部)
         * 2023-05-12 07:52:58.805  INFO 36652 --- [            lh2] it.com.controller.ThenApplyController    : 进入 thenApply
         * 2023-05-12 07:52:58.805  INFO 36652 --- [            lh2] it.com.service.DeptService               : 进入 getByIdSleep, id:1
         * 2023-05-12 07:52:58.805  INFO 36652 --- [            lh2] it.com.service.DeptService               : 执行中，3s后完成...
         * 2023-05-12 07:53:01.806  INFO 36652 --- [            lh2] it.com.controller.ThenApplyController    : 离开 thenApply, dept2:Dept(id=1, name=研发一部)
         * 2023-05-12 07:53:01.806  INFO 36652 --- [nio-8080-exec-2] it.com.controller.ThenApplyController    : 进入 main,获取结果， result:Dept(id=1, name=研发一部)
         */

        /**
         * 1.thenApply 可以获取上一个任务的结果，并异步执行自己的任务，并返回结果
         * 2.thenApply 用的是 supplyAsync 不同的线程
         * 3.future.get() 会阻塞当前线程
         */
    }
}
