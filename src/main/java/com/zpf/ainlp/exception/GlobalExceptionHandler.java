package com.zpf.ainlp.exception;

import com.zpf.ainlp.bean.ResultObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;

@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResultObject handleSystem(BusinessException ex) {
        logger.error(">>>>>>>>>{}==>>>", getStackTrace(ex));
        return new ResultObject(ex.getErrorCode().getCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResultObject handleException(Exception e) {
        logger.error(">>>>>>>>>{}==>>>{}>>>>>>>", ErrorCode.SERVER_ERROR.getMessage(), getStackTrace(e));
        return new ResultObject(ErrorCode.SERVER_ERROR.getCode(), ErrorCode.SERVER_ERROR.getMessage());
    }


    /**
     * GET/POST请求方法错误的拦截器
     * 因为开发时可能比较常见,而且发生在进入controller之前,上面的拦截器拦截不到这个错误
     * 所以定义了这个拦截器
     *
     * @return
     * @throws Exception
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultObject httpRequestMethodHandler(Exception e) throws Exception {
        logger.error(">>>>>>>>>{}==>>>", getStackTrace(e));
        return new ResultObject(ErrorCode.METHOD_ERROR.getCode(), ErrorCode.METHOD_ERROR.getMessage());

    }

    /**
     * 获得堆栈信息
     *
     * @param e
     * @return
     */
    private String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] trace = e.getStackTrace();
        for (StackTraceElement traceElement : trace) {
            sb.append("\tat ").append(traceElement).append("\n");
        }
        sb.append(e.toString());
        return sb.toString();
    }
}
