package cn.huava.sys.service;

import cn.huava.sys.mapper.SysUserMapper;
import cn.huava.sys.pojo.po.SysUser;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 * Type parameter 'cn.huava.sys.service.SysUserService' is not within its bound; should extend
 * 'ServiceImpl<BaseMapper<SysUser>,SysUser>'
 *
 * @author Camio1945
 */
@Slf4j
@Service
@AllArgsConstructor
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> {

}
