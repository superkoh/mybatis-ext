package com.github.superkoh.mybatis.generator;

import java.io.File;
import java.util.ArrayList;
import lombok.val;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

public class Generator {

  public void run(String configFileName) throws Exception {
    val url = Thread.currentThread().getContextClassLoader().getResource(configFileName + ".xml");
    val warnings = new ArrayList<String>();
    assert url != null;
    val configFile = new File(url.getPath());
    val cp = new ConfigurationParser(warnings);
    val config = cp.parseConfiguration(configFile);
    val callback = new DefaultShellCallback(true);
    val myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
    myBatisGenerator.generate(null);
  }
}
