package cn.huava.sys.service.oauth2authorization;

import cn.huava.sys.mapper.Oauth2AuthorizationMapper;
import cn.huava.sys.pojo.po.Oauth2AuthorizationPo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@AllArgsConstructor
public class Oauth2AuthorizationAceService
    extends ServiceImpl<Oauth2AuthorizationMapper, Oauth2AuthorizationPo> {}
