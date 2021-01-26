package com.ixyf.aspect;

import com.baomidou.mybatisplus.extension.api.IErrorCode;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.ixyf.model.R;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 内部api调用的异常处理
     */
    @ExceptionHandler(value = ApiException.class)
    public R handlerApiException(ApiException apiException) {
        final IErrorCode errorCode = apiException.getErrorCode();
        if (errorCode != null) {
            return R.fail(errorCode);
        }
        return R.fail(apiException.getMessage());
    }

    /**
     * 方法参数校验失败的异常
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handlerMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        final BindingResult bindingResult = exception.getBindingResult();
        if (bindingResult.hasErrors()) {
            final FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                return R.fail(fieldError.getField() + fieldError.getDefaultMessage());
            }
        }
        return R.fail(exception.getMessage());
    }


    /**
     * 对象内部使用validate 没有校验成功抛出的异常
     * @param exception
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    public R handlerBindException(BindException exception) {
        final BindingResult bindingResult = exception.getBindingResult();
        if (bindingResult.hasErrors()) {
            final FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                return R.fail(fieldError.getField() + fieldError.getDefaultMessage());
            }
        }
        return R.fail(exception.getMessage());
    }

}
