package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }


    /***
     * 若重名--捕获异常处理
     * @return
     */
    @ExceptionHandler
    public Result exceptionHadnler(SQLIntegrityConstraintViolationException ex){
        log.info("异常信息：{}",ex.getMessage());
        //Duplicate entry 'duanjinjin' for key 'employee.idx_username'
        String msg = ex.getMessage();
        if(msg.contains("Duplicate entry")){
            String split[] = msg.split(" ");  //根据空格分割异常信息
            String s = split[2];
            String re = s + MessageConstant.ALREADY_EXISTS;
            return Result.error(re);
        }else{
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }

    }

}
