from PySide6.QtWidgets import QDialog

from uicompiled.loader import Ui_Dialog as UiLoader

class LoaderDialog(QDialog):
    def __init__(self, parent = None):
        super().__init__(parent)
        self.ui = UiLoader()
        self.ui.setupUi(self)