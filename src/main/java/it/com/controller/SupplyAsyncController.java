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
@RequestMapping("supplyAsync")
public class SupplyAsyncController {

    @Autowired
    private DeptService deptService;

    @GetMapping("/t1")
    public R t1() throws ExecutionException, InterruptedException {
        log.info("进入 t1");
        // 1、创建异步任务，并返回future
        CompletableFuture<Dept> deptCompletableFuture = CompletableFuture.supplyAsync(() -> {
            log.info("进入 supplyAsync");
            return deptService.getById(1);
        }, DefaultExecutor.getExecutorService());
        log.info("进入 main, 获取结果");
        // 2、同步等待异步任务执行结果，此处是阻塞等待
        Dept dept = deptCompletableFuture.get();
        log.info("进入 main, dept:{}", dept);
        return R.ok(dept);

        /**
         * 2023-05-11 17:17:52.781  INFO 25956 --- [nio-8080-exec-2] it.com.controller.SupplyAsyncController  : 进入 t1
         * 2023-05-11 17:17:52.785  INFO 25956 --- [            lh1] it.com.controller.SupplyAsyncController  : 进入 supplyAsync
         * 2023-05-11 17:17:52.785  INFO 25956 --- [nio-8080-exec-2] it.com.controller.SupplyAsyncController  : 进入 main, 获取结果
         * 2023-05-11 17:17:52.785  INFO 25956 --- [            lh1] it.com.service.DeptService               : 进入 getById, id:1
         * 2023-05-11 17:17:52.787  INFO 25956 --- [nio-8080-exec-2] it.com.controller.SupplyAsyncController  : 进入 main, dept:Dept(id=1, name=研发一部)
         */

        /**
         * 1.猜测，从日志输出顺序来看，即使 supplyAsync 开启了新的线程[lh1]，但是会阻塞线程[nio-8080-exec-2]的执行
         */
    }

    @GetMapping("/t2")
    public R t2() throws ExecutionException, InterruptedException {
        log.info("进入 t2");
        // 1、创建异步任务，并返回future
        CompletableFuture<Dept> deptCompletableFuture = CompletableFuture.supplyAsync(() -> {
            log.info("进入 supplyAsync");
            // 睡眠3s
            return deptService.getByIdSleep(1);
        }, DefaultExecutor.getExecutorService());
        log.info("进入 main, 获取结果");
        // 2、同步等待异步任务执行结果，此处是阻塞等待
        Dept dept = deptCompletableFuture.get();
        log.info("进入 main, dept:{}", dept);
        return R.ok(dept);

        /**
         * 2023-05-11 17:25:01.257  INFO 26128 --- [nio-8080-exec-1] it.com.controller.SupplyAsyncController  : 进入 t2
         * 2023-05-11 17:25:01.262  INFO 26128 --- [nio-8080-exec-1] it.com.controller.SupplyAsyncController  : 进入 main, 获取结果
         * 2023-05-11 17:25:01.262  INFO 26128 --- [            lh1] it.com.controller.SupplyAsyncController  : 进入 supplyAsync
         * 2023-05-11 17:25:01.262  INFO 26128 --- [            lh1] it.com.service.DeptService               : 进入 getByIdSleep, id:1
         * 2023-05-11 17:25:01.263  INFO 26128 --- [            lh1] it.com.service.DeptService               : 执行中，3s后完成...
         * 2023-05-11 17:25:04.268  INFO 26128 --- [nio-8080-exec-1] it.com.controller.SupplyAsyncController  : 进入 main, dept:Dept(id=1, name=研发一部)
         */

        /**
         * 1.确定，从日志输出顺序来看，即使 supplyAsync 开启了新的线程[lh1]，但是会阻塞线程[nio-8080-exec-2]的执行
         */
    }
}
