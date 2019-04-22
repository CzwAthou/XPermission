//package com.athou.xpermission;
//
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.Set;
//
///**
// * @author athoucai
// * @date 2019/4/10
// */
//public class PermissionDispatcher {
//
//    private static Set<PermissionCallback> permissionCallbackSet = new HashSet<>();
//
//    public static void regist(PermissionCallback permissionCallback, boolean regist) {
//        if (regist) {
//            permissionCallbackSet.add(permissionCallback);
//        } else {
//            permissionCallbackSet.remove(permissionCallback);
//        }
//    }
//
//    static void permissionGrant(String... permissions) {
//        Iterator<PermissionCallback> iterator = permissionCallbackSet.iterator();
//        while (iterator.hasNext()) {
//            iterator.next().onResult(true, permissions);
//        }
//    }
//
//    static void permissionDenied(String... permissions) {
//        Iterator<PermissionCallback> iterator = permissionCallbackSet.iterator();
//        while (iterator.hasNext()) {
//            iterator.next().onResult(false, permissions);
//        }
//    }
//}
