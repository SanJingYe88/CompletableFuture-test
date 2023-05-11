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
    }
}
