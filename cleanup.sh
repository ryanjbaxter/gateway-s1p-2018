#!/usr/bin/env bash

cf d blueorgreengateway -f
cf d blueorgreenfrontend -f
cf d blueservice -f
cf d greenservice -f
cf d yellowservice -f
cf delete-orphaned-routes -f

#cf ds bluegreen-registry -f