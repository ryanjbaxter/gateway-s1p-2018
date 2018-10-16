#!/usr/bin/env bash

cf d colorgateway -f
cf d colorfrontend -f
cf d blueservice -f
cf d greenservice -f
cf d yellowservice -f
cf delete-orphaned-routes -f

#cf ds color-registry -f