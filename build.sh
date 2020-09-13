#!/bin/sh

echo $UID
(bundle check || bundle install) && bundle exec rake cucumber
