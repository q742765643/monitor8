package com.piesat.common.interceptor;

import com.piesat.common.annotation.HtParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/15 17:40
 */
@Component
public class CurrentUserArgumentResolver  implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        RequestParam ann = (RequestParam)methodParameter.getParameterAnnotation(RequestParam.class);
        HtParam htParam = (HtParam)methodParameter.getParameterAnnotation(HtParam.class);
        if(ann==null||htParam==null){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        if (binderFactory == null) {
            return null;
        }
        // 解析器中的自定义逻辑
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        Map<String, String[]> paramMap = (Map<String, String[]>) request
                .getAttribute("REQUEST_RESOLVER_PARAM_MAP_NAME");
        if(null==paramMap||paramMap.size()==0){
            return null;
        }
        Object arg = paramMap.get(parameter.getParameterName());
        String typeName=parameter.getParameterType().getName();
        if(typeName.toUpperCase().indexOf("DTO")!=-1||typeName.toUpperCase().indexOf("VO")!=-1){
            arg=null;
        }
        if (arg != null) {
            // 生成参数绑定器，第一个参数为request请求对象，第二个参数为需要绑定的目标对象，第三个参数为需要绑定的目标对象名
            WebDataBinder binder = binderFactory.createBinder(webRequest, null, parameter.getParameterName());
            arg = binder.convertIfNecessary(arg, parameter.getParameterType(), parameter);
        } else {
            String name = parameter.getParameterName();
            // 查找是否已有名为name的参数值的映射，如果没有则创建一个
            arg = mavContainer.containsAttribute(name) ? mavContainer.getModel().get(name)
                    : BeanUtils.instantiateClass(parameter.getParameterType());
            WebDataBinder binder = binderFactory.createBinder(webRequest, arg, name);
            if (binder.getTarget() != null) {
                PropertyValues propertyValues = new MutablePropertyValues(paramMap);
                // 将K-V绑定到binder.target属性上
                binder.bind(propertyValues);
            }
            // 将参数转到预期类型，第一个参数为解析后的值，第二个参数为绑定Controller参数的类型，第三个参数为绑定的Controller参数
            arg = binder.convertIfNecessary(binder.getTarget(), parameter.getParameterType(), parameter);
        }
        return arg;
    }
}
