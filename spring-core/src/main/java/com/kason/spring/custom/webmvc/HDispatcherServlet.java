package com.kason.spring.custom.webmvc;

import com.kason.spring.custom.annotation.HRequestMapping;
import com.kason.spring.custom.annotation.HRequestParam;
import com.kason.spring.custom.context.HApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.Introspector;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HDispatcherServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("coming into here");

        String requestURL = req.getRequestURI();
        requestURL= requestURL.replaceAll("/+", "/");
        System.out.println("request url " + requestURL);

        if (!urlMethodMapper.containsKey(requestURL)) {
            resp.getWriter().write("404 NOT FOUND");
            return;
        }
        Method method = urlMethodMapper.get(requestURL);

        Object bean = context.getBean(Introspector.decapitalize(method.getDeclaringClass().getSimpleName()));
        int i = 0;
        Map<String, String[]> parameterValueMap = req.getParameterMap();

        Parameter[] parameters = method.getParameters();
        Object[] params = new Object[parameters.length];

        for (Parameter parameter : parameters) {
            Class<?> clazz = parameter.getType();
            if (parameter.isAnnotationPresent(HRequestParam.class)) {
                String[] value = parameterValueMap.get(parameter.getDeclaredAnnotation(HRequestParam.class).value());
                String v = Arrays.toString(value);
                params[i++]=v;
            } else if ( clazz == HttpServletRequest.class) {
                params[i++] = req;
            } else if (clazz == HttpServletResponse.class) {
                params[i++] = resp;
            }
        }

        try {
            method.invoke(bean, params);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }
    HApplicationContext context;
    @Override
    public void init(ServletConfig config) throws ServletException {
        String configLocation = config.getInitParameter("contextConfigLocation");
        context = new HApplicationContext(configLocation);
        bindHandlerMapping();
        super.init(config);
    }

    Map<String, Method> urlMethodMapper = new HashMap<>();
    // url -> method
    private void bindHandlerMapping() {
        context.getBeanClass().forEach(beanClass -> {

            if (beanClass.isAnnotationPresent(HRequestMapping.class)) {
                HRequestMapping requestMappingAnnotation = beanClass.getDeclaredAnnotation(HRequestMapping.class);
                String requestUrl = requestMappingAnnotation.value();

                Method[] beanMethods = beanClass.getDeclaredMethods();
                for (Method beanMethod : beanMethods) {
                    if (beanMethod.isAnnotationPresent(HRequestMapping.class)) {
                        HRequestMapping requestMappingMethodAnnotation = beanMethod.getDeclaredAnnotation(HRequestMapping.class);
                        requestUrl = ("/" + requestUrl + "/" + requestMappingMethodAnnotation.value()).replaceAll("/+", "/");
                        urlMethodMapper.put(requestUrl, beanMethod);
                    }
                }
            }

        });
    }
}
