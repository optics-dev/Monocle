#!/bin/bash
set -e

git config --global user.email "truffaut.julien@gmail.com"
git config --global user.name "Julien Truffaut"
git config --global push.default simple

sbt docs/publishMicrosite