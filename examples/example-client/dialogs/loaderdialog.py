from PySide6.QtWidgets import QDialog
from typing import Callable, Any

from uicompiled.loader import Ui_Dialog as UiLoader

class LoaderDialog(QDialog):
    def __init__(self, job: Callable[[], Any], parent = None):
        super().__init__(parent)
        self.ui = UiLoader()
        self.ui.setupUi(self)
        self.__job = job

    def get_result(self):
        result = self.__job()
        self.done(0)
        return result