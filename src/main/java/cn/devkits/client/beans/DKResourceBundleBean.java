/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.beans;

import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

/**
 * https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html<br>
 * https://www.cnblogs.com/hujunzheng/p/11037577.html
 * @author Shaofeng Liu
 * @version 1.0.1
 * @time 2020年3月7日 下午11:35:11
 */
@Component("messageSource")
public class DKResourceBundleBean extends ResourceBundleMessageSource {

    public DKResourceBundleBean() {
        setBasename("language/message");
//        setDefaultEncoding("UTF-8");
    }
}
