package cn.huava.common;

import cn.huava.HuavaApplication;
import org.springframework.boot.test.context.SpringBootTest;

/** 基础类，用于 SpringBoot 项目的测试，用于被继承。 */
@SpringBootTest(classes = {HuavaApplication.class})
public abstract class WithSpringBootTestAnnotation {}
