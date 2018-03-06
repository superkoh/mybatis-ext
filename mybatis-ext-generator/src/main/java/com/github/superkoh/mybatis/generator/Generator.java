package com.github.superkoh.mybatis.generator;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

public class Generator {

  public void run(String configFileName) throws Exception {
    URL url = Thread.currentThread().getContextClassLoader()
        .getResource(configFileName + ".xml");
    List<String> warnings = new ArrayList<>();
    assert url != null;
    File configFile = new File(url.getPath());
    ConfigurationParser cp = new ConfigurationParser(warnings);
    Configuration config = cp.parseConfiguration(configFile);
    DefaultShellCallback callback = new DefaultShellCallback(true);
    MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
    myBatisGenerator.generate(null);
  }
}
