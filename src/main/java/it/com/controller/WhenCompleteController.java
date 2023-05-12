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
import java.util.function.BiConsumer;

@Slf4j
@RestController
@RequestMapping("whenComplete")
public class WhenCompleteController {

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
        }, DefaultExecutor.getExecutorService()).whenComplete((dept, throwable) -> {
            log.info("进入 whenComplete");
            deptService.sendMQSleep(dept);
            log.info("离开 whenComplete, dept:{}", dept);
        });
        log.info("进入 main,获取结果");
        Dept result = future.get();
        log.info("进入 main,获取结果， result:{}",result);
        return R.ok();

        /**
         * 2023-05-12 08:02:59.380  INFO 36962 --- [nio-8080-exec-1] i.com.controller.WhenCompleteController  : 进入 t1
         * 2023-05-12 08:02:59.385  INFO 36962 --- [            lh1] i.com.controller.WhenCompleteController  : 进入 supplyAsync
         * 2023-05-12 08:02:59.385  INFO 36962 --- [nio-8080-exec-1] i.com.controller.WhenCompleteController  : 进入 main,获取结果
         * 2023-05-12 08:02:59.385  INFO 36962 --- [            lh1] it.com.service.DeptService               : 进入 getByIdSleep, id:1
         * 2023-05-12 08:02:59.386  INFO 36962 --- [            lh1] it.com.service.DeptService               : 执行中，3s后完成...
         * 2023-05-12 08:03:02.391  INFO 36962 --- [            lh1] i.com.controller.WhenCompleteController  : 离开 supplyAsync, dept:Dept(id=1, name=研发一部)
         * 2023-05-12 08:03:02.392  INFO 36962 --- [            lh1] i.com.controller.WhenCompleteController  : 进入 whenComplete
         * 2023-05-12 08:03:02.392  INFO 36962 --- [            lh1] it.com.service.DeptService               : 进入 sendMQSleep, dept:Dept(id=1, name=研发一部)
         * 2023-05-12 08:03:02.392  INFO 36962 --- [            lh1] it.com.service.DeptService               : 执行中，3s后完成...
         * 2023-05-12 08:03:05.395  INFO 36962 --- [            lh1] i.com.controller.WhenCompleteController  : 离开 whenComplete, dept:Dept(id=1, name=研发一部)
         * 2023-05-12 08:03:05.396  INFO 36962 --- [nio-8080-exec-1] i.com.controller.WhenCompleteController  : 进入 main,获取结果， result:Dept(id=1, name=研发一部)
         */

        /**
         * 和 thenAccept 类似，没看出来两个的区别，但看其入参有 throwable，难道它可以对任务A的异常结果进行处理吗？用下一个接口测试下。
         */
    }

    @GetMapping("/t2")
    public R t2() throws ExecutionException, InterruptedException {
        log.info("进入 t2");
        // 1、创建异步任务
        CompletableFuture<Dept> future = CompletableFuture.supplyAsync(() -> {
            log.info("进入 supplyAsync");
            Dept dept = deptService.getByIdSleepThrow(1);
            log.info("离开 supplyAsync, dept:{}", dept);
            return dept;
        }, DefaultExecutor.getExecutorService()).whenComplete((dept, throwable) -> {
            log.info("进入 whenComplete");
            if(throwable == null){
                log.info("进入 whenComplete, 没有异常");
                deptService.sendMQSleep(dept);
                log.info("离开 whenComplete, dept:{}", dept);
            }else{
                log.info("进入 whenComplete, 存在异常");
                deptService.sendMQSleep(dept);
                log.info("离开 whenComplete, dept:{}", dept);
            }
        });
        log.info("进入 main,获取结果");
        Dept result = future.get();
        log.info("进入 main,获取结果， result:{}", result);
        return R.ok();

        /**
         * 2023-05-12 08:14:21.214  INFO 37580 --- [nio-8080-exec-2] i.com.controller.WhenCompleteController  : 进入 t2
         * 2023-05-12 08:14:21.218  INFO 37580 --- [            lh1] i.com.controller.WhenCompleteController  : 进入 supplyAsync
         * 2023-05-12 08:14:21.218  INFO 37580 --- [nio-8080-exec-2] i.com.controller.WhenCompleteController  : 进入 main,获取结果
         * 2023-05-12 08:14:21.218  INFO 37580 --- [            lh1] it.com.service.DeptService               : 进入 getByIdSleepThrow, id:1
         * 2023-05-12 08:14:21.220  INFO 37580 --- [            lh1] it.com.service.DeptService               : 执行中，3s后完成...
         * 2023-05-12 08:14:24.221  INFO 37580 --- [            lh1] it.com.service.DeptService               : 执行出错了...
         * 2023-05-12 08:14:24.222  INFO 37580 --- [            lh1] i.com.controller.WhenCompleteController  : 进入 whenComplete
         * 2023-05-12 08:14:24.223  INFO 37580 --- [            lh1] i.com.controller.WhenCompleteController  : 进入 whenComplete, 存在异常
         * 2023-05-12 08:14:24.223  INFO 37580 --- [            lh1] it.com.service.DeptService               : 进入 sendMQSleep, dept:null
         * 2023-05-12 08:14:24.223  INFO 37580 --- [            lh1] it.com.service.DeptService               : 执行中，3s后完成...
         * 2023-05-12 08:14:27.229  INFO 37580 --- [            lh1] i.com.controller.WhenCompleteController  : 离开 whenComplete, dept:null
         * 2023-05-12 08:14:27.258 ERROR 37580 --- [nio-8080-exec-2] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is java.util.concurrent.ExecutionException: java.lang.RuntimeException] with root cause
         *
         * java.lang.RuntimeException: null
         * 	at it.com.service.DeptService.getByIdSleepThrow(DeptService.java:49) ~[classes/:na]
         * 	at it.com.controller.WhenCompleteController.lambda$t2$2(WhenCompleteController.java:69) ~[classes/:na]
         * 	at java.util.concurrent.CompletableFuture$AsyncSupply.run(CompletableFuture.java:1590) ~[na:1.8.0_202]
         * 	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149) ~[na:1.8.0_202]
         * 	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624) ~[na:1.8.0_202]
         * 	at java.lang.Thread.run(Thread.java:748) [na:1.8.0_202]
         */

        /**
         * 看来 whenComplete 是能拿到上一步的执行异常的，你可以根据是否有异常来进行不同的操作。
         */
    }


}
