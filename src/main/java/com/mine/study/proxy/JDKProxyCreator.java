package com.mine.study.proxy;

import com.mine.study.annotation.ApiServer;
import com.mine.study.annotation.Delete;
import com.mine.study.annotation.Get;
import com.mine.study.annotation.Post;
import com.mine.study.bean.MethodInfo;
import com.mine.study.bean.ServerInfo;
import com.mine.study.handler.RestHandler;
import com.mine.study.handler.WebClientRestHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 使用jdk动态代理实现代理类
 */
@Slf4j
public class JDKProxyCreator implements ProxyCreator {

    @Override
    public Object createProxy(Class<?> type) {
        log.info("createProxy:" + type);

        // 根据接口得到API服务器信息
        ServerInfo serverInfo = extractServerInfo(type);

        log.info("serverInfo:" + serverInfo);

        // 给每一个代理类一个实现,初始化服务器信息(初始化webclient)
        RestHandler handler = new WebClientRestHandler(serverInfo);

        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{type}, new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                // 根据方法和参数得到调用信息
                MethodInfo methodInfo = extractMethodInfo(method, args);
                log.info("methodInfo:" + methodInfo);

                // 调用rest
                return handler.invokeRest(methodInfo);
            }

            /**
             * 根据方法定义和调用参数得到调用的相关信息
             *
             * @param method
             * @param args
             * @return
             */
            private MethodInfo extractMethodInfo(Method method, Object[] args) {
                MethodInfo methodInfo = new MethodInfo();

                extractUrlAndMethod(method, methodInfo);

                extractRequestParamAndBody(method, args, methodInfo);

                // 提取返回对象信息
                extractReturnInfo(method, methodInfo);

                return methodInfo;
            }

            /**
             * 提取返回对象信息
             *
             * @param method
             * @param methodInfo
             */
            private void extractReturnInfo(Method method, MethodInfo methodInfo) {
                // 返回flux还是mono
                // isAssignableFrom 判断类型是否某个的子类
                // instanceof 判断实例是否某个的子类
                boolean isFlux = method.getReturnType().isAssignableFrom(Flux.class);
                methodInfo.setReturnFlux(isFlux);

                // 得到返回对象的实际类型
                Class<?> elementType = extractElementType(method.getGenericReturnType());
                methodInfo.setReturnElementType(elementType);
            }

            /**
             * 得到泛型类型的实际类型
             *
             * @param genericReturnType
             * @return
             */
            private Class<?> extractElementType(Type genericReturnType) {
                Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();

                return (Class<?>) actualTypeArguments[0];
            }

            /**
             * 得到请求的param和body
             *
             * @param method
             * @param args
             * @param methodInfo
             */
            private void extractRequestParamAndBody(Method method, Object[] args, MethodInfo methodInfo) {
                // 得到调用的参数和body
                Parameter[] parameters = method.getParameters();

                // 参数和值对应的map
                Map<String, Object> params = new LinkedHashMap<>();
                methodInfo.setParams(params);

                for (int i = 0; i < parameters.length; i++) {
                    // 是否带 @PathVariable
                    PathVariable annoPath = parameters[i].getAnnotation(PathVariable.class);

                    if (annoPath != null) {
                        params.put(annoPath.value(), args[i]);
                    }

                    // 是否带了 RequestBody
                    RequestBody annoBody = parameters[i].getAnnotation(RequestBody.class);

                    if (annoBody != null) {
                        methodInfo.setBody((Mono<?>) args[i]);
                        // 请求对象的实际类型
                        methodInfo.setBodyElementType(extractElementType(parameters[i].getParameterizedType()));
                    }
                }
            }

            /**
             * 得到请求的URL和方法
             *
             * @param method
             * @param methodInfo
             */
            private void extractUrlAndMethod(Method method, MethodInfo methodInfo) {
                // 得到请求URL和请求方法
                Annotation[] annotations = method.getAnnotations();

                for (Annotation annotation : annotations) {
                    // GET
                    if (annotation instanceof Get) {
                        Get a = (Get) annotation;

                        methodInfo.setUrl(a.value()[0]);
                        methodInfo.setMethod(HttpMethod.GET);
                    }
                    // POST
                    else if (annotation instanceof Post) {
                        Post a = (Post) annotation;

                        methodInfo.setUrl(a.value()[0]);
                        methodInfo.setMethod(HttpMethod.POST);
                    }
                    // DELETE
                    else if (annotation instanceof Delete) {
                        Delete a = (Delete) annotation;

                        methodInfo.setUrl(a.value()[0]);
                        methodInfo.setMethod(HttpMethod.DELETE);
                    }

                }
            }
        });
    }

    /**
     * 提取服务器信息
     *
     * @param type
     * @return
     */
    private ServerInfo extractServerInfo(Class<?> type) {
        ServerInfo serverInfo = new ServerInfo();

        ApiServer anno = type.getAnnotation(ApiServer.class);
        Assert.isTrue(anno.contextPath().startsWith("/"), "contextPath must start with '/'");

        serverInfo.setUrl(anno.value() + anno.contextPath());
        serverInfo.setName(anno.name());
        serverInfo.setDescription(anno.description());

        return serverInfo;
    }

}
