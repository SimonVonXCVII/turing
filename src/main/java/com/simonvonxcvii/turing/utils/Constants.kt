package com.simonvonxcvii.turing.utils

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
        const val HEX_DIGEST = "md5DigestAsHex"

        const val AREA = "area"

        const val PATTERN = "%"

        const val ESCAPE_CHAR = '/'

        const val LOGIN_URL = "/api/auth/login"

        const val LOGOUT_URL = "/api/auth/logout"
    }
}
