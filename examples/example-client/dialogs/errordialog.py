from PySide6.QtWidgets import QDialog

from uicompiled.error import Ui_Dialog as UiError

class ErrorDialog(QDialog):
    def __init__(self, textError: str, parent=None):
        super().__init__(parent)
        self.ui = UiError()
        self.ui.setupUi(self)
        self.ui.textEdit.setText(textError)
        self.ui.closeButton.clicked.connect(self.close)
