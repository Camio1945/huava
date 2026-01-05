package cn.huava.common.graalvm;

import cn.hutool.v7.core.io.file.FileUtil;
import cn.hutool.v7.core.lang.Assert;
import cn.hutool.v7.core.thread.ThreadUtil;
import cn.hutool.v7.json.JSONObject;
import cn.hutool.v7.json.JSONUtil;
import org.junit.jupiter.api.Test;

class SerializationConfigGeneratorTest {

  @Test
  void generateSerializationConfigFile() {
    new SerializationConfigGenerator();
    SerializationConfigGenerator.generateSerializationConfigFile();
    int maxTryTimes = 100;
    int triedTimes = 0;
    while (triedTimes < maxTryTimes && SerializationConfigGenerator.jsonFilePath == null) {
      triedTimes++;
      ThreadUtil.safeSleep(5);
    }
    Assert.notBlank(SerializationConfigGenerator.jsonFilePath);
    String content = FileUtil.readUtf8String(SerializationConfigGenerator.jsonFilePath);
    JSONObject jsonObject = JSONUtil.parseObj(content);
    Assert.notEmpty(jsonObject.getJSONArray("lambdaCapturingTypes"));
  }
}
