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

@Slf4j
@RestController
@RequestMapping("thenRun")
public class ThenRunController {

    @Autowired
    private DeptService deptService;

    @GetMapping("/t1")
    public R t1() {
        log.info("进入 t1");
        // 1、创建异步任务
        CompletableFuture.supplyAsync(() -> {
            log.info("进入 supplyAsync");
            Dept dept = deptService.getByIdSleep(1);
            log.info("离开 supplyAsync, dept:{}", dept);
            return dept;
        }, DefaultExecutor.getExecutorService()).thenRun(new Runnable() {
            @Override
            public void run() {
                log.info("进入 thenRun");
                deptService.sendMQSleep();
                log.info("离开 thenRun");
            }
        });
        log.info("进入 main");
        return R.ok();

        /**
         * 2023-05-11 19:41:36.233  INFO 29827 --- [nio-8080-exec-1] it.com.controller.ThenRunController      : 进入 t1
         * 2023-05-11 19:41:36.237  INFO 29827 --- [            lh1] it.com.controller.ThenRunController      : 进入 supplyAsync
         * 2023-05-11 19:41:36.238  INFO 29827 --- [nio-8080-exec-1] it.com.controller.ThenRunController      : 进入 main
         * 2023-05-11 19:41:36.238  INFO 29827 --- [            lh1] it.com.service.DeptService               : 进入 getByIdSleep, id:1
         * 2023-05-11 19:41:36.240  INFO 29827 --- [            lh1] it.com.service.DeptService               : 执行中，3s后完成...
         * 2023-05-11 19:41:39.243  INFO 29827 --- [            lh1] it.com.controller.ThenRunController      : 离开 supplyAsync, dept:Dept(id=1, name=研发一部)
         * 2023-05-11 19:41:39.244  INFO 29827 --- [            lh1] it.com.controller.ThenRunController      : 进入 thenRun
         * 2023-05-11 19:41:39.244  INFO 29827 --- [            lh1] it.com.service.DeptService               : 进入 sendMQSleep
         * 2023-05-11 19:41:39.245  INFO 29827 --- [            lh1] it.com.service.DeptService               : 执行中，3s后完成...
         * 2023-05-11 19:41:42.249  INFO 29827 --- [            lh1] it.com.controller.ThenRunController      : 离开 thenRun
         */

        /**
         * 通过日志可以看到 supplyAsync 和 thenRun 使用的是同一个线程, 并且 supplyAsync 完成后，thenRun 才能运行
         */
    }

    @GetMapping("/t2")
    public R t2() {
        log.info("进入 t2");
        // 1、创建异步任务
        CompletableFuture.supplyAsync(() -> {
            log.info("进入 supplyAsync");
            Dept dept = deptService.getById(1);
            log.info("离开 supplyAsync, dept:{}", dept);
            return dept;
        }, DefaultExecutor.getExecutorService()).thenRunAsync(new Runnable() {
            @Override
            public void run() {
                log.info("进入 thenRun");
                deptService.sendMQSleep();
                log.info("离开 thenRun");
            }
        });
        log.info("进入 main");
        return R.ok();

        /**
         * 2023-05-11 19:46:22.333  INFO 29969 --- [nio-8080-exec-2] it.com.controller.ThenRunController      : 进入 t2
         * 2023-05-11 19:46:22.337  INFO 29969 --- [            lh1] it.com.controller.ThenRunController      : 进入 supplyAsync
         * 2023-05-11 19:46:22.337  INFO 29969 --- [nio-8080-exec-2] it.com.controller.ThenRunController      : 进入 main
         * 2023-05-11 19:46:22.337  INFO 29969 --- [            lh1] it.com.service.DeptService               : 进入 getById, id:1
         * 2023-05-11 19:46:22.338  INFO 29969 --- [            lh1] it.com.controller.ThenRunController      : 离开 supplyAsync, dept:Dept(id=1, name=研发一部)
         * 2023-05-11 19:46:22.339  INFO 29969 --- [onPool-worker-1] it.com.controller.ThenRunController      : 进入 thenRun
         * 2023-05-11 19:46:22.339  INFO 29969 --- [onPool-worker-1] it.com.service.DeptService               : 进入 sendMQSleep
         * 2023-05-11 19:46:22.339  INFO 29969 --- [onPool-worker-1] it.com.service.DeptService               : 执行中，3s后完成...
         * 2023-05-11 19:46:25.343  INFO 29969 --- [onPool-worker-1] it.com.controller.ThenRunController      : 离开 thenRun
         */

        /**
         * 通过日志可以看到  supplyAsync 和 thenRun 使用的不是同一个线程, 但是 supplyAsync 完成后，thenRun 才能运行
         */
    }

