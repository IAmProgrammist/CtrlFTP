#!/bin/bash

PYTHON_DIR="${PYTHON_DIR:-.venv}"
UI_COMPILE_OUTPUT=./uicompiled

echo "Python dir: ${PYTHON_DIR}"

echo "Installing requirments"
${PYTHON_DIR}/bin/pip3 install -r ./requirments.txt

echo "Compiling .qrc file"
$(find ${PYTHON_DIR}/lib -wholename */PySide6/*/rcc) resources.qrc -g python -o resources_rc.py; \

echo "Compiling .ui files"
for file in $(find ui -type f); do \
  filename=$(basename ${file}); \
  $(find ${PYTHON_DIR}/lib -wholename */PySide6/*/uic) ${file} -g python -o ${UI_COMPILE_OUTPUT}/"${filename%.*}".py; \
done

echo "Setup completed! Now you can run main"