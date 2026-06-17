package com.scs.utils;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 当前登录用户上下文
 */
public class UserContext {

    private static final ThreadLocal<CurrentUser> currentUser = new ThreadLocal<>();

    public static void setCurrentUser(CurrentUser user) {
        currentUser.set(user);
    }

    public static CurrentUser getCurrentUser() {
        return currentUser.get();
    }

    public static Long getUserId() {
        CurrentUser user = currentUser.get();
        return user != null ? user.getUserId() : null;
    }

    public static String getUsername() {
        CurrentUser user = currentUser.get();
        return user != null ? user.getUsername() : null;
    }

    public static String getRole() {
        CurrentUser user = currentUser.get();
        return user != null ? user.getRole() : null;
    }

    public static void clear() {
        currentUser.remove();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentUser {
        private Long userId;
        private String username;
        private String role;
    }
}