    @GetMapping("/t3")
    public R t3() {
        log.info("进入 t3");
        // 1、创建异步任务
        CompletableFuture.supplyAsync(() -> {
            log.info("进入 supplyAsync");
            Dept dept = deptService.getById(1);
            log.info("离开 supplyAsync, dept:{}", dept);
            return dept;
        }, DefaultExecutor.getExecutorService()).thenRunAsync(new Runnable() {
            @Override
            public void run() {
                log.info("进入 thenRun");
                deptService.sendMQSleep();
                log.info("离开 thenRun");
            }
        }, DefaultExecutor.getExecutorService());
        log.info("进入 main");
        return R.ok();

        /**
         * 2023-05-11 19:49:42.086  INFO 30038 --- [nio-8080-exec-3] it.com.controller.ThenRunController      : 进入 t3
         * 2023-05-11 19:49:42.091  INFO 30038 --- [            lh2] it.com.controller.ThenRunController      : 进入 supplyAsync
         * 2023-05-11 19:49:42.092  INFO 30038 --- [            lh2] it.com.service.DeptService               : 进入 getById, id:1
         * 2023-05-11 19:49:42.092  INFO 30038 --- [            lh2] it.com.controller.ThenRunController      : 离开 supplyAsync, dept:Dept(id=1, name=研发一部)
         * 2023-05-11 19:49:42.096  INFO 30038 --- [nio-8080-exec-3] it.com.controller.ThenRunController      : 进入 main
         * 2023-05-11 19:49:42.096  INFO 30038 --- [            lh3] it.com.controller.ThenRunController      : 进入 thenRun
         * 2023-05-11 19:49:42.096  INFO 30038 --- [            lh3] it.com.service.DeptService               : 进入 sendMQSleep
         * 2023-05-11 19:49:42.096  INFO 30038 --- [            lh3] it.com.service.DeptService               : 执行中，3s后完成...
         * 2023-05-11 19:49:45.099  INFO 30038 --- [            lh3] it.com.controller.ThenRunController      : 离开 thenRun
         */

        /**
         * 2023-05-11 19:50:31.567  INFO 30038 --- [nio-8080-exec-4] it.com.controller.ThenRunController      : 进入 t3
         * 2023-05-11 19:50:31.570  INFO 30038 --- [nio-8080-exec-4] it.com.controller.ThenRunController      : 进入 main
         * 2023-05-11 19:50:31.570  INFO 30038 --- [            lh4] it.com.controller.ThenRunController      : 进入 supplyAsync
         * 2023-05-11 19:50:31.570  INFO 30038 --- [            lh4] it.com.service.DeptService               : 进入 getById, id:1
         * 2023-05-11 19:50:31.571  INFO 30038 --- [            lh4] it.com.controller.ThenRunController      : 离开 supplyAsync, dept:Dept(id=1, name=研发一部)
         * 2023-05-11 19:50:31.572  INFO 30038 --- [            lh5] it.com.controller.ThenRunController      : 进入 thenRun
         * 2023-05-11 19:50:31.572  INFO 30038 --- [            lh5] it.com.service.DeptService               : 进入 sendMQSleep
         * 2023-05-11 19:50:31.572  INFO 30038 --- [            lh5] it.com.service.DeptService               : 执行中，3s后完成...
         * 2023-05-11 19:50:34.576  INFO 30038 --- [            lh5] it.com.controller.ThenRunController      : 离开 thenRun
         */

        /**
         * 通过日志可以看到  supplyAsync 和 thenRun 使用的不是同一个线程, 但是 supplyAsync 完成后，thenRun 才能运行
         */
    }

    @GetMapping("/t4")
    public R t4() {
        log.info("进入 t4");
        // 1、创建异步任务
        CompletableFuture.supplyAsync(() -> {
            log.info("进入 supplyAsync");
            Dept dept = deptService.getByIdSleep(1);
            log.info("离开 supplyAsync, dept:{}", dept);
            return dept;
        }, DefaultExecutor.getExecutorService()).thenRunAsync(new Runnable() {
            @Override
            public void run() {
                log.info("进入 thenRun");
                deptService.sendMQ();
                log.info("离开 thenRun");
            }
        }, DefaultExecutor.getExecutorService());
        log.info("进入 main");
        return R.ok();

        /**
         * 2023-05-11 19:56:31.707  INFO 30184 --- [nio-8080-exec-1] it.com.controller.ThenRunController      : 进入 t4
         * 2023-05-11 19:56:31.713  INFO 30184 --- [            lh1] it.com.controller.ThenRunController      : 进入 supplyAsync
         * 2023-05-11 19:56:31.713  INFO 30184 --- [nio-8080-exec-1] it.com.controller.ThenRunController      : 进入 main
         * 2023-05-11 19:56:31.713  INFO 30184 --- [            lh1] it.com.service.DeptService               : 进入 getByIdSleep, id:1
         * 2023-05-11 19:56:31.714  INFO 30184 --- [            lh1] it.com.service.DeptService               : 执行中，3s后完成...
         * 2023-05-11 19:56:34.719  INFO 30184 --- [            lh1] it.com.controller.ThenRunController      : 离开 supplyAsync, dept:Dept(id=1, name=研发一部)
         * 2023-05-11 19:56:34.720  INFO 30184 --- [            lh2] it.com.controller.ThenRunController      : 进入 thenRun
         * 2023-05-11 19:56:34.721  INFO 30184 --- [            lh2] it.com.service.DeptService               : 进入 sendMQSleep
         * 2023-05-11 19:56:34.721  INFO 30184 --- [            lh2] it.com.service.DeptService               : 执行立刻完成...
         * 2023-05-11 19:56:37.725  INFO 30184 --- [            lh2] it.com.controller.ThenRunController      : 离开 thenRun
         */
    }
}
