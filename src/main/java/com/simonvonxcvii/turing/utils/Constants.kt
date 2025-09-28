package com.simonvonxcvii.turing.utils

import org.springframework.util.AntPathMatcher

/**
 * 常量接口
 *
 * @author Simon Von
 * @since 2022/7/12 10:07 周二
 */
interface Constants {
    companion object {
        const val CAPTCHA = "captcha"
        const val REDIS_CAPTCHA = "$CAPTCHA:"
        const val LOGIN_REQUEST = "/login"
        const val HEX_DIGEST = "md5DigestAsHex"

        val ANT_PATH_MATCHER = AntPathMatcher()

        const val AREA = "area"

        const val PATTERN = "%"

        const val ESCAPE_CHAR = '/'
    }
}
