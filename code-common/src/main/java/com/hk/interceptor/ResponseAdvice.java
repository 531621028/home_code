package com.hk.interceptor;

import com.hk.common.JsonResult;
import com.hk.common.constants.GlobalConstants;
import com.hk.exception.ClientException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


@ControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    private final Logger LOG = LoggerFactory.getLogger(ResponseAdvice.class);

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
            ServerHttpResponse response) {
        if (!(body instanceof JsonResult)) {
            JsonResult result = new JsonResult();
            result.setSuccess(true);
            result.setData(body);
            result.setErrorCode(0);
            return result;
        }
        return body;
    }

    @ExceptionHandler(value = {Throwable.class})
    @ResponseBody
    public JsonResult handler(Throwable e) {
        if (e instanceof ClientException) {
            ClientException exception = (ClientException) e;
            JsonResult result = new JsonResult();
            result.setSuccess(false);
            result.setData(null);
            result.setMessage(exception.getMessage());
            result.setErrorCode(exception.getCode());
            return result;
        } else {
            LOG.error(null, e);
            JsonResult result = new JsonResult();
            result.setSuccess(false);
            result.setData(null);
            result.setMessage("服务器内部错误");
            result.setErrorCode(GlobalConstants.ErrorCode.APP_DEFAULT_CODE);
            return result;
        }
    }
}
