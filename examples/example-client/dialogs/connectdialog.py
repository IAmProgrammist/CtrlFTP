from PySide6.QtGui import QIntValidator
from PySide6.QtWidgets import QDialog

from uicompiled.connect import Ui_Dialog as UiConnect

class ConnectDialog(QDialog):
    def __init__(self, parent=None) -> None:
        super().__init__(parent)
        self.ui = UiConnect()
        self.ui.setupUi(self)
        self.ui.buttonConnect.clicked.connect(self.selected)
        self.ui.inputIsPassive.checkStateChanged.connect(self.is_passive_selected)
        self.ui.inputServerPort.setValidator(QIntValidator(0, 65536))
        self.ui.inputDataPort.setValidator(QIntValidator(0, 65536))
        self.is_passive_selected()

    def get_options(self):
        return {
            "server_address": self.ui.inputServerAddress.text(),
            "server_port": self.ui.inputServerPort.text(),
            "is_passive": self.ui.inputIsPassive.isChecked(),
            "user_port": self.ui.inputDataPort.text(),
            "user_address": self.ui.inputDataAddress.text(),
            "user_login": self.ui.inputLogin.text(),
            "user_password": self.ui.inputPassword.text()
        }

    def is_passive_selected(self):
        is_disabled = self.ui.inputIsPassive.isChecked()
        self.ui.inputDataPort.setDisabled(is_disabled)
        self.ui.inputDataAddress.setDisabled(is_disabled)

    def selected(self):
        self.done(1)