from PySide6.QtWidgets import QDialog

from uicompiled.create import Ui_Dialog as UiCreate

class CreateDialog(QDialog):
    def __init__(self, parent = None):
        super().__init__(parent)
        self.ui = UiCreate()
        self.ui.setupUi(self)
        self.ui.buttonBox.accepted.connect(self.accept)
        self.ui.buttonBox.rejected.connect(self.reject)

    def get_name(self):
        return self.ui.inputNewName.text()