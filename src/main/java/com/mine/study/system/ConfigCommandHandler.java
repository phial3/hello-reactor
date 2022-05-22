package com.mine.study.system;

import cn.hutool.core.bean.BeanDesc;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
import com.mine.study.MainApplication;
import com.mine.study.config.AppConfig;
import io.airlift.airline.Command;
import io.airlift.airline.Option;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Display config information
 * @since 2020/9/7
 * @author root
 */
@Command(name = "config", description = "Show server config")
public class ConfigCommandHandler extends CommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigCommandHandler.class);

    public ConfigCommandHandler(SystemMonitor monitor) {
        super(monitor);
    }

    @Option(name = {"-c", "--config"}, description = "Show AppConfig variables")
    private boolean showAppConfig;

    @Option(name = {"-e", "--environment"}, description = "Show system environment variables")
    private boolean showSystemEnvironment;

    @Option(name = {"-n", "--name"}, description = "Show the value of specified name")
    private String name;

    @Option(name = {"-p", "--profiles"}, description = "Show active profiles")
    private boolean showActiveProfiles;

    @Option(name = {"-s", "--system"}, description = "Show system properties")
    private boolean showSystemProperties;

    @Option(name = {"-t", "--thread"}, description = "Show all thread info")
    private boolean showAllThreads;

    @Option(name = {"-b", "--bean"}, description = "Show all bean info")
    private boolean showBeans;

    @Option(name = {"--set"}, title = "name value", description = "Set config", arity = 2)
    private List<String> nameAndValue;

    private AtomicBoolean beanRendered = new AtomicBoolean(false);

    private String allBeansDesc;

    @Override
    public String call() throws Exception {
        ConfigurableEnvironment environment = systemMonitor().environment();

        StringBuffer sb = new StringBuffer();

        if (showActiveProfiles) {
            title("Active Profiles", sb);
            body(0, sb, StringUtils.join(environment.getActiveProfiles(), ','));
        }

        if (StringUtils.isNoneBlank(name)) {
            title("Config(" + name + ")", sb);
            body(0, sb, name + " = " + environment.getProperty(name, "No the value to which the specified name is mapped"));
        } else {
            if (showAppConfig) showAppConfig(sb);
            if (showSystemProperties) showSystemProperties(sb);
            if (showSystemEnvironment) showSystemEnvironment(sb);
            if (showAllThreads) showAllThreads(sb);
            if (showBeans) showBeans(sb);
        }

        if (nameAndValue != null && nameAndValue.size() == 2) {
            ConfigurableApplicationContext context = systemMonitor().context();
            AppConfig config = context.getBean(AppConfig.class);
            String name = nameAndValue.get(0);
            String value = nameAndValue.get(1);

            PropertyDescriptor pd = BeanUtil.getPropertyDescriptor(AppConfig.class, name);
            if (pd != null) {
                Object oldValue = BeanUtil.getProperty(config, name);
                Class<?> returnType = pd.getReadMethod().getReturnType();
                if (environment.getConversionService().canConvert(String.class, returnType)) {
                    Object v = environment.getConversionService().convert(value, returnType);
                    BeanUtil.setProperty(config, name, v);
                    title("Set AppConfig" + name, sb);
                    body(0, sb, "Old Value: " + oldValue);
                    Object newValue = BeanUtil.getProperty(config, name);
                    body(0, sb, "New Value: " + newValue);
                } else {
                    sb.append("Can't convert value '" + value + "' to type " + returnType.getCanonicalName());
                }
            } else {
                sb.append("Can't set config:" + name);
            }
        }

        if (sb.length() == 0) {
            return "Nothing to do\r\n";
        }
        return sb.toString();
    }

    private void showBeans(StringBuffer sb) throws Exception {

        String projectPackage = MainApplication.class.getPackage().getName();

        if (!beanRendered.getAndSet(true)) {
            synchronized (this) {
                LOG.info("Initializing bean list...");
                ConfigurableApplicationContext context = systemMonitor().context();
                String names[] = context.getBeanDefinitionNames();

                // filter
                Map<String, Object> objectMap = new HashMap<>();

                for (int i = 0; i < names.length; i++) {
                    String name = names[i];
                    Object object = context.getBean(name);
                    if (object.getClass().getPackage().getName().startsWith(projectPackage)) {
                        objectMap.put(name, object);
                    }
                }

                Object data[][] = new Object[objectMap.size() + 1][3];
                data[0] = new String[] {
                        "Bean Name", "Bean Type", "Disposable"
                };

                final AtomicInteger count = new AtomicInteger(0);
                if (!objectMap.isEmpty()) {
                    objectMap.forEach((key, object) -> {
                        int row = count.incrementAndGet();
                        data[row] = new String[]{key, object.getClass().getSimpleName(), String.valueOf((object instanceof DisposableBean))};
                    });
                }

                DataTable dataTable = new DataTable(data, true);
                StringBuffer desc = new StringBuffer();
                dataTable.output(desc);
                allBeansDesc = desc.toString();
            }
        }
        title("All beans defined in package '" + projectPackage + "'", sb);
        sb.append(allBeansDesc);
    }

    private void showAllThreads(StringBuffer sb) throws Exception {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        ThreadGroup topGroup = group;

        // 遍历线程组树，获取根线程组
        while (group != null) {
            topGroup = group;
            group = group.getParent();
        }

        // 激活的线程数再加一倍，防止枚举时有可能刚好有动态线程生成
        int slackSize = topGroup.activeCount() * 2;
        Thread[] slackThreads = new Thread[slackSize];
        // 获取根线程组下的所有线程，返回的actualSize便是最终的线程数
        int actualSize = topGroup.enumerate(slackThreads);
        Thread[] atualThreads = new Thread[actualSize];
        // 复制slackThreads中有效的值到atualThreads
        System.arraycopy(slackThreads, 0, atualThreads, 0, actualSize);

        title("All Thread(" + atualThreads.length + ")", sb);

        String title[] = new String[] {
                "No.",
                "Thread ID",
                "Thread Name",
                "Alive",
                "Interrupted",
                "State",
                "Daemon",
                "Priority",
                "Group"
        };
        int[] maxLength = new int[title.length];
        Object [][] threadInfo = new Object[atualThreads.length + 1][title.length];

        for (int i = 0; i < title.length; i++) {
            threadInfo[0][i] = title[i];
            maxLength[i] = title[i].length();
        }

        for (int i = 0; i < atualThreads.length; i++) {
            Thread t = atualThreads[i];
            int row = i + 1;
            maxLength[0] = maxLength(maxLength[0], (i+1), threadInfo, row, 0);
            maxLength[1] = maxLength(maxLength[1], t.getId(), threadInfo, row, 1);
            maxLength[2] = maxLength(maxLength[2], t.getName(), threadInfo, row, 2);
            maxLength[3] = maxLength(maxLength[3], t.isAlive(), threadInfo, row, 3);
            maxLength[4] = maxLength(maxLength[4], t.isInterrupted(), threadInfo, row, 4);
            maxLength[5] = maxLength(maxLength[5], t.getState(), threadInfo, row, 5);
            maxLength[6] = maxLength(maxLength[6], t.isDaemon(), threadInfo, row, 6);
            maxLength[7] = maxLength(maxLength[7], t.getPriority(), threadInfo, row, 7);
            maxLength[8] = maxLength(maxLength[8], t.getThreadGroup(), threadInfo, row, 8);
        }

        new DataTable(maxLength, threadInfo, true).output(sb);
    }

    private int maxLength(int currentLen, Object o, Object [][] threadInfo, int row, int col) {
        String s = o.toString();
        int len = s.length();
        threadInfo[row][col] = s;
        if (len > currentLen) return len;
        return currentLen;
    }

    private void showSystemProperties(StringBuffer sb) throws Exception {
        ConfigurableEnvironment environment = systemMonitor().environment();
        Map<String, Object> propMap = environment.getSystemProperties();
        if (!propMap.isEmpty()) {
            title("System Properties", sb);
            propMap.forEach((key, value) -> body(0, sb, key + " = " + value));
        } else {
            sb.append("No system properties found");
        }
    }

    private void showSystemEnvironment(StringBuffer sb) throws Exception {
        ConfigurableEnvironment environment = systemMonitor().environment();
        Map<String, Object> propMap = environment.getSystemEnvironment();
        if (!propMap.isEmpty()) {
            title("System Environment", sb);
            propMap.forEach((key, value) -> body(0, sb, key + " = " + value));
        } else {
            sb.append("No system environment found");
        }
    }

    private void showAppConfig(StringBuffer sb) throws Exception {
        ConfigurableApplicationContext context = systemMonitor().context();
        // AppConfig config = context.getBean(AppConfig.class);

        BeanDesc beanDesc = BeanUtil.getBeanDesc(AppConfig.class);
        Map<String, PropDesc> descMap = beanDesc.getPropMap(false);

        //Map<String, String> desc = BeanUtil.getBeanDesc(AppConfig.class);
        if (!descMap.isEmpty()) {
            title("AppConfig", sb);
            Set<Map.Entry<String, PropDesc>> set = descMap.entrySet().stream()
                    .filter(ent -> !"class".equals(ent.getKey()))
                    .collect(Collectors.toSet());

            if (set.isEmpty()) {
                sb.append("No configuration found");
            } else {
                Object[][] data = new Object[set.size() + 1][2];
                data[0][0] = "Name";
                data[0][1] = "Value";
                int row = 0;
                for (Map.Entry<String, PropDesc> entry : set) {
                    row++;
                    data[row][0] = entry.getKey();
                    data[row][1] = entry.getValue();
                }
                new DataTable(data, true).output(sb);
            }
        } else {
            sb.append("No configuration found");
        }
    }

    private void title(String title, StringBuffer sb) {
        if (sb.length() > 0) sb.append("\r\n");
        sb.append(title).append(":\r\n");
    }

    private void body(int retracts, StringBuffer sb, String content) {
        for (int i = 0; i < retracts; i++) {
            sb.append("\t");
        }
        sb.append(content).append("\r\n");
    }
}
