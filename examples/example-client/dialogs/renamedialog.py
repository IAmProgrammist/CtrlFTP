from PySide6.QtWidgets import QDialog

from uicompiled.rename import Ui_Dialog as UiRename

class RenameDialog(QDialog):
    def __init__(self, old_name: str = None, parent = None):
        super().__init__()
        self.ui = UiRename()
        self.ui.setupUi(self)
        self.ui.buttonBox.accepted.connect(self.accept)
        self.ui.buttonBox.rejected.connect(self.reject)
        if old_name is not None:
            self.ui.inputNewName.setText(old_name)

    def get_name(self):
        return self.ui.inputNewName.text()