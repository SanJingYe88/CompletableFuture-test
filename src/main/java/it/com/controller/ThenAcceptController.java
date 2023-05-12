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
import java.util.function.Consumer;

@Slf4j
@RestController
@RequestMapping("thenAccept")
public class ThenAcceptController {

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
        }, DefaultExecutor.getExecutorService()).thenAccept(new Consumer<Dept>() {
            @Override
            public void accept(Dept dept) {
                log.info("进入 thenAccept");
                deptService.sendMQSleep(dept);
                log.info("离开 thenAccept");
            }
        });
        log.info("进入 main");
        return R.ok();

        /**
         * 2023-05-11 22:47:23.947  INFO 34715 --- [nio-8080-exec-1] it.com.controller.ThenAcceptController   : 进入 t1
         * 2023-05-11 22:47:23.952  INFO 34715 --- [            lh1] it.com.controller.ThenAcceptController   : 进入 supplyAsync
         * 2023-05-11 22:47:23.952  INFO 34715 --- [nio-8080-exec-1] it.com.controller.ThenAcceptController   : 进入 main
         * 2023-05-11 22:47:23.952  INFO 34715 --- [            lh1] it.com.service.DeptService               : 进入 getByIdSleep, id:1
         * 2023-05-11 22:47:23.954  INFO 34715 --- [            lh1] it.com.service.DeptService               : 执行中，3s后完成...
         * 2023-05-11 22:47:26.958  INFO 34715 --- [            lh1] it.com.controller.ThenAcceptController   : 离开 supplyAsync, dept:Dept(id=1, name=研发一部)
         * 2023-05-11 22:47:26.959  INFO 34715 --- [            lh1] it.com.controller.ThenAcceptController   : 进入 thenAccept
         * 2023-05-11 22:47:26.959  INFO 34715 --- [            lh1] it.com.service.DeptService               : 进入 sendMQSleep, dept:Dept(id=1, name=研发一部)
         * 2023-05-11 22:47:26.959  INFO 34715 --- [            lh1] it.com.service.DeptService               : 执行中，3s后完成...
         * 2023-05-11 22:47:29.961  INFO 34715 --- [            lh1] it.com.controller.ThenAcceptController   : 离开 thenAccept
         */

        /**
         * 可以看到 supplyAsync 和 thenAccept 使用相同的线程
         */
    }

    @GetMapping("/t2")
    public R t2() {
        log.info("进入 t2");
        // 1、创建异步任务
        CompletableFuture.supplyAsync(() -> {
            log.info("进入 supplyAsync");
            Dept dept = deptService.getByIdSleep(1);
            log.info("离开 supplyAsync, dept:{}", dept);
            return dept;
        }, DefaultExecutor.getExecutorService()).thenAcceptAsync(new Consumer<Dept>() {
            @Override
            public void accept(Dept dept) {
                log.info("进入 thenAcceptAsync");
                deptService.sendMQSleep(dept);
                log.info("离开 thenAcceptAsync");
            }
        },DefaultExecutor.getExecutorService());
        log.info("进入 main");
        return R.ok();

        /**
         * 2023-05-11 22:54:56.101  INFO 34919 --- [nio-8080-exec-1] it.com.controller.ThenAcceptController   : 进入 t2
         * 2023-05-11 22:54:56.106  INFO 34919 --- [            lh1] it.com.controller.ThenAcceptController   : 进入 supplyAsync
         * 2023-05-11 22:54:56.107  INFO 34919 --- [nio-8080-exec-1] it.com.controller.ThenAcceptController   : 进入 main
         * 2023-05-11 22:54:56.106  INFO 34919 --- [            lh1] it.com.service.DeptService               : 进入 getByIdSleep, id:1
         * 2023-05-11 22:54:56.107  INFO 34919 --- [            lh1] it.com.service.DeptService               : 执行中，3s后完成...
         * 2023-05-11 22:54:59.110  INFO 34919 --- [            lh1] it.com.controller.ThenAcceptController   : 离开 supplyAsync, dept:Dept(id=1, name=研发一部)
         * 2023-05-11 22:54:59.110  INFO 34919 --- [            lh2] it.com.controller.ThenAcceptController   : 进入 thenAcceptAsync
         * 2023-05-11 22:54:59.111  INFO 34919 --- [            lh2] it.com.service.DeptService               : 进入 sendMQSleep, dept:Dept(id=1, name=研发一部)
         * 2023-05-11 22:54:59.111  INFO 34919 --- [            lh2] it.com.service.DeptService               : 执行中，3s后完成...
         * 2023-05-11 22:55:02.111  INFO 34919 --- [            lh2] it.com.controller.ThenAcceptController   : 离开 thenAcceptAsync
         */

        /**
         * 可以看到 supplyAsync 和 thenAccept 使用不同的线程
         */
    }
}
