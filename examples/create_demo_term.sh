#!/bin/bash

XDG_CONFIG_HOME=~/.config_presentation xfce4-terminal --maximize --hide-menubar --disable-server -T "VmAnonymous3" -e "`pwd`/create_demo_tab.sh vmanonymous3" --tab -T "SA" -e "`pwd`/create_demo_tab.sh sa" --tab -T "Instrument2" -e "`pwd`/create_demo_tab.sh instrument2" --tab -T "jcmd" -e "`pwd`/create_demo_tab.sh jcmd" &

