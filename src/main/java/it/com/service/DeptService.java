package it.com.service;

import it.com.entity.Dept;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeptService {

    public Dept getById(Integer id) {
        log.info("进入 getById, id:{}", id);
        if (id == 1) {
            return new Dept(1, "研发一部");
        } else if (id == 2) {
            return new Dept(2, "研发二部");
        } else {
            throw new RuntimeException();
        }
    }

    public Dept getByIdSleep(Integer id) {
        log.info("进入 getByIdSleep, id:{}", id);
        try {
            log.info("执行中，3s后完成...");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (id == 1) {
            return new Dept(1, "研发一部");
        } else if (id == 2) {
            return new Dept(2, "研发二部");
        } else {
            throw new RuntimeException();
        }
    }

    public Dept getByIdSleepThrow(Integer id) {
        log.info("进入 getByIdSleepThrow, id:{}", id);
        try {
            log.info("执行中，3s后完成...");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 直接 throw
        log.info("执行出错了...");
        throw new RuntimeException("getByIdSleepThrow");
    }

    public void sendMQSleep() {
        log.info("进入 sendMQSleep");
        try {
            log.info("执行中，3s后完成...");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendMQ() {
        log.info("进入 sendMQSleep");
        try {
            log.info("执行立刻完成...");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendMQSleep(Dept dept) {
        log.info("进入 sendMQSleep, dept:{}", dept);
        try {
            log.info("执行中，3s后完成...");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}

