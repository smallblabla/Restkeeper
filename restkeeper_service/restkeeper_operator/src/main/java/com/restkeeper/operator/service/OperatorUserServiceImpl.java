package com.restkeeper.operator.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.restkeeper.operator.entity.OperatorUser;
import com.restkeeper.operator.mapper.OperatorUserMapper;
import com.restkeeper.utils.JWTUtil;
import com.restkeeper.utils.MD5CryptUtil;
import com.restkeeper.utils.Result;
import com.restkeeper.utils.ResultCode;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.HashMap;

//@Service("operatorUserService")
@Service(version = "1.0.0",protocol = "dubbo")
@RefreshScope
public class OperatorUserServiceImpl extends ServiceImpl<OperatorUserMapper, OperatorUser> implements IOperatorUserService{

    //token加密
    @Value("${gateway.secret}")
    private String secret;

    //根据name进行分页数据查询
    @Override
    public IPage<OperatorUser> queryPageByName(int pageNum, int pageSize, String name) {


        IPage<OperatorUser> page = new Page<>(pageNum,pageSize);

        QueryWrapper<OperatorUser> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(name)){
            queryWrapper.like("loginname",name);
        }
        return this.page(page,queryWrapper);
    }


    public Result login(String username, String password) {
        Result result = new Result();
        OperatorUser operatorUser = this.getOne(new QueryWrapper<OperatorUser>().eq("loginname",username));
        if (operatorUser == null){
            result.setStatus(ResultCode.error);
            result.setDesc("用户不存在");
            return result;
        }
        if (StringUtils.isEmpty(password)){
            result.setStatus(ResultCode.error);
            result.setDesc("密码不能为空");
            return result;
        }
        /**密码**/
        String salt = MD5CryptUtil.getSalts(password);
        if (!operatorUser.getLoginpass().equals(Md5Crypt.md5Crypt(password.getBytes(),salt))){
            result.setStatus(ResultCode.error);
            result.setDesc("密码错误");
            return result;
        }
        /**token信息**/
        HashMap<Object, Object> tokenInfo = Maps.newHashMap();
        tokenInfo.put("loginName",operatorUser.getLoginname());

        String token = null;
        try{
            //JWTUtil 是restkeeper_common提供的工具类
            token = JWTUtil.createJWTByObj(tokenInfo,secret);
        }catch (Exception e){
            log.error("加密失败"+e.getMessage());
            result.setStatus(ResultCode.error);
            result.setDesc("生成令牌失败");
            return result;
        }
        result.setStatus(ResultCode.success);
        result.setData(operatorUser);
        result.setDesc("登录成功");
        return result;
    }
}
