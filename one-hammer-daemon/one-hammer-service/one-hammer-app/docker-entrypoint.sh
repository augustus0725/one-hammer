#!/usr/bin/env bash

#
# /*
#  * Copyright (c) 2022. Jiangsu Hongwangweb Technology Co.,Ltd.
#  * Licensed under the private license, you may not use this file except you get the License.
#  */
#

cd /opt/app || exit
java -Xms32m -Xmx512m -jar one-hammer-app-1.0-SNAPSHOT.jar --spring.profiles.active=${1}
