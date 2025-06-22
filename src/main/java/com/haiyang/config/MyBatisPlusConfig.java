package com.haiyang.config;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
@MapperScan("com.haiyang.mapper")  //所有Mapper接口就不需要手动添加@Mapper
public class MyBatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //分页  方言
        interceptor.addInnerInterceptor( new PaginationInnerInterceptor( DbType.MYSQL ));
        //防止全表更新和删除
        interceptor.addInnerInterceptor( new BlockAttackInnerInterceptor());
        return interceptor;
    }
}
