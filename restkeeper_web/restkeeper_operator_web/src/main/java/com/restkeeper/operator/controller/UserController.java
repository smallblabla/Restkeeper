package com.restkeeper.operator.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.restkeeper.operator.entity.OperatorUser;
import com.restkeeper.operator.service.IOperatorUserService;
import com.restkeeper.operator.vo.LoginVO;
import com.restkeeper.response.vo.PageVO;
import com.restkeeper.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginContext;

/**
 * 管理员的登录接口
 */
@RestController
@RefreshScope //配置中心的自动刷新
@Slf4j
@Api(tags = {"管理员相关接口"})
public class UserController{


    @Value("${server.port}")
    private String port;

    @Value("${key:''}")
    private String key;

    @Value("${commonKey:''}")
    private String commonKey;

    @Value("${shareInfo:''}")
    private String shareInfo;

    @Reference(version = "1.0.0",check = false)
    private IOperatorUserService operatorUserService;

    @GetMapping(value = "/echo/{message}")
    public String echo(@PathVariable(value = "message") String message) {
        return "Hello Nacos Discovery " + message + ", i am from port " + port;
    }

    @GetMapping(value = "/config")
    public String config() {
        return "Hello Nacos Config get "+key+"      commonValue:"+commonKey ;
    }

    @GetMapping("/demo")
    public String demo(){
        return "shareInfo:  "+shareInfo;
    }

    @ApiOperation("分页列表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path",name= "page",value = "当前页码",required = true,dataType = "Integer"),
            @ApiImplicitParam(paramType = "path",name = "pageSize",value = "每页数据大小",required = true,dataType = "Integer"),
            @ApiImplicitParam(paramType = "query",name = "name",value = "用户名",required = false,dataType = "String"),
    })
    @GetMapping("/pageList/{page}/{pageSize}")
    public IPage<OperatorUser> findListByPage(@PathVariable("page") int pageNum,
                                              @PathVariable("pageSize") int pageSize,
                                              @RequestParam(value = "name",required = false) String name){

        /*IPage<OperatorUser> page = new Page<OperatorUser>(pageNum,pageSize);
        log.info("管理员数据分页查询："+ JSON.toJSONString(page));*/
        return operatorUserService.queryPageByName(pageNum,pageSize,name);
    }

    @ApiOperation("分页列表查询(按照前端要求)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path",name= "page",value = "当前页码",required = true,dataType = "Integer"),
            @ApiImplicitParam(paramType = "path",name = "pageSize",value = "每页数据大小",required = true,dataType = "Integer"),
            @ApiImplicitParam(paramType = "query",name = "name",value = "用户名",required = false,dataType = "String"),
    })
    @GetMapping("/pagevoList/{page}/{pageSize}")
    public PageVO<OperatorUser> findPageVO(@PathVariable("page") int pageNum,
                                           @PathVariable("pageSize") int pageSize,
                                           @RequestParam(value = "name",required = false) String name){

        IPage<OperatorUser> page = operatorUserService.queryPageByName(pageNum, pageSize, name);

        //int i =1/0;

        PageVO<OperatorUser> pageVO = new PageVO<>(page);
        return pageVO;
    }

    @ApiOperation("登录接口")
    @PostMapping("/login")
    public Result login(@RequestBody LoginVO loginVO) {
        return operatorUserService.login(loginVO.getLoginName(),loginVO.getLoginPass());
    }

}
