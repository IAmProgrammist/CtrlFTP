#!/bin/bash

PYTHON_DIR=../../.venv
UI_COMPILE_OUTPUT=./ui_compiled

${PYTHON_DIR}/bin/pip3 install -r ./requirments.txt

mkdir -p ${UI_COMPILE_OUTPUT}

for file in $(find ui -type f); do \
  filename=$(basename ${file}); \
  $(find ${PYTHON_DIR}/lib -wholename */PySide6/*/uic) ${file} -g python -o ${UI_COMPILE_OUTPUT}/"${filename%.*}".py; \
done