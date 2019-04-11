package com.athou.xpermission;

/**
 * @author athoucai
 * @date 2019/4/10
 */
public interface PermissionCallback {

    void onResult(boolean grant, String... permission);
}
