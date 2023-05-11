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
@RequestMapping("runAsync")
public class RunAsyncController {

    @Autowired
    private DeptService deptService;

    @GetMapping("/t1")
    public R t1() {
        log.info("进入 t1");
        // 1、创建异步任务,无返回值
        CompletableFuture.runAsync(() -> {
            log.info("进入 runAsync");
            Dept dept = deptService.getById(1);
            log.info("离开 runAsync, dept:{}",dept);
        }, DefaultExecutor.getExecutorService());
        log.info("进入 main");
        return R.ok();

        /**
         * 2023-05-11 17:31:50.201  INFO 26306 --- [nio-8080-exec-2] it.com.controller.RunAsyncController     : 进入 t1
         * 2023-05-11 17:31:50.206  INFO 26306 --- [nio-8080-exec-2] it.com.controller.RunAsyncController     : 进入 main
         * 2023-05-11 17:31:50.206  INFO 26306 --- [            lh1] it.com.controller.RunAsyncController     : 进入 runAsync
         * 2023-05-11 17:31:50.206  INFO 26306 --- [            lh1] it.com.service.DeptService               : 进入 getById, id:1
         * 2023-05-11 17:31:50.207  INFO 26306 --- [            lh1] it.com.controller.RunAsyncController     : 离开 runAsync, dept:Dept(id=1, name=研发一部)
         */

        /**
         * 1.通过日志可以看到，没有调用 CompletableFuture#get() 方法时，不会阻塞
         */
    }

    @GetMapping("/t2")
    public R t2() throws ExecutionException, InterruptedException {
        log.info("进入 t2");
        // 1、创建异步任务,无返回值
        CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> {
            log.info("进入 runAsync");
            Dept dept = deptService.getById(1);
            log.info("离开 runAsync, dept:{}", dept);
        }, DefaultExecutor.getExecutorService());
        log.info("进入 main, 获取结果");
        Void unused = runAsync.get();
        log.info("进入 main, unused:{}", unused);
        return R.ok(unused);

        /**
         * 2023-05-11 17:34:42.089  INFO 26379 --- [nio-8080-exec-2] it.com.controller.RunAsyncController     : 进入 t2
         * 2023-05-11 17:34:42.092  INFO 26379 --- [nio-8080-exec-2] it.com.controller.RunAsyncController     : 进入 main, 获取结果
         * 2023-05-11 17:34:42.093  INFO 26379 --- [            lh1] it.com.controller.RunAsyncController     : 进入 runAsync
         * 2023-05-11 17:34:42.093  INFO 26379 --- [            lh1] it.com.service.DeptService               : 进入 getById, id:1
         * 2023-05-11 17:34:42.094  INFO 26379 --- [            lh1] it.com.controller.RunAsyncController     : 离开 runAsync, dept:Dept(id=1, name=研发一部)
         * 2023-05-11 17:34:42.094  INFO 26379 --- [nio-8080-exec-2] it.com.controller.RunAsyncController     : 进入 main, unused:null
         */

        /**
         * 1.通过日志可以看到，调用 CompletableFuture#get() 方法时，就会阻塞当前线程
         * 查看 get() 方法的源码 return reportGet((r = result) == null ? waitingGet(true) : r);
         * 可知 有值时返回，无值时等待获取
         */
    }

    @GetMapping("/t3")
    public R t3() {
        log.info("进入 t3");
        // 1、创建异步任务,无返回值
        CompletableFuture.runAsync(() -> {
            log.info("进入 runAsync");
            Dept dept = deptService.getByIdSleep(1);
            log.info("离开 runAsync, dept:{}",dept);
        }, DefaultExecutor.getExecutorService());
        log.info("进入 main");
        return R.ok();

        /**
         * 2023-05-11 17:45:00.608  INFO 26884 --- [nio-8080-exec-1] it.com.controller.RunAsyncController     : 进入 t3
         * 2023-05-11 17:45:00.612  INFO 26884 --- [nio-8080-exec-1] it.com.controller.RunAsyncController     : 进入 main
         * 2023-05-11 17:45:00.612  INFO 26884 --- [            lh1] it.com.controller.RunAsyncController     : 进入 runAsync
         * 2023-05-11 17:45:00.613  INFO 26884 --- [            lh1] it.com.service.DeptService               : 进入 getByIdSleep, id:1
         * 2023-05-11 17:45:00.615  INFO 26884 --- [            lh1] it.com.service.DeptService               : 执行中，3s后完成...
         * 2023-05-11 17:45:03.620  INFO 26884 --- [            lh1] it.com.controller.RunAsyncController     : 离开 runAsync, dept:Dept(id=1, name=研发一部)
         */

        /**
         * 1.通过日志可以看到，没有调用 CompletableFuture#get() 方法时，不会阻塞
         */
    }


    @GetMapping("/t4")
    public R t4() throws ExecutionException, InterruptedException {
        log.info("进入 t4");
        // 1、创建异步任务,无返回值
        CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> {
            log.info("进入 runAsync");
            Dept dept = deptService.getById(1);
            log.info("离开 runAsync, dept:{}", dept);
        }, DefaultExecutor.getExecutorService());
        log.info("进入 main, 获取结果");
        Void unused = runAsync.get();
        log.info("进入 main, unused:{}", unused);
        return R.ok(unused);

        /**
         * 2023-05-11 17:48:20.482  INFO 26884 --- [nio-8080-exec-5] it.com.controller.RunAsyncController     : 进入 t4
         * 2023-05-11 17:48:20.486  INFO 26884 --- [nio-8080-exec-5] it.com.controller.RunAsyncController     : 进入 main, 获取结果
         * 2023-05-11 17:48:20.486  INFO 26884 --- [            lh2] it.com.controller.RunAsyncController     : 进入 supplyAsync
         * 2023-05-11 17:48:20.487  INFO 26884 --- [            lh2] it.com.service.DeptService               : 进入 getById, id:1
         * 2023-05-11 17:48:20.487  INFO 26884 --- [            lh2] it.com.controller.RunAsyncController     : 离开 supplyAsync, dept:Dept(id=1, name=研发一部)
         * 2023-05-11 17:48:20.490  INFO 26884 --- [nio-8080-exec-5] it.com.controller.RunAsyncController     : 进入 main, unused:null
         */
    }
}
