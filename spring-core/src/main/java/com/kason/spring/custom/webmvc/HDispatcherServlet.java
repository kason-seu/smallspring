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

//        try {
//            method.invoke(bean, null);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        } catch (InvocationTargetException e) {
//            throw new RuntimeException(e);
//        }

    }
    HApplicationContext context;
    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("servet Init");


        String configLocation = config.getInitParameter("contextConfigLocation");
        context = new HApplicationContext(configLocation);

        //doIOC(basePackage);
        // 依赖注入
        //doDI();
        //bindHandlerMapping
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

    /*private void doDI() {

        beanMap.forEach((beanName, beanInstance) -> {

            for (Field beanField : beanInstance.getClass().getDeclaredFields()) {

                if (beanField.isAnnotationPresent(HAutowired.class)) {
                    HAutowired fieldAnnotation = beanField.getDeclaredAnnotation(HAutowired.class);
                    String fieldName = fieldAnnotation.value();
                    if (fieldName == null || "".equals(fieldName)) {
                        String beanFieldName = beanField.getName();
                        fieldName = Introspector.decapitalize(beanFieldName);
                    }
                    Object fieldInstance = beanMap.get(fieldName);
                    if (fieldInstance == null) {
                        throw new NullPointerException("注入的bean为空");
                    }
                    beanField.setAccessible(true);
                    try {
                        beanField.set(beanInstance, fieldInstance);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        });

    }*/

    //ConcurrentMap<String, Object> beanMap = new ConcurrentHashMap<>();
    /*private void doIOC(String basePackage) {
        URL resource = this.getClass().getClassLoader().getResource(basePackage.replace(".", "/"));
        assert resource != null;
        File file = new File(resource.getFile());
        scan(file, basePackage);
        // 通过反射创建需要被接管的Bean
        for (String classFile : classFiles) {
            try {

                Class<?> beanClass = Class.forName(classFile.replace(".class", ""));
                if (beanClass.isAnnotationPresent(HController.class)) {

                    HController hControllerAnnotation = beanClass.getDeclaredAnnotation(HController.class);
                    String beanName = hControllerAnnotation.value();
                    if (beanName == null || beanName.equals("")) {
                        beanName = Introspector.decapitalize(beanClass.getSimpleName());
                    }
                    if (!beanMap.containsKey(beanName)) {
                        beanMap.put(beanName, beanClass.getDeclaredConstructor().newInstance());
                    }
                } else if (beanClass.isAnnotationPresent(HService.class)) {
                    HService hServiceAnnotation = beanClass.getDeclaredAnnotation(HService.class);
                    String beanName = hServiceAnnotation.value();
                    if (beanName == null || beanName.equals("")) {
                        beanName = Introspector.decapitalize(beanClass.getSimpleName());
                    }
                    if (!beanMap.containsKey(beanName)) {
                        beanMap.put(beanName, beanClass.getDeclaredConstructor().newInstance());
                    }
                    Type[] genericInterfaces = beanClass.getGenericInterfaces();
                    // 获取该类继承的所有接口，因为可能会继承多个接口，所以返回的是数组
                    for (Type genericInterface : genericInterfaces) {
                        beanMap.put(Introspector.decapitalize(((Class)genericInterface).getSimpleName()), beanMap.get(beanName));
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }*/

    /*List<String> classFiles = new ArrayList<>();
    private void scan(File file, String basePackage) {
        if (file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                scan(f, basePackage + "." +f.getName());
            }
        } else {
            if (!file.getName().endsWith(".class")) {
                return;
            }
            classFiles.add(basePackage);
        }

    }*/
}
