#!/bin/sh
set -e
if test "$CONFIGURATION" = "Debug"; then :
  cd /Users/jinny/MSD6010/my6010FinalProject/xcode
  make -f /Users/jinny/MSD6010/my6010FinalProject/xcode/CMakeScripts/ReRunCMake.make
fi
if test "$CONFIGURATION" = "Release"; then :
  cd /Users/jinny/MSD6010/my6010FinalProject/xcode
  make -f /Users/jinny/MSD6010/my6010FinalProject/xcode/CMakeScripts/ReRunCMake.make
fi
if test "$CONFIGURATION" = "MinSizeRel"; then :
  cd /Users/jinny/MSD6010/my6010FinalProject/xcode
  make -f /Users/jinny/MSD6010/my6010FinalProject/xcode/CMakeScripts/ReRunCMake.make
fi
if test "$CONFIGURATION" = "RelWithDebInfo"; then :
  cd /Users/jinny/MSD6010/my6010FinalProject/xcode
  make -f /Users/jinny/MSD6010/my6010FinalProject/xcode/CMakeScripts/ReRunCMake.make
fi

