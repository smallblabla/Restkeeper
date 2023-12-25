package com.restkeeper.operator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    /**
     * swagger2的配置文件，这里可以配置swagger2的一些基本的内容，比如扫描的包等等
     * @return
     */
    @Bean
    public Docket createRestfulApi(){
        // 创建参数列表
        List<Parameter> pars = new ArrayList<Parameter>();
        // 创建参数构建器
        ParameterBuilder tokenPar = new ParameterBuilder();
        // 设置参数名称、描述、类型、参数位置和是否必填
        tokenPar.name("Authorization").description("jwt token").modelRef(new ModelRef("string")).parameterType("header").required(true).build();
        // 将参数添加到参数列表
        pars.add(tokenPar.build());

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo()) // 设置api文档的详细信息
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.restkeeper.operator.controller"))  //暴露接口地址的包路径
                .build()
                .globalOperationParameters(pars); // 设置全局操作参数
    }

    /**
     * 构建 api文档的详细信息函数,注意这里的注解引用的是哪个
     * @return
     */
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                // 页面标题
                .title("RESTful API")
                // 版本号
                .version("2.0")
                // 描述
                .description("API 描述")
                .build();
    }
}
