package com.lueing.oh.aop;


import com.lueing.oh.commons.exception.BusinessException;
import com.lueing.oh.commons.standard.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author yesido0725@gmail.com
 * @date 2022/5/30 17:06
 */
@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    private static final String DEFAULT_MESSAGE = "未确定的异常信息";

    private static String getMessage(Exception e) {
        String message = e.getMessage();
        if (!StringUtils.hasLength(message)) {
            message = DEFAULT_MESSAGE;
        }
        return message;
    }

    private static StackTraceElement getRootErrorInfo(Exception e) {
        StackTraceElement data = null;
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace != null && stackTrace.length != 0) {
            data = stackTrace[0];
        }
        return data;
    }

    @ExceptionHandler({BusinessException.class, NullPointerException.class})
    public RestResponse<StackTraceElement> handleException(BusinessException e) {
        log.error("An business exception occurred.", e);

        RestResponse<StackTraceElement> restResponse = new RestResponse<>();
        int code = e.getCode() == null ? HttpStatus.INTERNAL_SERVER_ERROR.value() : e.getCode();
        restResponse.setCode(code);
        restResponse.setSuccess(false);

        // 获取异常堆栈的第一个元素
        StackTraceElement data = getRootErrorInfo(e);
        restResponse.setData(data);

        restResponse.setMessage(getMessage(e));
        return restResponse;
    }

    @ExceptionHandler(Exception.class)
    public RestResponse<Object> handleException(Exception e) {
        log.error("An unexpected exception occurred.", e);
        // 获取异常堆栈的第一个元素
        Map<String, Object> data = new HashMap<>(32);
        data.put("cause", e.getCause() == null ? null : e.getCause().getMessage());
        data.put("rootError", getRootErrorInfo(e));
        data.put("message", e.getMessage());
        return RestResponse.error(data, DEFAULT_MESSAGE);
    }

    @ExceptionHandler(BindException.class)
    public RestResponse<List<ObjectError>> handleException(BindException e) {
        log.error("An illegal argument exception occurred.", e);
        List<ObjectError> allErrors = e.getAllErrors();
        String message = allErrors.parallelStream().map(objectError -> {
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                String field = fieldError.getField();
                String defaultMessage = fieldError.getDefaultMessage();
                return field + defaultMessage;
            }
            return objectError.toString();
        }).collect(Collectors.joining(";"));

        RestResponse<List<ObjectError>> response = new RestResponse<>();
        response.setCode(HttpStatus.BAD_REQUEST.value());
        response.setData(allErrors);
        response.setMessage(message);
        response.setSuccess(false);

        return response;
    }

}
