package com.infoa2.core.callback

/**
 * Created by caio on 01/02/17.
 */

interface ResponseCallback<T, K> {

    fun onSuccess(data: T)
    fun onError(error: K)

}
