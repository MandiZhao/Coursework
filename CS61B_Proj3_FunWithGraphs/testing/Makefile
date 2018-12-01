# This makefile is defined to give you the following targets:
#
#    default: Same as check
#    make-check: Run integration tests on make application.
#    make-trip: Run integration tests on trip application.
#    check: Same as make-check + make-trip 
#    clean: Remove extraneous files.
#
# You can use this file without understanding most of it, of course, but
# I strongly recommend that you try to figure it out, and where you cannot,
# that you ask questions.  The Lab Reader contains documentation.

SHELL = /bin/bash

HERE := $(shell pwd)

# If your python has a different name, do NOT change this line (you will
# fail our 'make check' tests.  Instead, if for example your python3 is
# simply called 'python', run 'make check' as
#      make PYTHON=python check
PYTHON = python3

# These class paths have the form
#      "<UNIX/MAC CLASSPATH>:;<WINDOWS CLASSPATH"


.PHONY: default check clean

# First, and therefore default, target.
default:
	$(MAKE) -C .. 
	$(MAKE) check

check:
	$(MAKE) -C make check
	$(MAKE) -C trip check

# 'make clean' will clean up stuff you can reconstruct.
clean:
	$(MAKE) -C trip clean
	$(MAKE) -C make clean


