package com.base.and.http;

import com.base.and.base.BaseHttpResult;

/**
 * 返回错误处理
 * Created by Makise on 2017/2/4.
 */

public class ResultException extends RuntimeException {
    public ResultException(BaseHttpResult baseResult) {
        super(baseResult.codeDesc);
        handleException(baseResult);
    }

    /**
     * 处理返回异常信息
     *
     * @param baseResult
     */
    private void handleException(BaseHttpResult baseResult) {
    }
}
