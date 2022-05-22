#!/bin/bash
############################################################
# Deploy application
# Copy new jar file and backup jar file
# @author root
# @Email root@exam.com
############################################################

# init app

# custom scp home
SCP_HOME="/Users/root/Desktop/dddddddddd/scp"

BIN_DIR="$(cd `dirname $0`; pwd)"
APP_DIR="`dirname $BIN_DIR`"
APP_JAR_FILE="@maven.project.name@-@maven.project.version@.jar"
ROLLBACK_DIR="$APP_DIR/ROLLBACK"

# backup apps
if [[ ! -d "$ROLLBACK_DIR" ]]; then
	mkdir -p $ROLLBACK_DIR
fi

MAX_VERSION="`ls -1 $ROLLBACK_DIR |sort -nr|head -n1`"

if [[ -n "$MAX_VERSION" ]]; then
	echo "Max version: $MAX_VERSION"
else
	MAX_VERSION=0
fi

BACKUP_VERSION=$(($MAX_VERSION+1))
BACKUP_DIR="$ROLLBACK_DIR/$BACKUP_VERSION"

if [[ ! -f "$SCP_HOME/$APP_JAR_FILE" ]]; then
	echo "Deploy fail: New app file dose not exists: $SCP_HOME/$APP_JAR_FILE"
	exit 1
fi

# stop app first
$BIN_DIR/stop.sh

# mv current file to BACKUP_DIR
if [[ -f "$APP_DIR/$APP_JAR_FILE" ]]; then
	if [[ ! -d "$BACKUP_DIR" ]]; then
		mkdir -p $BACKUP_DIR
	fi
	echo "Backup version: $BACKUP_VERSION"

	mv $APP_DIR/$APP_JAR_FILE $BACKUP_DIR
	NOW_TIME=`date "+%Y-%m-%d %H:%M:%S"`
	echo "$NOW_TIME" > $BACKUP_DIR/backup-time
else
	echo "WARN: app file dose not exists and backup will be ignored: $APP_DIR/$APP_JAR_FILE"
fi

mv $SCP_HOME/$APP_JAR_FILE $APP_DIR


# start app
$BIN_DIR/start.sh


exit 0