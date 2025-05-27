#!/bin/bash

PYTHON_DIR=.venv
UI_COMPILE_OUTPUT=./uicompiled

${PYTHON_DIR}/bin/pip3 install -r ./requirments.txt

mkdir -p ${UI_COMPILE_OUTPUT}

$(find ${PYTHON_DIR}/lib -wholename */PySide6/*/rcc) resources.qrc -g python -o resources_rc.py; \

for file in $(find ui -type f); do \
  filename=$(basename ${file}); \
  $(find ${PYTHON_DIR}/lib -wholename */PySide6/*/uic) ${file} -g python -o ${UI_COMPILE_OUTPUT}/"${filename%.*}".py; \
done