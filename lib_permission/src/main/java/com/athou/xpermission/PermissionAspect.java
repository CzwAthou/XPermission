package com.athou.xpermission;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.athou.xpermission.annotation.NeedPermission;
import com.athou.xpermission.annotation.NeedPermissions;
import com.athou.xpermission.annotation.PermissionDenied;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author athoucai
 * @date 2019/4/10
 */
@Aspect
public class PermissionAspect {

    private final String TAG = "PermissionAspect";

    @Keep
    @Pointcut("execution(@com.athou.xpermission.annotation.NeedPermission * *(..))")
    public void needPermission() {
    }

    @Pointcut("execution(@com.athou.xpermission.annotation.NeedPermissions * *(..))")
    @Keep
    public void needPermissions() {
    }

    @Around("needPermission() || needPermissions()")
    public void aroundJoinPoint(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        //先检查参数
        Object[] args = joinPoint.getArgs();
        String methodName = method.getName();
        if (args == null || args.length <= 0) {
            throw new IllegalArgumentException(String.format("the method:%s's first argument must be FragmentActivity or v4.Fragment or View", methodName));
        } else {
            if (Modifier.isStatic(method.getModifiers())) {
                throw new IllegalArgumentException(String.format("the method:%s is static that not support!", methodName));
            }
            Object host = args[0];
            if (host instanceof FragmentActivity
                    || host instanceof Fragment
                    || host instanceof View) {
                insertPermissionRequest(host, joinPoint, method);
            } else {
                throw new IllegalArgumentException(String.format("the method:%s's first argument must be an activity", methodName));
            }
        }
    }

    private void insertPermissionRequest(Object host, ProceedingJoinPoint joinPoint, Method method) throws Throwable {
        //是否有权限请求
        boolean hasRequest = false;
        NeedPermissions needPermissions = method.getAnnotation(NeedPermissions.class);
        if (needPermissions != null) {
            NeedPermission[] permissionArray = needPermissions.value();
            if (permissionArray.length > 0) {
                hasRequest = excutePermission(host, joinPoint, Arrays.asList(permissionArray));
            }
        } else {
            NeedPermission needPermission = method.getAnnotation(NeedPermission.class);
            if (needPermission != null) {
                hasRequest = excutePermission(host, joinPoint, Collections.singletonList(needPermission));
            }
        }
        //有权限请求，拦截掉方法执行，
        if (hasRequest) {
            return;
        }
        //没有权限请求，直接继续执行原方法
        joinPoint.proceed();
    }

    /**
     * 请求权限
     *
     * @param joinPoint
     * @param needPermissions
     * @return 是否有权限请求
     */
    private boolean excutePermission(Object host, final ProceedingJoinPoint joinPoint, final List<NeedPermission> needPermissions) {
        if (needPermissions == null || needPermissions.isEmpty()) {
            return false;
        }
        final RxPermissions rxPermissions;
        if (host instanceof FragmentActivity) {
            rxPermissions = new RxPermissions((FragmentActivity) host);
        } else if (host instanceof Fragment) {
            rxPermissions = new RxPermissions((Fragment) host);
        } else if (host instanceof View) {
            Context context = ((View) host).getContext();
            if (context instanceof FragmentActivity) {
                rxPermissions = new RxPermissions((FragmentActivity) context);
            } else {
                return false;
            }
        } else {
            return false;
        }
        List<Observable<Pair<String[], Boolean>>> obs = new ArrayList<>();
        for (NeedPermission permissionItem : needPermissions) {
            final String[] permissions = permissionItem.permissions();
            if (permissions.length > 0) {
                Observable<Boolean> obRequest = rxPermissions.request(permissions);
                Observable<Boolean> obNecessary = Observable.just(permissionItem.necessary());
                obs.add(Observable.zip(obRequest, obNecessary, new BiFunction<Boolean, Boolean, Pair<String[], Boolean>>() {
                    @Override
                    public Pair<String[], Boolean> apply(Boolean requestRet, Boolean necessaryRet) throws Exception {
                        //如果是必要权限，结果为请求权限的结果
                        if (necessaryRet) {
                            return new Pair<>(permissions, requestRet);
                        }
                        //如果不是必要权限，跟请求权限结果无关，即使权限拒绝也要通过
                        return new Pair<>(permissions, true);
                        //以上代码可以用下面表达式，为了理解才用上面写法
                        // (!necessaryRet) || requestRet
                    }
                }));
            }
        }
        if (obs.isEmpty()) {
            throw new RuntimeException("NeedPermission config error, please check permissions's value is empty?");
        }

        Observable<Pair<String[], Boolean>>[] obsArray = new Observable[obs.size()];
        obs.toArray(obsArray);
        Observable.concatArray(obsArray)
                .buffer(obsArray.length)
                .map(new Function<List<Pair<String[], Boolean>>, List<Pair<String[], Boolean>>>() {
                    @Override
                    public List<Pair<String[], Boolean>> apply(List<Pair<String[], Boolean>> pairList) throws Exception {
                        return pairList;
                    }
                })
                .subscribe(new Consumer<List<Pair<String[], Boolean>>>() {
                    @Override
                    public void accept(List<Pair<String[], Boolean>> pairList) throws Exception {
                        boolean result = true;
                        List<String> deniedPermissions = new ArrayList<>();
                        for (Pair<String[], Boolean> pair : pairList) {
                            //收集拒绝的权限
                            if (!pair.second) {
                                deniedPermissions.addAll(Arrays.asList(pair.first));
                            }
                            result = result && pair.second;
                        }
                        //授权通过，则继续执行原方法
                        if (result) {
                            deniedPermissions.clear();
                            proceed(joinPoint);
                        } else {
                            //授权未通过，则中断
                            permissionDenied(joinPoint, deniedPermissions);
                        }
                    }
                });
        return true;
    }

    private void proceed(ProceedingJoinPoint joinPoint) {
        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    //处理权限拒绝
    private void permissionDenied(ProceedingJoinPoint joinPoint, List<String> permissions) throws ClassNotFoundException {
        Object ownerObject = Utils.getExternalClass(joinPoint.getTarget());
        if (ownerObject == null) {
            Log.w(TAG, "external class not be found");
            return;
        }
        Method[] declaredMethods = ownerObject.getClass().getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            PermissionDenied permissionDenied = declaredMethod.getAnnotation(PermissionDenied.class);
            if (permissionDenied != null) {
                try {
                    declaredMethod.invoke(ownerObject, permissions);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
