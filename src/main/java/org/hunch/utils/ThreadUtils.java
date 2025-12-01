package org.hunch.utils;

import lombok.Data;

@Data
public class ThreadUtils {

    public static ThreadLocal<String> jwtToken = new ThreadLocal<>();


}
