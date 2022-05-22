#!/bin/bash
############################################################
# Rollback application
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

STEP="$1"

if [[ -z $STEP ]]; then
	echo "ERROR: The step is not specified"
	echo "Usage rollback n"
	exit 1
fi

if [[ $STEP -gt 0 ]]; then
	echo "Rollback $STEP step(s)"
else
	echo "ERROR: The step is must be a positive integer and great than 0"
	echo "Usage rollback n"
	exit 2
fi

# check
if [[ ! -d "$ROLLBACK_DIR" ]]; then
	echo "Rollback fail: The rollback dir does not exists: $ROLLBACK_DIR"
	exit 3	
fi

BACKUP_VERSION="`ls -1 $ROLLBACK_DIR |sort -nr|head -n $STEP|tail -n1`"

BACKUP_DIR=""

if [[ -n "$BACKUP_VERSION" ]]; then
	BACKUP_DIR="$ROLLBACK_DIR/$BACKUP_VERSION"
else
	echo "Rollback fail: The ROLLBACK dir is empty: $ROLLBACK_DIR"
	exit 3
fi

if [[ ! -f "$BACKUP_DIR/$APP_JAR_FILE" ]]; then
    echo "Rollback fail: The ROLLBACK file does not exists: $ROLLBACK_DIR"
    exit 3
fi

# stop app first
$BIN_DIR/stop.sh

# remove current version
rm -rf $APP_DIR/$APP_JAR_FILE
cp $BACKUP_DIR/$APP_JAR_FILE $APP_DIR
echo "Backup to version $BACKUP_VERSION (`cat $BACKUP_DIR/backup-time`)"

# start app
$BIN_DIR/start.sh

exit 0