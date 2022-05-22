package com.mine.study.config;

import com.mine.study.utils.NetUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 系统配置。接受YAML文件中的配置
 * @since 2019-10-10
 * @author root
 * @vendor root.org
 * @generator consolegen 1.0
 * @manufacturer https://root.org
 */
@Component
@ConfigurationProperties(prefix = "app-config")
public class AppConfig implements InitializingBean {

    private String env;

    private String httpProtocol = "HTTP";

    private String domain;

    private String privateKey;

    private String publicKey;

    private String tokenCookieName;

    private String uploadDir;

    private String systemName;

    private String appName;

    private String aesSecretKey;

    private String aesIv;

    private String apiVerifySecretKey;

    private int webServerPort;

    private String nodeName;

    private String buildVersion;

    private boolean initMenu = false;


    public String getEnv() {
        return env;
    }

    /**
     * @param env The unique spring active profile
     */
    public void setEnv(String env) {
        this.env = env;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getTokenCookieName() {
        return tokenCookieName;
    }

    public void setTokenCookieName(String tokenCookieName) {
        this.tokenCookieName = tokenCookieName;
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getAesSecretKey() {
        return aesSecretKey;
    }

    public void setAesSecretKey(String aesSecretKey) {
        this.aesSecretKey = aesSecretKey;
    }

    public String getAesIv() {
        return aesIv;
    }

    public void setAesIv(String aesIv) {
        this.aesIv = aesIv;
    }

    public String getApiVerifySecretKey() {
        return apiVerifySecretKey;
    }

    public void setApiVerifySecretKey(String apiVerifySecretKey) {
        this.apiVerifySecretKey = apiVerifySecretKey;
    }


    public int getWebServerPort() {
        return webServerPort;
    }

    public void setWebServerPort(int webServerPort) {
        this.webServerPort = webServerPort;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getHttpProtocol() {
        return httpProtocol;
    }

    public void setHttpProtocol(String httpProtocol) {
        this.httpProtocol = httpProtocol;
    }

    public String getNodeName() {
        if (StringUtils.isBlank(this.nodeName)) this.nodeName = NetUtils.guessServerIp();
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }


    /**
     * 获取 initMenu
     *
     * @return initMenu
     */
    public boolean isInitMenu() {
        return initMenu;
    }

    /**
     * 设置 initMenu
     *
     * @param initMenu initMenu 值
     */
    public void setInitMenu(boolean initMenu) {
        this.initMenu = initMenu;
    }
}
