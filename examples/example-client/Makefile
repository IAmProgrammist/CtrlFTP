PYTHON_DIR=../../.venv
UI_COMPILE_OUTPUT=./ui_compiled
SHELL:=/bin/bash
# UI_FILE=ui/add.ui

clean:
	rm -rf ${UI_COMPILE_OUTPUT}

install_requirments: requirments.txt
	$(PYTHON_DIR)/bin/pip3 install -r ./requirments.txt

compileui: $(find ui -type f) install_requirments
	@echo $^
	mkdir -p ${UI_COMPILE_OUTPUT}

	for file in $(shell find ui -type f); do \
		$(shell find ${PYTHON_DIR}/lib -wholename */PySide6/*/uic) "$$file" -g python -o $(patsubst ui/%.ui, ${UI_COMPILE_OUTPUT}/%.py, "$$file"); \
	done