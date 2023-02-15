package com.touchbiz.chatgpt.infrastructure.utils;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

/**
 * @description yaml基本操作工具类
 * @date 2020-04-15
 */
@Slf4j
public class YamlUtils {

    /**
     * 导出yaml时无论有没有值都忽略的属性
     **/
    private final static ImmutableList<String> IGNORE_PROPERTIES_WHEN_DUMP_YAML = ImmutableList.of("resourceVersion");

    private static Yaml yaml;

    static {
        initYaml();
    }

    /**
     * 初始化Yaml
     */
    private static void initYaml() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        yaml = new Yaml(new Representer() {
            @Override
            protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
                // 如果属性为null空数组，忽略它
                boolean beIgnored = (propertyValue instanceof Collection && ((Collection) propertyValue).size() == 0);
                if (propertyValue == null || beIgnored) {
                    return null;
                } else {
                    if (IGNORE_PROPERTIES_WHEN_DUMP_YAML.contains(property.getName())) {
                        return null;
                    }
                    return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
                }
            }
        }, options);
    }

    /**
     * javabean转yaml字符串
     *
     * @param resource 对象
     * @return String
     */
    public static String convertToYaml(Object resource) {
        return yaml.dump(resource);
    }

    public static Object yamlToObject(String yamlStr) {
        return yaml.load(yamlStr);
    }

    /**
     * javabean导出成yaml文件
     *
     * @param resource 资源名称
     * @param filePath 文件路径
     * @return void
     */
    public static void dumpToYaml(Object resource, String filePath) {
        try (Writer writer = new FileWriter(filePath)) {
            yaml.dump(resource, writer);
        } catch (IOException e) {
            log.error( "dumpToYaml error:", e);
        }
    }


}