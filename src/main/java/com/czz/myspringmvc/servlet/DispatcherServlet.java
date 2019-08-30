package com.czz.myspringmvc.servlet;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.czz.myspringmvc.annocation.*;
import com.czz.myspringmvc.controller.UserController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chengzhzh@datangmobile.com
 * @create 2019-08-29 17:30
 * 基于注解的servlet配置
 */
@WebServlet(name = "dispatcherServlet",urlPatterns = "/",loadOnStartup = 1
,initParams = {
        @WebInitParam(name = "base-package",value = "com.czz.myspringmvc")
})
public class DispatcherServlet extends HttpServlet {
    Log log =  LogFactory.get();
    private String basePackage; // 扫描包路径
    private List<String> packageNames = new ArrayList<>(); // 基包下所有带包路径权限定类名
    private Map<String,Object> instanceMap = new HashMap<>(); // 注解实例化 注解上的名称:实例化对象
    private Map<String,String> nameMap = new HashMap<>(); //  带包路径权限定名称：注解上的名称 把注解存入到 map
    private Map<String,Method> urlMethodMap = new HashMap<>(); //url 地址和方法的映射关系 springmvc 就是方法调用链
    private Map<Method,String> methodPackageMap = new HashMap<>(); // 方法和权限定类的映射关系 利用method找到方法利用反射执行

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            // 1. 获取定义的包路径
            basePackage = config.getInitParameter("base-package");
            // 2. 根据包路径扫描包 把所有的注解类 放入到 map---> packageNames
            scanBasePackage(basePackage);
            // 3. 把所有注解放入 map key 为注解的名称
            instance(packageNames);
            // 4. ioc注入
            springIOC();
            // 5. 完成地址与方法的映射
            handlerUrlMethodMap();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
//        String path = uri.replaceAll(contextPath, "");
        Method method = urlMethodMap.get(contextPath);
        if (method != null) {
            String packageName = methodPackageMap.get(method);
            String controllerName = nameMap.get(packageName);
            UserController userController = (UserController) instanceMap.get(controllerName);
            try {
                method.setAccessible(true);
                method.invoke(userController);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void handlerUrlMethodMap() throws ClassNotFoundException {
        if (packageNames.size()<1) {
            return;
        }
        for (String string : packageNames) {
            Class c = Class.forName(string);

            if(c.isAnnotationPresent(Controller.class)){
                Method[] methods = c.getMethods();
                StringBuffer baseUrl = new StringBuffer();
                if(c.isAnnotationPresent(RequestMapping.class)){
                    RequestMapping requestMapping = (RequestMapping) c.getAnnotation(RequestMapping.class);
                    baseUrl.append(requestMapping.value());
                }

                for (Method method : methods) {
                    if(method.isAnnotationPresent(RequestMapping.class)){
                        RequestMapping requestMapping = (RequestMapping) method.getAnnotation(RequestMapping.class);
                        baseUrl.append(requestMapping.value());
                        urlMethodMap.put(baseUrl.toString(),method);
                        methodPackageMap.put(method,string);
                    }
                }
            }

            
        }

    }

    private void springIOC() throws IllegalAccessException {
        for (Map.Entry<String,Object> entry: instanceMap.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if(field.isAnnotationPresent(Qualifier.class)){
                    String name = field.getAnnotation(Qualifier.class).value();
                    field.setAccessible(true);
                    field.set(entry.getValue(),instanceMap.get(name));
                }
            }
        }
    }

    private void instance(List<String> packageNames) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (packageNames.size()<1) {
            return;
        }
        for (String string : packageNames) {
            Class c = Class.forName(string);
            if(c.isAnnotationPresent(Controller.class)){
                 Controller controller = (Controller) c.getAnnotation(Controller.class);
                 String controllerName = controller.value();
                 instanceMap.put(controllerName,c.newInstance());
                 nameMap.put(string,controllerName);
                 log.info("Controller :"+ string + ", value" + controller.value() );
            }else if(c.isAnnotationPresent(Service.class)){
                Service service = (Service) c.getAnnotation(Service.class);
                String serviceName = service.value();
                instanceMap.put(serviceName,c.newInstance());
                nameMap.put(string,serviceName);
                log.info("Controller :"+ string + ", value" + service.value() );
            }else if(c.isAnnotationPresent(Repository.class)){
                Repository repository = (Repository) c.getAnnotation(Repository.class);
                String repositoryName = repository.value();
                instanceMap.put(repositoryName,c.newInstance());
                nameMap.put(string,repositoryName);
                log.info("Controller :"+ string + ", value" + repository.value() );
            }
        }
    }

    private void scanBasePackage(String basePackage) {
        URL url = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.", "/"));
        File basePackageFile = new File(url.getPath());
        log.info("scan:"+ basePackageFile);
        File[] childFiles = basePackageFile.listFiles();
        for (File file : childFiles) {
            if (file.isDirectory()) {
                scanBasePackage(basePackage+"."+file.getName());
            }else if(file.isFile()){
                packageNames.add(basePackage+"."+file.getName().split("\\.")[0]);
            }
        }
    }
}
