package com.piesat.sso.client.shiro;

import com.alibaba.fastjson.JSON;
import com.piesat.common.grpc.exception.GrpcException;
import com.piesat.common.utils.OwnException;
import com.piesat.common.utils.SignException;
import com.piesat.util.ResultT;
import com.piesat.util.ReturnCodeEnum;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/11 11:14
 */
public class HtExceptionHandler implements HandlerExceptionResolver  {
    private Logger logger= LoggerFactory.getLogger(HtExceptionHandler.class);
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) {
        ResultT<String> resultT=new ResultT<>();
        String mess=OwnException.get(e);
        logger.error(mess);
        if (e instanceof UnauthorizedException || e instanceof AuthorizationException) {
             resultT.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_403_ERROR);
            return outJson(request, response, resultT);
        }
        if(e instanceof GrpcException){
            resultT.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_501_ERROR);
            return outJson(request, response, resultT);
        }
        if (e instanceof SignException) {
            resultT.setErrorMessage(e.getMessage());
            return outJson(request, response, resultT);
        }
        if(mess.length()>500){
            mess=mess.substring(0,499);
        }
        resultT.setErrorMessage(mess);
        return outJson(request, response, resultT);
    }

    private ModelAndView outJson(HttpServletRequest request, HttpServletResponse response, ResultT<String> res) {
        ModelAndView mv = new ModelAndView();
        /*  使用response返回    */
        response.setStatus(HttpStatus.OK.value()); //设置状态码
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); //设置ContentType
        response.setCharacterEncoding("UTF-8"); //避免乱码
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        try {
            String json = JSON.toJSONString(res);
            response.getWriter().write(json);
        } catch (IOException e) {
            logger.error(OwnException.get(e));
        }

        return mv;
    }
}
