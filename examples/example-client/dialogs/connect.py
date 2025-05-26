from PySide6.QtWidgets import QDialog

from ui_compiled.connect import Ui_Dialog as UiConnect

class Connect(QDialog):
    def __init__(self, parent=None) -> None:
        super().__init__(parent)
        self.ui = UiConnect()
        self.ui.setupUi(self)

    def get_options(self):
        return {
            "server_address": self.ui.
        }