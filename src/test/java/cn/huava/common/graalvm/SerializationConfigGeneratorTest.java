package cn.huava.common.graalvm;

import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.thread.ThreadUtil;
import org.dromara.hutool.json.JSONObject;
import org.dromara.hutool.json.JSONUtil;
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
