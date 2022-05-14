get_json_value(){
  local json=$1
  local key=$2

  if [[ -z "$3" ]]; then
    local num=1
  else
    local num=$3
  fi

  local value=$(echo "${json}" | awk -F"[,:}]" '{for(i=1;i<=NF;i++){if($i~/'${key}'\042/){print $(i+1)}}}' | tr -d '"' | sed -n ${num}p)
  echo ${value}
}
SUM(){
    PRIVATE_TOKEN=TWAPqsfjLxxx
    export TOKEN=$(get_json_value $(curl -s --header "PRIVATE-TOKEN: $PRIVATE_TOKEN" "$CI_API_V4_URL/projects/$CI_PROJECT_ID/triggers") token)
    [ -z "$TOKEN" ] && export TOKEN=$( get_json_value $(curl -s --request POST --header "PRIVATE-TOKEN: $PRIVATE_TOKEN" --form description="CI" "$CI_API_V4_URL/projects/$CI_PROJECT_ID/triggers") token)
    export trigger="curl -X POST -F token="$TOKEN" -F ref="$CI_COMMIT_REF_NAME" -F \"variables[NS]=$NS\" $CI_API_V4_URL/projects/$CI_PROJECT_ID/trigger/pipeline"

    # 当前 NS 一天的发布记录
    [ -d $ROOT_NS/.date ] || mkdir -p $ROOT_NS/.date && [ -e $ROOT_NS/.date/$(date +%F) ] || touch $ROOT_NS/.date/$(date +%F)
    export NUM1=$(expr $(wc -l $ROOT_NS/.date/$(date +%F)|awk '{print $1}') + 1)
    echo -e "$NUM1. $(date +%F-%X)  deploy [ $CI_PROJECT_PATH-$CI_COMMIT_SHORT_SHA-$CI_COMMIT_REF_NAME ] to [ $(echo $ROOT_K8S|awk -F'/' '{print $NF}').$NS ]  ( $GITLAB_USER_NAME )  $CI_PROJECT_URL  $trigger" >> $ROOT_NS/.date/$(date +%F)
    echo -e "$CI_PROJECT_URL  $trigger  $PACKAGES" > $ROOT_NS/.date/.Num-$CI_PROJECT_ID
    cat $ROOT_NS/.date/.Num-* > $ROOT_NS/.date/pipeline

    # 所有 NS 一天的发布记录
    [ -d $ROOT_CONFIG/.date ] || mkdir -p $ROOT_CONFIG/.date && [ -e $ROOT_CONFIG/.date/$(date +%F) ] || touch $ROOT_CONFIG/.date/$(date +%F)
    export NUM2=$(expr $(wc -l $ROOT_CONFIG/.date/$(date +%F)|awk '{print $1}') + 1)
    echo -e "$NUM2. $(date +%F-%X)  deploy [ $CI_PROJECT_PATH-$CI_COMMIT_SHORT_SHA-$CI_COMMIT_REF_NAME ] to [ $(echo $ROOT_K8S|awk -F'/' '{print $NF}').$NS ]  ( $GITLAB_USER_NAME )  $CI_PROJECT_URL  $trigger" >> $ROOT_CONFIG/.date/$(date +%F)
    echo -e "$CI_PROJECT_URL  $trigger  $PACKAGES" > $ROOT_CONFIG/.date/.Num-$CI_PROJECT_ID
    cat $ROOT_CONFIG/.date/.Num-* > $ROOT_CONFIG/.date/pipeline

    # 历史发布次数总计
    [ -e $ROOT_CONFIG/.num ] || echo 0 > $ROOT_CONFIG/.num
    export NUM3=$(cat $ROOT_CONFIG/.num)
    let "NUM3+=1"
    echo $NUM3 > $ROOT_CONFIG/.num
}
MSG(){
    [ $DEPLOY -eq 1 ] && export DEPLOY_MSG="新镜像"
    export MOTD=$(curl -s motd.xabc.io --referer "CI/CD")
    export KS=$(echo $ROOT_K8S|awk -F'/' '{print $NF}').$NS
    # 定向消息通知
    export MSG="$(date +%F-%X) $CI_PIPELINE_URL $STATE\n\n$CI_COMMIT_MESSAGE\n\ndeploy [ $CI_PROJECT_PATH-$CI_COMMIT_SHORT_SHA-$CI_COMMIT_REF_NAME ] to [ $(echo $ROOT_K8S|awk -F'/' '{print $NF}').$NS ] ( $GITLAB_USER_NAME )\n$CI_PROJECT_NAME [ $CI_PROJECT_REPOSITORY_LANGUAGES ] [ $LANGUAGE ]\n$PACKAGES $ROLE\n$ALL_HOST\n\n$MOTD"
    [ -e $ROOT_NS/.kube/msg.sh ] && $ROOT_NS/.kube/msg.sh &>/dev/null || echo ""
    # 全局消息通知
    curl -s 'https://oapi.dingtalk.com/robot/send?access_token=xxx3cd61a0f0a1058d0501f' -H 'Content-Type: application/json' -d "{\"msgtype\": \"text\", \"text\": { \"content\": \"$KS | $NUM1 | $NUM2 | $NUM3 [ $CI_PROJECT_NAMESPACE ] $DEPLOY_MSG [ $LANGUAGE ]\n$MSG\" }}" &>/dev/null
}
main(){
    SUM
    MSG
}
main
