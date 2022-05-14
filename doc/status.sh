# 检测新 Pod 是否成功上线,发布成功则系统统计/消息通知,失败则输出日志后退出
FLAG=0
[ -z $OBJ ] && OBJ=deployment
if [ "$LANGUAGE" == "JAVA" ]; then
    [ -n "$springboot" ] && echo -e "\033[1;36m基于 Dev/Ops 之间的约定,探测新启动 Pod 的 http://127.0.0.1:7999/actuator/health/liveness 访问返回状态码是否 200\033[0m" || echo -e "\033[1;36m基于 Dev/Ops 之间的约定,探测新启动 Pod 的 http://127.0.0.1:8080/heart.jsp 访问返回状态码是否 200\033[0m"
    for pack in $PACKAGES
    do
        export NAME=${pack%.*}
        ATTEMPTS=0
        ROLLOUT_STATUS_CMD="kubectl --kubeconfig /root/.kube/$NS rollout status $OBJ/$NAME -n $NS --timeout=180s"
        until $ROLLOUT_STATUS_CMD || [ $ATTEMPTS -eq 15 ]; do
            $ROLLOUT_STATUS_CMD
            ATTEMPTS=$((ATTEMPTS + 1))
        done
        $ROLLOUT_STATUS_CMD | grep 'success\|complete' && FLAG=1
    done
else
    echo $NS
    echo "等待新 Pod 状态检测..."
    ATTEMPTS=0
    ROLLOUT_STATUS_CMD="kubectl --kubeconfig /root/.kube/$NS rollout status $OBJ/$NAME -n $NS --timeout=120s"
    until $ROLLOUT_STATUS_CMD || [ $ATTEMPTS -eq 3 ]; do
        $ROLLOUT_STATUS_CMD
        ATTEMPTS=$((ATTEMPTS + 1))
    done
    $ROLLOUT_STATUS_CMD | grep 'success' && FLAG=1
fi

if [ $FLAG -gt 0 ];then
    source $ROOT_CONFIG/bin/utils.sh
    echo -e "\033[1;36m$MOTD  [ $NS ]\033[0m"
else
    export STATE="FAIL"
    export MOTD="看开点兄弟,程序员才是生产力,所以应用出了问题,那么责任一定在于你"
    source $ROOT_CONFIG/bin/utils.sh
    echo -e "\033[31m新 Pod 状态检测异常,此次发布失败\033[0m"
    echo -e "\033[1;36m$MOTD [ $NS ]\033[0m"
    exit 1
fi
