#!/usr/bin/env sh

cd `pwd`/csound
echo `pwd`
csound analyze_and_process.csd && csound analyze_results.csd
