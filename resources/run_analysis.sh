#!/usr/bin/env sh

cd csound
csound analyze_and_process.csd && csound analyze_and_print_to_file.csd
