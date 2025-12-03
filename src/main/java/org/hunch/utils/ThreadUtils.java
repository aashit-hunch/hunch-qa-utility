package org.hunch.utils;

import lombok.Data;
import org.hunch.dto.UserDetailsDTO;

@Data
public class ThreadUtils {

    public static ThreadLocal<String> jwtToken = new ThreadLocal<>();
    public static ThreadLocal<UserDetailsDTO> userDto = new ThreadLocal<>();


}
