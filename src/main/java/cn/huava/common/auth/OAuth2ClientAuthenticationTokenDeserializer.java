/*
 * Copyright (c) 2020-2030 liuhung<ov_001@163.com>
 *
 * quafer Engine licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * quafer Engine 采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改 quafer Engine 源码头部的版权声明。
 * 3.请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://git.liuhung.com/gz/quafer-engine
 * 5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://git.liuhung.com/gz/quafer-engine
 * 6.若您的项目无法满足以上几点，可申请商业授权
 */

package cn.huava.common.auth;

import cn.huava.sys.auth.SysUserUserDetails;
import cn.huava.sys.pojo.po.SysUser;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

/**
 * Description: OAuth2ClientAuthenticationTokenDeserializer
 *
 * @author : liuh
 * @date : 2022/10/24 14:43
 */
public class OAuth2ClientAuthenticationTokenDeserializer
    extends JsonDeserializer<OAuth2ClientAuthenticationToken> {

  @Override
  public OAuth2ClientAuthenticationToken deserialize(
      JsonParser jsonParser, DeserializationContext context) throws IOException, JacksonException {

    ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
    JsonNode jsonNode = mapper.readTree(jsonParser);
    return deserialize(jsonParser, mapper, jsonNode);
  }

  private OAuth2ClientAuthenticationToken deserialize(
      JsonParser parser, ObjectMapper mapper, JsonNode root) throws IOException {
    RegisteredClient registeredClient =
        JsonNodeUtils.findValue(root, "registeredClient", new TypeReference<>() {}, mapper);
    JsonNode userJson = root.get("details").get("sysUser");
    SysUser sysUser = new SysUser();
    sysUser.setLoginName(userJson.get("loginName").asText());
    sysUser.setUserName(userJson.get("userName").asText());
    sysUser.setPassword(userJson.get("password").asText());
    SysUserUserDetails details = new SysUserUserDetails(sysUser, new HashSet<>());

    String credentials = JsonNodeUtils.findStringValue(root, "credentials");
    ClientAuthenticationMethod clientAuthenticationMethod =
        JsonNodeUtils.findValue(
            root, "clientAuthenticationMethod", new TypeReference<>() {}, mapper);

    OAuth2ClientAuthenticationToken clientAuthenticationToken =
        new OAuth2ClientAuthenticationToken(
            registeredClient, clientAuthenticationMethod, credentials);
    clientAuthenticationToken.setDetails(details);
    return clientAuthenticationToken;
  }
}
