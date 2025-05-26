from PySide6.QtWidgets import QDialog

from uicompiled.connect import Ui_Dialog as UiConnect

class ConnectDialog(QDialog):
    def __init__(self, parent=None) -> None:
        super().__init__(parent)
        self.ui = UiConnect()
        self.ui.setupUi(self)
        self.ui.buttonConnect.clicked.connect(self.close)

    def get_options(self):
        return {
            "server_address": self.ui.inputServerAddress.text(),
            "server_port": self.ui.inputServerPort.text(),
            "is_passive": self.ui.inputIsPassive.isChecked(),
            "user_port": self.ui.inputDataPort.text(),
            "user_login": self.ui.inputLogin.text(),
            "user_password": self.ui.inputPassword.text()
        }