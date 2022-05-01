package com.qdot.watery.constants

class Constant {
    companion object {
        const val PREF_NAME = "login-stat"
        const val LOGIN_KEY = "login-key"
        const val USER_TYPE_KEY = "user-type"
        const val SESSION_KEY = "session"
        const val AS_USER = 1
        const val AS_DELIVERY = 2
        const val AS_ADMIN = 3
        const val LAT_KEY = "lat"
        const val LONG_KEY = "long"

        const val LAT_KEY_ACTIVITY = "lat-act"
        const val LON_KEY_ACTIVITY = "lon-act"
        const val ADDRESS_KEY_ACTIVITY = "address-act"
        const val MOBILE_KEY_ACTIVITY = "mob-act"

        const val ORDER_CREATED = 0
        const val ORDER_ACCEPTED = 1
        const val ORDER_SHIPPED = 2
        const val ORDER_DELIVERED = 3
        const val ORDER_CANCELLED = 4

        const val PAYMENT_ONLINE = 1
        const val PAYMENT_CASH_ON_DELIVERY = 2

        const val ORDER_ID_KEY = "id-key"
        const val ORDER_STATUS_KEY = "stat-key"

        const val ORDER_SUCCESS = 0
        const val ORDER_FAILED = 1

        const val ORDER_DET_PASS_KEY = "od-key"
        const val DEL_BOY_ID_PASS_KEY = "del-boy-key"

        const val BASE_URL = "https://api.razorpay.com/v1/orders"

        const val APP_WRITE_URL = "http://8080-appwrite-integrationforg-koz811r49x8.ws-us43.gitpod.io/v1"
    }
}