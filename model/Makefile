# This is a simple Makefile that generates client library source code
# for Truward APIs using Protocol Buffers
# However, it does not compile the generated code into final
# libraries that can be directly used with application code.
#
# Syntax example: make OUTPUT=./target LANGUAGE=java
#

# Choose the output directory
OUTPUT ?= ./target

# Choose the target language.
LANGUAGE ?= cpp

# Choose grpc plugin
# TODO: enable grpc support
#GRPCPLUGIN ?= /usr/local/bin/grpc_$(LANGUAGE)_plugin

# Choose the proto include directory.
PROTOINCLUDE ?= /usr/local/include

# Choose protoc binary
PROTOC ?= protoc

# Compile the entire repository
#
# NOTE: if "protoc" command is not in the PATH, you need to modify this file.
#

ifeq ($(LANGUAGE),go)
$(error Go source files are not generated from this repository.)
# TODO: enable Go - see example: /usr/local/include
endif

FLAGS+= --proto_path=.:$(PROTOINCLUDE)

# TODO: remove when grpc enabled
FLAGS+= --$(LANGUAGE)_out=$(OUTPUT)

# TODO: enable grpc
#FLAGS+= --$(LANGUAGE)_out=$(OUTPUT) --grpc_out=$(OUTPUT)
#FLAGS+=	--plugin=protoc-gen-grpc=$(GRPCPLUGIN)

SUFFIX:= pb.cc

DEPS:= $(shell find truward $(PROTOINCLUDE)/google/protobuf -type f -name '*.proto' | sed "s/proto$$/$(SUFFIX)/")

all: $(DEPS)

%.$(SUFFIX):  %.proto
	mkdir -p $(OUTPUT)
	$(PROTOC) $(FLAGS) $*.proto

clean:
	rm $(patsubst %,$(OUTPUT)/%,$(DEPS)) 2> /dev/null
	rm -rd $(OUTPUT)
