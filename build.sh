#!/bin/sh

# export DISPLAY=:99.0
# Xvfb :99 &
# echo $UID
bundle install && bundle exec rake cucumber
